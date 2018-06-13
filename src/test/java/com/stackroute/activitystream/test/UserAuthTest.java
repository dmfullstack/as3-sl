package com.stackroute.activitystream.test;

import static org.junit.Assert.*;

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
import com.stackroute.activitystream.dao.MessageDAO;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.Message;
import com.stackroute.activitystream.model.User;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@Transactional
@ContextConfiguration(classes = { ApplicationContextConfig.class })
public class UserAuthTest {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private User user;

	@Autowired
	private SessionFactory sessionFactory;

	@Before
	public void setup() {
		if (userDAO.get("john") != null) {
			userDAO.delete(userDAO.get("john"));
		}
		if (userDAO.get("will") != null) {
			userDAO.delete(userDAO.get("will"));
		}
		
		User testUser = new User();
		testUser.setName("John");
		testUser.setPassword("password");
		testUser.setUsername("john");
		userDAO.save(testUser);

		User testUser2 = new User();
		testUser2.setName("Will");
		testUser2.setPassword("password");
		testUser2.setUsername("will");
		userDAO.save(testUser2);

	}

	@After
	public void teardown() {
		
	}

	@Test
	public void testUserAuthentication() {

		assertEquals("Authentication failed for legitimate user.", true,userDAO.validate("john","password"));
		
	}

	@Test
	public void testUserAuthenticationFailure() {

		assertNotEquals("Authentication logic is not correct. Please check. User with incorrect password is also being logged in", true,userDAO.validate("john","password2"));
		
	}

}
