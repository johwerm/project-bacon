package evemanutool.utils.datahandling;

import evemanutool.utils.datahandling.DatabaseHandler.Stage;
import evemanutool.utils.datahandling.DatabaseHandler.State;

public class Database {
	
	/*
	 * Subclasses:
	 * - Should implement an init(...) method to initialize dependencies.
	 * - Should setComplete(True) to avoid unnecessary work.
	 * - Should explicitly use a super constructor if database
	 *   needs termination or is a provider. (See methods for explanation).
	 * - All public methods should be protected by synchronized access to data.
	 *   i.e thread-safe wrappers if objects are changed or volatile fields if only reference is changed.
	 * - The initialization method should be able to run independent of each other
	 *   as long as the previous stages has been executed at some point in application life.
	 */
	
	//Constant info.
	private final boolean provider;
	private final boolean needsTermination;
	private final Stage firstProvidingStage;
	private final Stage finalStage;
	
	//Variable info.
	private State state;
	private Stage stage;
	private boolean enabled;
	private boolean complete;
	
	//Lock objects.
	private final Object completeLock = new Object();
	private final Object enableLock = new Object();
	private final Object stateLock = new Object();
	private final Object stageLock = new Object();

	public Database(boolean provider, boolean needsTermination, Stage finalStage, Stage firstProvidingStage) {
		this.provider = provider;
		this.needsTermination = needsTermination;
		this.finalStage = finalStage;
		this.firstProvidingStage = firstProvidingStage;
		this.enabled = true;
		this.complete = false;
	}

	/*
	 * Reflects whether other Databases depend on the
	 * data from this one.
	 */
	public boolean isProvider() {
		return provider;
	}
	
	/*
	 * Reflects whether this Database has data
	 * to save on application termination.
	 */
	public boolean needsTermination() {
		return needsTermination;
	}
	
	/*
	 * The first stage this Database has data
	 * to provide to other Databases.
	 */
	public Stage getFirstProividingStage() {
		return firstProvidingStage;
	}

	/*
	 * The last needed stage.
	 */
	public Stage getFinalStage() {
		return finalStage;
	}

	/*
	 * Reflects which state in initiation 
	 * this Database has at the current stage.
	 */
	public State getState() {
		synchronized (stateLock) {
			return state;
		}
	}

	public void setState(State state) {
		synchronized (stateLock) {
			this.state = state;
		}
	}

	/*
	 * Reflects which stage in initiation 
	 * this Database is at.
	 */
	public Stage getStage() {
		synchronized (stageLock) {
			return stage;
		}
	}

	public void setStage(Stage stage) {
		synchronized (stageLock) {
			this.stage = stage;
		}
	}

	/*
	 * Reflects whether this Database 
	 * is enabled and still in use.
	 * i.e has not experienced any critical failure or exception.
	 */
	public boolean isEnabled() {
		synchronized (enableLock) {
			return enabled;
		}
	}

	protected void setEnabled(boolean enabled) {
		synchronized (enableLock) {
			this.enabled = enabled;
		}
	}

	/*
	 * Reflects whether this Database 
	 * has completed initiation in it's lifetime.
	 * 
	 * To be set by stage-load implementation to 
	 * avoid running unnecessary stages.
	 */
	public boolean isComplete() {
		synchronized (completeLock) {
			return complete;
		}
	}

	protected void setComplete(boolean complete) {
		synchronized (completeLock) {
			this.complete = complete;
		}
	}
	
	/*
	 * Reflects whether this Database 
	 * is currently past it's last stage.
	 */
	public boolean pastFinalStage() {
		return finalStage.key < getStage().key;
	}
	

	/*
	 * Importing data.
	 * No processing using dependencies.
	 * Not including collected data from dependencies. 
	 */
	public synchronized void loadRawData() throws Exception{}

	/*
	 * Importing data.
	 * No processing using dependencies.
	 * Including collected data from dependencies.
	 */
	public synchronized void loadNestedData() throws Exception {}
	
	/*
	 * Importing data.
	 * Processing using dependencies.
	 * Including collected data using dependencies.
	 */
	public synchronized void loadDerivedData() throws Exception {}

	/*
	 * Processing data.
	 * Sorting restructuring etc.
	 * Updates and completes the data for displaying or final computation.
	 */
	public synchronized void processData() throws Exception {}

	/*
	 * Computing data.
	 * Computes the desired results using earlier data-structures.
	 */
	public synchronized void computeData() throws Exception {}

	/*
	 * Saving data.
	 * Saves cached data to text files.
	 */
	public void saveData() {}
	
	/*
	 * End database.
	 * Kill any processes running internally.
	 * Only to be used at program failure.
	 */
	public void kill() {}
}
