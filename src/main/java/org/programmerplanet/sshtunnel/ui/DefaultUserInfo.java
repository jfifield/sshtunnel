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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.jcraft.jsch.UserInfo;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class DefaultUserInfo implements UserInfo {

	private static final int MAX_ATTEMPTS = 3;

	private Shell parent;
	private String password;
	private boolean savedPassword;
	private int attempt = 0;

	public DefaultUserInfo(Shell parent) {
		this(parent, null);
	}

	public DefaultUserInfo(Shell parent, String password) {
		this.parent = parent;
		this.password = password;
		this.savedPassword = (password != null);
	}
	
	public boolean promptPassword(String message) {
		attempt++;
		if (attempt > MAX_ATTEMPTS) {
			return false;
		} else if (savedPassword && attempt == 1) {
			return true;
		} else {
			PromptRunnable promptDialog = new PromptRunnable() {
                @Override
                public void run() {
					PasswordDialog dialog = new PasswordDialog(parent);
					dialog.setMessage(message);
					int result = dialog.open();
					if (result == SWT.OK) {
						password = dialog.getPassword();
						//return true;
						ret = true;
					}
//					} else {
//						//return false;
//						ret = false;
//					}
                }
			};
			Display.getDefault().syncExec(promptDialog);
			return promptDialog.getReturnState();
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
//		MessageBox messageBox = new MessageBox(parent, SWT.ICON_WARNING | SWT.YES | SWT.NO);
//		messageBox.setText("Warning");
//		messageBox.setMessage(str);
//		int result = messageBox.open();
//		return result == SWT.YES;
		PromptRunnable promptDialog = new PromptRunnable() {
            @Override
            public void run() {
            	MessageBox messageBox = new MessageBox(parent, SWT.ICON_WARNING | SWT.YES | SWT.NO);
        		messageBox.setText("Warning");
        		messageBox.setMessage(str);
        		int result = messageBox.open();
        		ret = (result == SWT.YES);
            }
		};
		Display.getDefault().syncExec(promptDialog);
		return promptDialog.getReturnState();
		
	}

	public void showMessage(String message) {
//		MessageBox messageBox = new MessageBox(parent, SWT.ICON_INFORMATION | SWT.OK);
//		messageBox.setText("Message");
//		messageBox.setMessage(message);
//		messageBox.open();
		
		PromptRunnable promptDialog = new PromptRunnable() {
            @Override
            public void run() {
        		MessageBox messageBox = new MessageBox(parent, SWT.ICON_INFORMATION | SWT.OK);
        		messageBox.setText("Message");
        		messageBox.setMessage(message);
        		messageBox.open();
            }
		};
		Display.getDefault().syncExec(promptDialog);
	}
	
	class PromptRunnable implements Runnable {
		boolean ret = false;
		
		public boolean getReturnState(){
			return ret;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
		}
	}

}