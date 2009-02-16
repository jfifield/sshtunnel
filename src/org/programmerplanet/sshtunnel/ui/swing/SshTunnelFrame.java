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
import java.awt.Frame;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;
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
	private static final String APPLICATION_ICON_PATH = "/images/bullet_triangle_yellow.png";
	
	private static final ImageIcon CONNECTED_ICON = new ImageIcon(SshTunnelFrame.class.getResource("/images/bullet_ball_glass_green.png"));
	private static final ImageIcon DISCONNECTED_ICON = new ImageIcon(SshTunnelFrame.class.getResource("/images/bullet_ball_glass_red.png"));

	private JButton connectButton;
	private JButton disconnectButton;

	private JButton connectAllButton;
	private JButton disconnectAllButton;
	
	private JPopupMenu menu;
	private ActionListener menuItemActionListener;

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
				// add session menu item
				SessionMenuItem sessionMenuItem = createSessionMenuItem(session);
				int index = configuration.getSessions().indexOf(session);
				menu.insert(sessionMenuItem, index);
			}

			public void sessionChanged(Session session) {
				// update session menu item
				MenuElement[] menuElements = menu.getSubElements();
				for (int i = 0; i < menuElements.length; i++) {
					MenuElement menuElement = menuElements[i];
					if (menuElement instanceof SessionMenuItem) {
						SessionMenuItem menuItem = (SessionMenuItem)menuElement;
						if (session == menuItem.getSession()) {
							updateSessionMenuItem(menuItem);
						}
					}
				}
			}

			public void sessionRemoved(Session session) {
				updateConnectButtons();
				// remove session menu item
				MenuElement[] menuElements = menu.getSubElements();
				for (int i = 0; i < menuElements.length; i++) {
					MenuElement menuElement = menuElements[i];
					if (menuElement instanceof SessionMenuItem) {
						SessionMenuItem menuItem = (SessionMenuItem)menuElement;
						if (session == menuItem.getSession()) {
							menu.remove(menuItem);
						}
					}
				}
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
		    public void windowIconified(WindowEvent e) {
		    	System.out.println("windowIconified: setting window visible = false");
		    	e.getWindow().setVisible(false);
		    }
		});
		
		initializeTrayIcon();
	}

	private void connectionStatusChanged() {
		sessionsPanel.repaint();
		tunnelsPanel.repaint();
		updateConnectButtons();
		updateSessionMenuItems();
	}
	
	private void updateConnectButtons() {
		connectButton.setEnabled(currentSession != null ? !ConnectionManager.getInstance().isConnected(currentSession) : false);
		disconnectButton.setEnabled(currentSession != null ? ConnectionManager.getInstance().isConnected(currentSession) : false);
		connectAllButton.setEnabled(anyDisconnectedSessions());
		disconnectAllButton.setEnabled(anyConnectedSessions());
	}

	private void updateSessionMenuItems() {
		MenuElement[] menuElements = menu.getSubElements();
		for (int i = 0; i < menuElements.length; i++) {
			MenuElement menuElement = menuElements[i];
			if (menuElement instanceof SessionMenuItem) {
				SessionMenuItem menuItem = (SessionMenuItem)menuElement;
				updateSessionMenuItem(menuItem);
			}
		}
	}

	private boolean anyDisconnectedSessions() {
		boolean result = false;
		for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
			Session session = (Session)i.next();
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
			Session session = (Session)i.next();
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
				try { ConnectionManager.getInstance().disconnect(session); } catch (Exception e) {}
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
				Session session = (Session)i.next();
				if (!ConnectionManager.getInstance().isConnected(session)) {
					ConnectionManager.getInstance().connect(session, this);
				}
			}
		} catch (IOException ioe) {
			for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
				Session session = (Session)i.next();
				try { ConnectionManager.getInstance().disconnect(session); } catch (Exception e) {}
			}
		}
		connectionStatusChanged();
	}

	private void disconnectAll() {
		save();
		for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
			Session session = (Session)i.next();
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
		}
		catch (IOException e) {
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
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to save configuration.");
		}
	}
	
	private void exit() {
		disconnectAll();
		save();
		System.exit(0);
	}
	
	private void initializeTrayIcon() {
		// not sure what this does yet
		if (Integer.parseInt(System.getProperty("java.version").substring(2, 3)) >= 5)
			System.setProperty("javax.swing.adjustPopupLocationToFit", "false");

		menu = new JPopupMenu(APPLICATION_TITLE);

		// get tray icon image
		ImageIcon imageIcon = new ImageIcon(SshTunnelFrame.class.getResource(APPLICATION_ICON_PATH));
		final TrayIcon ti = new TrayIcon(imageIcon, APPLICATION_TITLE, menu);

		// build menu
		menuItemActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source instanceof SessionMenuItem) {
					SessionMenuItem menuItem = (SessionMenuItem)source;
					final Session session = menuItem.getSession();
					new Thread() {
						public void run() {
							if (!ConnectionManager.getInstance().isConnected(session)) {
								connect(session);
							}
							else if (ConnectionManager.getInstance().isConnected(session)) {
								disconnect(session);
							}
							String title = "Session: " + session.getSessionName();
							String message = session.getSessionName() + " is now " + (ConnectionManager.getInstance().isConnected(session) ? "connected" : "disconnected") + ".";
							ti.displayMessage(title, message, TrayIcon.INFO_MESSAGE_TYPE);
						}
					}.start();
				}
			}
		};

		for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
			Session session = (Session)i.next();
			SessionMenuItem sessionMenuItem = createSessionMenuItem(session);
			menu.add(sessionMenuItem);
		}
		
		menu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		menu.add(exitMenuItem);
		
		// build the tray icon
		ti.setIconAutoSize(true);
		ti.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	System.out.print("tray icon clicked: toggling window visibility");
				JFrame frame = SshTunnelFrame.this;
		    	System.out.print("...was " + frame.isVisible());
				frame.setVisible(!frame.isVisible());
		    	System.out.print("...is now " + frame.isVisible());
				if (frame.isVisible()) {
			    	System.out.print("...setting frame state to NORMAL");
					frame.setState(Frame.NORMAL);
					frame.toFront();
				}
				System.out.println();
			}
		});

		try {
			SystemTray tray = SystemTray.getDefaultSystemTray();
			tray.addTrayIcon(ti);
		}
		catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SessionMenuItem createSessionMenuItem(Session session) {
		SessionMenuItem sessionMenuItem = new SessionMenuItem(session);
		sessionMenuItem.addActionListener(menuItemActionListener);
		updateSessionMenuItem(sessionMenuItem);
		return sessionMenuItem;
	}
	
	private void updateSessionMenuItem(SessionMenuItem sessionMenuItem) {
		Session session = sessionMenuItem.getSession();
		// set text
		final String text = session.getSessionName();
		sessionMenuItem.setText(text);
		// set icon
		ImageIcon icon = ConnectionManager.getInstance().isConnected(session) ? CONNECTED_ICON : DISCONNECTED_ICON;
		sessionMenuItem.setIcon(icon);
	}
	
	private static class SessionMenuItem extends JMenuItem {
		
		private Session session;

		public SessionMenuItem(Session session) {
			this.session = session;
		}
		
		public Session getSession() {
			return session;
		}
		
	}

}
