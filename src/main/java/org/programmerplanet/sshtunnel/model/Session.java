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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a session to an ssh host.
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 * @author <a href="agungm@outlook.com">Mulya Agung</a>
 */
public class Session implements Comparable<Session> {

	private static final int DEFAULT_PORT = 22;

	private String sessionName;
	private String hostname;
	private int port = DEFAULT_PORT;
	private String username;
	private String password;
	private List<Tunnel> tunnels = new ArrayList<Tunnel>();
	private String identityPath;
	private String passPhrase;

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String name) {
		this.sessionName = name;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Tunnel> getTunnels() {
		return tunnels;
	}

	public String toString() {
		return "Session (" + sessionName + ": " + username + "@" + hostname + (port != DEFAULT_PORT ? ":" + port : "") + ")";
	}

	public int compareTo(Session other) {
		return sessionName.compareTo(other.sessionName);
	}

	public String getIdentityPath() {
		return identityPath;
	}

	public void setIdentityPath(String identityPath) {
		this.identityPath = identityPath;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}
	
}
