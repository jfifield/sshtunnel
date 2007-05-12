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
package org.programmerplanet.sshtunnel;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.programmerplanet.sshtunnel.ui.SshTunnelFrame;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class Main {

	public static void main(String[] args) throws Exception {
		addWorkingDirectoryToLibraryPath();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGui();
			}
		});
	}

	private static void addWorkingDirectoryToLibraryPath() {
		final String LIBRARY_PATH_KEY = "java.library.path";
		File workingDirectory = new File(".");
		String path = workingDirectory.getAbsolutePath();
		String libraryPath = System.getProperty(LIBRARY_PATH_KEY);
		String pathSeparator = System.getProperty("path.separator");
		libraryPath += pathSeparator + path;
		System.setProperty(LIBRARY_PATH_KEY, libraryPath);
	}

	private static void createAndShowGui() {
		if (System.getProperty("plastic.defaulttheme") != null) {
			try {
				String themeClassName = System.getProperty("plastic.defaulttheme");
				Class themeClass = Class.forName(themeClassName);
				PlasticTheme theme = (PlasticTheme) themeClass.newInstance();
				PlasticLookAndFeel.setMyCurrentTheme(theme);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (System.getProperty("swing.defaultlaf") == null) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JFrame frame = new SshTunnelFrame();
		frame.pack();
		frame.setVisible(true);
	}

}
