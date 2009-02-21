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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.programmerplanet.sshtunnel.model.Session;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class EditSessionDialog extends CustomDialog {

	private Session session;
	private Text nameText;
	private Text hostText;
	private Text userText;
	private Button savePassCheckbox;
	private Text passText;

	public EditSessionDialog(Shell parent, Session session) {
		super(parent);
		this.setText("Session");
		this.session = session;
	}

	protected void initialize(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		Label nameLabel = new Label(parent, SWT.RIGHT);
		nameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		nameLabel.setText("Name:");

		nameText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gridData1 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData1.widthHint = 200;
		nameText.setLayoutData(gridData1);

		Label hostLabel = new Label(parent, SWT.RIGHT);
		hostLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		hostLabel.setText("Host:");

		hostText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gridData2 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData2.widthHint = 200;
		hostText.setLayoutData(gridData2);

		Label userLabel = new Label(parent, SWT.RIGHT);
		userLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		userLabel.setText("Username:");

		userText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gridData3 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData3.widthHint = 200;
		userText.setLayoutData(gridData3);

		Label savePassLabel = new Label(parent, SWT.RIGHT);
		savePassLabel.setText("");

		savePassCheckbox = new Button(parent, SWT.CHECK);
		savePassCheckbox.setText("Save Password?");
		savePassCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setSavePassword(savePassCheckbox.getSelection());
			}
		});

		Label passLabel = new Label(parent, SWT.RIGHT);
		passLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		passLabel.setText("Password:");

		passText = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		GridData gridData4 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData4.widthHint = 200;
		passText.setLayoutData(gridData4);

		setSavePassword(false);

		setSessionName(session.getSessionName());
		setHostname(session.getHostname());
		setUsername(session.getUsername());
		setPassword(session.getPassword());
	}

	protected void okPressed() {
		session.setSessionName(getSessionName());
		session.setHostname(getHostname());
		session.setUsername(getUsername());
		session.setPassword(getPassword());
		super.okPressed();
	}

	private void setSavePassword(boolean savePassword) {
		savePassCheckbox.setSelection(savePassword);
		passText.setEnabled(savePassword);
		passText.setEditable(savePassword);
		if (!savePassword) {
			passText.setText("");
		}
	}

	private String getSessionName() {
		return nameText.getText();
	}

	private String getHostname() {
		return hostText.getText();
	}

	private String getUsername() {
		return userText.getText();
	}

	private String getPassword() {
		return passText.getText();
	}

	private void setSessionName(String sessionName) {
		nameText.setText(sessionName != null ? sessionName : "");
	}

	private void setHostname(String hostname) {
		hostText.setText(hostname != null ? hostname : "");
	}

	private void setUsername(String username) {
		userText.setText(username != null ? username : "");
	}

	private void setPassword(String password) {
		passText.setText(password != null ? password : "");
		setSavePassword(password != null && password.trim().length() > 0);
	}

}