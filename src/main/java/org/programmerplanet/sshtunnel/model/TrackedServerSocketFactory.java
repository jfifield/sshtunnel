package org.programmerplanet.sshtunnel.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jcraft.jsch.ServerSocketFactory;

public class TrackedServerSocketFactory implements ServerSocketFactory {
	private Map<String, ServerSocket> socketMap;
	
	public TrackedServerSocketFactory() {
		this.socketMap = new HashMap<>();
	}

	@Override
	public ServerSocket createServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException {
		//ServerSocket socket = new ServerSocket(port, backlog, bindAddr);	
		ServerSocket serverSocket = new CustomServerSocket(port, backlog, bindAddr);
		String key = bindAddr.toString() + ":" + Integer.toString(port);
		socketMap.put(key, serverSocket);
		return serverSocket;
	}

	public void closeSocket(String addr, int port) {
		try {
			InetAddress bindAddr = InetAddress.getByName(normalize(addr));
			
			String key = bindAddr.toString() + ":" + Integer.toString(port);
			ServerSocket socket = socketMap.get(key);
			if (socket != null) {
				CustomServerSocket customSocket = (CustomServerSocket) socket;
				customSocket.closeTunnelSocket(bindAddr.toString(), port);
				try {
					customSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					socketMap.remove(key);
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static String normalize(String address){
	    if(address!=null){
	      if(address.length()==0 || address.equals("*"))
	        address="0.0.0.0";
	      else if(address.equals("localhost"))
	        address="127.0.0.1";
	    }
	    return address;
	}

}

class CustomServerSocket extends ServerSocket {

	private Map<String, List<Socket>> tunnelSockets;
	
	public CustomServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException {
		super(port, backlog, bindAddr);
		this.tunnelSockets = new ConcurrentHashMap<>();
	}

	@Override
	public Socket accept() throws IOException {
		Socket socket = super.accept();
		String key = socket.getInetAddress().toString() + ":" + socket.getLocalPort();
		
		if (!tunnelSockets.containsKey(key))
			tunnelSockets.put(key, new ArrayList<>());
		tunnelSockets.get(key).add(socket);
		
		return socket;
	}
	
	public void closeTunnelSocket(String addr, int port) {
		String key = addr + ":" + Integer.toString(port);
		List<Socket> sockets = tunnelSockets.get(key);
		if ((sockets != null) && (!sockets.isEmpty())) {
			for (Socket socket: sockets) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			tunnelSockets.remove(key);
		}
	}

	
}
