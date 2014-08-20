package za.ac.sun.cs.intlola.processing.paths;

public interface IPaths {
	public static final String IGNORE_NAME = ".impendulo_info.json",
			STORE_NAME = ".intlola", ARCHIVE_NAME = "archive";

	public String projectPath();

	public String ignorePath();

	public String archivePath();

	public String storePath();
}
