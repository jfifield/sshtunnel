/*
 * Copyright 2009 Joseph Fifield
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
package org.programmerplanet.sshtunnel.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.programmerplanet.sshtunnel.model.Configuration;
import org.programmerplanet.sshtunnel.model.ConnectionException;
import org.programmerplanet.sshtunnel.model.ConnectionManager;
import org.programmerplanet.sshtunnel.model.Session;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class SshTunnelComposite extends Composite {

	private static final String APPLICATION_TITLE = "SSH Tunnel";
	private static final String APPLICATION_IMAGE_PATH = "/images/sshtunnel.png";
	private static final String CONNECT_IMAGE_PATH = "/images/connect.png";
	private static final String DISCONNECT_IMAGE_PATH = "/images/disconnect.png";
	private static final String CONNECT_ALL_IMAGE_PATH = "/images/connect_all.png";
	private static final String DISCONNECT_ALL_IMAGE_PATH = "/images/disconnect_all.png";
	private static final String CONNECTED_IMAGE_PATH = "/images/bullet_green.png";
	private static final String DISCONNECTED_IMAGE_PATH = "/images/bullet_red.png";

	private Image applicationImage;
	private Image connectImage;
	private Image disconnectImage;
	private Image connectAllImage;
	private Image disconnectAllImage;
	private Image connectedImage;
	private Image disconnectedImage;

	private Button connectButton;
	private Button disconnectButton;

	private Button connectAllButton;
	private Button disconnectAllButton;
	private TrayItem trayItem;

	private Configuration configuration;
	private SashForm sashForm;
	private SessionsComposite sessionsComposite;
	private TunnelsComposite tunnelsComposite;

	private Session currentSession = null;

	private Shell shell;

	public SshTunnelComposite(Shell shell) {
		super(shell, SWT.NONE);
		this.shell = shell;
		load();
		initialize();
	}

	private void load() {
		configuration = new Configuration();
		try {
			configuration.read();
		} catch (IOException e) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			messageBox.setText("Error");
			messageBox.setMessage("Unable to load configuration.");
			messageBox.open();
		}
	}

	private void save() {
		configuration.setLeft(shell.getBounds().x);
		configuration.setTop(shell.getBounds().y);
		configuration.setWidth(shell.getBounds().width);
		configuration.setHeight(shell.getBounds().height);
		configuration.setWeights(sashForm.getWeights());
		try {
			configuration.write();
		} catch (IOException e) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			messageBox.setText("Error");
			messageBox.setMessage("Unable to save configuration.");
			messageBox.open();
		}
	}

	private void initialize() {
		createImages();

		shell.setText(APPLICATION_TITLE);
		shell.setLayout(new FillLayout());
		shell.setImage(applicationImage);
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				exit();
			}

			public void shellIconified(ShellEvent e) {
				shell.setVisible(false);
			}
		});

		this.setLayout(new GridLayout());

		createSashForm();
		createSessionsComposite();
		createTunnelsComposite();
		createButtonBarComposite();
		createTrayIcon();

		shell.setBounds(configuration.getLeft(), configuration.getTop(), configuration.getWidth(), configuration.getHeight());
		sashForm.setWeights(configuration.getWeights());
		
		// Run connection monitor
		SessionConnectionMonitor.getInstance().setSshTunnelComposite(this);
		SessionConnectionMonitor.getInstance().startMonitor();
		//ConnectionMonitor.getInstance().setThreadStopped(false);
		//Display.getDefault().asyncExec(ConnectionMonitor.getInstance());
	}

	private void createImages() {
		applicationImage = loadImage(APPLICATION_IMAGE_PATH);
		connectImage = loadImage(CONNECT_IMAGE_PATH);
		disconnectImage = loadImage(DISCONNECT_IMAGE_PATH);
		connectAllImage = loadImage(CONNECT_ALL_IMAGE_PATH);
		disconnectAllImage = loadImage(DISCONNECT_ALL_IMAGE_PATH);
		connectedImage = loadImage(CONNECTED_IMAGE_PATH);
		disconnectedImage = loadImage(DISCONNECTED_IMAGE_PATH);
	}

	private Image loadImage(String path) {
		InputStream stream = SshTunnelComposite.class.getResourceAsStream(path);
		try {
			return new Image(this.getDisplay(), stream);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	private void createSashForm() {
		sashForm = new SashForm(this, SWT.VERTICAL);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		sashForm.setLayoutData(gridData);
	}

	private void createSessionsComposite() {
		SessionChangeListener sessionChangeListener = new SessionChangeAdapter() {

			public void sessionAdded(Session session) {
				updateConnectButtons();
			}

			public void sessionRemoved(Session session) {
				updateConnectButtons();
			}

			public void sessionSelectionChanged(Session session) {
				SshTunnelComposite.this.currentSession = session;
				tunnelsComposite.setSession(session);
				// Check session alive status
//				Exception err = ConnectionManager.getInstance().getSessionException(session);
//				if (err != null) {
//					ConnectionManager.getInstance().disconnect(session);
//					showErrorMessage("Connection error", err);
//				}
				updateConnectButtons();
			}

		};
		sessionsComposite = new SessionsComposite(sashForm, SWT.NONE, configuration.getSessions(), sessionChangeListener);
	}

	private void createTunnelsComposite() {
		TunnelChangeListener tunnelChangeListener = new TunnelChangeAdapter();
		tunnelsComposite = new TunnelsComposite(sashForm, SWT.NONE, tunnelChangeListener);
	}

	private void createButtonBarComposite() {
		Composite buttonBarComposite = new Composite(this, SWT.NONE);
		buttonBarComposite.setLayout(new FillLayout());

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		buttonBarComposite.setLayoutData(gridData);

		connectButton = new Button(buttonBarComposite, SWT.PUSH);
		connectButton.setText("Connect");
		connectButton.setToolTipText("Connect");
		connectButton.setImage(connectImage);
		connectButton.setEnabled(false);

		disconnectButton = new Button(buttonBarComposite, SWT.PUSH);
		disconnectButton.setText("Disconnect");
		disconnectButton.setToolTipText("Disconnect");
		disconnectButton.setImage(disconnectImage);
		disconnectButton.setEnabled(false);

		connectAllButton = new Button(buttonBarComposite, SWT.PUSH);
		connectAllButton.setText("Connect All");
		connectAllButton.setToolTipText("Connect All");
		connectAllButton.setImage(connectAllImage);
		connectAllButton.setEnabled(true);

		disconnectAllButton = new Button(buttonBarComposite, SWT.PUSH);
		disconnectAllButton.setText("Disconnect All");
		disconnectAllButton.setToolTipText("Disconnect All");
		disconnectAllButton.setImage(disconnectAllImage);
		disconnectAllButton.setEnabled(false);

		connectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Display display = SshTunnelComposite.this.getDisplay();
				Runnable runnable = new Runnable() {
					public void run() {
						connect();
					}
				};
				display.asyncExec(runnable);
			}
		});

		disconnectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Display display = SshTunnelComposite.this.getDisplay();
				Runnable runnable = new Runnable() {
					public void run() {
						disconnect();
					}
				};
				display.asyncExec(runnable);
			}
		});

		connectAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Display display = SshTunnelComposite.this.getDisplay();
				Runnable runnable = new Runnable() {
					public void run() {
						connectAll();
					}
				};
				display.asyncExec(runnable);
			}
		});

		disconnectAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Display display = SshTunnelComposite.this.getDisplay();
				Runnable runnable = new Runnable() {
					public void run() {
						disconnectAll();
					}
				};
				display.asyncExec(runnable);
			}
		});
	}

	public void connectionStatusChanged() {
		sessionsComposite.updateTable();
		tunnelsComposite.updateTable();
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
				ConnectionManager.getInstance().connect(session, shell);
				// Put to monitored list
				SessionConnectionMonitor.getInstance().addSession(session.getSessionName(), session);
			} catch (ConnectionException ce) {
				try {
					ConnectionManager.getInstance().disconnect(session);
				} catch (Exception e) {
				}
				showErrorMessage("Unable to connect to '" + session.getSessionName() + "'", ce);
			}
		}
		connectionStatusChanged();
	}

	private void showErrorMessage(String message, Exception e) {
		Throwable cause = getOriginatingCause(e);
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		messageBox.setText("Error");
		messageBox.setMessage(message + ": " + cause.getMessage());
		messageBox.open();
	}
	
	public void showDisconnectedMessage(Session session) {
//		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
//		messageBox.setText("Error");
//		messageBox.setMessage("Lost connection to " + session.getSessionName() + 
//				" (" + session.getHostname() + ")");
//		messageBox.open();
		if (trayItem != null && trayItem.getVisible()) {
			ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_ERROR);
			tip.setText("Session: " + session.getSessionName());
			tip.setMessage("Connection to "+ session.getHostname() + " has been lost.");
			trayItem.setToolTip(tip);
			tip.setVisible(true);
		}
	}

	private Throwable getOriginatingCause(Exception e) {
		Throwable cause = e;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause;
	}

	private void disconnect() {
		disconnect(currentSession);
	}

	private void disconnect(Session session) {
		save();
		if (session != null && ConnectionManager.getInstance().isConnected(session)) {
			ConnectionManager.getInstance().disconnect(session);
			SessionConnectionMonitor.getInstance().removeSession(session.getSessionName());
		}
		connectionStatusChanged();
	}

	private void connectAll() {
		save();
		Session sessionToConnect = null;
		try {
			for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
				sessionToConnect = (Session) i.next();
				if (!ConnectionManager.getInstance().isConnected(sessionToConnect)) {
					ConnectionManager.getInstance().connect(sessionToConnect, shell);
				}
			}
		} catch (ConnectionException ce) {
			for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
				Session session = (Session) i.next();
				try {
					ConnectionManager.getInstance().disconnect(session);
				} catch (Exception e) {
				}
			}
			showErrorMessage("Unable to connect to '" + sessionToConnect.getSessionName() + "'", ce);
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

	private void createTrayIcon() {
		Tray tray = this.getDisplay().getSystemTray();

		//final TrayItem trayItem = new TrayItem(tray, 0);
		trayItem = new TrayItem(tray, 0);
		trayItem.setToolTipText(APPLICATION_TITLE);
		trayItem.setImage(applicationImage);

		trayItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(!shell.isVisible());
			}
		});

		trayItem.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				Shell s = new Shell(e.display);
				Menu m = new Menu(s, SWT.POP_UP);

				// build menu
				Listener menuItemListener = new Listener() {
					public void handleEvent(Event event) {
						Object source = event.widget.getData();
						if (source instanceof Session) {
							final Session session = (Session) source;
							Runnable runnable = new Runnable() {
								public void run() {
									boolean connectedBefore = ConnectionManager.getInstance().isConnected(session);
									if (!connectedBefore) {
										connect(session);
									} else if (connectedBefore) {
										disconnect(session);
									}
									boolean connectedAfter = ConnectionManager.getInstance().isConnected(session);
									if (connectedBefore != connectedAfter) {
										String title = "Session: " + session.getSessionName();
										String message = session.getSessionName() + " is now " + (connectedAfter ? "connected" : "disconnected") + ".";
										ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
										tip.setText(title);
										tip.setMessage(message);
										trayItem.setToolTip(tip);
										tip.setVisible(true);
									}
								}
							};
							event.display.asyncExec(runnable);
						}
					}
				};

				for (Iterator i = configuration.getSessions().iterator(); i.hasNext();) {
					Session session = (Session) i.next();
					MenuItem menuItem = new MenuItem(m, SWT.NONE);
					// set session
					menuItem.setData(session);
					// set text
					String text = session.getSessionName();
					menuItem.setText(text);
					// set image
					Image image = ConnectionManager.getInstance().isConnected(session) ? connectedImage : disconnectedImage;
					menuItem.setImage(image);
					menuItem.addListener(SWT.Selection, menuItemListener);
				}

				if (!configuration.getSessions().isEmpty()) {
					new MenuItem(m, SWT.SEPARATOR);
				}

				MenuItem exit = new MenuItem(m, SWT.NONE);
				exit.setText("Exit");
				exit.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						exit();
					}
				});

				m.setVisible(true);
			};
		});
	}

	private void exit() {
		disconnectAll();
		SessionConnectionMonitor.getInstance().stopMonitor();
		save();
		Tray tray = this.getDisplay().getSystemTray();
		for (TrayItem trayItem : tray.getItems()) {
			trayItem.dispose();
		}
		System.exit(0);
	}

	private void disposeImages() {
		applicationImage.dispose();
		connectImage.dispose();
		disconnectImage.dispose();
		connectAllImage.dispose();
		disconnectAllImage.dispose();
		connectedImage.dispose();
		disconnectedImage.dispose();
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		disposeImages();
		super.dispose();
	}

}
