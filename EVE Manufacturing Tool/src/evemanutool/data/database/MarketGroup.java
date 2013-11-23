package evemanutool.data.database;

import java.util.ArrayList;

public class MarketGroup {

	private final String name;
	private final int groupId;
	private final String description;
	private final ArrayList<Integer> childGroups;
	
	public MarketGroup(	String name, int groupId, String description) {
		
		this.name = name;
		this.groupId = groupId;
		this.description = description;
		this.childGroups = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ArrayList<Integer> getChildGroups() {
		return childGroups;
	}
}
