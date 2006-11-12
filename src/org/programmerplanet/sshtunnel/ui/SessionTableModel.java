package org.programmerplanet.sshtunnel.ui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.programmerplanet.sshtunnel.model.Session;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class SessionTableModel extends AbstractTableModel {

	private static final String[] COLUMNS = { " ", "Name", "Hostname", "Username" };
	
	private List sessions;

	public SessionTableModel(List sessions) {
		this.sessions = sessions;
	}
	
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	public int getColumnCount() {
		return COLUMNS.length;
	}

	public int getRowCount() {
		return sessions.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Session session = (Session)sessions.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return session;
		case 1:
			return session.getSessionName();
		case 2:
			return session.getHostname();
		case 3:
			return session.getUsername();
		}
		return null;
	}

}
