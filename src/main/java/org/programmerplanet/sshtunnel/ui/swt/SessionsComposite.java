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
package org.programmerplanet.sshtunnel.ui.swt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.programmerplanet.sshtunnel.model.ConnectionManager;
import org.programmerplanet.sshtunnel.model.Session;
import org.programmerplanet.sshtunnel.ui.SessionChangeListener;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class SessionsComposite extends Composite {

	private static final String CONNECTED_IMAGE_PATH = "/images/bullet_green.png";
	private static final String DISCONNECTED_IMAGE_PATH = "/images/bullet_red.png";
	private static final String ADD_IMAGE_PATH = "/images/add.png";
	private static final String EDIT_IMAGE_PATH = "/images/edit.png";
	private static final String DELETE_IMAGE_PATH = "/images/delete.png";

	private List sessions;
	private Table sessionTable;
	private Button addSessionButton;
	private Button editSessionButton;
	private Button removeSessionButton;
	private SessionChangeListener listener;

	private Image connectedImage;
	private Image disconnectedImage;
	private Image addImage;
	private Image editImage;
	private Image deleteImage;

	public SessionsComposite(Composite parent, int style, List sessions, SessionChangeListener listener) {
		super(parent, style);
		this.sessions = sessions;
		this.listener = listener;
		initialize();
	}

	private void initialize() {
		createImages();

		this.setLayout(new FillLayout());

		Group group = new Group(this, SWT.NULL);
		group.setText("Sessions");
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

		addSessionButton = new Button(buttonBarComposite, SWT.PUSH);
		addSessionButton.setText("Add");
		addSessionButton.setToolTipText("Add Session");
		addSessionButton.setImage(addImage);

		editSessionButton = new Button(buttonBarComposite, SWT.PUSH);
		editSessionButton.setText("Edit");
		editSessionButton.setToolTipText("Edit Session");
		editSessionButton.setImage(editImage);
		editSessionButton.setEnabled(false);

		removeSessionButton = new Button(buttonBarComposite, SWT.PUSH);
		removeSessionButton.setText("Remove");
		removeSessionButton.setToolTipText("Remove Session");
		removeSessionButton.setImage(deleteImage);
		removeSessionButton.setEnabled(false);

		addSessionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addSession();
			}
		});

		editSessionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSession();
			}
		});

		removeSessionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeSession();
			}
		});
	}

	private void createTable(Group group) {
		sessionTable = new Table(group, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		sessionTable.setHeaderVisible(true);
		sessionTable.setLinesVisible(true);

		TableColumn column1 = new TableColumn(sessionTable, SWT.NULL);
		column1.setText(" ");

		TableColumn column2 = new TableColumn(sessionTable, SWT.NULL);
		column2.setText("Name");

		TableColumn column3 = new TableColumn(sessionTable, SWT.NULL);
		column3.setText("Hostname");

		TableColumn column4 = new TableColumn(sessionTable, SWT.NULL);
		column4.setText("Username");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		sessionTable.setLayoutData(gridData);

		sessionTable.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				int fixedWidth = 22;

				Rectangle area = sessionTable.getClientArea();
				int relativeWidth = area.width;
				relativeWidth -= fixedWidth;
				relativeWidth /= 3;

				TableColumn column1 = sessionTable.getColumn(0);
				column1.setWidth(fixedWidth);
				TableColumn column2 = sessionTable.getColumn(1);
				column2.setWidth(relativeWidth);
				TableColumn column3 = sessionTable.getColumn(2);
				column3.setWidth(relativeWidth);
				TableColumn column4 = sessionTable.getColumn(3);
				column4.setWidth(relativeWidth);

			}
		});

		sessionTable.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				editSession();
			}
		});

		sessionTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int row = sessionTable.getSelectionIndex();
				editSessionButton.setEnabled(row > -1);
				removeSessionButton.setEnabled(row > -1);
				Session session = null;
				if (row > -1) {
					session = (Session) sessions.get(row);
				}
				listener.sessionSelectionChanged(session);
			}
		});
	}

	private void createImages() {
		connectedImage = loadImage(CONNECTED_IMAGE_PATH);
		disconnectedImage = loadImage(DISCONNECTED_IMAGE_PATH);
		addImage = loadImage(ADD_IMAGE_PATH);
		editImage = loadImage(EDIT_IMAGE_PATH);
		deleteImage = loadImage(DELETE_IMAGE_PATH);
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

	public void updateTable() {
		sessionTable.removeAll();
		for (Iterator i = sessions.iterator(); i.hasNext();) {
			Session session = (Session) i.next();
			TableItem tableItem = new TableItem(sessionTable, SWT.NULL);
			tableItem.setText(new String[] { "", session.getSessionName(), session.getHostname(), session.getUsername() });
			Image image = ConnectionManager.getInstance().isConnected(session) ? connectedImage : disconnectedImage;
			tableItem.setImage(0, image);
		}
	}

	private void addSession() {
		Session session = new Session();
		EditSessionDialog dialog = new EditSessionDialog(this.getShell(), session);
		int result = dialog.open();
		if (result == SWT.OK) {
			sessions.add(session);
			updateTable();
			listener.sessionAdded(session);
		}
	}

	private void editSession() {
		int row = sessionTable.getSelectionIndex();
		if (row > -1) {
			Session session = (Session) sessions.get(row);
			EditSessionDialog dialog = new EditSessionDialog(this.getShell(), session);
			int result = dialog.open();
			if (result == SWT.OK) {
				updateTable();
				listener.sessionChanged(session);
			}
		}
	}

	private void removeSession() {
		int row = sessionTable.getSelectionIndex();
		if (row > -1) {
			Session session = (Session) sessions.remove(row);
			updateTable();
			listener.sessionRemoved(session);
		}
	}

	private void disposeImages() {
		connectedImage.dispose();
		disconnectedImage.dispose();
		addImage.dispose();
		editImage.dispose();
		deleteImage.dispose();
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		disposeImages();
		super.dispose();
	}

}
