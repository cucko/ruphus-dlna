package ruphus.media.indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TCPListener extends Thread {
	
	private final static Logger log = Logger.getLogger(Main.class.getName());
	
	private ServerSocket server;
	private boolean running;
	
	public TCPListener(int port) throws IOException {
		server = new ServerSocket(port);
		running = true;
	}
	
	abstract public String serveRequest(String request);
	
	public void exit() {
		running = false;
		
		log.finer("TCP Test Server stopping");
		try {
			if (server != null && !server.isClosed()) server.close();
		}
		catch (Exception e) { /**/ }
		finally {
			log.finer("TCP Test Server stopped");
		}
	}
	
	public void run() {

		while (running) {
			Socket socket = null;
			
			try {
				socket = server.accept();
				
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				String request = fromClient.readLine();
				log.finer("TCP Server receiving request: "+request);
				
				if (request != null) {
					String response = serveRequest(request);

					if (response != null) {
						log.finer("TCP Server sending response "+response);
						PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true);
						toClient.println(response);
					}
				}
			}
			catch (Exception e) {
				log.log(Level.SEVERE, "TCP Server threw error: ", e);
				running = false;
			}
			finally {
				try {
					if (socket != null && !socket.isClosed()) socket.close();
				}
				catch (Exception e) {
					log.log(Level.SEVERE, "TCP Server threw error: ", e);
					running = false;
				}
			}
			
		}
		
		exit();
	}
	
}
