package org.programmerplanet.sshtunnel.ui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.programmerplanet.sshtunnel.model.Tunnel;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class TunnelTableModel extends AbstractTableModel {

	private static final String[] COLUMNS = { "Local Address", "Local Port", " ", "Remote Address", "Remote Port" };
	
	private List tunnels;

	public TunnelTableModel() {
	}
	
	public void setTunnels(List tunnels) {
		this.tunnels = tunnels;
	}
	
	public List getTunnels() {
		return tunnels;
	}
	
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	public int getColumnCount() {
		return COLUMNS.length;
	}

	public int getRowCount() {
		return tunnels != null ? tunnels.size() : 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (tunnels != null) {
			Tunnel tunnel = (Tunnel) tunnels.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return tunnel.getLocalAddress();
			case 1:
				return new Integer(tunnel.getLocalPort());
			case 2:
				return tunnel.getLocal() ? "->" : "<-";
			case 3:
				return tunnel.getRemoteAddress();
			case 4:
				return new Integer(tunnel.getRemotePort());
			}
		}
		return null;
	}

}
