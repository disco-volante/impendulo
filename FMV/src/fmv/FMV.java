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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
		public void actionPerformed(final ActionEvent event) {
			if ("file.prefs".equals(event.getActionCommand())) {
				FMV.prefs.activate();
			} else if ("file.quit".equals(event.getActionCommand())) {
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

	public static final String TEST_DIR = System.getProperty("user.home")
			+ File.separator + ".impendulo" + File.separator + "tests";
	public static final String ZIP_DIR = System.getProperty("user.home")
			+ File.separator + ".impendulo" + File.separator + "zips";
	private static Logger logger;

	/**
	 * The main frame that constitutes this application.
	 */
	private static JFrame mainFrame;

	/**
	 * A dialog for the preferences window.
	 */
	public static PreferencesDialog prefs;

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

	private static SubmissionPane subPane;

	private static Map<String, String> testZips;

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
		FMV.prefs = new PreferencesDialog(FMV.mainFrame);
		FMV.help = new HelpContents(FMV.mainFrame);
		FMV.annotateDialog = new AnnotateDialog(FMV.mainFrame);
		FMV.mainFrame.setVisible(true);
	}

	private static Container createContentPane() {

		FMV.tablePane = new TablePane();

		FMV.diffPane = new DiffPane();

		FMV.timeGraph = new VersionTimeline(false);
		FMV.timeGraph.setMinimumSize(new Dimension(650, 400));

		FMV.subPane = new SubmissionPane(FMV.retriever);

		FMV.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				FMV.subPane, FMV.tablePane);

		FMV.splitPane.setOneTouchExpandable(true);
		FMV.splitPane.setResizeWeight(0);
		FMV.splitPane.setDividerLocation(150);

		final JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
		FMV.toolBox = new JComboBox<String>(Tools.getTools());
		controls.add(FMV.toolBox);
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

	protected static String getClassName(final Object o) {
		final String classString = o.getClass().getName();
		final int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1);
	}

	public static OutputDialog getDialog() {
		return new OutputDialog(FMV.mainFrame);
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

	public static String getTests(final String name) {
		String outfile;
		if ((outfile = FMV.testZips.get(name)) == null) {
			outfile = FMV.TEST_DIR + File.separator + name + "_TESTING.zip";
			final byte[] testData = FMV.retriever.getTests(name);
			if (testData != null) {
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(new File(outfile));
					fos.write(testData);
					FMV.testZips.put(name, outfile);
				} catch (final FileNotFoundException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				outfile = null;
			}
		}
		return outfile;
	}

	public static void log(final String className, final String content) {
		FMV.logger.log(Level.SEVERE, className + "\n" + content);
	}

	public static void main(final String[] args) {
		try {
			FMV.setup();
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

	private static void setup() throws SecurityException, IOException {
		FMV.logger = Logger.getLogger(FMV.class.getName());
		FMV.logger.setUseParentHandlers(false);
		FMV.logger.setLevel(Level.ALL);
		FMV.retriever = new DataRetriever("localhost");
		FMV.testZips = new HashMap<String, String>();
		FMV.logger.addHandler(new FileHandler("fmv.log"));
		final File dir = new File(FMV.TEST_DIR);
		dir.mkdirs();
	}

}
