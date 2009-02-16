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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.programmerplanet.sshtunnel.model.Configuration;
import org.programmerplanet.sshtunnel.model.ConnectionManager;
import org.programmerplanet.sshtunnel.model.Session;
import org.programmerplanet.sshtunnel.ui.SessionChangeAdapter;
import org.programmerplanet.sshtunnel.ui.SessionChangeListener;
import org.programmerplanet.sshtunnel.ui.TunnelChangeAdapter;
import org.programmerplanet.sshtunnel.ui.TunnelChangeListener;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class SshTunnelFrame extends JFrame {

	private static final String APPLICATION_TITLE = "SSH Tunnel";
	private static final String APPLICATION_ICON_PATH = "/images/sshtunnel.png";

	private static final ImageIcon CONNECTED_ICON = new ImageIcon(SshTunnelFrame.class.getResource("/images/bullet_green.png"));
	private static final ImageIcon DISCONNECTED_ICON = new ImageIcon(SshTunnelFrame.class.getResource("/images/bullet_red.png"));

	private JButton connectButton;
	private JButton disconnectButton;

	private JButton connectAllButton;
	private JButton disconnectAllButton;

	private Dimension size = new Dimension();

	private Configuration configuration;
	private SessionsPanel sessionsPanel;
	private TunnelsPanel tunnelsPanel;

	private Session currentSession = null;

	public SshTunnelFrame() {
		super(APPLICATION_TITLE);
		load();
		initialize();
	}

	private void initialize() {
		final JFrame frame = this;

		ImageIcon imageIcon = new ImageIcon(SshTunnelFrame.class.getResource(APPLICATION_ICON_PATH));
		frame.setIconImage(imageIcon.getImage());

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());

		/* Main Panel */
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);

		/* Tunnels Panel */
		TunnelChangeListener tunnelChangeListener = new TunnelChangeAdapter();
		tunnelsPanel = new TunnelsPanel(tunnelChangeListener);

		/* Sessions Panel */
		SessionChangeListener sessionChangeListener = new SessionChangeAdapter() {

			public void sessionAdded(Session session) {
				updateConnectButtons();
			}

			public void sessionRemoved(Session session) {
				updateConnectButtons();
			}

			public void sessionSelectionChanged(Session session) {
				SshTunnelFrame.this.currentSession = session;
				tunnelsPanel.setSession(session);
				updateConnectButtons();
			}

		};
		sessionsPanel = new SessionsPanel(configuration.getSessions(), sessionChangeListener);

		/* Connect/Disconnect Buttons Panel */
		JPanel connectPanel = new JPanel();

		connectButton = new JButton("Connect");
		connectButton.setEnabled(false);
		connectPanel.add(connectButton);
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setEnabled(false);
		connectPanel.add(disconnectButton);

		connectAllButton = new JButton("Connect All");
		connectAllButton.setEnabled(true);
		connectPanel.add(connectAllButton);
		disconnectAllButton = new JButton("Disconnect All");
		disconnectAllButton.setEnabled(false);
		connectPanel.add(disconnectAllButton);

		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						connect();
					}
				}.start();
			}
		});

		disconnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						disconnect();
					}
				}.start();
			}
		});

		connectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						connectAll();
					}
				}.start();
			}
		});

		disconnectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						disconnectAll();
					}
				}.start();
			}
		});

		/* Main Window Panels */
		mainPanel.add(sessionsPanel);
		mainPanel.add(tunnelsPanel);
		mainPanel.add(connectPanel);

		/* Window Events */
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
	}

	private void connectionStatusChanged() {
		sessionsPanel.repaint();
		tunnelsPanel.repaint();
		updateConnectButtons();
	}

	private void updateConnectButtons() {
		connectButton.setEnabled(currentSession != null ? !ConnectionManager.getInstance().isConnected(currentSession) : false);
		disconnectButton.setEnabled(currentSession != null ? ConnectionManager.getInstance().isConnected(currentSession) : false);
		connectAllButton.setEnabled(anyDisconnectedSessions());
		disconnectAllButton.setEnabled(anyConnectedSessions());
	}

	private boolean anyDisconnectedSessions() {
		boolean result = false;
		for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
			Session session = (Session) i.next();
			if (!ConnectionManager.getInstance().isConnected(session)) {
				result = true;
				break;
			}
		}
		return result;
	}

	private boolean anyConnectedSessions() {
		boolean result = false;
		for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
			Session session = (Session) i.next();
			if (ConnectionManager.getInstance().isConnected(session)) {
				result = true;
				break;
			}
		}
		return result;
	}

	private void connect() {
		connect(currentSession);
	}

	private void connect(Session session) {
		save();
		if (session != null && !ConnectionManager.getInstance().isConnected(session)) {
			try {
				ConnectionManager.getInstance().connect(session, this);
			} catch (IOException ioe) {
				try {
					ConnectionManager.getInstance().disconnect(session);
				} catch (Exception e) {
				}
			}
		}
		connectionStatusChanged();
	}

	private void disconnect() {
		disconnect(currentSession);
	}

	private void disconnect(Session session) {
		save();
		if (session != null && ConnectionManager.getInstance().isConnected(session)) {
			ConnectionManager.getInstance().disconnect(session);
		}
		connectionStatusChanged();
	}

	private void connectAll() {
		save();
		try {
			for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
				Session session = (Session) i.next();
				if (!ConnectionManager.getInstance().isConnected(session)) {
					ConnectionManager.getInstance().connect(session, this);
				}
			}
		} catch (IOException ioe) {
			for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
				Session session = (Session) i.next();
				try {
					ConnectionManager.getInstance().disconnect(session);
				} catch (Exception e) {
				}
			}
		}
		connectionStatusChanged();
	}

	private void disconnectAll() {
		save();
		for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
			Session session = (Session) i.next();
			if (ConnectionManager.getInstance().isConnected(session)) {
				ConnectionManager.getInstance().disconnect(session);
			}
		}
		connectionStatusChanged();
	}

	private void load() {
		configuration = new Configuration();
		try {
			configuration.read();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to load configuration.");
		}

		this.setLocation(configuration.getLeft(), configuration.getTop());
		size.width = configuration.getWidth();
		size.height = configuration.getHeight();
	}

	public Dimension getPreferredSize() {
		return size;
	}

	private void save() {
		configuration.setLeft(this.getLocation().x);
		configuration.setTop(this.getLocation().y);
		configuration.setWidth(this.getSize().width);
		configuration.setHeight(this.getSize().height);
		try {
			configuration.write();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to save configuration.");
		}
	}

	private void exit() {
		disconnectAll();
		save();
		System.exit(0);
	}

}
