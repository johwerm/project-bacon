package evemanutool.utils.databases;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;

import evemanutool.constants.DBConstants;
import evemanutool.utils.datahandling.Database;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;

public class GraphicDB extends Database implements DBConstants{

	//Data.
	private ConcurrentHashMap<Integer, ImageIcon> types32Db = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, ImageIcon> types64Db = new ConcurrentHashMap<>();
	
	//Indexed type-images.
	private volatile ArrayList<Integer> indexedImgs;
	
	public GraphicDB() {
		super(true, false, Stage.RAW, Stage.PROCESS);
	}

	@Override
	public synchronized void loadRawData() throws Exception {

		//Temp.
		ArrayList<Integer> tmpIndexedImgs = new ArrayList<>();
		
		//Read and create a index-list of icons of all types.
		//Loading all icons are too memory and CPU intensive.
		File dir = new File(TYPE_IMGAGES_PATH);
		//File filter to allow only .png files.
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".png");
			}
		};
		//Temp filename.
		String [] fileName;
		//Go through files in image directory.
		for (File img : dir.listFiles(filter)) {
			fileName = img.getName().split("_");
			//Add type.
			tmpIndexedImgs.add(Integer.parseInt(fileName[0]));
		}
		//Set new database to global reference.
		indexedImgs = tmpIndexedImgs;
		
		//Last initiation step, set complete.
		super.setComplete(true);
	}
	
	private void readTypeImage(int typeId) {
		//Load both images.
		types32Db.put(typeId, new ImageIcon(TYPE_IMGAGES_PATH + "/" + typeId + "_32.png"));
		//types64Db.put(typeId, new ImageIcon(TYPE_IMGAGES_PATH + "/" + typeId + "_64.png"));
	}
	
	public void preBufferIcons(Collection<Integer> typeIds) {
		//Go through the list and add valid icons to database.
		for (Integer typeId : typeIds) {
			if (indexedImgs.contains(typeId)) {
				readTypeImage(typeId);
			}
		}
	}

	public ImageIcon get32Icon(int typeId) {
		if (indexedImgs.contains(typeId)) {
			//Load images if not in database.
			if (!types32Db.containsKey(typeId)) {
				readTypeImage(typeId);
			}
			return types32Db.get(typeId);
		}else {
			return null;
		}
	}

	public ImageIcon get64Icon(int typeId) {
		if (indexedImgs.contains(typeId)) {
			//Load images if not in database.
			if (!types64Db.containsKey(typeId)) {
				readTypeImage(typeId);
			}
			return types64Db.get(typeId);
		}else {
			return null;
		}
	}
}
