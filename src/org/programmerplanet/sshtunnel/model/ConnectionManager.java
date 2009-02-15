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

import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public abstract class ConnectionManager {

	private static final Log log = LogFactory.getLog(ConnectionManager.class);

	private static final ConnectionManager INSTANCE = createConnectionManager();

	private static ConnectionManager createConnectionManager() {
		ConnectionManager connectionManager = null;
		try {
			Properties properties = getProperties();
			String connectionManagerClassName = properties.getProperty("connectionManager");
			Class connectionManagerClass = Class.forName(connectionManagerClassName);
			connectionManager = (ConnectionManager) connectionManagerClass.newInstance();
		} catch (Exception e) {
			log.error("Error creating configured connection manager; using default.", e);
			connectionManager = new JSchConnectionManager();
		}
		log.info("Using ConnectionManager: " + connectionManager);
		return connectionManager;
	}

	private static Properties getProperties() throws IOException {
		Properties properties = new Properties();
		InputStream inputStream = ConnectionManager.class.getResourceAsStream("/sshtunnel.properties");
		try {
			properties.load(inputStream);
		} finally {
			inputStream.close();
		}
		return properties;
	}

	public static ConnectionManager getInstance() {
		return INSTANCE;
	}

	public abstract void connect(Session session, Frame parent) throws IOException;

	public abstract void disconnect(Session session);

	public abstract boolean isConnected(Session session);

}
