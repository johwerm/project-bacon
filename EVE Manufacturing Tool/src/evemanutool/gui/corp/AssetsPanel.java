package evemanutool.gui.corp;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import evemanutool.data.display.BlueprintAsset;
import evemanutool.gui.corp.components.BlueprintAssetModel;
import evemanutool.gui.corp.components.TreeAssetsPanel;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class AssetsPanel extends JPanel implements GUIUpdater {

	private CorpApiDB cdb;
	
	private TreeAssetsPanel assetsPanel;
	private ScrollableTablePanel<BlueprintAsset> blueprintPanel;

	public AssetsPanel(CorpApiDB cdb) {

		this.cdb = cdb;
		
		//Create Industry stats chart.
		setLayout(new GridLayout(1, 2));
		
		//Create asset panel.
		assetsPanel = new TreeAssetsPanel("");
		assetsPanel.setBorder(BorderFactory.createTitledBorder("Corporation Assets"));

		//Create blueprints panels.
		blueprintPanel = new ScrollableTablePanel<>(new BlueprintAssetModel());
		blueprintPanel.setBorder(BorderFactory.createTitledBorder("Corporation Blueprints"));
		
		//Add top level components.
		add(assetsPanel);
		add(blueprintPanel);
	}

	@Override
	public void updateGUI() {
		assetsPanel.setAssets(cdb.getAssets());
		blueprintPanel.setData(cdb.getBpos());
	}
}
