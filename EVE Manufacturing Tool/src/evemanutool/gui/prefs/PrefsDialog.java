package evemanutool.gui.prefs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import evemanutool.constants.DBConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.gui.general.components.LabelBox;
import evemanutool.gui.general.components.NumberField;
import evemanutool.gui.main.EMT;
import evemanutool.user.Preferences;
import evemanutool.user.Preferences.API;
import evemanutool.user.Preferences.Account;
import evemanutool.user.Preferences.BlueprintStat;
import evemanutool.user.Preferences.DefaultPriority;
import evemanutool.user.Preferences.ImplantMod;
import evemanutool.user.Preferences.InstallationMod;
import evemanutool.user.Preferences.ManufacturingCost;
import evemanutool.user.Preferences.MarketAction;
import evemanutool.user.Preferences.MarketPriceType;
import evemanutool.user.Preferences.MarketSetting;
import evemanutool.user.Preferences.MarketSystem;
import evemanutool.user.Preferences.MarketTax;
import evemanutool.user.Preferences.MiningCycle;
import evemanutool.user.Preferences.MiningLasers;
import evemanutool.user.Preferences.MiningYield;
import evemanutool.user.Preferences.Skill;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.databases.PriceDB;
import evemanutool.utils.databases.QuoteDB;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;


@SuppressWarnings("serial")
public class PrefsDialog extends JDialog implements UserPrefConstants, DBConstants, ActionListener {

	//Settings object.
	private Preferences prefs;
	
	//Databases.
	private QuoteDB qdb;
	private PriceDB pdb;
	private CorpApiDB cdb;
	
	
	//Skill panel.
	private JComboBox<String> industry;
	private JComboBox<String> efficiency;
	private JComboBox<String> science;
	private JComboBox<String> encryption;
	private JComboBox<String> datacore;
	private JComboBox<String> reverse;
	private JComboBox<String> peImplant;
	private JComboBox<String> invImplant;
	
	//Manufacturing panel.
	private NumberField install;
	private NumberField installh;
	private NumberField broker;
	private NumberField sales; 
	private JComboBox<String> peMod;
	private JComboBox<String> meMod;
	private JComboBox<String> invMod;
	private JComboBox<String> copyMod;
	private JComboBox<String> matPrio;
	private JComboBox<String> invPrio;
	private JComboBox<String> revPrio;
	private NumberField meLevel;
	private NumberField peLevel;
	
	//Market panel.
	private JComboBox<String> sellSystem;
	private JComboBox<String> buySystem;
	private JComboBox<String> sellAim;
	private JComboBox<String> buyAim;
	private JComboBox<String> sellType;
	private JComboBox<String> buyType;
	private NumberField updates;
	
	//Mining panel.
	private NumberField oreLasers;
	private NumberField iceLasers;
	private NumberField oreCycle;
	private NumberField iceCycle;
	private NumberField oreYield;
	
	//Corp panel.
	private NumberField apiId;	
	private JTextField apiKey;
	private JComboBox<String> industryHangar;	
	private JComboBox<String> industryWallet;
	
	//Action Buttons.
	private JButton save_btn;
	private JButton cancel_btn;


	public PrefsDialog(Preferences prefs, QuoteDB qdb, PriceDB pdb, CorpApiDB cdb) {
		this.prefs = prefs;
		this.qdb = qdb;
		this.pdb = pdb;
		this.cdb = cdb;
		
		//Setup main dialog, set modal, the user should not ignore it.
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Preferences");
		setLayout(new BorderLayout(0, 20));
		
		//Create Top panels.
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
		JPanel actionPanel = new JPanel();
		actionPanel.setPreferredSize(new Dimension(100, 50));
		
		//Create settings panels.
		//Character.
		JPanel charPanel = new JPanel();
		charPanel.setLayout(new BoxLayout(charPanel, BoxLayout.Y_AXIS));
		JPanel charSubPanel1 = new JPanel();
		charSubPanel1.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 10));
		JPanel charSubPanel2 = new JPanel();
		charSubPanel2.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 10));
		
		//Manufacturing.
		JPanel manuPanel = new JPanel();
		manuPanel.setLayout(new BoxLayout(manuPanel, BoxLayout.Y_AXIS));
		JPanel manuSubPanel1 = new JPanel();
		manuSubPanel1.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 10));
		JPanel manuSubPanel2 = new JPanel();
		manuSubPanel2.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 10));
		
		//Market.
		JPanel marketPanel = new JPanel();
		marketPanel.setLayout(new BoxLayout(marketPanel, BoxLayout.Y_AXIS));
		JPanel marketSubPanel1 = new JPanel();
		marketSubPanel1.setLayout(new FlowLayout(FlowLayout.LEADING, 40, 10));
		JPanel marketSubPanel2 = new JPanel();
		marketSubPanel2.setLayout(new FlowLayout(FlowLayout.LEADING, 40, 10));
		JPanel marketSubPanel3 = new JPanel();
		marketSubPanel3.setLayout(new FlowLayout(FlowLayout.LEADING, 40, 10));
		
		//Mining.
		JPanel miningPanel = new JPanel();
		miningPanel.setLayout(new BoxLayout(miningPanel, BoxLayout.Y_AXIS));
		JPanel miningSubPanel1 = new JPanel();
		miningSubPanel1.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 10));
		JPanel miningSubPanel2 = new JPanel();
		miningSubPanel2.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 10));
		
		//Corporation.
		JPanel corpPanel = new JPanel();
		corpPanel.setLayout(new BoxLayout(corpPanel, BoxLayout.Y_AXIS));
		JPanel corpSubPanel1 = new JPanel();
		corpSubPanel1.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 5));
		JPanel corpSubPanel2 = new JPanel();
		corpSubPanel2.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 10));
		JPanel corpSubPanel3 = new JPanel();
		corpSubPanel3.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 10));
		
		//Setup character panel.
		//Skills.
		industry = new JComboBox<>(SKILL_LEVEL_LABEL);
		industry.setSelectedIndex(prefs.getSkillLvlIndex(Skill.INDUSTRY));
		
		charSubPanel1.add(new LabelBox("Industry", industry, BoxLayout.X_AXIS));

		efficiency = new JComboBox<>(SKILL_LEVEL_LABEL);
		efficiency.setSelectedIndex(prefs.getSkillLvlIndex(Skill.PRODUCTION_EFFICIENCY));
		
		charSubPanel1.add(new LabelBox("Production Efficiency", efficiency, BoxLayout.X_AXIS));

		peImplant = new JComboBox<>(MOD_IMPLANT_LABEL);
		peImplant.setSelectedIndex(prefs.getImplantModIndex(ImplantMod.MOD_PE));
		
		charSubPanel1.add(new LabelBox("Implant PE Modifier", peImplant, BoxLayout.X_AXIS));
		
		invImplant = new JComboBox<>(MOD_IMPLANT_LABEL);
		invImplant.setSelectedIndex(prefs.getImplantModIndex(ImplantMod.MOD_PE));
		
		charSubPanel1.add(new LabelBox("Implant Invention Time Modifier", invImplant, BoxLayout.X_AXIS));
		
		science = new JComboBox<>(SKILL_LEVEL_LABEL);
		science.setSelectedIndex(prefs.getSkillLvlIndex(Skill.SCIENCE));
		
		charSubPanel2.add(new LabelBox("Science", science, BoxLayout.X_AXIS));
		
		encryption = new JComboBox<>(SKILL_LEVEL_LABEL);
		encryption.setSelectedIndex(prefs.getSkillLvlIndex(Skill.SCIENCE));
		
		charSubPanel2.add(new LabelBox("Racial Encryption", encryption, BoxLayout.X_AXIS));
		
		datacore = new JComboBox<>(SKILL_LEVEL_LABEL);
		datacore.setSelectedIndex(prefs.getSkillLvlIndex(Skill.SCIENCE));
		
		charSubPanel2.add(new LabelBox("Datacore Skills", datacore, BoxLayout.X_AXIS));
		
		reverse = new JComboBox<>(SKILL_LEVEL_LABEL);
		reverse.setSelectedIndex(prefs.getSkillLvlIndex(Skill.REVERSE_ENGINEERING));
		
		charSubPanel2.add(new LabelBox("Reverse Engineering", reverse, BoxLayout.X_AXIS));
		
		charPanel.add(charSubPanel1);
		charPanel.add(charSubPanel2);
		
		//Setup manufacturing panel.
		install = new NumberField(prefs.getManufacturingCost(ManufacturingCost.INSTALLATION_COST), false, 0, 100000, 5);		
		manuSubPanel1.add(new LabelBox("Installation Cost", install, BoxLayout.Y_AXIS));
		
		installh = new NumberField(prefs.getManufacturingCost(ManufacturingCost.INSTALLATION_COST_H), false, 0, 100000, 5);
		manuSubPanel1.add(new LabelBox("Installation Cost/Hour", installh, BoxLayout.Y_AXIS));
		
		meMod = new JComboBox<>(MOD_SLOT_LABEL);
		meMod.setSelectedIndex(prefs.getInstallationModIndex(InstallationMod.SLOT_MOD_ME));
		manuSubPanel1.add(new LabelBox("ME Modifier", meMod, BoxLayout.Y_AXIS));
		
		peMod = new JComboBox<>(MOD_SLOT_LABEL);
		peMod.setSelectedIndex(prefs.getInstallationModIndex(InstallationMod.SLOT_MOD_PE));
		manuSubPanel1.add(new LabelBox("PE Modifier", peMod, BoxLayout.Y_AXIS));

		invMod = new JComboBox<>(MOD_INV_LABEL);
		invMod.setSelectedIndex(prefs.getInstallationModIndex(InstallationMod.SLOT_MOD_INV));
		manuSubPanel1.add(new LabelBox("Invention Time Modifier", invMod, BoxLayout.Y_AXIS));
		
		copyMod = new JComboBox<>(MOD_COPY_LABEL);
		copyMod.setSelectedIndex(prefs.getInstallationModIndex(InstallationMod.SLOT_MOD_COPY));
		manuSubPanel1.add(new LabelBox("Copy Time Modifier", copyMod, BoxLayout.Y_AXIS));
		
		broker = new NumberField(prefs.getMarketTax(MarketTax.BROKER_FEE), true, 0 , 1.5, 4);
		manuSubPanel1.add(new LabelBox("Broker's Fee %", broker, BoxLayout.Y_AXIS));

		sales = new NumberField(prefs.getMarketTax(MarketTax.SALES_TAX), true, 0, 1.5, 4);
		manuSubPanel1.add(new LabelBox("Sales Tax %", sales, BoxLayout.Y_AXIS));
		
		meLevel = new NumberField(prefs.getBlueprintStat(BlueprintStat.MOD_ME), false, -100, 1000, 4);
		manuSubPanel2.add(new LabelBox("Default ME", meLevel, BoxLayout.Y_AXIS));
		
		peLevel = new NumberField(prefs.getBlueprintStat(BlueprintStat.MOD_PE), false, -100, 1000, 4);
		manuSubPanel2.add(new LabelBox("Default PE", peLevel, BoxLayout.Y_AXIS));
		
		matPrio = new JComboBox<>(MAT_ACQUIRE_PRIO_LABEL);
		matPrio.setSelectedIndex(prefs.getDefaultPriorityIndex(DefaultPriority.MAT_CALC));
		manuSubPanel2.add(new LabelBox("Default Material Acquire Priority", matPrio, BoxLayout.Y_AXIS));

		invPrio = new JComboBox<>(INV_PRIO_LABEL);
		invPrio.setSelectedIndex(prefs.getDefaultPriorityIndex(DefaultPriority.INV_CALC));
		manuSubPanel2.add(new LabelBox("Default Invention Priority", invPrio, BoxLayout.Y_AXIS));

		revPrio = new JComboBox<>(REV_PRIO_LABEL);
		revPrio.setSelectedIndex(prefs.getDefaultPriorityIndex(DefaultPriority.REV_CALC));
		manuSubPanel2.add(new LabelBox("Default Reverse Engineering Priority", revPrio, BoxLayout.Y_AXIS));
		
		manuPanel.add(manuSubPanel1);
		manuPanel.add(manuSubPanel2);
		
		//Setup marketpanel.
		//Systems
		sellSystem = new JComboBox<>(MARKET_SYSTEM_LABEL);
		sellSystem.setSelectedIndex(prefs.getMarketSystemIndex(MarketSystem.SELL_SYSTEM));
		buySystem = new JComboBox<>(MARKET_SYSTEM_LABEL);
		buySystem.setSelectedIndex(prefs.getMarketSystemIndex(MarketSystem.BUY_SYSTEM));
		
		marketSubPanel1.add(new LabelBox("Sell System", sellSystem, BoxLayout.Y_AXIS));
		marketSubPanel2.add(new LabelBox("Buy System", buySystem, BoxLayout.Y_AXIS));
		
		//Price
		sellAim = new JComboBox<>(MARKET_AIM_LABEL);
		sellAim.setSelectedIndex(prefs.getMarketOrderAimIndex(MarketAction.SELL_ACTION));
		buyAim = new JComboBox<>(MARKET_AIM_LABEL);
		buyAim.setSelectedIndex(prefs.getMarketOrderAimIndex(MarketAction.BUY_ACTION));
		
		marketSubPanel1.add(new LabelBox("Sell Price", sellAim, BoxLayout.Y_AXIS));
		marketSubPanel2.add(new LabelBox("Buy Price", buyAim, BoxLayout.Y_AXIS));
		
		//Price
		sellType = new JComboBox<>(MARKET_PRICE_LABEL);
		sellType.setSelectedIndex(prefs.getMarketPriceTypeIndex(MarketPriceType.SELL_TYPE));
		buyType = new JComboBox<>(MARKET_PRICE_LABEL);
		buyType.setSelectedIndex(prefs.getMarketPriceTypeIndex(MarketPriceType.BUY_TYPE));
		
		marketSubPanel1.add(new LabelBox("Sell Price Type", sellType, BoxLayout.Y_AXIS));
		marketSubPanel2.add(new LabelBox("Buy Price Type", buyType, BoxLayout.Y_AXIS));
		
		//Updates.
		updates = new NumberField(prefs.getMarketSetting(MarketSetting.UPDATE_FREQ), false, 0, 500, 3);
		marketSubPanel3.add(new LabelBox("Update Frequency (h)", updates, BoxLayout.X_AXIS));
		
		marketPanel.add(marketSubPanel1);
		marketPanel.add(marketSubPanel2);
		marketPanel.add(marketSubPanel3);
		
		//Setup mining panel.
		//Lasers.
		oreLasers = new NumberField(prefs.getMiningLasers(MiningLasers.ORE), false, 0, 9, 1);
		iceLasers = new NumberField(prefs.getMiningLasers(MiningLasers.ICE), false, 0, 9, 1);
		
		miningSubPanel1.add(new LabelBox("Ore Lasers", oreLasers, BoxLayout.Y_AXIS));
		miningSubPanel2.add(new LabelBox("Ice Lasers", iceLasers, BoxLayout.Y_AXIS));

		//Cycle.
		oreCycle = new NumberField(prefs.getMiningCycle(MiningCycle.ORE), false, 0, 1000, 3);
		iceCycle = new NumberField(prefs.getMiningCycle(MiningCycle.ICE), false, 0, 1000, 3);
		
		miningSubPanel1.add(new LabelBox("Ore Cycle", oreCycle, BoxLayout.Y_AXIS));
		miningSubPanel2.add(new LabelBox("Ice Cycle", iceCycle, BoxLayout.Y_AXIS));
		
		//Yield.
		oreYield = new NumberField(prefs.getMiningYield(MiningYield.ORE), false, 0, 10000, 4);
		
		miningSubPanel1.add(new LabelBox("Ore Yield", oreYield, BoxLayout.Y_AXIS));
		
		miningPanel.add(miningSubPanel1);
		miningPanel.add(miningSubPanel2);
		
		//Corporation.
		//Access mask label.
		long mask = 0;
		for (long i : CORP_ACCESS_MASKS) {
			mask += i;
		}
		
		JTextPane accessMask = new JTextPane();
		accessMask.setText("" + mask);
		accessMask.setEditable(false);
		accessMask.setBackground(getBackground());
		accessMask.setBorder(null);

		corpSubPanel1.add(new JLabel("Access mask:"));
		corpSubPanel1.add(accessMask);
		
		//API.
		apiId = new NumberField(prefs.getAPIId(API.ID), false, 0, Double.MAX_VALUE, 15);
		apiKey = new JTextField(prefs.getAPIKey(API.KEY), 45);
		
		corpSubPanel2.add(new LabelBox("User Id", apiId, BoxLayout.X_AXIS));
		corpSubPanel2.add(new LabelBox("vCode", apiKey, BoxLayout.X_AXIS));

		//Accounts.
		industryHangar = new JComboBox<>(ACCOUNT_LABEL);
		industryHangar.setSelectedIndex(prefs.getAccountIndex(Account.INDUSTRY_HANGAR));
		industryWallet = new JComboBox<>(ACCOUNT_LABEL);
		industryWallet.setSelectedIndex(prefs.getAccountIndex(Account.INDUSTRY_WALLET));
		
		corpSubPanel3.add(new LabelBox("Industry Hangar", industryHangar, BoxLayout.Y_AXIS));
		corpSubPanel3.add(new LabelBox("Industry Wallet", industryWallet, BoxLayout.Y_AXIS));
		
		corpPanel.add(corpSubPanel1);
		corpPanel.add(corpSubPanel2);
		corpPanel.add(corpSubPanel3);

		//Add borders.
		charPanel.setBorder(BorderFactory.createTitledBorder("Character"));
		manuPanel.setBorder(BorderFactory.createTitledBorder("Manufacturing"));
		marketPanel.setBorder(BorderFactory.createTitledBorder("Market"));
		miningPanel.setBorder(BorderFactory.createTitledBorder("Mining"));
		corpPanel.setBorder(BorderFactory.createTitledBorder("Corporation"));
		
		//Add main panels.
		settingsPanel.add(charPanel);
		settingsPanel.add(manuPanel);
		settingsPanel.add(marketPanel);
		settingsPanel.add(miningPanel);
		settingsPanel.add(corpPanel);
		
		//Create action components.
		save_btn = new JButton("Save");
		save_btn.addActionListener(this);
		cancel_btn = new JButton("Cancel");
		cancel_btn.addActionListener(this);
		
		actionPanel.add(save_btn);
		actionPanel.add(cancel_btn);
		
		//Add top panels.
		add(settingsPanel,BorderLayout.CENTER);
		add(actionPanel, BorderLayout.SOUTH);
		pack();
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocation(EMT.MAIN.getLocation());
		setVisible(true);
	}	
	
	private void savePreferences() {
		
		//Save values.
		//Character.
		prefs.setSkillLvlIndex(Skill.INDUSTRY, industry.getSelectedIndex());
		prefs.setSkillLvlIndex(Skill.PRODUCTION_EFFICIENCY, efficiency.getSelectedIndex());
		prefs.setImplantModIndex(ImplantMod.MOD_PE, peImplant.getSelectedIndex());
		prefs.setSkillLvlIndex(Skill.SCIENCE, science.getSelectedIndex());
		prefs.setSkillLvlIndex(Skill.RACIAL_ENCRYPTION, encryption.getSelectedIndex());
		prefs.setSkillLvlIndex(Skill.DATACORE_SKILLS, datacore.getSelectedIndex());
		prefs.setSkillLvlIndex(Skill.REVERSE_ENGINEERING, reverse.getSelectedIndex());
		prefs.setImplantModIndex(ImplantMod.MOD_INV, invImplant.getSelectedIndex());
		
		//Manufacturing.
		prefs.setManufacturingCost(ManufacturingCost.INSTALLATION_COST, (int) install.getValue());
		prefs.setManufacturingCost(ManufacturingCost.INSTALLATION_COST_H, (int) installh.getValue());
		prefs.setMarketTax(MarketTax.BROKER_FEE, broker.getValue());
		prefs.setMarketTax(MarketTax.SALES_TAX, sales.getValue());
		prefs.setBlueprintStat(BlueprintStat.MOD_ME, (int) meLevel.getValue());
		prefs.setBlueprintStat(BlueprintStat.MOD_PE, (int) peLevel.getValue());
		prefs.setManufacturingModIndex(InstallationMod.SLOT_MOD_PE, peMod.getSelectedIndex());
		prefs.setManufacturingModIndex(InstallationMod.SLOT_MOD_ME, meMod.getSelectedIndex());
		prefs.setManufacturingModIndex(InstallationMod.SLOT_MOD_INV, invMod.getSelectedIndex());
		prefs.setManufacturingModIndex(InstallationMod.SLOT_MOD_COPY, copyMod.getSelectedIndex());
		prefs.setDefaultPriorityIndex(DefaultPriority.MAT_CALC, matPrio.getSelectedIndex());
		prefs.setDefaultPriorityIndex(DefaultPriority.INV_CALC, invPrio.getSelectedIndex());
		prefs.setDefaultPriorityIndex(DefaultPriority.REV_CALC, revPrio.getSelectedIndex());
		
		//Market.
		prefs.setMarketSystem(MarketSystem.SELL_SYSTEM, sellSystem.getSelectedIndex());
		prefs.setMarketSystem(MarketSystem.BUY_SYSTEM, buySystem.getSelectedIndex());
		prefs.setMarketActionAimIndex(MarketAction.SELL_ACTION, sellAim.getSelectedIndex());
		prefs.setMarketActionAimIndex(MarketAction.BUY_ACTION, buyAim.getSelectedIndex());
		prefs.setMarketPriceTypeIndex(MarketPriceType.SELL_TYPE, sellType.getSelectedIndex());
		prefs.setMarketPriceTypeIndex(MarketPriceType.BUY_TYPE, buyType.getSelectedIndex());
		prefs.setMarketSetting(MarketSetting.UPDATE_FREQ, (int) updates.getValue());	
		
		//Mining.
		prefs.setMiningLasers(MiningLasers.ORE, (int) oreLasers.getValue());
		prefs.setMiningLasers(MiningLasers.ICE, (int) iceLasers.getValue());
		prefs.setMiningCycle(MiningCycle.ORE, (int) oreCycle.getValue());
		prefs.setMiningCycle(MiningCycle.ICE, (int) iceCycle.getValue());
		prefs.setMiningYield(MiningYield.ORE, oreYield.getValue());
		
		//Corporation.
		prefs.setAPIId(API.ID, (int) apiId.getValue());
		prefs.setAPIKey(API.KEY, apiKey.getText());
		prefs.setAccountIndex(Account.INDUSTRY_HANGAR, industryHangar.getSelectedIndex());
		prefs.setAccountIndex(Account.INDUSTRY_WALLET, industryWallet.getSelectedIndex());
		
		System.out.println("Preferences saved to file.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		JButton b = (JButton) e.getSource();
		
		if (b == cancel_btn) {
			dispose();
		}else if (b == save_btn) {
			//Check all NumberFields.
			if (install.isValidInput() && installh.isValidInput() && broker.isValidInput() && sales.isValidInput() && updates.isValidInput()) {
				savePreferences();
				
				//Update affected Databases.
				HashSet<String> tmp = prefs.getChangedSettings();
				boolean doCorpRaw = false, doQuoteCompute = false, doQuoteProcess = false, updatePriceProcess = false, doPriceProcess = false;
				for (String key : tmp) {
					if (Arrays.asList(Preferences.CORP_DEPENDANT_KEYS).contains(key)) {
						doCorpRaw = true;
					} else if (Arrays.asList(Preferences.PRICE_DEPENDANT_KEYS).contains(key)) {
						updatePriceProcess = true;
					} else if (Arrays.asList(Preferences.MARKET_UPDATE_DEPENDANT_KEYS).contains(key)) {
						doPriceProcess = true;
					} else if (Arrays.asList(Preferences.INDUSTRY_DEPENDANT_KEYS).contains(key)) {
						doQuoteCompute = true;
					} else if (Arrays.asList(Preferences.BPO_DEPENDANT_KEYS).contains(key)) {
						doQuoteProcess = true;
					}
				}
				
				if (doCorpRaw) {
					EMT.D_HANDLER.updateDBAtStage(cdb, Stage.RAW);
				}
				
				if (doPriceProcess) {
					EMT.D_HANDLER.updateDBAtStage(pdb, Stage.PROCESS);
				} else if (updatePriceProcess) {
					EMT.D_HANDLER.reportDBUpdateAtStage(pdb, Stage.PROCESS);
				} else if (doQuoteProcess) {
					EMT.D_HANDLER.updateDBAtStage(qdb, Stage.PROCESS);
				} else if (doQuoteCompute) {
					EMT.D_HANDLER.updateDBAtStage(qdb, Stage.COMPUTE);
				}
				
				tmp.clear();
				
				dispose();

			}else {
				//Show a dialog
				if (JOptionPane.showConfirmDialog(	null, 
						"Some of the values you entered are incorrect.\nDo you want close the Preferences and disregard your chages?", 
						"Alert", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					
					dispose();
				}
			}
		}
	}
}
