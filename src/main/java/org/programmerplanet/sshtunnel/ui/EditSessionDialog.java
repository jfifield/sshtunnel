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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.programmerplanet.sshtunnel.model.Session;

/**
 * @author <a href="agungm@outlook.com">Mulya Agung</a>
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
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
	private Button compressionCheckbox;
	private Text ciphersText;
	private Button chooseCiphersCheckbox;
	private Button privKeyButton;
	private Text debugLogDirText;
	private Button debugLogDirButton;
	private static final String[] PRIVATE_KEY_NAMES = {"All Files"};
	private static final String[] PRIVATE_KEY_EXT = {"*"};

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
		// Padding space
		//new Label(parent, SWT.LEAD).setLayoutData(new GridData(GridData.END, GridData.END, false, false));

		Label hostLabel = new Label(parent, SWT.RIGHT);
		hostLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		hostLabel.setText("Host:");

		hostText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		hostText.setLayoutData(gridData);
		//new Label(parent, SWT.LEAD).setLayoutData(new GridData(GridData.END, GridData.END, false, false));

		Label portLabel = new Label(parent, SWT.RIGHT);
		portLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		portLabel.setText("Port:");

		portText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		portText.setLayoutData(gridData);
		//new Label(parent, SWT.LEAD).setLayoutData(new GridData(GridData.END, GridData.END, false, false));

		Label userLabel = new Label(parent, SWT.RIGHT);
		userLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		userLabel.setText("Username:");

		userText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		userText.setLayoutData(gridData);
		//new Label(parent, SWT.LEAD).setLayoutData(new GridData(GridData.END, GridData.END, false, false));

//		Label savePassLabel = new Label(parent, SWT.RIGHT);
//		savePassLabel.setText("");

		savePassCheckbox = new Button(parent, SWT.CHECK);
		savePassCheckbox.setText("Password");
		savePassCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setSavePassword(savePassCheckbox.getSelection());
			}
		});

//		Label passLabel = new Label(parent, SWT.RIGHT);
//		passLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
//		passLabel.setText("Password:");

		passText = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		passText.setLayoutData(gridData);
		setSavePassword(false);
		//new Label(parent, SWT.LEAD).setLayoutData(new GridData(GridData.END, GridData.END, false, false));
		
//		Label privKeyLabel = new Label(parent, SWT.RIGHT);
//		privKeyLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
//		privKeyLabel.setText("Identity file:");
		
		privKeyButton = new Button(parent, SWT.PUSH);
		privKeyButton.setText("Identify file");
		// privKeyButton.setLayoutData(gridData);
		privKeyButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		privKeyButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		        FileDialog dlg = new FileDialog(parent.getShell(), SWT.OPEN);
		        dlg.setFilterNames(PRIVATE_KEY_NAMES);
		        dlg.setFilterExtensions(PRIVATE_KEY_EXT);
		        String fn = dlg.open();
		        if (fn != null) {
		          privKeyText.setText(fn);
		        }
		      }
		    });
		
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
		//new Label(parent, SWT.LEAD).setLayoutData(new GridData(GridData.END, GridData.END, false, false));
		
//		Label chooseCiphersLabel = new Label(parent, SWT.RIGHT);
//		chooseCiphersLabel.setText("");
		
		chooseCiphersCheckbox = new Button(parent, SWT.CHECK);
		chooseCiphersCheckbox.setText("Ciphers");
		chooseCiphersCheckbox.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		chooseCiphersCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setChooseCiphers(chooseCiphersCheckbox.getSelection());
			}
		});
		
//		Label ciphersLabel = new Label(parent, SWT.RIGHT);
//		ciphersLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
//		ciphersLabel.setText("Ciphers:");
		
		ciphersText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 200;
		ciphersText.setLayoutData(gridData);
		setChooseCiphers(false);
		
		debugLogDirButton = new Button(parent, SWT.PUSH);
		debugLogDirButton.setText("Log directory");
		debugLogDirButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		debugLogDirButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		        DirectoryDialog dlg = new DirectoryDialog(parent.getShell());
		        String fn = dlg.open();
		        if (fn != null) {
		          debugLogDirText.setText(fn);
		        }
		      }
		    });
		
		debugLogDirText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		//debugLogDirText.setEnabled(false);
		//gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		//gridData.widthHint = 200;
		debugLogDirText.setLayoutData(gridData);
		
		//new Label(parent, SWT.LEAD).setLayoutData(new GridData(GridData.END, GridData.END, false, false));
		
//		Label compressionLabel = new Label(parent, SWT.RIGHT);
//		compressionLabel.setText("");
		new Label(parent, SWT.LEAD).setLayoutData(new GridData(GridData.END, GridData.END, false, false));
		
		compressionCheckbox = new Button(parent, SWT.CHECK);
		compressionCheckbox.setText("Compression");
		compressionCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCompression(compressionCheckbox.getSelection());
			}
		});
		
		setSessionName(session.getSessionName());
		setHostname(session.getHostname());
		setPort(session.getPort());
		setUsername(session.getUsername());
		setPassword(session.getPassword());
		setIdentityPath(session.getIdentityPath());
		setPassPhrase(session.getPassPhrase());
		setCompression(session.isCompressed());
		setCiphers(session.getCiphers());
		setDebugLogDir(session.getDebugLogPath());
	}

	protected void okPressed() {
		session.setSessionName(getSessionName());
		session.setHostname(getHostname());
		session.setPort(getPort());
		session.setUsername(getUsername());
		session.setPassword(getPassword());
		session.setIdentityPath(getIdentityPath());
		session.setPassPhrase(getPassPhrase());
		session.setCompressed(getCompression());
		session.setCiphers(getCiphers());
		session.setCompressed(getCompression());
		session.setDebugLogPath(getDebugLogDir());
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
	
	private void setCompression(boolean isCompressed) {
		compressionCheckbox.setSelection(isCompressed);
	}
	
	private boolean getCompression() {
		return compressionCheckbox.getSelection();
	}
	
	private void setChooseCiphers(boolean isChosen) {
		chooseCiphersCheckbox.setSelection(isChosen);
		ciphersText.setEnabled(isChosen);
		ciphersText.setEditable(isChosen);
		if (!isChosen) {
			ciphersText.setText("");
		}
	}
	
	private String getCiphers() {
		return ciphersText.getText();
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
	
	private String getDebugLogDir() {
		return debugLogDirText.getText();
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
	
	private void setCiphers(String ciphers) {
		ciphersText.setText(ciphers != null ? ciphers : "");
		setChooseCiphers(ciphers != null && ciphers.trim().length() > 0);
	}
	
	private void setDebugLogDir(String dirPath) {
		debugLogDirText.setText(dirPath != null ? dirPath : "");
	}

}