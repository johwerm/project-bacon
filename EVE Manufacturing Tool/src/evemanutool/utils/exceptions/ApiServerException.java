package evemanutool.utils.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beimin.eveapi.exception.ApiException;

import evemanutool.constants.ErrorConstants;

@SuppressWarnings("serial")
public class ApiServerException extends ServerException implements ErrorConstants {
	
	public ApiServerException(ApiException e) {
		super(e);
	}
	
	public ApiServerException(String userMessage) {
		super(userMessage);
	}
	
	@Override
	protected String decodeException(Exception e) {
		
		String msg = e.getMessage();
		
		Pattern p = Pattern.compile(HTTP_CODE_PATTERN);
		Matcher m = p.matcher(msg);
		
		if (m.find()) {
			//HTTP message.
			p = Pattern.compile(ERROR_CODE_PATTERN);
			m = p.matcher(msg);
			if (m.find()) {
				//React to code.
				//m.group();
				return API_HTTP_ERROR_MESSAGE;
			}
			
		} else {
			//API message.
			p = Pattern.compile(ERROR_CODE_PATTERN);
			m = p.matcher(msg);
			
			if (m.find()) {
				//React to code.
				//m.group();
				return API_INTERNAL_ERROR_MESSAGE;
			}
		}
		
		return API_UNKNOWN_ERROR_MESSAGE;
	}
}
