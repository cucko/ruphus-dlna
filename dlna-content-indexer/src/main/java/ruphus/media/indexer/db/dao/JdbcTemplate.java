package ruphus.media.indexer.db.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import ruphus.media.indexer.Configuration;

import com.mysql.jdbc.Driver;

public abstract class JdbcTemplate {
	
	private static final Logger log = Logger.getLogger(JdbcTemplate.class.getName());
	private static Connection connection;
	
	protected static synchronized Connection getConnection() throws Exception {
		if (connection == null || connection.isClosed()) {
			long startTime = System.currentTimeMillis();
			Class.forName(Driver.class.getName());
			
			Configuration conf = Configuration.getInstance();
			
			connection = DriverManager.getConnection(
				conf.getDbUrl(), conf.getDbUser(), conf.getDbPassword()
			);
			
			log.finer("Obtained db connection in "+(System.currentTimeMillis() - startTime)+" ms");
		}
		
		return connection;
	}
	
	private static synchronized void closeConnection() throws SQLException {
		if (connection != null) connection.close();
	}
	
	private static void closeStatement(Statement stmt) throws SQLException {
		if (stmt != null) stmt.close();
	}
	
	protected static void closeSession(ResultSet rs) throws SQLException {
		if (rs != null) {
			closeStatement(rs.getStatement());
			rs.close();
		}
	}
	
	private static PreparedStatement prepareStatement(String sql, Object... values) throws SQLException, Exception {
		PreparedStatement pstmt = getConnection().prepareStatement(sql);
		
		for (int i=1; i<=values.length; i++) {
			Object value = values[i-1];
			if (value instanceof String) pstmt.setString(i, ((String) value));
			else if (value instanceof Long) pstmt.setLong(i, (Long) value);
			else if (value instanceof Integer) pstmt.setInt(i, (Integer) value);
			else if (value instanceof Date) pstmt.setTimestamp(i, new Timestamp(((Date) value).getTime()));
		}
		
		return pstmt;
	}
	
	protected static void insert(String table, LinkedHashMap<String, Object> columnValues) throws Exception {
		StringBuffer columnList = new StringBuffer();
		StringBuffer valuesPHList = new StringBuffer();
		for (String column : columnValues.keySet()){
			columnList.append(column).append(",");
			valuesPHList.append("?,");
		}
		
		columnList.deleteCharAt( columnList.lastIndexOf(",") );
		valuesPHList.deleteCharAt( valuesPHList.lastIndexOf(",") );
		
		String sql = new StringBuffer("INSERT INTO ").append(table)
			.append(" (")
			.append(columnList)
			.append(") VALUES (")
			.append(valuesPHList)
			.append(")")
			.toString()
		;
		
		PreparedStatement pstmt = null;
		try {
			pstmt = prepareStatement(sql, columnValues.values().toArray());
			pstmt.executeUpdate();
		}
		finally {
			closeStatement(pstmt);
		}
	}
	
	protected static void execute(String sql, Object... values) throws Exception {
		PreparedStatement pstmt = null;
		try {
			pstmt = prepareStatement(sql, values);
			pstmt.executeUpdate();
		}
		finally {
			closeStatement(pstmt);
		}
	}

	protected static void update(String table, LinkedHashMap<String, Object> columnValues) throws Exception {
		StringBuffer fieldList = new StringBuffer();
		for (String column : columnValues.keySet()){
			fieldList.append(" ").append(column).append(" = ?,");
		}
		
		fieldList.deleteCharAt( fieldList.lastIndexOf(",") );
		
		String sql = new StringBuffer("UPDATE ").append(table)
			.append(" SET")
			.append(fieldList)
			.append(" WHERE id = '").append( columnValues.get("id") ).append("'")
			.toString()
		;
		
		PreparedStatement pstmt = null;
		try {
			pstmt = prepareStatement(sql, columnValues.values().toArray());
			pstmt.executeUpdate();
		}
		finally {
			closeStatement(pstmt);
		}
	}

	protected static ResultSet query(String sql, Object... values) throws Exception {
		ResultSet rs = null;
		
		if (values != null) rs = prepareStatement(sql, values).executeQuery();
		else rs = getConnection().createStatement().executeQuery(sql);
		
		return rs;
	}
	
	@Override
	public void finalize() throws Throwable {
		closeConnection();
		connection = null;
	}
	
}
