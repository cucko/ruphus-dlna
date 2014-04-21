package ruphus.media.indexer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClient {

	public static void main(String[] args) throws IOException {
		String request;   
		String response;   
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in)); 
		
		
		while (true) {
			Socket clientSocket = new Socket("localhost", 4444);   
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
			request = inFromUser.readLine();   
			outToServer.writeBytes(request + '\n');
			response = inFromServer.readLine();   
			System.out.println("FROM SERVER: " + response);  
			clientSocket.close();
		}
	}
}
