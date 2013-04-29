package za.ac.sun.cs.intlola;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.jface.dialogs.IInputValidator;

public class ConnValidator implements IInputValidator {
	@Override
	public String isValid(String newText) {
		String[] args = newText.split(":");
		if(args.length != 2){
			return "Invalid connection specified.";
		}
		
		if(!UrlValidator.getInstance().isValid("http://"+args[0])){
			return "Invalid URL.";
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
