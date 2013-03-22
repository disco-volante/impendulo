package fmv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fmv.db.DataRetriever;

public class DBPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9127904335874421540L;

	private JList<String> projList, tokenList, subList;
	private HashMap<String, ProjectListModel> projects;

	private DataRetriever retriever;

	public DBPane(DataRetriever retriever) {
		super();
		this.retriever = retriever;
		createGUI();
		addProjects(retriever.getProjects());

	}

	private void createGUI() {
		Dimension d = new Dimension(150, 400);
		projList = new JList<String>();
		projList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String p = projList.getSelectedValue();
					if (p != null) {
						ProjectListModel val = projects.get(p);
						val.Load();
						subList.setModel(val);
						subList.setSelectedIndex(0);
					}
				}
			}
		});
		projList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				String p = projList.getSelectedValue();
				if (p != null) {
					ProjectListModel val = projects.get(p);
					val.Load();
					subList.setModel(val);
					subList.setSelectedIndex(0);
				}
			}
		});
		JScrollPane projScrollPane = new JScrollPane(projList);
		projScrollPane.setMinimumSize(d);
		projScrollPane.setPreferredSize(d);
		subList = new JList<String>();
		subList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					String s = subList.getSelectedValue();
					if (s != null) {
						ListModel<String> tokens = ((ProjectListModel) subList
								.getModel()).drillDown(s,
								subList.getSelectedIndex());
						System.out.println(s);
						if (tokens != null) {
							tokenList.setModel(tokens);
						}
					}
				}
			}
		});
		JScrollPane subScrollPane = new JScrollPane(subList);
		subScrollPane.setMinimumSize(d);
		subScrollPane.setPreferredSize(d);
		tokenList = new JList<String>();
		tokenList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					retriever.retrieveFiles(tokenList.getSelectedValue());
				}
			}
		});
		JScrollPane tokenScrollPane = new JScrollPane(tokenList);
		tokenScrollPane.setMinimumSize(d);
		tokenScrollPane.setPreferredSize(d);
		add(projScrollPane, BorderLayout.WEST);
		add(subScrollPane, BorderLayout.CENTER);
		add(tokenScrollPane, BorderLayout.EAST);
	}

	public void addProjects(List<String> proj) {
		projects = new HashMap<String, ProjectListModel>();
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (String p : proj) {
			model.addElement(p);
			projects.put(p, new ProjectListModel(p));
		}
		projList.setModel(model);
	}

	class ProjectListModel extends DefaultListModel<String> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7407750081315305369L;
		public String name;
		public Date[] dates;
		public String[] users;
		public String[] top = new String[] { "users", "dates" };
		private Object[] current;

		public ProjectListModel(String name) {
			this.name = name;
		}

		public void Load() {
			if (users == null) {
				List<String> temp = retriever.getProjectUsers(name);
				users = temp.toArray(new String[temp.size()]);
			}
			if (dates == null) {
				List<Date> temp = retriever.getProjectDates(name);
				dates = retriever.getProjectDates(name).toArray(
						new Date[temp.size()]);
			}
			addAll(top);
		}

		public ListModel<String> drillDown(String s, int index) {
			DefaultListModel<String> ret = null;
			if (s != null && current.equals(top)) {
				if (s.equals("users")) {
					addAll(users);
				} else if (s.equals("dates")) {
					addAll(dates);
				}
			} else {
				String user = null;
				Date date = null;
				if (current.equals(users)) {
					user = s;
				} else if (current.equals(dates)) {
					date = dates[index];
				}
				List<String> temp = retriever
						.getProjectTokens(name, user, date);
				ret = new DefaultListModel<String>();
				for (String val : temp) {
					ret.addElement(val);
				}
			}
			return ret;
		}

		public void addAll(Object[] obs) {
			clear();
			for (Object o : obs) {
				this.addElement(o.toString());
			}
			current = obs;
		}
	}

	public static void main(String[] args) {
		try {
			DataRetriever ret = new DataRetriever("localhost");
			JFrame parent = new JFrame();
			parent.setSize(new Dimension(500, 300));
			DBPane pane = new DBPane(ret);
			parent.add(pane);
			parent.setVisible(true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
