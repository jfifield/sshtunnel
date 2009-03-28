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
package org.programmerplanet.sshtunnel.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import org.programmerplanet.sshtunnel.util.EncryptionUtil;

/**
 * Responsible for storing and loading the configuration for the application.
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class Configuration {

	private transient int top = 0;
	private transient int left = 0;
	private transient int width = 500;
	private transient int height = 400;
	private List sessions = new ArrayList();

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List getSessions() {
		return sessions;
	}

	public void write() throws IOException {
		Properties properties = new Properties();

		properties.setProperty("top", Integer.toString(this.top));
		properties.setProperty("left", Integer.toString(this.left));
		properties.setProperty("width", Integer.toString(this.width));
		properties.setProperty("height", Integer.toString(this.height));

		for (ListIterator si = sessions.listIterator(); si.hasNext();) {
			Session session = (Session) si.next();

			String sessionKey = "sessions[" + si.previousIndex() + "]";

			properties.setProperty(sessionKey + ".sessionName", session.getSessionName());
			properties.setProperty(sessionKey + ".hostname", session.getHostname());
			properties.setProperty(sessionKey + ".port", Integer.toString(session.getPort()));
			properties.setProperty(sessionKey + ".username", session.getUsername());
			if (session.getPassword() != null) {
				String keyString = EncryptionUtil.createKeyString();
				String encryptedPassword = EncryptionUtil.encrypt(session.getPassword(), keyString);
				properties.setProperty(sessionKey + ".key", keyString);
				properties.setProperty(sessionKey + ".password", encryptedPassword);
			}

			for (ListIterator ti = session.getTunnels().listIterator(); ti.hasNext();) {
				Tunnel tunnel = (Tunnel) ti.next();

				String tunnelKey = sessionKey + ".tunnels[" + ti.previousIndex() + "]";

				properties.setProperty(tunnelKey + ".localAddress", tunnel.getLocalAddress());
				properties.setProperty(tunnelKey + ".localPort", Integer.toString(tunnel.getLocalPort()));
				properties.setProperty(tunnelKey + ".remoteAddress", tunnel.getRemoteAddress());
				properties.setProperty(tunnelKey + ".remotePort", Integer.toString(tunnel.getRemotePort()));
			}
		}

		storeProperties(properties);
	}

	public void read() throws IOException {
		Properties properties = loadProperties();

		this.top = Integer.parseInt(properties.getProperty("top", Integer.toString(this.top)));
		this.left = Integer.parseInt(properties.getProperty("left", Integer.toString(this.left)));
		this.width = Integer.parseInt(properties.getProperty("width", Integer.toString(this.width)));
		this.height = Integer.parseInt(properties.getProperty("height", Integer.toString(this.height)));

		int sessionIndex = 0;
		boolean moreSessions = true;
		while (moreSessions) {
			String sessionKey = "sessions[" + sessionIndex + "]";

			String sessionName = properties.getProperty(sessionKey + ".sessionName");
			moreSessions = (sessionName != null);
			if (moreSessions) {
				String hostname = properties.getProperty(sessionKey + ".hostname");
				String port = properties.getProperty(sessionKey + ".port");
				String username = properties.getProperty(sessionKey + ".username");

				String keyString = properties.getProperty(sessionKey + ".key");
				String password = properties.getProperty(sessionKey + ".password");
				if (keyString != null && password != null) {
					password = EncryptionUtil.decrypt(password, keyString);
				}

				Session session = new Session();
				session.setSessionName(sessionName);
				session.setHostname(hostname);
				if (port != null && port.length() > 0) {
					session.setPort(Integer.parseInt(port));
				}
				session.setUsername(username);
				session.setPassword(password);

				sessions.add(session);

				int tunnelIndex = 0;
				boolean moreTunnels = true;
				while (moreTunnels) {
					String tunnelKey = sessionKey + ".tunnels[" + tunnelIndex + "]";

					String localAddress = properties.getProperty(tunnelKey + ".localAddress");
					moreTunnels = (localAddress != null);
					if (moreTunnels) {
						String localPort = properties.getProperty(tunnelKey + ".localPort");
						String remoteAddress = properties.getProperty(tunnelKey + ".remoteAddress");
						String remotePort = properties.getProperty(tunnelKey + ".remotePort");

						Tunnel tunnel = new Tunnel();
						tunnel.setLocalAddress(localAddress);
						tunnel.setLocalPort(Integer.parseInt(localPort));
						tunnel.setRemoteAddress(remoteAddress);
						tunnel.setRemotePort(Integer.parseInt(remotePort));

						session.getTunnels().add(tunnel);
					}
					tunnelIndex++;
				}
			}
			sessionIndex++;
		}
	}

	/**
	 * Loads the properties from the user's configuration file.
	 */
	private Properties loadProperties() throws IOException {
		Properties properties = new Properties();

		File configFile = getConfigurationFile();
		if (configFile.exists()) {
			FileInputStream fis = new FileInputStream(configFile);
			try {
				properties.load(fis);
			} finally {
				try {
					fis.close();
				} catch (Exception e) { /* ignore */
				}
			}
		}

		return properties;
	}

	/**
	 * Stores the properties to the user's configuration file.
	 */
	private void storeProperties(Properties properties) throws IOException {
		File configFile = getConfigurationFile();

		FileOutputStream fos = new FileOutputStream(configFile);
		try {
			properties.store(fos, "SSH Tunnel Configuration");
		} finally {
			try {
				fos.close();
			} catch (Exception e) { /* ignore */
			}
		}
	}

	/**
	 * Gets the configuration file (a file named .sshtunnel in the user's
	 * home directory).
	 */
	private File getConfigurationFile() {
		String userDir = System.getProperty("user.home");
		File configFile = new File(userDir, ".sshtunnel");
		return configFile;
	}

}
