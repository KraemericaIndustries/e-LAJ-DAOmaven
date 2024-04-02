package com.ki.mavenDAO.DAOmaven;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;

public class UserDaoImplTest {
	
	private Connection conn;
	private List<User> users;
	
	private static final int NUM_TEST_USERS = 419;
	
	private List<User> loadUsers() throws IOException {
		
		// @formatter:off
		
		return Files
			.lines(Paths.get("../FiveLetterWords.txt"))
			.map(line -> line.split("[^A-Za-z]"))
			.map(Arrays::asList)
			.flatMap(list -> list.stream())
			.filter(word -> word.length() > 3 && word.length() < 20)
			.map(word -> new User(word))
			.limit(NUM_TEST_USERS)
			.collect(Collectors.toList());
		
		// @formatter:on
		
	}
	
	@Before
	public void setUp() throws SQLException, IOException {
		
		users = loadUsers();
		
//		System.out.println(users);
//		System.out.println(users.size());
		
		var props = Profile.getProperties("db");
		
		var db = Database.instance();
		
		Database.instance().connect(props);
		
		db.connect(props);
		
		conn = db.getConnection();
		conn.setAutoCommit(false);
	}
	
	@After
	public void tearDown() throws SQLException {
		Database.instance().close();
	}
	
	private int getMaxId() throws SQLException {
		
		var stmt = conn.createStatement();
		
		var rs = stmt.executeQuery("select max(id) as id from user");
		
		rs.next();
		
		var id = rs.getInt("id");
		
		stmt.close();
		
		return id;
	}
	
	
	private List<User> getUsersInRange(int minId, int maxId) throws SQLException {
		
		List <User> retrieved = new ArrayList<User>();
		
		var stmt = conn.prepareStatement("select id, name from user where id >= ? and id <= ?");
		
		stmt.setInt(1, minId);
		stmt.setInt(2, maxId);
		
		var rs = stmt.executeQuery();
		
		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			
			var user = new User(id, name);
			
			retrieved.add(user);
		}
		
		stmt.close();
		
		return retrieved;
	}
	
	
	@Test
	public void testFindAndUpdate() throws SQLException {
		var user = users.get(0);
		
		UserDao userDao = new UserDaoImpl();
		
		userDao.save(user);
		
		var maxId = getMaxId();
		
		user.setId(maxId);
		
		var retrievedUserOpt = userDao.findById(maxId);
		
		assertTrue("No user retrieved", retrievedUserOpt.isPresent());
		
		var retrievedUser = retrievedUserOpt.get();
		
		assertEquals("Retrieved user doesn't match saved user", user, retrievedUser);
		
		user.setName("XYZABC");
		
		userDao.update(user);
		
		retrievedUserOpt = userDao.findById(maxId);
		
		assertTrue("No user retrieved", retrievedUserOpt.isPresent());
		
		retrievedUser = retrievedUserOpt.get();
		
		assertEquals("Retrieved user doesn't match updated user", user, retrievedUser);
	}
	
	@Test
	public void testSaveMultiple() throws SQLException {		
		UserDao userDao = new UserDaoImpl();
		
		for(var u : users) {
			userDao.save(u);
		}
		
		var maxId = getMaxId();
		
		for(int i = 0; i < users.size(); i++) {
			int id = (maxId - users.size()) + i + 1;
			
			users.get(i).setId(id);
		}
		
		var retrievedUsers = getUsersInRange((maxId - users.size()) + 1, maxId);
		
		assertEquals("Size of retrieved users not equal to number of test users", retrievedUsers.size(), NUM_TEST_USERS);
		assertEquals("Retrieved users don't match saved users", users, retrievedUsers);
		
//		System.out.println(maxId);
	}
	
	@Test
	public void testGetAll() throws SQLException {		
		UserDao userDao = new UserDaoImpl();
		
		for(var u : users) {
			userDao.save(u);
		}
		
		var maxId = getMaxId();
		
		for(int i = 0; i < users.size(); i++) {
			int id = (maxId - users.size()) + i + 1;
			
			users.get(i).setId(id);
		}
		
		var dbUsers = userDao.getAll();
		dbUsers = dbUsers.subList(dbUsers.size() - users.size(), dbUsers.size());
		
		assertEquals("Size of retrieved users not equal to number of test users", dbUsers.size(), NUM_TEST_USERS);
		assertEquals("Retrieved users don't match saved users", users, dbUsers);
		
//		System.out.println(maxId);
	}

	@Test
	public void testSave() throws SQLException {
		User user = new User("Jupiter");
		
		UserDao userDao = new UserDaoImpl();
		
		userDao.save(user);
		
		var stmt = conn.createStatement();
		
		var rs = stmt.executeQuery("select id, name from user order by id desc");
		
		var result = rs.next();
		
		assertTrue("Cannot retrieve inserted user", result);
		
		var name = rs.getString("name");
		
		assertEquals("User name doesn't match retrieved", user.getName(), name);
		
		stmt.close();
	}
}