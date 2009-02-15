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

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.jcraft.jsch.UserInfo;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class DefaultUserInfo implements UserInfo {

	private static final int MAX_ATTEMPTS = 3;

	private String password;
	private boolean savedPassword;
	private int attempt = 0;

	public DefaultUserInfo() {
	}

	public DefaultUserInfo(String password) {
		this.password = password;
		this.savedPassword = true;
	}

	public boolean promptPassword(String message) {
		attempt++;
		if (attempt > MAX_ATTEMPTS) {
			return false;
		} else if (savedPassword && attempt == 1) {
			return true;
		} else {
			// This does not set focus to the password field; unable to find a work-around (without writing a custom dialog)
			JPasswordField passwordField = new JPasswordField(20);
			int result = JOptionPane.showConfirmDialog(null, passwordField, message, JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				password = new String(passwordField.getPassword());
				return true;
			} else {
				return false;
			}
		}
	}

	public String getPassword() {
		return password;
	}

	public boolean promptPassphrase(String message) {
		return true;
	}

	public String getPassphrase() {
		return null;
	}

	public boolean promptYesNo(String str) {
		int result = JOptionPane.showOptionDialog(null, str, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
		return result == JOptionPane.OK_OPTION;
	}

	public void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message);
	}

}