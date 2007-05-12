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
package org.programmerplanet.sshtunnel.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class EditSessionPanel extends JPanel {

	private JTextField nameText;
	private JTextField hostText;
	private JTextField userText;
	private JCheckBox savePassCheckbox;
	private JPasswordField passText;
	
	public EditSessionPanel() {
		initialize();
	}
	
	private void initialize() {
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);

		JLabel nameLabel = new JLabel("Name:", JLabel.TRAILING);
		this.add(nameLabel);
		nameText = new JTextField(20);
		nameLabel.setLabelFor(nameText);
		this.add(nameText);
		
		JLabel hostLabel = new JLabel("Host:", JLabel.TRAILING);
		this.add(hostLabel);
		hostText = new JTextField(20);
		hostLabel.setLabelFor(hostText);
		this.add(hostText);

		JLabel userLabel = new JLabel("Username:", JLabel.TRAILING);
		this.add(userLabel);
		userText = new JTextField(20);
		userLabel.setLabelFor(userText);
		this.add(userText);
		
		JLabel savePassLabel = new JLabel("", JLabel.TRAILING);
		this.add(savePassLabel);
		savePassCheckbox = new JCheckBox("Save Password?");
		this.add(savePassCheckbox);

		savePassCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSavePassword(savePassCheckbox.isSelected());
			}
		});
		
		JLabel passLabel = new JLabel("Password:", JLabel.TRAILING);
		this.add(passLabel);
		passText = new JPasswordField(20);
		passLabel.setLabelFor(passText);
		this.add(passText);

		setSavePassword(false);

		int rows = 5;
		int cols = 2;
		int initX = 6;
		int initY = 6;
		int xPad = 6;
		int yPad = 6;
		SpringUtilities.makeCompactGrid(this, rows, cols, initX, initY, xPad, yPad);
	}

	private void setSavePassword(boolean savePassword) {
		savePassCheckbox.setSelected(savePassword);
		passText.setEnabled(savePassword);
		passText.setEditable(savePassword);
		if (!savePassword) {
			passText.setText(null);
		}
	}
	
	public String getSessionName() {
		return nameText.getText();
	}
	
	public String getHostname() {
		return hostText.getText();
	}

	public String getUsername() {
		return userText.getText();
	}

	public String getPassword() {
		String password = null;
		char[] passwordChars = passText.getPassword();
		if (passwordChars.length > 0) {
			password = new String(passwordChars); 
		}
		return password;
	}

	public void setSessionName(String sessionName) {
		nameText.setText(sessionName);
	}
	
	public void setHostname(String hostname) {
		hostText.setText(hostname);
	}

	public void setUsername(String username) {
		userText.setText(username);
	}

	public void setPassword(String password) {
		passText.setText(password);
		setSavePassword(password != null);
	}

}
