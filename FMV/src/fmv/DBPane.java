package fmv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fmv.db.DBFile;
import fmv.db.DataRetriever;
import fmv.db.Project;

public class DBPane extends JPanel {

	public enum Level {
		PROJECTS, CHOICES, USERS, DATES, U_TOKENS, D_TOKENS;
	}

	class ProjectListModel extends DefaultListModel<String> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7407750081315305369L;
		public HashMap<String, Project> projects;
		private Level level;
		public String[] choices = new String[] { "Dates", "Users" };
		private String project, user, token;
		private long date;
		private DateFormat df = new SimpleDateFormat("HH:mm:ss, d MMM yyyy");

		public ProjectListModel(final List<String> projList) {
			projects = new HashMap<String, Project>();
			for (String p : projList) {
				projects.put(p, new Project(p));
				addElement(p);
			}
			level = Level.PROJECTS;
		}

		public void addAll(final String[] obs) {
			clear();
			for (final Object o : obs) {
				addElement(o.toString());
			}
		}

		private void addDates(Long[] dates) {
			clear();
			for (final Long d : dates) {
				addElement(df.format(new Date(d)));
			}
		}

		public void drillDown(final String s, final int index) {
			if (level.equals(Level.PROJECTS)) {
				addAll(choices);
				level = Level.CHOICES;
				project = s;
				load(projects.get(project));
				label.setText("Choices");
			} else if (level.equals(Level.CHOICES)) {
				if (s.equals("Users")) {
					addAll(projects.get(project).users);
					level = Level.USERS;
					label.setText("Users");
				} else if (s.equals("Dates")) {
					addDates(projects.get(project).dates);
					level = Level.DATES;
					label.setText("Dates");
				}
			} else if (level.equals(Level.USERS)) {
				user = s;
				addAll(projects.get(project).getTokens(user));
				level = Level.U_TOKENS;
				label.setText("Tokens for: " + user);
			} else if (level.equals(Level.DATES)) {
				date = projects.get(project).dates[index];
				addAll(projects.get(project).getTokens(date));
				level = Level.D_TOKENS;
				label.setText("Tokens from: " + df.format(new Date(date)));
			} else if (level.equals(Level.D_TOKENS)
					|| level.equals(Level.U_TOKENS)) {
				token = s;
				if (projects.get(project).tokenData.isEmpty()) {
					List<DBFile> files = retriever.retrieveFiles(s);
					projects.get(project).tokenData.put(s, new ProjectData(s,
							files));
				}
			}
		}

		public void moveUp() {
			if (level.equals(Level.CHOICES)) {
				addAll(projects.keySet().toArray(new String[projects.size()]));
				level = Level.PROJECTS;
				load(projects.get(project));
				label.setText("Projects");
			} else if (level.equals(Level.USERS) || level.equals(Level.DATES)) {
				addAll(choices);
				level = Level.CHOICES;
				label.setText("Choices");
			} else if (level.equals(Level.U_TOKENS)) {
				addAll(projects.get(project).users);
				level = Level.USERS;
				label.setText("Users");
			} else if (level.equals(Level.D_TOKENS)) {
				addDates(projects.get(project).dates);
				level = Level.DATES;
				label.setText("Dates");
			}
		}

		public void load(Project proj) {
			if (!proj.loaded) {
				final List<String> temp0 = retriever.getProjectUsers(proj.name);
				proj.users = temp0.toArray(new String[temp0.size()]);
				final List<Long> temp1 = retriever.getProjectDates(proj.name);
				proj.dates = temp1.toArray(new Long[temp1.size()]);
				proj.tokens.putAll(retriever.getTokens(project, "date"));
				proj.tokens.putAll(retriever.getTokens(project, "user"));
				proj.loaded = true;
			}
		}

		public ProjectData getProjectData() {
			return projects.get(project).tokenData.get(token);
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
		super();
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

		label = new JLabel("Projects");
		label.setLabelFor(itemList);

		JButton upBtn = new JButton(FMV.getMyImageIcon("upfolder.gif"));
		upBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				((ProjectListModel) itemList.getModel()).moveUp();
			}
		});

		add(label, BorderLayout.NORTH);
		add(upBtn, BorderLayout.NORTH);
		add(itemScrollPane, BorderLayout.CENTER);
	}

	public ProjectData getProjectData() {
		return ((ProjectListModel) itemList.getModel()).getProjectData();
	}

}
