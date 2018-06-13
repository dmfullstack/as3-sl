package com.stackroute.activitystream.commander;

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
import com.stackroute.activitystream.dao.CircleDAO;
import com.stackroute.activitystream.dao.MessageDAO;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.Circle;
import com.stackroute.activitystream.model.Message;
import com.stackroute.activitystream.model.User;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@Transactional
@ContextConfiguration(classes = { ApplicationContextConfig.class })
public class CircleTest {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private CircleDAO circleDAO;

	@Autowired
	private Circle circle;

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
	public void testCreateCircle() {

		circle.setCircleName("Angular");
		circle.setCreatedDate();
		circle.setCreatorId("john");
		circleDAO.save(circle);
		assertNotNull("Creating of circle failed.", circleDAO.get("Angular"));
		circleDAO.delete(circle);
	}

	@Test
	public void testCreateCircleInvalidCreatorFailure() {

		circle.setCircleName("Angular");
		circle.setCreatedDate();
		circle.setCreatorId("chris");
		assertNotEquals("Creation of circle with invalid creatorID successful", true, circleDAO.save(circle));
		circleDAO.delete(circle);
	}

	@Test
	public void testCreateCircleDuplicateFailure() {

		circle.setCircleName("Java");
		circle.setCreatedDate();
		circle.setCreatorId("john");
		assertNotEquals("Creation of circle with the same name as of an existing circle successful", true,
				circleDAO.save(circle));
		circleDAO.delete(circle);
	}

	@Test
	public void testGetAllCircles() {

		assertNotNull("Creation of circle with the same name as of an existing circle successful",
				circleDAO.getAllCircles());
		
	}
	
	@Test
	public void testGetAllCirclesWithSearchString() {

		assertNotNull("Retrieval of all circles unsuccessful",
				circleDAO.getAllCircles("Java"));
	}
	
	@Test
	public void testGetAllCirclesWithSearchStringFailure() {

		assertNotNull("Retrieval of all circles containing a Search String unsuccessful",
				circleDAO.getAllCircles("Spring"));
	}

}
