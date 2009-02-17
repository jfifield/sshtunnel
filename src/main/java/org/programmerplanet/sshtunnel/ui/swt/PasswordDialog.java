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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class PasswordDialog extends CustomDialog {

	private String message;
	private Text passText;
	private String password;

	public PasswordDialog(Shell parent) {
		super(parent);
		this.setText("Password");
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	protected void initialize(Composite parent) {
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);

		if (message != null) {
			Label messageLabel = new Label(parent, SWT.LEFT);
			GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
			messageLabel.setLayoutData(gridData);
			messageLabel.setText(message + ":");
		}

		passText = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		GridData gridData4 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData4.widthHint = 200;
		passText.setLayoutData(gridData4);
	}

	protected void okPressed() {
		password = passText.getText();
		super.okPressed();
	}

	public String getPassword() {
		return password;
	}

}
