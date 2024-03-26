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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Application entry point.
 * 
 * @author <a href="agungm@outlook.com">Mulya Agung</a>
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class Main {

	public static void main(String[] args) {
		Display.setAppName(SshTunnelComposite.APPLICATION_TITLE);
		Display display = new Display();
		try {
			final Shell shell = new Shell(display);
			SshTunnelComposite sshTunnelComposite = new SshTunnelComposite(shell);
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} finally {
			display.dispose();
		}
	}

}
