package com.stackroute.activitystream.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Spring;
import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.stackroute.activitystream.config.ApplicationContextConfig;
import com.stackroute.activitystream.dao.CircleDAO;
import com.stackroute.activitystream.dao.MessageDAO;
import com.stackroute.activitystream.dao.UserCircleDAO;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.Circle;
import com.stackroute.activitystream.model.Message;
import com.stackroute.activitystream.model.User;
import com.stackroute.activitystream.model.UserCircle;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@Transactional
@ContextConfiguration(classes = { ApplicationContextConfig.class })
public class UserCircleTest {

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	UserCircleDAO userCircleDAO;

	@Autowired
	private CircleDAO circleDAO;

	@Autowired
	private Circle circle;
	
	@Autowired
	private UserCircle userCircle;

	@Before
	public void setup() {
		if (userDAO.get("john") != null) {
			userDAO.delete(userDAO.get("john"));
		}

		if (circleDAO.get("Java") != null) {
			circleDAO.delete(circleDAO.get("Java"));
		}
		User testUser = new User();
		testUser.setName("John");
		testUser.setPassword("password");
		testUser.setUsername("john");
		userDAO.save(testUser);

		Circle testCircle = new Circle();
		testCircle.setCircleName("Java");
		testCircle.setCreatedDate();
		testCircle.setCreatorId("John");
		circleDAO.save(testCircle);

	}

	@After
	public void teardown() {
		if (userDAO.get("john") != null) {
			userDAO.delete(userDAO.get("john"));
		}

		if (circleDAO.get("Java") != null) {
			circleDAO.delete(circleDAO.get("Java"));
		}
	}

	@Test
	public void testAddUserToCircle() {

		assertEquals("Adding user to circle failed",true,userCircleDAO.addUser("john", "Java"));
		
	}
	
	@Test
	public void testAddUserToCircleInvalidUserFailure() {

		assertEquals("Adding user to circle failed",false,userCircleDAO.addUser("chris", "Java"));
		
	}

	@Test
	public void testAddUserToCircleInvalidCircleFailure() {

		assertEquals("Adding user to circle failed",false,userCircleDAO.addUser("john", "Spring"));
		
	}
	
	@Test
	public void testRemoveUserFromCircle() {
		userCircleDAO.addUser("john", "Java");
		assertEquals("Removing user from circle failed",true,userCircleDAO.removeUser("john", "Java"));
		
	}
	
	@Test
	public void testRemoveUserFromCircleInvalidUserFailure() {

		assertEquals("Removing user from circle failed",false,userCircleDAO.removeUser("chris", "Java"));
		
	}

	@Test
	public void testRemoveUserFromCircleInvalidCircleFailure() {

		assertEquals("Removing user from circle failed",false,userCircleDAO.removeUser("john", "Spring"));
		
	}
	
	@Test
	public void testUserSubscriptionToCircle() {
		userCircleDAO.addUser("john", "Java");
		List<String> userSubscription=new ArrayList<String>();
		userSubscription.add("Java");
		assertEquals("Removing user from circle failed",userSubscription,userCircleDAO.getMyCircles("john"));
		
	}

}
