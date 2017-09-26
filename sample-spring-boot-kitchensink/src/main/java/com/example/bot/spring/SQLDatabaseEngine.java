package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		//Write your code here
		String result = null;
		Integer i = 0;
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM dbquery");
			ResultSet rs = stmt.executeQuery();
			while (result == null && rs.next()) {
				String[] parts = {rs.getString(1),rs.getString(2)};
				if (text.toLowerCase().contains(parts[0].toLowerCase())) {
					i = rs.getInt(3);
					result = parts[1];
				}
			}
			rs.close();
			stmt.close();
			try {
				stmt = conn.prepareStatement("UPDATE dbquery SET count = ? WHERE response = ?");
				stmt.setInt(1, i+1);
				stmt.setString(2, result);
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
			stmt.close();
			result += i;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				conn.close();
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		if (result != null) {
			
			return result;
		}
		throw new Exception("NOT FOUND");
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
