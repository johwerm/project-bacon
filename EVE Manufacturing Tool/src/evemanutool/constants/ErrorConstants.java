package evemanutool.constants;

public interface ErrorConstants {

	//Patterns.
	public static final String ERROR_CODE_PATTERN = "(.*)(\\d+)(.*)";
	public static final String HTTP_CODE_PATTERN = "(.*)(HTTP)(.*)";
	
	//General error messages.
	public static final String CRITICAL_APPLICATION_ERROR_MESSAGE = "Critical loading error, data files may be corrupt.";
	
	//API error messages.
	public static final String API_HTTP_ERROR_MESSAGE = "The EVE API server may be offline or malfunctioning.";
	public static final String API_INTERNAL_ERROR_MESSAGE = "Internal API error.";
	public static final String API_AUTH_ACCESS_ERROR_MESSAGE = "API key access not sufficent.";
	public static final String API_AUTH_ERROR_MESSAGE = "API key not authorized.";
	public static final String API_UNKNOWN_ERROR_MESSAGE = "Unknown API error.";
}
