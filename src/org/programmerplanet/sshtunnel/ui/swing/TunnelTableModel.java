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
