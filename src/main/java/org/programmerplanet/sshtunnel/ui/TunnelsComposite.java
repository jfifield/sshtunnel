/*
 * Copyright 2023 Mulya Agung
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.agung.sshtunnel.addon.CsvConfigImporter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.programmerplanet.sshtunnel.model.Session;
import org.programmerplanet.sshtunnel.model.Tunnel;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 * @author <a href="agungm@outlook.com">Mulya Agung</a>
 */
public class TunnelsComposite extends Composite {

	private static final Log log = LogFactory.getLog(TunnelsComposite.class);
	
	private static final String ADD_IMAGE_PATH = "/images/add.png";
	private static final String EDIT_IMAGE_PATH = "/images/edit.png";
	private static final String DELETE_IMAGE_PATH = "/images/delete.png";
	private static final String IMPORT_IMAGE_PATH = "/images/import.png";
	private static final String[] TUNNEL_CONF_NAMES = {"CSV files"};
	private static final String[] TUNNEL_CONF_EXT = {"*.csv"};

	private Table tunnelTable;
	private Button addTunnelButton;
	private Button editTunnelButton;
	private Button removeTunnelButton;
	private Button importTunnelButton;
	private TunnelChangeListener listener;
	private Session session;
	private Image addImage;
	private Image editImage;
	private Image deleteImage;
	private Image importImage;
	
	private Shell shell;
	private CsvConfigImporter csvConfigImporter;

	public TunnelsComposite(Composite parent, int style, TunnelChangeListener listener) {
		super(parent, style);
		this.listener = listener;
		initialize();
		csvConfigImporter = new CsvConfigImporter();
	}
	
	public TunnelsComposite(Composite parent, Shell shell, int style, TunnelChangeListener listener) {
		super(parent, style);
		this.listener = listener;
		this.shell = shell;
		csvConfigImporter = new CsvConfigImporter();
		initialize();
	}

	public void setSession(Session session) {
		this.session = session;
		addTunnelButton.setEnabled(session != null);
		importTunnelButton.setEnabled(session != null);
		updateTable();
	}

	private void initialize() {
		createImages();

		this.setLayout(new FillLayout());

		Group group = new Group(this, SWT.NULL);
		group.setText("Tunnels");
		group.setLayout(new GridLayout());

		createButtonBarComposite(group);
		createTable(group);

		updateTable();
	}

	private void createButtonBarComposite(Group group) {
		Composite buttonBarComposite = new Composite(group, SWT.NONE);
		buttonBarComposite.setLayout(new FillLayout());

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		buttonBarComposite.setLayoutData(gridData);

		addTunnelButton = new Button(buttonBarComposite, SWT.PUSH);
		addTunnelButton.setText("Add");
		addTunnelButton.setToolTipText("Add Tunnel");
		addTunnelButton.setImage(addImage);
		addTunnelButton.setEnabled(false);

		editTunnelButton = new Button(buttonBarComposite, SWT.PUSH);
		editTunnelButton.setText("Edit");
		editTunnelButton.setToolTipText("Edit Tunnel");
		editTunnelButton.setImage(editImage);
		editTunnelButton.setEnabled(false);

		removeTunnelButton = new Button(buttonBarComposite, SWT.PUSH);
		removeTunnelButton.setText("Remove");
		removeTunnelButton.setToolTipText("Remove Tunnel");
		removeTunnelButton.setImage(deleteImage);
		removeTunnelButton.setEnabled(false);
		
		importTunnelButton = new Button(buttonBarComposite, SWT.PUSH);
		importTunnelButton.setText("Import");
		importTunnelButton.setToolTipText("Import Tunnels from a CSV file");
		importTunnelButton.setImage(importImage);
		importTunnelButton.setEnabled(false);

		addTunnelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addTunnel();
			}
		});

		editTunnelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editTunnel();
			}
		});

		removeTunnelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeTunnel();
			}
		});
		
		importTunnelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
		        FileDialog dlg = new FileDialog(group.getShell(), SWT.OPEN);
		        dlg.setText("Import Tunnel Configuration");
		        dlg.setFilterNames(TUNNEL_CONF_NAMES);
		        dlg.setFilterExtensions(TUNNEL_CONF_EXT);
		        String fn = dlg.open();
		        if (fn != null) {
		          importTunnels(fn);
		        }
		      }
		});
	}

	private void createTable(Group group) {
		tunnelTable = new Table(group, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		tunnelTable.setHeaderVisible(true);
		tunnelTable.setLinesVisible(true);

		TableColumn column1 = new TableColumn(tunnelTable, SWT.NULL);
		column1.setText("Local Address");

		TableColumn column2 = new TableColumn(tunnelTable, SWT.NULL);
		column2.setText("Local Port");

		TableColumn column3 = new TableColumn(tunnelTable, SWT.CENTER);
		column3.setText(" ");

		TableColumn column4 = new TableColumn(tunnelTable, SWT.NULL);
		column4.setText("Remote Address");

		TableColumn column5 = new TableColumn(tunnelTable, SWT.NULL);
		column5.setText("Remote Port");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		tunnelTable.setLayoutData(gridData);

		tunnelTable.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				int directionColumnWidth = 30;
				int portColumnWidth = 100;

				Rectangle area = tunnelTable.getClientArea();
				int relativeWidth = area.width;
				relativeWidth -= directionColumnWidth;
				relativeWidth -= (portColumnWidth * 2);
				relativeWidth /= 2;

				TableColumn column1 = tunnelTable.getColumn(0);
				column1.setWidth(relativeWidth);
				TableColumn column2 = tunnelTable.getColumn(1);
				column2.setWidth(portColumnWidth);
				TableColumn column3 = tunnelTable.getColumn(2);
				column3.setWidth(directionColumnWidth);
				TableColumn column4 = tunnelTable.getColumn(3);
				column4.setWidth(relativeWidth);
				TableColumn column5 = tunnelTable.getColumn(4);
				column5.setWidth(portColumnWidth);
			}
		});

		tunnelTable.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				editTunnel();
			}
		});

		tunnelTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int row = tunnelTable.getSelectionIndex();
				editTunnelButton.setEnabled(row > -1);
				removeTunnelButton.setEnabled(row > -1);
				Tunnel tunnel = null;
				if (row > -1) {
					tunnel = (Tunnel) session.getTunnels().get(row);
				}
				listener.tunnelSelectionChanged(tunnel);
			}
		});
	}

	public void updateTable() {
		tunnelTable.removeAll();
		if (session != null) {
			Color red = this.getDisplay().getSystemColor(SWT.COLOR_RED);
			List<Tunnel> tunnels = session.getTunnels();
			Collections.sort(tunnels);
			for (Iterator<Tunnel> i = tunnels.iterator(); i.hasNext();) {
				Tunnel tunnel = i.next();
				TableItem tableItem = new TableItem(tunnelTable, SWT.NULL);
				//tableItem.setText(new String[] { tunnel.getLocalAddress(), Integer.toString(tunnel.getLocalPort()), tunnel.getLocal() ? "->" : "<-", tunnel.getRemoteAddress(), Integer.toString(tunnel.getRemotePort()) });
				tableItem.setText(new String[] { tunnel.getLocalAddress(),
						Integer.toString(tunnel.getLocalPort()), 
						tunnel.getLocal() ? "\u21D2" : "\u21D0",
						tunnel.getRemoteAddress(),
						Integer.toString(tunnel.getRemotePort()) });
				if (tunnel.getException() != null) {
					tableItem.setForeground(red);
				}
			}
		}
	}

	private void addTunnel() {
		Tunnel tunnel = new Tunnel();
		EditTunnelDialog dialog = new EditTunnelDialog(getShell(), tunnel);
		int result = dialog.open();
		if (result == SWT.OK) {
			session.getTunnels().add(tunnel);
			updateTable();
			listener.tunnelAdded(session, tunnel);
		}
	}

	private void editTunnel() {
		int row = tunnelTable.getSelectionIndex();
		if (row > -1) {
			Tunnel tunnel = (Tunnel) session.getTunnels().get(row);
			Tunnel prevTunnel = tunnel.copy();
			EditTunnelDialog dialog = new EditTunnelDialog(getShell(), tunnel);
			int result = dialog.open();
			if (result == SWT.OK) {
				updateTable();
				int status = listener.tunnelChanged(session, tunnel, prevTunnel);
				if (status != 0) {
					// Failed cancelling tunnel bind
					log.error("Unable to stop existing tunnel");
				}
			}
		}
	}

	private void removeTunnel() {
		int row = tunnelTable.getSelectionIndex();
		if (row > -1) {
			Tunnel tunnel = (Tunnel) session.getTunnels().remove(row);
			updateTable();
			listener.tunnelRemoved(session, tunnel);
		}
	}
	
	private void importTunnels(String csvPath) {
		//List<Tunnel> tunnels = session.getTunnels();
		Set<Tunnel> importedTunnels = csvConfigImporter.readCsv(csvPath);
		if (importedTunnels != null) {
			if (importedTunnels.isEmpty()) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
	    		messageBox.setText("Info");
	    		messageBox.setMessage("Imported file has no tunnel records");
	    		messageBox.open();
			}
			else {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
        		messageBox.setText("Warning");
        		messageBox.setMessage("Found " + importedTunnels.size() + 
        				" records\nDo you want to import them now?\n(Existing tunnel will be updated)");
        		int result = messageBox.open();
        		boolean proceed = (result == SWT.YES);
        		
        		if (proceed) {
					importedTunnels.addAll(session.getTunnels());
					Iterator<Tunnel> it = session.getTunnels().iterator();
					while (it.hasNext()) {
						listener.tunnelRemoved(session, it.next());
						it.remove();
					}
					for (Tunnel tunnel: importedTunnels) {
						session.getTunnels().add(tunnel);
						listener.tunnelAdded(session, tunnel);
					}
					updateTable();
        		}
			}
		}
		else {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
    		messageBox.setText("Error");
    		messageBox.setMessage("Importing failed!\nCheck if columns = " + 
    				Arrays.toString(csvConfigImporter.tunnelConfHeaders.toArray()));
    		messageBox.open();
		}
//		try {
//			csvConfigImporter.readCsv(csvPath, session);
//		} catch (Exception e) {
//			//log.error(e);
//			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
//    		messageBox.setText("Error");
//    		messageBox.setMessage(e.getMessage());
//    		int result = messageBox.open();
    		//ret = (result == SWT.YES);
//			Display.getDefault().syncExec(new Runnable() {
//				@Override
//				public void run() {
//					MessageBox messageBox = new MessageBox(getShell()), SWT.ICON_WARNING | SWT.YES | SWT.NO);
//	        		messageBox.setText("Warning");
//	        		messageBox.setMessage(str);
//	        		int result = messageBox.open();
//	        		ret = (result == SWT.YES);
//				}
//			});
//		}
	}

	private void createImages() {
		addImage = loadImage(ADD_IMAGE_PATH);
		editImage = loadImage(EDIT_IMAGE_PATH);
		deleteImage = loadImage(DELETE_IMAGE_PATH);
		importImage = loadImage(IMPORT_IMAGE_PATH);
	}

	private Image loadImage(String path) {
		InputStream stream = SessionsComposite.class.getResourceAsStream(path);
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

	private void disposeImages() {
		addImage.dispose();
		editImage.dispose();
		deleteImage.dispose();
		importImage.dispose();
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		disposeImages();
		super.dispose();
	}

}
