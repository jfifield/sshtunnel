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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sshtools.common.authentication.PasswordAuthenticationDialog;
import com.sshtools.common.hosts.DialogKnownHostsKeyVerification;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.SshAuthenticationClient;
import com.sshtools.j2ssh.forwarding.ForwardingClient;
import com.sshtools.j2ssh.transport.HostKeyVerification;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class ConnectionManager {

	private Map<Session, SshClient> connections = new HashMap<Session, SshClient>();

	private static final ConnectionManager INSTANCE = new ConnectionManager();

	public static ConnectionManager getInstance() {
		return INSTANCE;
	}

	private ConnectionManager() {
	}

	public void connect(Session session, Frame parent) throws IOException {
		HostKeyVerification hostKeyVerification = new DialogKnownHostsKeyVerification(parent);

		PasswordAuthenticationClient authenticationClient = new PasswordAuthenticationClient();
		authenticationClient.setUsername(session.getUsername());
		if (session.getPassword() != null && session.getPassword().trim().length() > 0) {
			authenticationClient.setPassword(session.getPassword());
		} else {
			PasswordAuthenticationDialog passwordAuthenticationDialog = new PasswordAuthenticationDialog(parent);
			authenticationClient.setAuthenticationPrompt(passwordAuthenticationDialog);
		}

		connect(session, hostKeyVerification, authenticationClient);
	}

	private void connect(Session session, HostKeyVerification hostKeyVerification, SshAuthenticationClient authenticationClient) throws IOException {
		SshClient sshClient = connections.get(session);
		if (sshClient == null) {
			sshClient = new SshClient();
		}
		clearTunnelExceptions(session);
		try {
			sshClient.connect(session.getHostname(), hostKeyVerification);

			int result = sshClient.authenticate(authenticationClient);

			if (result == AuthenticationProtocolState.COMPLETE) {
				startTunnels(session, sshClient);
			} else if (result == AuthenticationProtocolState.FAILED) {
				throw new IOException("The authentication failed");
			} else if (result == AuthenticationProtocolState.PARTIAL) {
				throw new IOException("The authentication succeeded but another authentication is required");
			} else if (result == AuthenticationProtocolState.CANCELLED) {
				throw new IOException("The authentication was cancelled by the user");
			} else {
				throw new IOException("Unknown authentication error");
			}
		} catch (IOException e) {
			sshClient.disconnect();
			sshClient = null;
			throw e;
		}
		connections.put(session, sshClient);
	}

	private void startTunnels(Session session, SshClient sshClient) {
		ForwardingClient forwardingClient = sshClient.getForwardingClient();
		for (Iterator i = session.getTunnels().iterator(); i.hasNext();) {
			Tunnel tunnel = (Tunnel) i.next();
			try {
				startTunnel(forwardingClient, tunnel);
			} catch (Exception e) {
				tunnel.setException(e);
			}
		}
	}

	private void startTunnel(ForwardingClient forwardingClient, Tunnel tunnel) throws IOException {
		if (tunnel.getLocal()) {
			forwardingClient.addLocalForwarding(tunnel.getTunnelName(), tunnel.getLocalAddress(), tunnel.getLocalPort(), tunnel.getRemoteAddress(), tunnel.getRemotePort());
			forwardingClient.startLocalForwarding(tunnel.getTunnelName());
		} else {
			forwardingClient.addRemoteForwarding(tunnel.getTunnelName(), tunnel.getRemoteAddress(), tunnel.getRemotePort(), tunnel.getLocalAddress(), tunnel.getLocalPort());
			forwardingClient.startRemoteForwarding(tunnel.getTunnelName());
		}
	}

	public void disconnect(Session session) {
		clearTunnelExceptions(session);
		SshClient sshClient = connections.get(session);
		if (sshClient != null) {
			stopTunnels(sshClient);
			sshClient.disconnect();
		}
		connections.remove(session);
	}

	private void stopTunnels(SshClient sshClient) {
		ForwardingClient forwardingClient = sshClient.getForwardingClient();

		Map localForwardings = forwardingClient.getLocalForwardings();
		List localForwardNames = new ArrayList(localForwardings.keySet());
		stopLocalTunnels(forwardingClient, localForwardNames);

		Map remoteForwardings = forwardingClient.getRemoteForwardings();
		List remoteForwardNames = new ArrayList(remoteForwardings.keySet());
		stopRemoteTunnels(forwardingClient, remoteForwardNames);
	}

	private void stopLocalTunnels(ForwardingClient forwardingClient, Collection names) {
		for (Iterator i = names.iterator(); i.hasNext();) {
			String name = (String) i.next();
			try {
				stopLocalTunnel(forwardingClient, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void stopLocalTunnel(ForwardingClient forwardingClient, String name) throws IOException {
		forwardingClient.stopLocalForwarding(name);
		forwardingClient.removeLocalForwarding(name);
	}

	private void stopRemoteTunnels(ForwardingClient forwardingClient, Collection names) {
		for (Iterator i = names.iterator(); i.hasNext();) {
			String name = (String) i.next();
			try {
				stopRemoteTunnel(forwardingClient, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void stopRemoteTunnel(ForwardingClient forwardingClient, String name) throws IOException {
		forwardingClient.stopRemoteForwarding(name);
		forwardingClient.removeRemoteForwarding(name);
	}

	private void clearTunnelExceptions(Session session) {
		for (Iterator i = session.getTunnels().iterator(); i.hasNext();) {
			Tunnel tunnel = (Tunnel) i.next();
			tunnel.setException(null);
		}
	}

	public boolean isConnected(Session session) {
		SshClient sshClient = connections.get(session);
		return sshClient != null && sshClient.isConnected() && sshClient.isAuthenticated();
	}

}
