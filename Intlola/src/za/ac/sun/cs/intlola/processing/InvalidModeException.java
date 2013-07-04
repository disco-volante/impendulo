package za.ac.sun.cs.intlola.processing;

public class InvalidModeException extends Exception {

	private IntlolaMode mode;

	public InvalidModeException(IntlolaMode mode) {
		this.mode = mode;
	}
	
	@Override
	public String getMessage(){
		return this.mode.getDescription();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8544705417322155195L;

}