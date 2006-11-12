package org.programmerplanet.sshtunnel.ui;

import java.awt.Color;
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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.programmerplanet.sshtunnel.model.Session;
import org.programmerplanet.sshtunnel.model.Tunnel;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class TunnelsPanel extends JPanel {

	private JTable tunnelTable;
	private TunnelTableModel tunnelTableModel;
	private JButton addTunnelButton;
	private JButton editTunnelButton;
	private JButton removeTunnelButton;
	private TunnelChangeListener listener;
	private Session session;

	public TunnelsPanel(TunnelChangeListener listener) {
		this.listener = listener;
		initialize();
	}

	public void setSession(Session session) {
		this.session = session;
		List tunnels = null;
		if (session != null) {
			tunnels = session.getTunnels();
		}
		tunnelTableModel.setTunnels(tunnels);
		tunnelTableModel.fireTableDataChanged();
		addTunnelButton.setEnabled(tunnels != null);
	}
	
	private void initialize() {
		JPanel tunnelsPanel = this;
		tunnelsPanel.setLayout(new BoxLayout(tunnelsPanel, BoxLayout.Y_AXIS));
		tunnelsPanel.setBorder(BorderFactory.createTitledBorder("Tunnels"));

		JPanel tunnelsButtonPanel = new JPanel();
		tunnelsPanel.add(tunnelsButtonPanel);

		addTunnelButton = new JButton("Add");
		tunnelsButtonPanel.add(addTunnelButton);
		addTunnelButton.setEnabled(false);
		editTunnelButton = new JButton("Edit");
		editTunnelButton.setEnabled(false);
		tunnelsButtonPanel.add(editTunnelButton);
		removeTunnelButton = new JButton("Remove");
		removeTunnelButton.setEnabled(false);
		tunnelsButtonPanel.add(removeTunnelButton);

		tunnelTableModel = new TunnelTableModel();
		tunnelTable = new JTable(tunnelTableModel);
		TableColumnModel tunnelTableColumnModel = tunnelTable.getColumnModel();
		TableColumn tunnelDirectionColumn = tunnelTableColumnModel.getColumn(2);
		tunnelDirectionColumn.setMaxWidth(18);
		
		TableCellRenderer defaultTunnelCellRenderer = tunnelTable.getDefaultRenderer(Object.class);
		TunnelStatusCellRenderer tunnelStatusCellRenderer = new TunnelStatusCellRenderer(defaultTunnelCellRenderer, tunnelTableModel);
		tunnelTable.setDefaultRenderer(Object.class, tunnelStatusCellRenderer);

		JScrollPane scrollPane = new JScrollPane(tunnelTable);
		tunnelsPanel.add(scrollPane);

		addTunnelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTunnel();
			}
		});

		editTunnelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSelectedTunnel();
			}
		});

		removeTunnelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeTunnel();
			}
		});

		tunnelTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					editSelectedTunnel();
				}
			}
		});

		tunnelTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int row = tunnelTable.getSelectedRow();
				editTunnelButton.setEnabled(row > -1);
				removeTunnelButton.setEnabled(row > -1);
				Tunnel tunnel = null;
				if (row > -1) {
					tunnel = (Tunnel)session.getTunnels().get(row);
				}
				listener.tunnelSelectionChanged(tunnel);
			}
		});
	}

	private void addTunnel() {
		EditTunnelPanel tunnelPanel = new EditTunnelPanel();
		int result = CustomDialog.show(getParentFrame(), "Tunnel", tunnelPanel);
		if (result == CustomDialog.OK) {
			Tunnel tunnel = new Tunnel();
			tunnel.setLocalAddress(tunnelPanel.getLocalAddress());
			tunnel.setLocalPort(tunnelPanel.getLocalPort());
			tunnel.setRemoteAddress(tunnelPanel.getRemoteAddress());
			tunnel.setRemotePort(tunnelPanel.getRemotePort());
			tunnel.setLocal(tunnelPanel.getLocal());
			session.getTunnels().add(tunnel);
			tunnelTableModel.fireTableDataChanged();
			listener.tunnelAdded(tunnel);
		}
	}

	private void editSelectedTunnel() {
		int row = tunnelTable.getSelectedRow();
		if (row > -1) {
			Tunnel tunnel = (Tunnel)session.getTunnels().get(row);
			EditTunnelPanel tunnelPanel = new EditTunnelPanel();

			tunnelPanel.setLocalAddress(tunnel.getLocalAddress());
			tunnelPanel.setLocalPort(tunnel.getLocalPort());
			tunnelPanel.setRemoteAddress(tunnel.getRemoteAddress());
			tunnelPanel.setRemotePort(tunnel.getRemotePort());
			tunnelPanel.setLocal(tunnel.getLocal());

			int result = CustomDialog.show(getParentFrame(), "Tunnel", tunnelPanel);
			if (result == CustomDialog.OK) {
				tunnel.setLocalAddress(tunnelPanel.getLocalAddress());
				tunnel.setLocalPort(tunnelPanel.getLocalPort());
				tunnel.setRemoteAddress(tunnelPanel.getRemoteAddress());
				tunnel.setRemotePort(tunnelPanel.getRemotePort());
				tunnel.setLocal(tunnelPanel.getLocal());
				tunnelTable.repaint();
				listener.tunnelChanged(tunnel);
			}
		}
	}

	private void removeTunnel() {
		int row = tunnelTable.getSelectedRow();
		if (row > -1) {
			Tunnel tunnel = (Tunnel)session.getTunnels().remove(row);
			tunnelTableModel.fireTableDataChanged();
			listener.tunnelRemoved(tunnel);
		}
	}

	private Frame getParentFrame() {
		Container container = this;
		while (!(container instanceof Frame)) {
			container = container.getParent();
		}
		return (Frame)container;
	}

	private static class TunnelStatusCellRenderer implements TableCellRenderer {

		private TableCellRenderer delegate;
		private TunnelTableModel tunnelTableModel;

		public TunnelStatusCellRenderer(TableCellRenderer delegate, TunnelTableModel tunnelTableModel) {
			this.delegate = delegate;
			this.tunnelTableModel = tunnelTableModel;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			// reset default color
			if (isSelected) {
				c.setForeground(table.getSelectionForeground());
			} else {
				c.setForeground(table.getForeground());
			}

			// get selected tunnel
			Tunnel tunnel = null;
			List tunnels = tunnelTableModel.getTunnels();
			if (tunnels != null) {
				tunnel = (Tunnel)tunnels.get(row);
			}

			// set error highlight if necessary
			if (tunnel != null && tunnel.getException() != null) {
				c.setForeground(Color.RED);
			}

			return c;
		}

	}

}
