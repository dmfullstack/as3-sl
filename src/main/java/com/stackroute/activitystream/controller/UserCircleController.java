package com.stackroute.activitystream.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.activitystream.dao.CircleDAO;
import com.stackroute.activitystream.dao.UserCircleDAO;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.Circle;
import com.stackroute.activitystream.model.Message;
import com.stackroute.activitystream.model.UserCircle;

@RestController
@RequestMapping("/api/usercircle")
public class UserCircleController {

	@Autowired
	UserCircleDAO userCircleDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private CircleDAO circleDAO;

	@Autowired
	UserCircle userCircle;

	@PutMapping("/addToCircle/{username}/{circleName}")
	public ResponseEntity<UserCircle> addUser(@PathVariable("username") String username,
			@PathVariable("circleName") String circleName,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<UserCircle>(HttpStatus.UNAUTHORIZED);
		}
		if(userDAO.get(username)==null||circleDAO.get(circleName)==null) {
			return new ResponseEntity<UserCircle>(HttpStatus.NOT_FOUND);
			
		}
		if(userCircleDAO.get(username, circleName)!=null) {
			return new ResponseEntity<UserCircle>(HttpStatus.CONFLICT);
		}
		boolean status = userCircleDAO.addUser(username, circleName);
		if (status == false) {

			return new ResponseEntity<UserCircle>(HttpStatus.INTERNAL_SERVER_ERROR);

		} else {

			return new ResponseEntity<UserCircle>(HttpStatus.OK);
		}

	}

	@PutMapping("/removeFromCircle/{username}/{circleName}")
	public ResponseEntity<UserCircle> removeUser(@PathVariable("username") String username,
			@PathVariable("circleName") String circleName,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<UserCircle>(HttpStatus.UNAUTHORIZED);
		}
		boolean status = userCircleDAO.removeUser(username, circleName);
		if (status == false) {

			return new ResponseEntity<UserCircle>(HttpStatus.INTERNAL_SERVER_ERROR);

		} else {
			return new ResponseEntity<UserCircle>(HttpStatus.OK);

		}

	}
	
	/*
	 * Retrieve circles for a specific user
	 */	
	
	@GetMapping("/searchByUser/{username}")
	public ResponseEntity<List<String>> getMyCircles(@PathVariable("username") String username,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<List<String>>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<List<String>>(userCircleDAO.getMyCircles(username), HttpStatus.OK);

	}

}
