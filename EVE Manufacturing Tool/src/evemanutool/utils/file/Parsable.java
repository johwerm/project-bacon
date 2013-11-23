package evemanutool.utils.file;

public interface Parsable <T>{
	
	public String toParseString();
	public T fromParseString(String s);
}
