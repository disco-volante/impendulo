package za.ac.sun.cs.intlola;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;

public class ConnValidator implements IInputValidator {
	private static final String IP_PATTERN = 
	        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public String isValid(String newText) {
		String[] args = newText.split(":");
		if(args.length != 2){
			return "Invalid connection paramaters.";
		}
		Pattern ipPattern = Pattern.compile(IP_PATTERN);
		if(!ipPattern.matcher(args[0]).matches()){
			return "Invalid IP address.";
		}
		try{
			int i = Integer.parseInt(args[1]);
			if(i < 0){
				return "Invalid port number.";
			}
		} catch(NumberFormatException ne){
			return "No port number specified.";
		}
		return null;
	}

}
