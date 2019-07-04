package org.programmerplanet.sshtunnel.ui;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.programmerplanet.sshtunnel.model.ConnectionManager;
import org.programmerplanet.sshtunnel.model.Session;

public class SessionConnectionMonitor implements Runnable {

	private static final Log log = LogFactory.getLog(SessionConnectionMonitor.class);
	
	private static final int SLEEP_INTERVAL = 5000;
	private static final SessionConnectionMonitor INSTANCE = new SessionConnectionMonitor();
	
	private Thread thread;
	private boolean threadStopped;
	private Map<String, Session> sessions;
	private SshTunnelComposite sshTunnelComposite;
	
	public SessionConnectionMonitor() {
		sessions = new ConcurrentHashMap<String, Session>();
		threadStopped = false;
	}
	
	public void addSession(String name, Session session) {
		sessions.put(name, session);
	}
	
	public void removeSession(String name) {
		sessions.remove(name);
	}
	
	public void run() {
		if (log.isWarnEnabled()) {
			log.warn("Connection monitor is now running..");
		}
		synchronized (this) {
			while (!threadStopped) {
				Iterator<Entry<String, Session>> it = sessions.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Session> entry = it.next();
					if (!ConnectionManager.getInstance().isConnected(
							entry.getValue())) {
						ConnectionManager.getInstance().disconnect(entry.getValue());
						if (log.isWarnEnabled()) {
							log.warn("Session " + entry.getKey() + " has disconnected.");
						}
						it.remove();
					}
					if (sshTunnelComposite != null) {
						//sshTunnelComposite.disconnect(entry.getValue());
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								  sshTunnelComposite.connectionStatusChanged();
				               }
				        });
					}
				}
				try {
					Thread.sleep(SLEEP_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//notifyAll();
		}
	}
	
	public synchronized void startMonitor(SshTunnelComposite sshTunnelComposite) {
		if (thread == null) {
			this.sshTunnelComposite = sshTunnelComposite;
			threadStopped = false;
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void setThreadStopped(boolean stop) {
		threadStopped = stop;
	}
	
	public void stopMonitor() {
		if (thread != null) {
			threadStopped = true;
//			synchronized (this) {
//				try {
//					wait(5000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				thread = null;
//			}
			thread = null;
		}
	}
	
	public static SessionConnectionMonitor getInstance() {
		return INSTANCE;
	}

}
