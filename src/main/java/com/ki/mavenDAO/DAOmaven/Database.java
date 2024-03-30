package com.ki.mavenDAO.DAOmaven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

	private static Database db = new Database();
	private static final String dbUrl = "jdbc:mysql://localhost:3306/people";
	private Connection conn;
	
	public static Database instance() {
		return db;
	}
	private Database() {
		
	}
	
	public void connect() throws SQLException {
		conn = DriverManager.getConnection(dbUrl, "root", "22Open,ses");
	}
	
	public void close() throws SQLException {
		conn.close();
	}
}