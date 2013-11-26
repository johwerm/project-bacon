package evemanutool.gui.corp.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import evemanutool.constants.DBConstants;
import evemanutool.data.database.AbstractStation;
import evemanutool.data.display.Asset;

@SuppressWarnings("serial")
public class TreeAssetsPanel extends JPanel{
	
	//Graphical components.
	private AssetTree tree;
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	
	public TreeAssetsPanel(String topLabel) {
		
		//Set values.
		rootNode = new DefaultMutableTreeNode(topLabel);
		treeModel = new DefaultTreeModel(rootNode);
		
		//Set Layout.
		setLayout(new BorderLayout());
		tree = new AssetTree(rootNode);
		tree.setCellRenderer(new AssetTreeRenderer());
		
		//Remove topLabel if set to "".
		if (topLabel.equals("")) {
			tree.setRootVisible(false);
		}
	    JScrollPane treeView = new JScrollPane(tree);
	    add(treeView, BorderLayout.CENTER);
	}
	
	
	public void setAssets(ArrayList<Asset> assets) {
		
		//Clear the tree.
		rootNode.removeAllChildren();
		//Create nodes.
		createNodes(rootNode, assets);
		//Reload.
		treeModel.reload();
		tree.expandPath(new TreePath(rootNode.getPath()));
	}

	private void createNodes(DefaultMutableTreeNode top, ArrayList<Asset> assets) {
		
		//Temp node.
		DefaultMutableTreeNode node;
		
		if (assets != null) {
			for (Asset a : assets) {
				node = new DefaultMutableTreeNode(a);
				top.add(node);
				createNodes(node, a.getAssets());
			}
		}
	}
	
	private class AssetTree extends JTree implements DBConstants {
		
		public AssetTree(MutableTreeNode rootNode) {
			super(rootNode);
		}
		
		@Override
		public String convertValueToText(Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {

			if (value instanceof DefaultMutableTreeNode && 
					((DefaultMutableTreeNode) value).getUserObject() instanceof Asset) {
				//Object-value is an asset.
				Asset a = (Asset) ((DefaultMutableTreeNode) value).getUserObject();
				
				if (a.getFlag() == MARKET_DELIVERIES_FLAG) {
					return a.getLocation().getName() + " - " + "Market Deliveries";
				}else {
					return (a.getLocation() != null ? a.getLocation().getName() : a.getItem().getName()) +
							(a.getPlayerName() == null && a.getLocation() != null ? " - " + a.getItem().getName() :  "") +
							(a.getPlayerName() != null ? " - " + a.getPlayerName() :  "") +
							(a.getAssets() == null ? " x " + a.getQuantity() : "");
				}
			}
			return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
		}
	}
		
	private class AssetTreeRenderer extends DefaultTreeCellRenderer {

		public AssetTreeRenderer() {}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			//Set which icon to use.
			if (value instanceof DefaultMutableTreeNode && 
					((DefaultMutableTreeNode) value).getUserObject() instanceof Asset) {
				//Object-value is an asset.
				Asset a = (Asset) ((DefaultMutableTreeNode) value).getUserObject();
				if (a.getLocation() != null && a.getLocation() instanceof AbstractStation && 
						((AbstractStation) a.getLocation()).getItem().getIcon() != null) {
					
					//Set icon from station type. 
					setIcon(((AbstractStation) a.getLocation()).getItem().getIcon());
				}else if (a.getItem().getIcon() != null) {
						//Set icon from asset type.
						setIcon(a.getItem().getIcon());
				}
			}
			return this;
		}
	}
}

