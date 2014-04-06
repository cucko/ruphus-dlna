package ruphus.media.indexer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TCPListener extends Thread{
	
	private final static Logger log = Logger.getLogger(Main.class.getName());
	
	private ServerSocket serverSocket;
	private Socket socket;
	private boolean running;
	
	public TCPListener(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		running = true;
	}
	
	public void exit() {
		running = false;
	}
	
	public void finalize() {
		log.finer("TCP Test Server stopping");
		try {
			if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
		}
		catch (Exception e) { /**/ }
		finally {
			log.finer("TCP Test Server stopped");
		}
	}
	
	abstract public String serveRequest(String request);
	
	public void run() {
		log.finer("TCP Test Server starting");
		try {
			socket = serverSocket.accept();
			
			DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			while (running) {
				log.finer("TCP Server receiving");
				
				String request = fromClient.readLine();
				String response = serveRequest(request);
				
				if (response != null) {
					log.finer("TCP Server sending");
					toClient.writeBytes(response+"\n");
				}
				
				Thread.sleep(1000);
			}
		}
		catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			exit();
		}
	}
	
}
