package fmv;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import fmv.db.DataRetriever;
import fmv.tools.Tools;

/**
 * Main class for the Fault Measurement Visualizer.
 * 
 * @author jaco
 */
public class FMV {

	public static class CompileListener implements ActionListener {

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (FMV.directoryPane.isEnabled()) {
				final int i = FMV.directoryPane.directoryList
						.getSelectedIndex();
				if (i != -1) {
					final ProjectData archive = FMV.directory.getArchive(i);
					if (!archive.isSetup()) {
						archive.setup();
					}
					if (!archive.isCompiled()) {
						archive.runTool("Tests");
					}
					FMV.directoryPane.sourceList.setModel(FMV.directory
							.getModel(i));
					FMV.directoryPane.sourceList.setSelectedIndex(0);
					FMV.directory.setDiff(i, 0);
				}
			} else if (FMV.dbPane.isEnabled()) {
				final ProjectData archive = FMV.dbPane.getProjectData();
				if (archive != null) {
					if (!archive.isSetup()) {
						archive.setup();
					}
					if (!archive.isCompiled()) {
						archive.runTool("Tests");
					}
				}

			}
		}
	}

	/**
	 * Inner class that responds to menu commands.
	 */
	private static class MenuAction implements ActionListener {

		/**
		 * A file chooser for opening directories.
		 */
		private JFileChooser fc = null;

		public MenuAction() {
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}

		/**
		 * Respond to a menu button.
		 * 
		 * @param event
		 *            the event that caused this action
		 */
		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void actionPerformed(final ActionEvent event) {
			if ("file.open".equals(event.getActionCommand())) {
				FMV.saveProperties();
				switchContexts(true);
				if (fc == null) {
					fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
				if (fc.showOpenDialog(FMV.mainFrame) == JFileChooser.APPROVE_OPTION) {
					final File f = fc.getSelectedFile();
					try {
						FMV.directory = new Directory(f.getCanonicalPath());
					} catch (final IOException e) {
						e.printStackTrace();
					}
					FMV.tablePane.clear();
					for (final String n : f.list()) {
						if (n.endsWith(".zip") && !n.endsWith("TESTING.zip")) {
							FMV.directory.addArchive(n);
						}
					}
					FMV.prefs.loadProperties();
					FMV.directoryPane.directoryList.setModel(FMV.directory
							.getModel());
					FMV.directoryPane.sourceList
							.setModel(new DefaultListModel());
					FMV.splitPane.setRightComponent(FMV.tablePane);
				}
			} else if ("server.open".equals(event.getActionCommand())) {
				saveProperties();
				switchContexts(false);
			} else if ("file.prefs".equals(event.getActionCommand())) {
				FMV.prefs.activate();
			} else if ("file.quit".equals(event.getActionCommand())) {
				FMV.saveProperties();
				FMV.mainFrame.dispose();
				System.exit(0);
			} else if ("view.table".equals(event.getActionCommand())) {
				FMV.splitPane.setRightComponent(FMV.tablePane);
			} else if ("view.progress".equals(event.getActionCommand())) {
				FMV.splitPane.setRightComponent(FMV.timeGraph);
			} else if ("view.diffs".equals(event.getActionCommand())) {
				FMV.splitPane.setRightComponent(FMV.diffPane);
			} else if ("help.contents".equals(event.getActionCommand())) {
				FMV.help.setVisible(true);
			} else if ("help.about".equals(event.getActionCommand())) {
				JOptionPane
						.showMessageDialog(
								FMV.mainFrame,
								"Fault Measurement Visualizer\n"
										+ "(C) 2009 Computer Science, Stellenbosch University",
								"About FMV", JOptionPane.INFORMATION_MESSAGE,
								FMV.fmvIcon);
			}
		}
	}

	public static class ToolListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (FMV.directoryPane.isEnabled()) {
				final int i = FMV.directoryPane.directoryList
						.getSelectedIndex();
				if (i != -1 && FMV.toolBox.getSelectedIndex() != -1) {
					final ProjectData archive = FMV.directory.getArchive(i);
					if (!archive.isSetup()) {
						archive.setup();
					}
					archive.runTool((String) FMV.toolBox.getSelectedItem());
				}
			} else if (FMV.dbPane.isEnabled()) {
				final ProjectData proj = FMV.dbPane.getProjectData();
				if (proj != null) {
					if (!proj.isSetup()) {
						proj.setup();
					}
					proj.runTool((String) FMV.toolBox.getSelectedItem());
				}
			}
		}
	}

	private static Logger logger;

	/**
	 * The main frame that constitutes this application.
	 */
	private static JFrame mainFrame;

	/**
	 * A dialog for the preferences window.
	 */
	public static PreferencesDialog prefs;

	/**
	 * Dialog for showing the compiler/tester progress.
	 */
	public static ToolRunnerDialog toolrunner;

	/**
	 * A container for a list of zip files inside a directory.
	 */
	private static Directory directory;

	private static JSplitPane splitPane;

	public static VersionTimeline timeGraph;

	public static AnnotateDialog annotateDialog;

	public static DiffPane diffPane;

	public static TablePane tablePane;

	private static Image fmvImage;

	private static ImageIcon fmvIcon;

	private static HelpContents help;
	private static JComboBox<String> toolBox;

	public static DataRetriever retriever;

	private static DirectoryPane directoryPane;

	private static DBPane dbPane;

	private static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		FMV.mainFrame = new JFrame("Fault Measurement Visualizer");
		FMV.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FMV.mainFrame.setJMenuBar(FMV.createMenu());
		FMV.mainFrame.setContentPane(FMV.createContentPane());
		FMV.mainFrame.setSize(800, 400);
		FMV.mainFrame.setLocationRelativeTo(null);
		FMV.fmvIcon = new ImageIcon(FMV.getMyImage("fmv.gif"));
		FMV.fmvImage = FMV.fmvIcon.getImage();
		FMV.mainFrame.setIconImage(FMV.fmvImage);
		FMV.toolrunner = new ToolRunnerDialog(FMV.mainFrame);
		FMV.prefs = new PreferencesDialog(FMV.mainFrame);
		FMV.help = new HelpContents(FMV.mainFrame);
		FMV.annotateDialog = new AnnotateDialog(FMV.mainFrame);
		FMV.mainFrame.setVisible(true);
	}

	public static void switchContexts(boolean dir) {
		FMV.tablePane.clear();
		FMV.splitPane.setRightComponent(FMV.tablePane);
		if (dir) {
			FMV.directoryPane.setEnabled(true);
			FMV.dbPane.setEnabled(false);
			FMV.splitPane.setLeftComponent(directoryPane);

		} else {
			FMV.directoryPane.setEnabled(false);
			FMV.dbPane.setEnabled(true);
			FMV.splitPane.setLeftComponent(dbPane);
		}
	}

	private static Container createContentPane() {

		FMV.tablePane = new TablePane();

		FMV.diffPane = new DiffPane();

		FMV.timeGraph = new VersionTimeline(false);
		FMV.timeGraph.setMinimumSize(new Dimension(650, 400));

		FMV.directoryPane = new DirectoryPane();
		FMV.dbPane = new DBPane(FMV.retriever);

		FMV.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				FMV.directoryPane, FMV.tablePane);

		switchContexts(true);

		FMV.splitPane.setOneTouchExpandable(true);
		FMV.splitPane.setResizeWeight(0);
		FMV.splitPane.setDividerLocation(150);

		final JButton compileBtn = new JButton("Compile");
		compileBtn.addActionListener(new CompileListener());
		final JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
		controls.add(compileBtn);
		FMV.toolBox = new JComboBox<String>(Tools.getTools());
		controls.add(FMV.toolBox);
		final JButton toolButton = new JButton("Run");
		toolButton.setToolTipText("Run static analysis tools.");
		toolButton.addActionListener(new ToolListener());
		controls.add(toolButton);
		final JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		contentPane.add(controls, BorderLayout.NORTH);
		contentPane.add(FMV.splitPane, BorderLayout.CENTER);
		return contentPane;
	}

	/**
	 * Create a menu bar for our application.
	 * 
	 * @return the new menu bar
	 */
	private static JMenuBar createMenu() {
		final MenuAction a = new MenuAction();
		final JMenuBar b = new JMenuBar();

		JMenu m = new JMenu("File");
		m.add(FMV.createMenuItem("Open Directory", "file.open", a,
				KeyEvent.VK_O));
		m.addSeparator();
		m.add(FMV.createMenuItem("Get Server Projects", "server.open", a,
				KeyEvent.VK_U));
		m.addSeparator();
		m.add(FMV.createMenuItem("Compile/test all", "file.comptest", a,
				KeyEvent.VK_C));
		m.add(FMV.createMenuItem("Print", "file.print", a, KeyEvent.VK_P));
		m.addSeparator();
		m.add(FMV.createMenuItem("Preferences", "file.prefs", a, -1));
		m.addSeparator();
		m.add(FMV.createMenuItem("Quit", "file.quit", a, KeyEvent.VK_Q));
		b.add(m);

		m = new JMenu("View");
		b.add(m);
		m.add(FMV.createMenuItem("Summary table", "view.table", a,
				KeyEvent.VK_T));
		m.add(FMV.createMenuItem("Progress graph", "view.progress", a,
				KeyEvent.VK_G));
		m.add(FMV.createMenuItem("Differences", "view.diffs", a, KeyEvent.VK_D));

		m = new JMenu("Help");
		b.add(m);
		m.add(FMV.createMenuItem("Help contents", "help.contents", a, -1));
		m.addSeparator();
		m.add(FMV.createMenuItem("About FMV", "help.about", a, -1));
		return b;
	}

	private static JMenuItem createMenuItem(final String name,
			final String cmd, final MenuAction action, final int key) {
		final JMenuItem item = new JMenuItem(name, FMV.getMyImageIcon(cmd
				+ ".gif"));
		item.setActionCommand(cmd);
		item.addActionListener(action);
		if (key != -1) {
			item.setAccelerator(KeyStroke.getKeyStroke(key,
					ActionEvent.CTRL_MASK));
		}
		return item;
	}

	public static String getArchiveProperty(final ProjectData archive,
			final String defualt) {
		if (FMV.directory != null) {
			return FMV.directory.getXArchiveProperty(archive, null, defualt);
		} else {
			return defualt;
		}
	}

	public static String getArchiveProperty(final ProjectData archive,
			final String key, final String defualt) {
		if (FMV.directory != null) {
			return FMV.directory.getXArchiveProperty(archive, key, defualt);
		} else {
			return defualt;
		}
	}

	protected static String getClassName(final Object o) {
		final String classString = o.getClass().getName();
		final int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1);
	}

	public static OutputDialog getDialog() {
		return new OutputDialog(FMV.mainFrame);
	}

	public static String getDirectoryProperty(final String key,
			final String defualt) {
		if (FMV.directory != null) {
			return FMV.directory.getXDirectoryProperty(key, defualt);
		} else {
			return defualt;
		}
	}

	public static Image getMyImage(final String imageName) {
		final URL imageUrl = Thread.currentThread().getContextClassLoader()
				.getResource(imageName);
		if (imageUrl == null) {
			return null;
		}
		Image image = null;
		try {
			image = ImageIO.read(imageUrl);
		} catch (final IOException e) {
			image = null;
		}
		return image;
	}

	public static ImageIcon getMyImageIcon(final String imageName) {
		final URL imageUrl = Thread.currentThread().getContextClassLoader()
				.getResource(imageName);
		if (imageUrl == null) {
			return null;
		}
		ImageIcon imageIcon = null;
		try {
			imageIcon = new ImageIcon(ImageIO.read(imageUrl));
		} catch (final IOException e) {
			imageIcon = null;
		}
		return imageIcon;
	}

	public static SplitDialog getSplitDialog() {
		return new SplitDialog(FMV.mainFrame);
	}

	public static String getVersionProperty(final ProjectData archive,
			final Source source, final Date date, final String key,
			final String defualt) {
		if (FMV.directory != null) {
			return FMV.directory.getXVersionProperty(archive, source, date,
					key, defualt);
		} else {
			return defualt;
		}
	}

	public static void log(final String className, final String content) {
		FMV.logger.log(Level.SEVERE, className + "\n" + content);
	}

	public static void main(final String[] args) {
		FMV.logger = Logger.getLogger(FMV.class.getName());
		FMV.logger.setUseParentHandlers(false);
		FMV.logger.setLevel(Level.ALL);
		try {
			FMV.retriever = new DataRetriever("localhost");
			FMV.logger.addHandler(new FileHandler("fmv.log"));
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					FMV.createAndShowGUI();
				}
			});
		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	protected static void retrieveProjectDates(final String project) {
		FMV.retriever.getProjectDates(project);
	}

	public static void saveProperties() {
		if (FMV.directory != null) {
			FMV.prefs.saveProperties();
			FMV.directory.saveXProperties();
		}
	}

	public static void setArchiveProperty(final ProjectData archive,
			final String value) {
		if (FMV.directory != null) {
			FMV.directory.setXArchiveProperty(archive, null, value);
		}
	}

	public static void setArchiveProperty(final ProjectData archive,
			final String key, final String value) {
		if (FMV.directory != null) {
			FMV.directory.setXArchiveProperty(archive, key, value);
		}
	}

	public static void setDirectoryProperty(final String key, final String value) {
		if (FMV.directory != null) {
			FMV.directory.setXDirectoryProperty(key, value);
		}
	}

	public static void setVersionProperty(final ProjectData archive,
			final Source source, final Date date, final String key,
			final String value) {
		if (FMV.directory != null) {
			FMV.directory
					.setXVersionProperty(archive, source, date, key, value);
		}
	}

}
