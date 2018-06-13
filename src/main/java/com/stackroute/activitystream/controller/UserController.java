package com.stackroute.activitystream.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.Message;
import com.stackroute.activitystream.model.User;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	UserDAO userDAO;

	/* 
	 * Retrieve All Users 
	 */
	@GetMapping
	public ResponseEntity<List<User>> listAllUsers(HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<List<User>>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<User>>(userDAO.list(), HttpStatus.OK);

	}

	/*  
	 * Retrieve Single User 
	 */
	@GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getUser(@PathVariable("username") String username,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
		User user = userDAO.get(username);
		if (user == null) {
			
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
			
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	/* 
	 * Create a new user 
	 */
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User u = userDAO.get(user.getUsername());
		if (u != null) {
			return new ResponseEntity<User>(HttpStatus.CONFLICT);
		}

		userDAO.save(user);
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}

	/* 
	 * Update an User 
	 */
	@PutMapping(value = "/{username}")
	public ResponseEntity<User> updateUser(@PathVariable("username") String username, @RequestBody User user,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
		User currentUser = userDAO.get(username);

		if (currentUser == null) {

			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}

		currentUser.setName(user.getName());
		currentUser.setPassword(user.getPassword());

		userDAO.update(currentUser);
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

}
