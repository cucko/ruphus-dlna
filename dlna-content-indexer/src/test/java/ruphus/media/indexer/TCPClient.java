package ruphus.media.indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {

	public static void main(String[] args) throws IOException {
		String request = null;   
		String response;   
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in)); 
		
		
		while ( !"\n".equals(request = inFromUser.readLine()) ) {
			Socket socket = new Socket("localhost", 4444);   
			PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));   
			outToServer.println(request);
			response = inFromServer.readLine();
			System.out.println("FROM SERVER: " + response); 
			socket.close();
		}
	}
}
