package com.ki.mavenDAO.DAOmaven;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

public class UserDaoTest {
	
	private Connection conn;
	
	@Before
	public void setUp() throws SQLException {
		var props = Profile.getProperties("db");
		
		var db = Database.instance();
		
		Database.instance().connect(props);
		
		db.connect(props);
		
		conn = db.getConnection();
	}
	
	@After
	public void tearDown() throws SQLException {
		Database.instance().close();
	}

	@Test
	public void testSave() {
		assertTrue("Hello", true);
	}
}