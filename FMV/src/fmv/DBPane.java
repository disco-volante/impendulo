package fmv;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import sun.awt.VerticalBagLayout;
import fmv.db.DBFile;
import fmv.db.DataRetriever;
import fmv.db.Project;
import fmv.db.Submission;

public class DBPane extends JPanel {

	public enum Level {
		PROJECTS, USERS, SUBMISSIONS;
	}

	class ProjectListModel extends DefaultListModel<String> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7407750081315305369L;
		private final HashMap<String, Project> projects;
		private final HashMap<Submission, Archive> submissionData;
		private Level level;
		private Project project;
		private String user;
		private Submission sub;

		public ProjectListModel(final List<String> projList) {
			submissionData = new HashMap<Submission, Archive>();
			projects = new HashMap<String, Project>();
			for (final String p : projList) {
				projects.put(p, new Project(p));
				addElement(p);
			}
			level = Level.PROJECTS;
		}

		public void addAll(final Object[] obs) {
			clear();
			for (final Object o : obs) {
				addElement(o.toString());
			}
		}

		public void drillDown(final String s, final int index) {
			if (level.equals(Level.PROJECTS)) {
				project = projects.get(s);
				load(project);
				addAll(project.getUsers());
				label.setText("Users");
				level = Level.USERS;
			} else if (level.equals(Level.USERS)) {
				user = s;
				addAll(project.getSubmissions(user));
				level = Level.SUBMISSIONS;
				label.setText("Tokens for: " + user);
			} else if (level.equals(Level.SUBMISSIONS)) {
				sub = project.getSubmission(user, index);
				if (submissionData.get(sub) == null) {
					final List<DBFile> files = retriever.retrieveFiles(sub);
					final boolean archive = sub.getFormat().equals(
							"UNCOMPRESSED") ? false : true;
					submissionData.put(sub,
							new Archive(project.getName(), sub.toString(),
									files, archive));
				}
			}
		}

		public Archive getProjectData() {
			return submissionData.get(sub);
		}

		private void load(final Project proj) {
			if (!proj.isLoaded()) {
				proj.addUsers(retriever.getProjectUsers(proj.getName()));
				proj.addSubmissions(retriever.getSubmissions(project.getName()));
				proj.setLoaded(true);
			}
		}

		public void moveUp() {
			if (level.equals(Level.USERS)) {
				addAll(projects.keySet().toArray(new String[projects.size()]));
				level = Level.PROJECTS;
				label.setText("Projects");
			} else if (level.equals(Level.SUBMISSIONS)) {
				addAll(project.getUsers());
				level = Level.USERS;
				label.setText("Users");
			}
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -9127904335874421540L;

	public static void main(final String[] args) {
		try {
			final DataRetriever ret = new DataRetriever("localhost");
			final JFrame parent = new JFrame();
			parent.setSize(new Dimension(300, 400));
			final DBPane pane = new DBPane(ret);
			parent.add(pane);
			parent.setVisible(true);
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private JList<String> itemList;

	private final DataRetriever retriever;

	private JLabel label;

	public DBPane(final DataRetriever retriever) {
		super(new VerticalBagLayout());
		this.retriever = retriever;
		createGUI();
		itemList.setModel(new ProjectListModel(retriever.getProjects()));

	}

	private void createGUI() {
		final Dimension d = new Dimension(250, 500);
		itemList = new JList<String>();
		itemList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					final String s = itemList.getSelectedValue();
					final int i = itemList.getSelectedIndex();
					if (s != null && i != -1) {
						((ProjectListModel) itemList.getModel())
								.drillDown(s, i);
					}
				}
			}
		});
		final JScrollPane itemScrollPane = new JScrollPane(itemList);
		itemScrollPane.setMinimumSize(d);
		itemScrollPane.setPreferredSize(d);
		itemScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		itemScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		label = new JLabel("Projects");
		label.setLabelFor(itemList);

		final JButton upBtn = new JButton(FMV.getMyImageIcon("upfolder.gif"));
		upBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				((ProjectListModel) itemList.getModel()).moveUp();
			}
		});
		upBtn.setBorderPainted(false);
		upBtn.setContentAreaFilled(false);
		upBtn.setFocusPainted(false);
		upBtn.setOpaque(false);
		final JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.add(upBtn);
		top.add(label);
		add(top);
		add(itemScrollPane);
	}

	public Archive getProjectData() {
		return ((ProjectListModel) itemList.getModel()).getProjectData();
	}

}
