package za.ac.sun.cs.intlola.processing;

public enum IntlolaError {
	ARCHIVE("Archive error"), CONNECTION("Connection error"), CORE("Core error"), DEFAULT(
			"Core error"), FILE("Core error"), LOGIN("Login error"), SERVER(
			"Server error"), SOCKET("Socket error"), SUCCESS("Success"), USER("User created error.");

	private String description;
	
	IntlolaError(final String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public IntlolaError specific(final String description) {
		this.description = description;
		return this;
	}
}
