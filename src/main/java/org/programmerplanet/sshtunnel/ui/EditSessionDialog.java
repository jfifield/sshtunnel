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
 * @author <a href="agungm@outlook.com">Mulya Agung</a>
 */
public class EditSessionDialog extends CustomDialog {

	private Session session;
	private Text nameText;
	private Text hostText;
	private Text portText;
	private Text userText;
	private Button savePassCheckbox;
	private Text passText;
	private Text privKeyText;
	private Text passPhraseText;

	public EditSessionDialog(Shell parent, Session session) {
		super(parent);
		this.setText("Session");
		this.session = session;
	}

	protected void initialize(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		GridData gridData = null;

		Label nameLabel = new Label(parent, SWT.RIGHT);
		nameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		nameLabel.setText("Name:");

		nameText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		nameText.setLayoutData(gridData);

		Label hostLabel = new Label(parent, SWT.RIGHT);
		hostLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		hostLabel.setText("Host:");

		hostText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		hostText.setLayoutData(gridData);

		Label portLabel = new Label(parent, SWT.RIGHT);
		portLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		portLabel.setText("Port:");

		portText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		portText.setLayoutData(gridData);

		Label userLabel = new Label(parent, SWT.RIGHT);
		userLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		userLabel.setText("Username:");

		userText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		userText.setLayoutData(gridData);

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
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		passText.setLayoutData(gridData);

		setSavePassword(false);
		
		Label privKeyLabel = new Label(parent, SWT.RIGHT);
		privKeyLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		privKeyLabel.setText("Private key:");
		
		privKeyText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		privKeyText.setLayoutData(gridData);
		
		Label passPhraseLabel = new Label(parent, SWT.RIGHT);
		passPhraseLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		passPhraseLabel.setText("Passphrase:");
		
		passPhraseText = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		passPhraseText.setLayoutData(gridData);

		setSessionName(session.getSessionName());
		setHostname(session.getHostname());
		setPort(session.getPort());
		setUsername(session.getUsername());
		setPassword(session.getPassword());
		setIdentityPath(session.getIdentityPath());
		setPassPhrase(session.getPassPhrase());
	}

	protected void okPressed() {
		session.setSessionName(getSessionName());
		session.setHostname(getHostname());
		session.setPort(getPort());
		session.setUsername(getUsername());
		session.setPassword(getPassword());
		session.setIdentityPath(getIdentityPath());
		session.setPassPhrase(getPassPhrase());
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

	private int getPort() {
		return Integer.parseInt(portText.getText());
	}

	private String getUsername() {
		return userText.getText();
	}

	private String getPassword() {
		return passText.getText();
	}
	
	private String getIdentityPath() {
		return privKeyText.getText();
	}
	
	private String getPassPhrase() {
		return passPhraseText.getText();
	}

	private void setSessionName(String sessionName) {
		nameText.setText(sessionName != null ? sessionName : "");
	}

	private void setHostname(String hostname) {
		hostText.setText(hostname != null ? hostname : "");
	}

	private void setPort(int port) {
		portText.setText(port > 0 ? Integer.toString(port) : "");
	}

	private void setUsername(String username) {
		userText.setText(username != null ? username : "");
	}

	private void setPassword(String password) {
		passText.setText(password != null ? password : "");
		setSavePassword(password != null && password.trim().length() > 0);
	}
	
	private void setIdentityPath(String identityPath) {
		privKeyText.setText(identityPath != null ? identityPath : "");
	}
	
	private void setPassPhrase(String passPhrase) {
		passPhraseText.setText(passPhrase != null ? passPhrase : "");
	}

}