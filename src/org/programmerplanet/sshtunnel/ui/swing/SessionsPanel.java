/*
 * Copyright 2007 Joseph Fifield
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.programmerplanet.sshtunnel.ui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.programmerplanet.sshtunnel.model.ConnectionManager;
import org.programmerplanet.sshtunnel.model.Session;
import org.programmerplanet.sshtunnel.ui.SessionChangeListener;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class SessionsPanel extends JPanel {

	private List sessions;
	private JTable sessionTable;
	private SessionTableModel sessionTableModel;
	private JButton addSessionButton;
	private JButton editSessionButton;
	private JButton removeSessionButton;
	private SessionChangeListener listener;

	public SessionsPanel(List sessions, SessionChangeListener listener) {
		this.sessions = sessions;
		this.listener = listener;
		initialize();
	}

	private void initialize() {
		JPanel sessionsPanel = this;
		sessionsPanel.setLayout(new BoxLayout(sessionsPanel, BoxLayout.Y_AXIS));
		sessionsPanel.setBorder(BorderFactory.createTitledBorder("Sessions"));
		
		JPanel sessionsButtonPanel = new JPanel();
		sessionsPanel.add(sessionsButtonPanel);

		addSessionButton = new JButton("Add");
		sessionsButtonPanel.add(addSessionButton);
		editSessionButton = new JButton("Edit");
		editSessionButton.setEnabled(false);
		sessionsButtonPanel.add(editSessionButton);
		removeSessionButton = new JButton("Remove");
		removeSessionButton.setEnabled(false);
		sessionsButtonPanel.add(removeSessionButton);

		sessionTableModel = new SessionTableModel(sessions);
		sessionTable = new JTable(sessionTableModel);
		TableColumnModel sessionTableColumnModel = sessionTable.getColumnModel();
		TableColumn sessionStatusColumn = sessionTableColumnModel.getColumn(0);
		sessionStatusColumn.setMaxWidth(18);
		sessionStatusColumn.setCellRenderer(new SessionStatusCellRenderer());

		JScrollPane sessionsScrollPane = new JScrollPane(sessionTable);
		sessionsPanel.add(sessionsScrollPane);

		addSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addSession();
			}
		});

		editSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSession();
			}
		});

		removeSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSession();
			}
		});

		sessionTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					editSession();
				}
			}
		});

		sessionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				int row = sessionTable.getSelectedRow();
				editSessionButton.setEnabled(row > -1);
				removeSessionButton.setEnabled(row > -1);
				Session session = null;
				if (row > -1) {
					session = (Session)sessions.get(row);
				}
				listener.sessionSelectionChanged(session);
			}

		});
	}
	
	private void addSession() {
		EditSessionPanel sessionPanel = new EditSessionPanel();
		int result = CustomDialog.show(getParentFrame(), "Session", sessionPanel);
		if (result == CustomDialog.OK) {
			Session session = new Session();
			session.setSessionName(sessionPanel.getSessionName());
			session.setHostname(sessionPanel.getHostname());
			session.setUsername(sessionPanel.getUsername());
			session.setPassword(sessionPanel.getPassword());
			sessions.add(session);
			sessionTableModel.fireTableDataChanged();
			listener.sessionAdded(session);
		}
	}
	
	private void editSession() {
		int row = sessionTable.getSelectedRow();
		if (row > -1) {
			Session session = (Session)sessions.get(row);
			EditSessionPanel sessionPanel = new EditSessionPanel();

			sessionPanel.setSessionName(session.getSessionName());
			sessionPanel.setHostname(session.getHostname());
			sessionPanel.setUsername(session.getUsername());
			sessionPanel.setPassword(session.getPassword());

			int result = CustomDialog.show(getParentFrame(), "Session", sessionPanel);
			if (result == CustomDialog.OK) {
				session.setSessionName(sessionPanel.getSessionName());
				session.setHostname(sessionPanel.getHostname());
				session.setUsername(sessionPanel.getUsername());
				session.setPassword(sessionPanel.getPassword());
				sessionTable.repaint();
				listener.sessionChanged(session);
			}
		}
	}

	private void removeSession() {
		int row = sessionTable.getSelectedRow();
		if (row > -1) {
			Session session = (Session)sessions.remove(row);
			sessionTableModel.fireTableDataChanged();
			listener.sessionRemoved(session);
		}
	}

	private Frame getParentFrame() {
		Container container = this;
		while (!(container instanceof Frame)) {
			container = container.getParent();
		}
		return (Frame)container;
	}

	private static class SessionStatusCellRenderer implements TableCellRenderer {

		private static ImageIcon connectedIcon = new ImageIcon(SessionStatusCellRenderer.class.getResource("/images/bullet_ball_glass_green.png"));
		private static ImageIcon disconnectedIcon = new ImageIcon(SessionStatusCellRenderer.class.getResource("/images/bullet_ball_glass_red.png"));

		private static final JLabel connected = new JLabel(connectedIcon);
		private static final JLabel disconnected = new JLabel(disconnectedIcon);
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Session session = (Session)value;
			if (ConnectionManager.getInstance().isConnected(session)) {
				return connected;
			}
			else {
				return disconnected;
			}
		}
		
	}

}
