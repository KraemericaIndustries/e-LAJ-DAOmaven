package com.ki.mavenDAO.DAOmaven;

import java.sql.SQLException;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		
		Properties props = new Properties();
		
		String propertiesFile = "/config/db.properties";
		try {
			props.load(App.class.getResourceAsStream(propertiesFile));
		} catch (Exception e) {
			System.out.println("Cannot load properties file: " + propertiesFile);
			return;
		}
		
		var db = Database.instance();
		
		try {
			db.connect(props);
		} catch (SQLException e) {
			System.out.println("Cannot connect to database");
			return;
		}
		
		System.out.println("Connected");
		
		UserDao userDao = new UserDaoImpl();
		
//		userDao.save(new User("Mars"));
//		userDao.save(new User("Mercury"));
		
		var users = userDao.getAll();
		
		users.forEach(System.out::println);
		
		var userOpt = userDao.findById(4);
		
		if(userOpt.isPresent()) {
			
			User user = userOpt.get();
			System.out.println("Retrieved: " + user);
			user.setName("Jeebs");	
			
			userDao.update(user);
			
		} else {
			System.out.println("No user was retrieved");
		}
		
		
		userDao.delete(new User(5, null));
		
		
		
		try {
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot close database connection.");
		}
	}
}