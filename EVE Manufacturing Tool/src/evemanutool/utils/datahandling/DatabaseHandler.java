package evemanutool.utils.datahandling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.beimin.eveapi.exception.ApiException;

import evemanutool.data.general.Pair;
import evemanutool.gui.main.EMT;

public class DatabaseHandler extends ThreadedHandler{
	
	//Constants.
	public enum Stage {RAW(0), NESTED(1), DERIVED(2), PROCESS(3), COMPUTE(4), SAVE(5);
		public int key;	
		Stage(int key){this.key = key;}
	}
	public enum State {RUNNING, WAITING}
	
	//Databases.
	private ArrayList<Database> databases = new ArrayList<>();
	
	//GUI updates.
	private ConcurrentHashMap<Pair<Database, Stage>, ArrayList<GUIUpdater>> gUpdates = new ConcurrentHashMap<>();

	//GUI disables.
	private ConcurrentHashMap<Database, GUIDisabler> gDisables = new ConcurrentHashMap<>();
	
	//ThreadPool.
	private ExecutorService es;
	
	//Object locks.
	private final Object dbLock = new Object();
	
	public DatabaseHandler() {}
	
	public void addDatabase(Database db) {
		//Only add if handler isn't running.
		if (!isRunning()) {
			synchronized (dbLock) {
				databases.add(db);
				//Set initial values for the database.
				db.setStage(Stage.RAW);
				db.setState(State.WAITING);
			}
		}
	}
	
	public void addGUIDisabler(GUIDisabler gD, Database db) {
		//Only add if handler isn't running.
		if (!isRunning()) {
			//Add / Overwrite the GUIDisabler.
			gDisables.put(db, gD);
		}
	}
	
	public void addGUIUpdater(GUIUpdater gU, Stage stage, Database db) {
		//Only add if handler isn't running.
		if (!isRunning()) {
			//Add the GUIUpdater to the right key-entry.
			Pair<Database, Stage> key = new Pair<>(db, stage);
			if (gUpdates.get(key) == null) {
				ArrayList<GUIUpdater> tmpL = new ArrayList<>();
				gUpdates.put(key, tmpL);
			}
			gUpdates.get(key).add(gU);
		}
	}

	public void addGUIUpdaters(Collection<GUIUpdater> l, Stage stage, Database db) {
		for (GUIUpdater gU : l) {
			addGUIUpdater(gU, stage, db);
		}
	}

	@Override
	public void init() {
		if (!isRunning()) {
			//Status.
			setRunning(true);
			
			//Create the ThreadPool.
			es = Executors.newCachedThreadPool();
			
			//Execute main scheduler task.
			es.execute(new DBTaskScheduler());
			System.out.println("DataHandler Initialized.");
		}
	}
	
	@Override
	public void exit() {
		if (isRunning()) {
			
			es.shutdownNow();
			//Create a new ThreadPool.
			es = Executors.newCachedThreadPool();
			synchronized (dbLock) {
				for (Database db : databases) {
					if (db.needsTermination() && db.isComplete()) {
						launchTask(db, Stage.SAVE);
					}
				}
			}
			es.shutdown();
			try {
				es.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void kill() {
		if (isRunning()) {
			es.shutdownNow();
			synchronized (dbLock) {
				for (Database db : databases) {
					db.kill();
				}
			}
		}
	}
	
	public void reportDBUpdateAtStage(Database db, Stage stage) {
		
		handleDatabaseUpdate(db, stage);
	}
	
	public boolean updateDBAtStage(Database db, Stage stage) {
		
		//Only update if database is past the stage or is executing it currently.
		if (db.getStage().key > stage.key ||
				(db.getStage() == stage && db.getState() == State.RUNNING)) {
			
			//Setup database for update.
			db.setStage(stage);
			db.setState(State.WAITING);
			
			handleDatabaseUpdate(db, stage);
			
			return true;
		}
		return false;
	}
	
	private void handleDatabaseUpdate(Database db, Stage stage) {
		
		Stage updateStage;
		//Don't update other Databases if it hasn't reached the stage preceding the firstProvidingStage.
		if (db.isProvider() && db.getFirstProividingStage().key <= getNextStage((updateStage = stage)).key) {
			updateStage = updateStage.key > db.getFirstProividingStage().key ?
								updateStage : db.getFirstProividingStage();
			synchronized (dbLock) {
				//Update other databases if needed.
				for (Database itrDb : databases) {
					if (itrDb.getFinalStage().key >= updateStage.key && (itrDb.getStage().key > updateStage.key ||
							(db.getStage() == updateStage && db.getState() == State.RUNNING))) {
						//Ready databases to be included in next task launch.
						//Synchronized init-methods should keep data safe 
						//from multiple stages being executed concurrently.
						itrDb.setStage(updateStage);
						itrDb.setState(State.WAITING);
					}
				}
			}
		}
	}
	
	private void launchGUIUpdates(Database db, Stage stage) {
		//Create key.
		Pair<Database, Stage> key = new Pair<>(db, stage);
		final ArrayList<GUIUpdater> l = gUpdates.get(key);
		
		if (l != null) {
			//Schedule GUI thread to do updates.
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					for (GUIUpdater gU : l) {
						System.out.println("GUI updated: " + gU.getClass().getSimpleName());
						gU.updateGUI();
					}
				}
			});
		}
	}
	
	private void launchGUIDisabler(final Database db) {
		//Create key.
		final GUIDisabler gD = gDisables.get(db);
		
		if (gD != null) {
			//Schedule GUI thread to do updates.
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					System.out.println("GUI disabled: " + db.getClass().getSimpleName());
					gD.disableGUI();
				}
			});
		}
	}
	
	private void launchTask(Database db, Stage stage) {
		db.setStage(stage);
		db.setState(State.RUNNING);
		es.execute(new DBWorker(db, stage));
	}
	
	private ArrayList<Database> getReadyDatabases() {
		
		int min = Stage.COMPUTE.key; //Last possible stage.
		ArrayList<Database> ans = new ArrayList<>();
		
		synchronized (dbLock) {
			//Find the earliest unfinished stage of the Databases.
			for (Database itrDb : databases) {
				if (	itrDb.getStage().key < min && 
						!itrDb.pastFinalStage() && 
						itrDb.isProvider() &&
						itrDb.isEnabled()) {
					
					min = itrDb.getStage().key;
				}
			}
			//Add all earlier or equal and waiting Databases.
			for (Database itrDb : databases) {
				if (	!itrDb.pastFinalStage() &&
						itrDb.getState() == State.WAITING &&
						itrDb.getStage().key <= min &&
						itrDb.isEnabled()) { 
					
					ans.add(itrDb);
				}
			}
		}
		return ans;
	}
	
	private Stage getNextStage(Database db) {
		//Returns the next stage in Stage Enum.
		return Stage.values()[Stage.SAVE.key > db.getStage().key ? db.getStage().key + 1 : db.getStage().key];
	}

	private Stage getNextStage(Stage stage) {
		//Returns the next stage in Stage Enum.
		return Stage.values()[Stage.SAVE.key > stage.key ? stage.key + 1 : stage.key];
	}

	private class DBTaskScheduler implements Runnable{
		
		//Constants.
		public static final long REFRESH_DELAY = 100; //ms
		
		@Override
		public void run() {
			
			while (!Thread.interrupted()) {
				
				//Launch the readied tasks.
				for (Database db : getReadyDatabases()) {
					System.out.println("Task launched: " + db.getClass().getSimpleName() + " / " + db.getStage());
					launchTask(db, db.getStage());
				}

				try {
					//Wait until next update.
					Thread.sleep(REFRESH_DELAY);
					
				} catch (InterruptedException e) {
					//End thread.
					return;
				}
			}
		}
	}

	private class DBWorker implements Runnable{
		
		private Database db;
		private Stage stage;
		
		public DBWorker(Database db, Stage stage) {
			this.db = db;
			this.stage = stage;
		}

		@Override
		public void run() {
			//Switch depending on the stage.
			try {
				switch (stage) {
				
				case RAW:
					db.loadRawData();
					break;
					
				case NESTED:
					db.loadNestedData();
					break;
					
				case DERIVED:
					db.loadDerivedData();
					break;
					
				case PROCESS:
					db.processData();
					break;
					
				case COMPUTE:
					db.computeData();
					break;

				case SAVE:
					db.saveData();
					break;
					
				default:
					break;
				}
			} catch (Exception e) {
				System.out.println("Exception during task: "  + db.getClass().getSimpleName() + "-" + db.getStage());
				if (e instanceof ApiException) {
					System.out.println(((ApiException) e).getMessage());
				}
				e.printStackTrace();
				
				//An unexpected error has occurred and the database is disabled.
				db.setEnabled(false);
				db.setState(State.WAITING);
				
				//Database is critical to application, terminate with message.
				if (db.isProvider()) {
					//Shows the message, and kills app.
					JOptionPane.showMessageDialog(EMT.MAIN, "Critical loading error, some data files may have been corrupted.",
							"Error", JOptionPane.ERROR_MESSAGE);
					EMT.MAIN.killApp();
					
				} else {
					//Disable GUI if database isn't a provider.
					launchGUIDisabler(db);
				}
				return;
			}
			System.out.println("Task finished: " + db.getClass().getSimpleName() + " / " + stage);
			
			//Launch GUI updates if needed.
			launchGUIUpdates(db, stage);
			
			//Only change status of the database if no changes has occurred during execution.
			if (db.getStage() == stage && db.getState() == State.RUNNING) {
				//Change the State and Stage on completion.
				db.setStage(getNextStage(db));
				db.setState(State.WAITING);
			}
		}
	}
}
