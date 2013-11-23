package evemanutool.data.cache;

import java.util.ArrayList;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;

public class CharacterEntry implements Parsable<CharacterEntry>, DBConstants{
	
	private long id;
	private String name;
	
	public CharacterEntry(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public CharacterEntry() {}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(getId()); ss.add(getName());
		return ParseTools.join(ss, LEVEL3_DELIM);
	}

	@Override
	public CharacterEntry fromParseString(String s) {
		String[] ss = s.split(LEVEL3_DELIM, -1);
		setId(Long.parseLong(ss[0]));
		setName(ss[1]);
		return this;
	}
}
