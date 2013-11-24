package evemanutool.gui.main;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.configuration.ConfigurationException;

import evemanutool.constants.AppConstants;
import evemanutool.constants.DBConstants;
import evemanutool.constants.GUIConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.data.database.ManuQuote;
import evemanutool.gui.corp.AssetsPanel;
import evemanutool.gui.corp.POSPanel;
import evemanutool.gui.corp.ProductionPanel;
import evemanutool.gui.corp.StatsPanel;
import evemanutool.gui.corp.SupplyPanel;
import evemanutool.gui.corp.TradePanel;
import evemanutool.gui.general.components.AnimatedLabel;
import evemanutool.gui.manu.MiningPanel;
import evemanutool.gui.manu.QuoteListPanel;
import evemanutool.gui.manu.components.InventionInspectPanel;
import evemanutool.gui.manu.components.ManuInspectPanel;
import evemanutool.gui.manu.components.ReveseEngineeringInspectPanel;
import evemanutool.gui.prefs.PrefsDialog;
import evemanutool.prefs.Preferences;
import evemanutool.utils.databases.BlueprintDB;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.databases.GraphicDB;
import evemanutool.utils.databases.ItemDB;
import evemanutool.utils.databases.LocationDB;
import evemanutool.utils.databases.MarketGroupDB;
import evemanutool.utils.databases.PriceDB;
import evemanutool.utils.databases.QuoteDB;
import evemanutool.utils.databases.TechDB;
import evemanutool.utils.datahandling.DatabaseHandler;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;
import evemanutool.utils.datahandling.GUIDisabler;
import evemanutool.utils.datahandling.GUIUpdater;
import evemanutool.utils.datahandling.MessageHandler;
import evemanutool.utils.httpdata.ProgressWorker;

@SuppressWarnings("serial")
public class EMT extends JFrame implements UserPrefConstants, DBConstants, GUIConstants, AppConstants {

	//Reference to main class instance, used to top level GUI.
	//Only to be used from dispatch thread.
	public static EMT MAIN;

	//Reference to main datahandler, used to control updates and programflow.
	public static final DatabaseHandler D_HANDLER = new DatabaseHandler();
	public static final MessageHandler M_HANDLER = new MessageHandler();

	//DB:s
	private final BlueprintDB bdb;
	private final MarketGroupDB mdb;
	private final ItemDB idb;
	private final TechDB tdb;
	private final PriceDB pdb;
	private final GraphicDB gdb;
	private final LocationDB ldb;
	private final CorpApiDB cdb;
	private final QuoteDB qdb;
	
	//User.
	private Preferences prefs;
	
	//Graphical components.
	private JTabbedPane tabbedPane;
	private JTabbedPane indyTabbedPane;
	private JTabbedPane corpTabbedPane;
	private MiningPanel orePanel;
	private MiningPanel icePanel;
	private QuoteListPanel manuPanel;
	private QuoteListPanel invPanel;
	private QuoteListPanel revPanel;
	private ProductionPanel prodPanel;
	private StatsPanel statsPanel;
	private POSPanel posPanel;
	private SupplyPanel supplyPanel;
	private AssetsPanel assetsPanel;
	private TradePanel tradePanel;
	
	//Status GUI.
	private JLabel marketStatus;
	private JProgressBar marketPB;
	private JLabel historyStatus;
	private JProgressBar historyPB;
	
	//Menubar.
	private JMenuItem prefItem;
	private JMenuItem exitItem;
	private JMenuItem updateItem;
	private JMenuItem creditsItem;
	private AnimatedLabel statusLabel;

	public EMT() {
		
		//Set the look an feel to make it look awesome!
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {}
		
		//Create instances.
		idb = new ItemDB();
		mdb = new MarketGroupDB();
		gdb = new GraphicDB();
		ldb = new LocationDB();
		tdb = new TechDB();
		bdb = new BlueprintDB();
		pdb = new PriceDB();
		qdb = new QuoteDB();
		cdb = new CorpApiDB();
		
		//Setup settings.
		prefs = new Preferences();
		
		//Setup the menubar.
		initMenuBar();
		
		//Do main layout.
		initMainLayout();
		
		//Setup progress and status indicators.
		initStatusLayout();
		
		//Do corp layout.
		initCorpLayout();

		//Do manufacturing layout last to get references to production in corpTab.
		initManuLayout();
		
		//Setup message service.
		initMessageHandler();

		//Setup databases.
		initDatabaseHandler();
		
		//Pack and show.
		pack();
		setMinimumSize(new Dimension(1300, 600));
		setSize(1400, 800);
		//Set icon.
		ImageIcon img = new ImageIcon(WINDOW_ICON_PATH);
		setIconImage(img.getImage());
		//Set title with version number.
		setTitle("EMT - EVE Manufacturing Tool  v" + APP_VERSION);
		setVisible(true);
	}
	
	private void initMainLayout() {
	
		//Setup Layout.
		setLayout(new BorderLayout());
		
		//Outer tab.
		tabbedPane = new JTabbedPane();
		
		//Mining pane.
		indyTabbedPane = new JTabbedPane();
	
		//Corp pane.
		corpTabbedPane = new JTabbedPane();
		
		//Add tabs
		tabbedPane.add("Industry & Manufacturing", indyTabbedPane);
		tabbedPane.add("Corporation Management", corpTabbedPane);
		
		//Add components to frame.
		add(tabbedPane, BorderLayout.CENTER);
		
		//Setup Frame.
		addWindowListener(new ExitListener());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void initStatusLayout() {
		
		//Setup progressbar for market queries.
		//Market.
		marketPB = new JProgressBar(ProgressWorker.PROGRESS_MIN, ProgressWorker.PROGRESS_MAX);
		marketStatus = new JLabel();
		marketStatus.setPreferredSize(new Dimension(50, 0)); //Height doesn't matter, (GridBagLayout).
		marketStatus.setHorizontalTextPosition(SwingConstants.LEFT);
		
		//History.
		historyPB = new JProgressBar(ProgressWorker.PROGRESS_MIN, ProgressWorker.PROGRESS_MAX);
		historyStatus = new JLabel();
		historyStatus.setPreferredSize(new Dimension(50, 0)); //Height doesn't matter, (GridBagLayout).
		historyStatus.setHorizontalTextPosition(SwingConstants.LEFT);
		
		//ProgressBar and statusField.	
		JPanel progressPanel = new JPanel(new GridBagLayout());
		progressPanel.setPreferredSize(new Dimension(0, 27));
		progressPanel.setBorder(BorderFactory.createEtchedBorder());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1.0;
		c.insets = new Insets(0, 0, 0, 7);
		progressPanel.add(new JLabel("Market data:"), c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.weightx = 0.5;
		c.insets = new Insets(0, 0, 0, 10);
		progressPanel.add(marketPB, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1.0;
		c.insets = new Insets(0, 0, 0, 7);
		progressPanel.add(new JLabel("Status:"), c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.weightx = 0.2;
		progressPanel.add(marketStatus, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1.0;
		c.insets = new Insets(0, 0, 0, 7);
		progressPanel.add(new JLabel("Trade history:"), c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.weightx = 0.5;
		c.insets = new Insets(0, 0, 0, 10);
		progressPanel.add(historyPB, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1.0;
		c.insets = new Insets(0, 0, 0, 7);
		progressPanel.add(new JLabel("Status:"), c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.weightx = 0.2;
		progressPanel.add(historyStatus, c);
		
		add(progressPanel, BorderLayout.SOUTH);
	}

	private void initCorpLayout() {
		
		//Corporation Management Tab.
		//Statistics.
		statsPanel = new StatsPanel(cdb);
		corpTabbedPane.addTab("Statistics & Members", statsPanel);
		
		//Trade.
		tradePanel = new TradePanel(cdb);
		corpTabbedPane.addTab("Market & Contracts", tradePanel);
		
		//Assets.
		assetsPanel = new AssetsPanel(cdb);
		corpTabbedPane.addTab("Assets & Blueprints", assetsPanel);

		//POS.
		posPanel = new POSPanel(cdb);
		corpTabbedPane.addTab("POS", posPanel);
		
		//Supplies.
		supplyPanel = new SupplyPanel(cdb);
		
		//Production.
		prodPanel = new ProductionPanel(cdb, pdb, supplyPanel);
		
		corpTabbedPane.addTab("Production", prodPanel);
		corpTabbedPane.addTab("Supplies", supplyPanel);
	}

	private void initManuLayout() {
		
		//Manufacturing Tab.
		//T1.
		manuPanel = new QuoteListPanel(QuoteType.T1, qdb, mdb, new ManuInspectPanel(prefs, pdb, bdb, cdb, prodPanel));
		indyTabbedPane.addTab("Manufacturing", manuPanel);
		
		//Invention.
		invPanel = new QuoteListPanel(QuoteType.INV, qdb, mdb, new InventionInspectPanel(prefs, pdb, bdb, tdb, cdb, prodPanel));
		indyTabbedPane.addTab("Invention", invPanel);
		
		//Reverse engineering.
		revPanel = new QuoteListPanel(QuoteType.REV, qdb, mdb, new ReveseEngineeringInspectPanel(prefs, pdb, bdb, idb, cdb, prodPanel));
		indyTabbedPane.addTab("Reverse Engineering", revPanel);
		
		//Ice & Ore.
		JPanel jP = new JPanel(new GridLayout(1, 2));
		orePanel = new MiningPanel(QuoteType.ORE, qdb, "Ore");
		icePanel = new MiningPanel(QuoteType.ICE, qdb, "Ice");
		jP.add(orePanel);
		jP.add(icePanel);
		indyTabbedPane.addTab("Mining", jP);
	}
	
	private void initMenuBar() {
		
		//Setup menubar.
		MenuListener ml = new MenuListener();
		JMenuBar menubar = new JMenuBar();		
		
		//Create menus.
		JMenu fileMenu = new JMenu("Application");
		menubar.add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		menubar.add(editMenu);

		JMenu helpMenu = new JMenu("Help");
		menubar.add(helpMenu);
		
		//Create menuItems.
		prefItem = new JMenuItem("Preferences");
		prefItem.addActionListener(ml);
		editMenu.add(prefItem);
		
		updateItem = new JMenuItem("Force Market Update");
		updateItem.addActionListener(ml);
		fileMenu.add(updateItem);
		
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(ml);
		fileMenu.add(exitItem);

		creditsItem = new JMenuItem("Credits");
		creditsItem.addActionListener(ml);
		helpMenu.add(creditsItem);
		
		//Add message bar.
		menubar.add(Box.createHorizontalGlue());
		statusLabel = new AnimatedLabel(300, 20);
		statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
		menubar.add(statusLabel);
		
		//Add it to the frame.
		setJMenuBar(menubar);
	}

	private void initMessageHandler() {
		
		//Set label.
		M_HANDLER.setLabel(statusLabel);
		
		//Start message service.
		M_HANDLER.init();
	}

	private void initDatabaseHandler() {
		
		//Initiate.
		ldb.init(idb);
		tdb.init(idb);
		bdb.init(idb, mdb, tdb);
		pdb.init(bdb, prefs, marketPB, marketStatus, historyPB, historyStatus);
		qdb.init(pdb, bdb, tdb, idb, prefs);
		cdb.init(prefs, bdb, idb, pdb, ldb, gdb, tdb);
		
		//Add databases.
		D_HANDLER.addDatabase(idb);
		D_HANDLER.addDatabase(mdb);
		D_HANDLER.addDatabase(gdb);
		D_HANDLER.addDatabase(ldb);
		D_HANDLER.addDatabase(tdb);
		D_HANDLER.addDatabase(bdb);
		D_HANDLER.addDatabase(pdb);
		D_HANDLER.addDatabase(qdb);
		D_HANDLER.addDatabase(cdb);
		
		//Add GUI updates.
		ArrayList<GUIUpdater> tmp = new ArrayList<>();
		tmp.add(manuPanel); tmp.add(invPanel); tmp.add(revPanel);
		tmp.add(orePanel); tmp.add(icePanel);
		D_HANDLER.addGUIUpdaters(tmp, Stage.COMPUTE, qdb);
		D_HANDLER.addGUIUpdater(statsPanel, Stage.COMPUTE, cdb);
		D_HANDLER.addGUIUpdater(tradePanel, Stage.PROCESS, cdb);
		D_HANDLER.addGUIUpdater(assetsPanel, Stage.PROCESS, cdb);
		D_HANDLER.addGUIUpdater(posPanel, Stage.PROCESS, cdb);
		D_HANDLER.addGUIUpdater(prodPanel, Stage.COMPUTE, cdb);
		D_HANDLER.addGUIUpdater(supplyPanel, Stage.COMPUTE, cdb);
		
		//Add GUI disablers.
		D_HANDLER.addGUIDisabler(new GUIDisabler() {
			
			@Override
			public void disableGUI() {
				tabbedPane.setEnabledAt(CORP_TAB_INDEX, false);
			}
		}, cdb);
		D_HANDLER.addGUIDisabler(new GUIDisabler() {
			
			@Override
			public void disableGUI() {
				tabbedPane.setEnabledAt(INDY_TAB_INDEX, false);
			}
		}, qdb);
		
		//Start handler.
		D_HANDLER.init();
	}
	
	private void savePrefs() {
		try {
			//Only save if valid.
			if (prefs != null) {
				prefs.save();
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void exitHandlers() {
		
		//Close the handler and save database content.
		D_HANDLER.exit();
		M_HANDLER.exit();
	}
	
	public void killApp() {
		D_HANDLER.kill();
		M_HANDLER.kill();
		dispose();
	}
	
	/*
	 * Tries to open the given quote in the manufacturing tab.
	 * Returns weather successful or not.
	 */
	public boolean showQuote(ManuQuote q) {
		
		//Try to select in all panels.
		if (manuPanel.selectQuote(q)) {
			switchTopTab(0);
			switchSubTab(0);
			return true;
		}else if (invPanel.selectQuote(q)) {
			switchTopTab(0);
			switchSubTab(1);
			return true;
		}else if (revPanel.selectQuote(q)) {
			switchTopTab(0);
			switchSubTab(2);
			return true;
		}
		//No selection successful, return false.
		return false;
	}
	
	/*
	 * Transfers focus to the tab of the given index.
	 * Only performed if index is valid.
	 * In the top tabbedPane.
	 */
	public void switchTopTab(int tabIndex) {
		
		//Check if index is valid.
		if (tabbedPane.getTabCount() > tabIndex && tabIndex >= 0) {
			tabbedPane.setSelectedIndex(tabIndex);
		}
	}
	
	/*
	 * Transfers focus to the tab of the given index.
	 * Only performed if index is valid.
	 * In the selected sub tabbedPane.
	 */
	public void switchSubTab(int tabIndex) {
		//Check if index is valid.
		JTabbedPane subTab = (JTabbedPane) tabbedPane.getSelectedComponent();
		
		if (subTab.getTabCount() > tabIndex && tabIndex >= 0) {
			subTab.setSelectedIndex(tabIndex);
		}
	}
	
	private class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			JMenuItem i = (JMenuItem) e.getSource();
			
			if (i == prefItem) {
				//Open preference dialog.
				new PrefsDialog(prefs, qdb, pdb, cdb);	

			}else if (i == exitItem) {
				//Save and exit.
				savePrefs();
				exitHandlers();
				//Close GUI.
				dispose();
			}else if (i == updateItem) {
				if (pdb.forceUpdateMarketData()) {
					//Update commenced, do nothing.
				}else {
					//Not performed, launch dialog.
					JOptionPane.showMessageDialog(EMT.MAIN, "Updates are limited to ease server load, please wait!\n" +
							"(" + pdb.getMinsToNextMarketUpdate() + " min)", "Info", JOptionPane.INFORMATION_MESSAGE);
				}
			}else if (i == creditsItem) {
				//Show credits.
				JOptionPane.showMessageDialog(EMT.MAIN, CREDITS_TEXT, "Credits", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
	
	private class ExitListener implements WindowListener {

		@Override
		public void windowClosing(WindowEvent e) {
			savePrefs();
			exitHandlers();
		}

		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowClosed(WindowEvent e) {}

		@Override
		public void windowDeactivated(WindowEvent e) {}

		@Override
		public void windowDeiconified(WindowEvent e) {}

		@Override
		public void windowIconified(WindowEvent e) {}

		@Override
		public void windowOpened(WindowEvent e) {}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
			public void run() {
		    	MAIN = new EMT();
		    }
		});
	}
}
