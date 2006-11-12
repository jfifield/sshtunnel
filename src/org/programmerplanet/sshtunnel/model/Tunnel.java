package org.programmerplanet.sshtunnel.model;

/**
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class Tunnel {

	private String localAddress;
	private int localPort;
	private String remoteAddress;
	private int remotePort;
	private boolean local = true;
	
	private transient Exception exception;

	public String getTunnelName() {
		String localName = getLocalAddress() + ":" + getLocalPort();
		String direction = getLocal() ? "->" : "<-";
		String remoteName = getRemoteAddress() + ":" + getRemotePort();
		return localName + direction + remoteName;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public boolean getLocal() {
		return local;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Exception getException() {
		return exception;
	}

}
