/*
 * Copyright 2009 Joseph Fifield
 * Copyright 2022 Mulya Agung
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;
import org.programmerplanet.sshtunnel.ui.DefaultUserInfo;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;

/**
 * Responsible for connecting and disconnecting ssh connections and the
 * underlying tunnels.
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 * @author <a href="agungm@outlook.com">Mulya Agung</a>
 */
public class ConnectionManager {

	private static final Log log = LogFactory.getLog(ConnectionManager.class);

	private static final int TIMEOUT = 20000;
	private static final int KEEP_ALIVE_INTERVAL = 40000;
	private static final String DEF_CIPHERS = "aes128-gcm@openssh.com,chacha20-poly1305@openssh.com,aes128-cbc,aes128-ctr";

	private static final ConnectionManager INSTANCE = new ConnectionManager();
	
	public static ConnectionManager getInstance() {
		return INSTANCE;
	}
	
	enum TunnelUpdateState {
		START,
		STOP,
		CHANGE
	}
	
//	public ConnectionManager() {
//		//JSch.setLogger(new MyLogger(log));
//		JSch.setLogger(new MyLogger());
//	}

	private TrackedServerSocketFactory serverSocketFactory = new TrackedServerSocketFactory();
	
	private Map<Session, com.jcraft.jsch.Session> connections = new HashMap<Session, com.jcraft.jsch.Session>();

	public void connect(Session session, Shell parent) throws ConnectionException {
		log.info("Connecting session: " + session);
		clearTunnelExceptions(session);
		com.jcraft.jsch.Session jschSession = connections.get(session);
		try {
			if (jschSession == null) {
				JSch jsch = new JSch();
				File knownHosts = getKnownHostsFile();
				jsch.setKnownHosts(knownHosts.getAbsolutePath());
				
				if (session.getIdentityPath() != null && session.getIdentityPath().trim().length() > 0) {
					try {
						if (session.getPassPhrase() != null && session.getPassPhrase().trim().length() > 0) {
							jsch.addIdentity(session.getIdentityPath(), session.getPassPhrase());
						} else {
							jsch.addIdentity(session.getIdentityPath());
						}
					} catch (JSchException e) {
						e.printStackTrace();
						// Jsch does not support newer format, you may convert the key to the pem format:
						// ssh-keygen -p -f key_file -m pem -P passphrase -N passphrase
						//log.error("Invalid private key: " + session.getIdentityPath(), e);
						throw new ConnectionException(e);
					}
				}
				jschSession = jsch.getSession(session.getUsername(), session.getHostname(), session.getPort());
			}
			UserInfo userInfo = null;
			if (session.getPassword() != null && session.getPassword().trim().length() > 0) {
				userInfo = new DefaultUserInfo(parent, session.getPassword());
			} else {
				userInfo = new DefaultUserInfo(parent);
			}
			
			jschSession.setUserInfo(userInfo);
			jschSession.setServerAliveInterval(KEEP_ALIVE_INTERVAL);
			jschSession.setServerAliveCountMax(2);
			
			if (session.getCiphers() != null && !session.getCiphers().isEmpty()) {
				// Set ciphers to use aes128-gcm if possible, as it is fast on many systems
				jschSession.setConfig("cipher.s2c", session.getCiphers() + "," + DEF_CIPHERS);
				jschSession.setConfig("cipher.c2s", session.getCiphers() + "," + DEF_CIPHERS);
				jschSession.setConfig("CheckCiphers", session.getCiphers());
			}
		    
		    if (session.isCompressed()) {
		    	jschSession.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
		        jschSession.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
		        //jschSession.setConfig("compression_level", "9");
		    }
		    
			jschSession.connect(TIMEOUT);

			startTunnels(session, jschSession);
		} catch (JSchException e) {
			jschSession.disconnect();
			jschSession = null;
			throw new ConnectionException(e);
		}
		connections.put(session, jschSession);
	}

	private File getKnownHostsFile() {
		String userHome = System.getProperty("user.home");
		File f = new File(userHome);
		f = new File(f, ".ssh");
		f = new File(f, "known_hosts");
		return f;
	}

	private void startTunnels(Session session, com.jcraft.jsch.Session jschSession) {
		for (Iterator<Tunnel> i = session.getTunnels().iterator(); i.hasNext();) {
			Tunnel tunnel = i.next();
			try {
				startTunnel(jschSession, tunnel);
			} catch (Exception e) {
				tunnel.setException(e);
				log.error("Error starting tunnel: " + tunnel, e);
			}
		}
	}

	private void startTunnel(com.jcraft.jsch.Session jschSession, Tunnel tunnel) throws JSchException {
		if (tunnel.getLocal()) {
			//jschSession.setPortForwardingL(tunnel.getLocalAddress(), tunnel.getLocalPort(), tunnel.getRemoteAddress(), tunnel.getRemotePort());
			jschSession.setPortForwardingL(tunnel.getLocalAddress(),
					tunnel.getLocalPort(), tunnel.getRemoteAddress(),
					tunnel.getRemotePort(), serverSocketFactory);
		} else {
			jschSession.setPortForwardingR(tunnel.getRemoteAddress(), tunnel.getRemotePort(), tunnel.getLocalAddress(), tunnel.getLocalPort());
		}
	}
	
	private int updateTunnelIfSessionConnected(Session session, TunnelUpdateState state, Tunnel tunnel, Tunnel prevTunnel) {
		int status = 0;
		com.jcraft.jsch.Session jschSession = connections.get(session);
		if (jschSession != null && jschSession.isConnected()) {
			try {
				switch (state) {
				case START:
					startTunnel(jschSession, tunnel);
					break;
				case STOP:
					stopTunnel(jschSession, tunnel);
					break;
				default:
					stopTunnel(jschSession, prevTunnel);
					startTunnel(jschSession, tunnel);
					break;
				}
			} catch (JSchException e) {
				status = -1;
				e.printStackTrace();
			}
		}
		return status;
	}
	
	public int startTunnelIfSessionConnected(Session session, Tunnel tunnel) {
		return updateTunnelIfSessionConnected(session, TunnelUpdateState.START, tunnel, null);
	}
	
	public int stopTunnelIfSessionConnected(Session session, Tunnel tunnel) {
		return updateTunnelIfSessionConnected(session, TunnelUpdateState.STOP, tunnel, null);
	}
	
	public int changeTunnelIfSessionConnected(Session session, Tunnel tunnel, Tunnel prevTunnel) {
		return updateTunnelIfSessionConnected(session, TunnelUpdateState.CHANGE, tunnel, prevTunnel);
	}

	public void disconnect(Session session) {
		log.info("Disconnecting session: " + session);
		clearTunnelExceptions(session);
		com.jcraft.jsch.Session jschSession = connections.get(session);
		if (jschSession != null) {
			stopTunnels(session, jschSession);
			jschSession.disconnect();
		}
		connections.remove(session);
	}

	private void stopTunnels(Session session, com.jcraft.jsch.Session jschSession) {
		for (Iterator<Tunnel> i = session.getTunnels().iterator(); i.hasNext();) {
			Tunnel tunnel = i.next();
			try {
				stopTunnel(jschSession, tunnel);
			} catch (Exception e) {
				log.error("Error stopping tunnel: " + tunnel, e);
			}
		}
	}

	private void stopTunnel(com.jcraft.jsch.Session jschSession, Tunnel tunnel) throws JSchException {
		if (tunnel.getLocal()) {
			jschSession.delPortForwardingL(tunnel.getLocalAddress(), tunnel.getLocalPort());
			serverSocketFactory.closeSocket(tunnel.getLocalAddress(), tunnel.getLocalPort());
		} else {
			jschSession.delPortForwardingR(tunnel.getRemotePort());
		}
	}

	private void clearTunnelExceptions(Session session) {
		for (Iterator<Tunnel> i = session.getTunnels().iterator(); i.hasNext();) {
			Tunnel tunnel = i.next();
			tunnel.setException(null);
		}
	}

	public boolean isConnected(Session session) {
		com.jcraft.jsch.Session jschSession = connections.get(session);
		return jschSession != null && jschSession.isConnected();
	}
	
	public Exception getSessionException(Session session) {
		// Currently use keepAliveMsg
		//boolean hasError = false;
		Exception err = null;
		com.jcraft.jsch.Session jschSession = connections.get(session);
		if (jschSession != null ) {//&& !jschSession.isConnected()) {
			try {
				ChannelExec testChannel = (ChannelExec) jschSession.openChannel("exec");
				testChannel.setCommand("true");
				testChannel.connect();
				testChannel.disconnect();
				//jschSession.sendKeepAliveMsg();
			} catch (Exception e) {
				err = e;
				//e.printStackTrace();
			}
			//hasError = true;
		}
		return err;
	}
	
}

class MyLogger implements com.jcraft.jsch.Logger {
    static java.util.Hashtable<Integer, String> name = new java.util.Hashtable<Integer, String>();
    
//    private Log logger;
//    
//    public MyLogger(Log logger) {
//		this.logger = logger;
//	}
    
    static{
      name.put(new Integer(DEBUG), "DEBUG: ");
      name.put(new Integer(INFO), "INFO: ");
      name.put(new Integer(WARN), "WARN: ");
      name.put(new Integer(ERROR), "ERROR: ");
      name.put(new Integer(FATAL), "FATAL: ");
    }
    
    public boolean isEnabled(int level){
      return true;
    }
    
    public void log(int level, String message){
      System.err.print(name.get(level));
      System.err.println(message);
//    	switch (level) {
//		case INFO:
//			logger.info(message);
//			break;
//		case WARN:
//			logger.warn(message);
//			break;
//		case ERROR:
//			logger.error(message);
//			break;
//		case FATAL:
//			logger.error(message);
//			break;
//		default:
//			break;
//		}
    }
 }


