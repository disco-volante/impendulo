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
		public HashMap<String, Project> projects;
		private Level level;
		private String project, user;
		private Submission sub;

		public ProjectListModel(final List<String> projList) {
			projects = new HashMap<String, Project>();
			for (String p : projList) {
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
				project = s;
				load(projects.get(project));
				addAll(projects.get(project).users);
				label.setText("Users");
				level = Level.USERS;
			} else if (level.equals(Level.USERS)) {
				user = s;
				addAll(projects.get(project).getSubmissions(user));
				level = Level.SUBMISSIONS;
				label.setText("Tokens for: " + user);
			} else if (level.equals(Level.SUBMISSIONS)) {
				sub = projects.get(project).submissions.get(user).get(index);
				if (projects.get(project).submissionData.get(sub) == null) {
					List<DBFile> files = retriever.retrieveFiles(sub);
					projects.get(project).submissionData.put(sub, new Archive(project,
							sub.toString(), files));
				}
			}
		}

		public void moveUp() {
			if (level.equals(Level.USERS)) {
				addAll(projects.keySet().toArray(new String[projects.size()]));
				level = Level.PROJECTS;
				label.setText("Projects");
			} else if (level.equals(Level.SUBMISSIONS)) {
				addAll(projects.get(project).users);
				level = Level.USERS;
				label.setText("Users");
			} 
		}

		public void load(Project proj) {
			if (!proj.loaded) {
				final List<String> temp0 = retriever.getProjectUsers(proj.name);
				proj.users = temp0.toArray(new String[temp0.size()]);
				proj.submissions.putAll(retriever.getSubmissions(project));
				proj.loaded = true;
			}
		}

		public Archive getProjectData() {
			return projects.get(project).submissionData.get(sub);
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

	protected List<DBFile> projectData;

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
		itemScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		itemScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		label = new JLabel("Projects");
		label.setLabelFor(itemList);

		JButton upBtn = new JButton(FMV.getMyImageIcon("upfolder.gif"));
		upBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				((ProjectListModel) itemList.getModel()).moveUp();
			}
		});
		upBtn.setBorderPainted(false);
		upBtn.setContentAreaFilled(false); 
		upBtn.setFocusPainted(false); 
		upBtn.setOpaque(false);
		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.add(upBtn);
		top.add(label);
		add(top);
		add(itemScrollPane);
	}

	public Archive getProjectData() {
		return ((ProjectListModel) itemList.getModel()).getProjectData();
	}

}
