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
public class MessageTest {

	@Autowired
	private MessageDAO messageDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	UserCircleDAO userCircleDAO;

	@Autowired
	private CircleDAO circleDAO;

	@Autowired
	private Message message;
	
	@Autowired
	private Circle circle;
	
	@Autowired
	private UserCircle userCircle;

	@Before
	public void setup() {
		if (userDAO.get("john") != null) {
			userDAO.delete(userDAO.get("john"));
		}
		
		if (userDAO.get("will") != null) {
			userDAO.delete(userDAO.get("will"));
		}
		
		if (circleDAO.get("Java") != null) {
			circleDAO.delete(circleDAO.get("Java"));
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

		Circle testCircle = new Circle();
		testCircle.setCircleName("Java");
		testCircle.setCreatedDate();
		testCircle.setCreatorId("John");
		circleDAO.save(testCircle);
		
		userCircleDAO.addUser("john", "Java");
		userCircleDAO.addUser("will", "Java");
	}

	@After
	public void teardown() {
		if (userDAO.get("john") != null) {
			userDAO.delete(userDAO.get("john"));
		}
		
		if (userDAO.get("will") != null) {
			userDAO.delete(userDAO.get("will"));
		}

		if (circleDAO.get("Java") != null) {
			circleDAO.delete(circleDAO.get("Java"));
		}
		
		if (userCircleDAO.get("john", "Java") != null) {
			userCircleDAO.removeUser("john","Java");
		}
		
		if (userCircleDAO.get("will", "Java") != null) {
			userCircleDAO.removeUser("will","Java");
		}
	}

	@Test
	public void testSendMessageToCircle() {
		message.setMessage("Sample Message");
		message.setStreamType("text");
		message.setSenderName("john");
		assertEquals("Sending message to Circle failed",true,messageDAO.sendMessageToCircle("Java", message));
		//messageDAO.removeMessage(message);
	}
	
	@Test
	public void testSendMessageToCircleInvalidSenderFailure() {
		message.setMessage("Sample Message");
		message.setStreamType("text");
		message.setSenderName("chris");
		assertEquals("Sending message to Circle failed",false,messageDAO.sendMessageToCircle("Java", message));
		//messageDAO.removeMessage(message);
	}
	
	@Test
	public void testSendMessageToCircleInvalidCircleFailure() {
		message.setMessage("Sample Message");
		message.setStreamType("text");
		message.setSenderName("john");
		assertEquals("Sending message to Circle failed",false,messageDAO.sendMessageToCircle("Angular", message));
		//messageDAO.removeMessage(message);
	}
	
	@Test
	public void testSendMessageToUser() {
		message.setMessage("Sample Message");
		message.setStreamType("text");
		message.setSenderName("john");
		message.setTag("sampleTag");
		assertEquals("Sending message to Circle failed",true,messageDAO.sendMessageToUser("will", message));
		//messageDAO.removeMessage(message);
	}
	
	@Test
	public void testSendMessageToInvalidSenderFailure() {
		message.setMessage("Sample Message");
		message.setStreamType("text");
		message.setSenderName("chris");
		assertEquals("Sending message to Circle failed",false,messageDAO.sendMessageToUser("will", message));
		
	}
	
	@Test
	public void testSendMessageToInvalidReceiverFailure() {
		message.setMessage("Sample Message");
		message.setStreamType("text");
		message.setSenderName("john");
		assertEquals("Sending message to Circle failed",false,messageDAO.sendMessageToUser("chris", message));
		
	}
	
	
	
}
