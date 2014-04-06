package ruphus.media.indexer;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCExample {
	 
	public static void main(String[] argv) throws Exception {
 
		System.out.println("-------- JDBC Connection Testing ------------");
		
		Class.forName("com.mysql.jdbc.Driver");

		System.out.println("JDBC Driver Registered!");

		Connection connection = DriverManager.getConnection(
//			"jdbc:mysql://localhost:3306/ruphus_dlna_catalog", "root", "password"
			"jdbc:mysql://ruphus.dyndns.org:3306/ruphus_dlna_catalog", "root", "password"
		);
 
		System.out.println(!connection.isClosed());
 
		
	}
	
	
 
}
