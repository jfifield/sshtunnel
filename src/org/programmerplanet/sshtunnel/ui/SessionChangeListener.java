package org.programmerplanet.sshtunnel.ui;

import org.programmerplanet.sshtunnel.model.Session;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public interface SessionChangeListener {

	public void sessionAdded(Session session);
	
	public void sessionChanged(Session session);

	public void sessionRemoved(Session session);

	public void sessionSelectionChanged(Session session);

}
