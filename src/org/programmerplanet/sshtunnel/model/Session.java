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
package org.programmerplanet.sshtunnel.model;

import java.awt.Frame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sshtools.common.authentication.PasswordAuthenticationDialog;
import com.sshtools.common.hosts.DialogKnownHostsKeyVerification;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.forwarding.ForwardingClient;
import com.sshtools.j2ssh.transport.HostKeyVerification;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class Session {

	private String sessionName;
	private String hostname;
	private String username;
	private String password;
	private List tunnels = new ArrayList();
	private transient SshClient sshClient;

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

	public List getTunnels() {
		return tunnels;
	}

	public void connect(Frame parent) throws IOException {
		if (sshClient == null) {
			sshClient = new SshClient();
		}
		try {
			HostKeyVerification hostKeyVerification = new DialogKnownHostsKeyVerification(parent);
			sshClient.connect(hostname, hostKeyVerification);
			PasswordAuthenticationClient auth = new PasswordAuthenticationClient();
			auth.setUsername(username);
			if (password != null && password.trim().length() > 0) {
				auth.setPassword(password);
			}
			else {
				PasswordAuthenticationDialog passwordAuthenticationDialog = new PasswordAuthenticationDialog(parent);
				auth.setAuthenticationPrompt(passwordAuthenticationDialog);
			}
			
			int result = sshClient.authenticate(auth);

			if (result == AuthenticationProtocolState.FAILED) {
				throw new IOException("The authentication failed");
			} else if (result == AuthenticationProtocolState.PARTIAL) {
				throw new IOException("The authentication succeeded but another authentication is required");
			} else if (result == AuthenticationProtocolState.CANCELLED) {
				throw new IOException("The authentication was cancelled by the user");
			} else if (result == AuthenticationProtocolState.COMPLETE) {
				ForwardingClient forwarding = sshClient.getForwardingClient();
				for (Iterator i = tunnels.iterator(); i.hasNext();) {
					Tunnel tunnel = (Tunnel)i.next();
					tunnel.setException(null);
					try {
						if (tunnel.getLocal()) {
							forwarding.addLocalForwarding(tunnel.getTunnelName(), tunnel.getLocalAddress(), tunnel.getLocalPort(), tunnel.getRemoteAddress(), tunnel.getRemotePort());
							forwarding.startLocalForwarding(tunnel.getTunnelName());
						}
						else {
							forwarding.addRemoteForwarding(tunnel.getTunnelName(), tunnel.getRemoteAddress(), tunnel.getRemotePort(), tunnel.getLocalAddress(), tunnel.getLocalPort());
							forwarding.startRemoteForwarding(tunnel.getTunnelName());
						}
					} catch (Exception e) {
						tunnel.setException(e);
					}
				}
			} else {
				throw new IOException("Unknown authentication error");
			}
		} catch (IOException e) {
			sshClient.disconnect();
			sshClient = null;
			throw e;
		}
	}

	public void disconnect() {
		for (Iterator i = tunnels.iterator(); i.hasNext();) {
			Tunnel tunnel = (Tunnel)i.next();
			tunnel.setException(null);
		}
		ForwardingClient forwarding = sshClient.getForwardingClient();

		Map localForwardings = forwarding.getLocalForwardings();
		List localForwardNames = new ArrayList(localForwardings.keySet());
		for (Iterator i = localForwardNames.iterator(); i.hasNext();) {
			String name = (String)i.next();
			try {
				forwarding.stopLocalForwarding(name);
				forwarding.removeLocalForwarding(name);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		Map remoteForwardings = forwarding.getRemoteForwardings();
		List remoteForwardNames = new ArrayList(remoteForwardings.keySet());
		for (Iterator i = remoteForwardNames.iterator(); i.hasNext();) {
			String name = (String)i.next();
			try {
				forwarding.stopRemoteForwarding(name);
				forwarding.removeRemoteForwarding(name);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		sshClient.disconnect();
		sshClient = null;
	}

	public boolean isConnected() {
		return sshClient != null && sshClient.isConnected() && sshClient.isAuthenticated();
	}

}
