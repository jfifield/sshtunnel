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

/**
 * Represents a tunnel (port forward) over an ssh connection.
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class Tunnel implements Comparable<Tunnel> {

	private String localAddress;
	private int localPort;
	private String remoteAddress;
	private int remotePort;
	private boolean local = true;

	private transient Exception exception;

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

	public String toString() {
		String localName = getLocalAddress() + ":" + getLocalPort();
		String direction = getLocal() ? "->" : "<-";
		String remoteName = getRemoteAddress() + ":" + getRemotePort();
		return "Tunnel (" + localName + direction + remoteName + ")";
	}

	public int compareTo(Tunnel other) {
		int i = localAddress.compareTo(other.localAddress);
		if (i == 0) {
			i = Integer.valueOf(localPort).compareTo(Integer.valueOf(other.localPort));
		}
		return i;
	}

}
