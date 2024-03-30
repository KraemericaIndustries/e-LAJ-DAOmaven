package com.ki.mavenDAO.DAOmaven;

import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		
		var db = Database.instance();
		
		try {
			db.connect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot connect to database");
		}
		
		System.out.println("Connected");
		
		UserDao userDao = new UserDaoImpl();
		
//		userDao.save(new User("Mars"));
//		userDao.save(new User("Mercury"));
		
		var users = userDao.getAll();
		
		users.forEach(System.out::println);
		
		var userOpt = userDao.findById(1);
		
		if(userOpt.isPresent()) {
			System.out.println("Retrieved: " + userOpt.get());	
		} else {
			System.out.println("No user was retrieved");
		}
		
		
		try {
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot close database connection.");
		}
	}
}