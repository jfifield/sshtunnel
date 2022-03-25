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

/**
 * 
 * @author <a href="agungm@outlook.com">Mulya Agung</a>
 */

public class SessionConnectionMonitor implements Runnable {

	private static final Log log = LogFactory.getLog(SessionConnectionMonitor.class);

	private static final int DEF_MONITOR_INTERVAL = 5000;
	private static final SessionConnectionMonitor INSTANCE = new SessionConnectionMonitor();

	private Thread thread;
	private Boolean threadStopped;
	private int monitorInterval;
	private Map<String, Session> sessions;
	private SshTunnelComposite sshTunnelComposite;

	public SessionConnectionMonitor() {
		this(DEF_MONITOR_INTERVAL);
	}

	public SessionConnectionMonitor(int monitorInterval) {
		sessions = new ConcurrentHashMap<String, Session>();
		threadStopped = false;
		this.monitorInterval = monitorInterval;
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
		// synchronized (this) {
		while (!threadStopped) {
			boolean anyRemoved = false;
			Iterator<Entry<String, Session>> it = sessions.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Session> entry = it.next();
				if (!ConnectionManager.getInstance().isConnected(entry.getValue())) {
					ConnectionManager.getInstance().disconnect(entry.getValue());
					if (log.isWarnEnabled()) {
						log.warn("Session " + entry.getKey() + " has disconnected.");
					}
					if (sshTunnelComposite != null) {
						final Session s = entry.getValue();
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								sshTunnelComposite.showDisconnectedMessage(s);
							}
						});
					}
					it.remove();
					if (!anyRemoved)
						anyRemoved = true;
					
				}
			}
			if (sshTunnelComposite != null && anyRemoved) {
				// sshTunnelComposite.disconnect(entry.getValue());
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						sshTunnelComposite.connectionStatusChanged();
					}
				});
			}
			try {
				Thread.sleep(monitorInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// notifyAll();
		// }
	}
	
	public void setSshTunnelComposite(SshTunnelComposite sshTunnelComposite) {
		this.sshTunnelComposite = sshTunnelComposite;
	}

	public void startMonitor() {
		synchronized (threadStopped) {
			if (thread == null) {
				thread = new Thread(this);
				threadStopped = false;
				thread.start();
			}
		}
	}

	public void setThreadStopped(boolean stop) {
		threadStopped = stop;
	}

	public void stopMonitor() {
		synchronized (threadStopped) {
			if (thread != null) {
				threadStopped = true;
				thread = null;
			}
		}
	}

	public static SessionConnectionMonitor getInstance() {
		return INSTANCE;
	}

}
