package com.stackroute.activitystream.commander;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.fabric.xmlrpc.base.Data;
import com.stackroute.activitystream.config.ApplicationContextConfig;
import com.stackroute.activitystream.controller.CircleController;
import com.stackroute.activitystream.controller.MessageController;
import com.stackroute.activitystream.controller.UserAuthController;
import com.stackroute.activitystream.controller.UserCircleController;
import com.stackroute.activitystream.controller.UserController;
import com.stackroute.activitystream.dao.CircleDAO;
import com.stackroute.activitystream.dao.MessageDAO;
import com.stackroute.activitystream.dao.UserCircleDAO;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.daoimpl.MessageDAOImpl;
import com.stackroute.activitystream.model.Circle;
import com.stackroute.activitystream.model.Message;
import com.stackroute.activitystream.model.User;
import com.stackroute.activitystream.model.UserCircle;

import junit.framework.Assert;

import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpSession;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContextConfig.class})
@WebAppConfiguration
public class UserCircleControllerTest {

	private MockMvc userCircleMockMvc;
	private MockMvc userAuthMockMvc;
	
	
	@Mock
	private Circle circle;
	
	@Mock
	private CircleDAO circleDAO;
	
	@Mock
	private UserCircleDAO userCircleDAO;
	
	@Mock
	private UserDAO userDAO;
	
	@InjectMocks
	private UserCircleController userCircleController=new UserCircleController();
	
	@InjectMocks
	private UserAuthController userAuthController=new UserAuthController();
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		userCircleMockMvc=MockMvcBuilders.standaloneSetup(userCircleController)
				.build();
		userAuthMockMvc=MockMvcBuilders.standaloneSetup(userAuthController)
				.build();
			
	}
	
	@Test
    public void testAddUserToCircle() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password")).thenReturn(true);
		when(userDAO.get("john")).thenReturn(user);
	
        HttpSession session=userAuthMockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("password")))
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
		
		Circle circle=new Circle("Spring", null, null);
		UserCircle userCircle=new UserCircle("john", "Spring");
		
		when(userDAO.get("john")).thenReturn(user);
		when(circleDAO.get("spring")).thenReturn(circle);
		when(userCircleDAO.get("john", "spring")).thenReturn(null);
		when(userCircleDAO.addUser("john", "spring")).thenReturn(true);
		
		userCircleMockMvc.perform(put("/api/usercircle/addToCircle/john/spring")
        		.session((MockHttpSession)session))
                .andExpect(status().isOk());
                
        verify(userDAO, times(2)).get("john");
        verify(circleDAO, times(1)).get("spring");
        verify(userCircleDAO, times(1)).get("john", "spring");
        verify(userCircleDAO, times(1)).addUser("john", "spring");
    }
	
	@Test
    public void testAddUserToCircleDuplicateFailure() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password")).thenReturn(true);
		when(userDAO.get("john")).thenReturn(user);
	
        HttpSession session=userAuthMockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("password")))
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
		
		Circle circle=new Circle("Spring", null, null);
		UserCircle userCircle=new UserCircle("john", "Spring");
		
		when(userDAO.get("john")).thenReturn(user);
		when(circleDAO.get("spring")).thenReturn(circle);
		when(userCircleDAO.get("john", "spring")).thenReturn(userCircle);
		when(userCircleDAO.addUser("john", "spring")).thenReturn(true);
		
		userCircleMockMvc.perform(put("/api/usercircle/addToCircle/john/spring")
        		.session((MockHttpSession)session))
                .andExpect(status().isConflict());
                
        verify(userDAO, times(2)).get("john");
        verify(circleDAO, times(1)).get("spring");
        verify(userCircleDAO, times(1)).get("john", "spring");
        verify(userCircleDAO, times(0)).addUser("john", "spring");
    }
	
	@Test
    public void testAddUserToCircleInvalidUserFailure() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password")).thenReturn(true);
		when(userDAO.get("john")).thenReturn(user);
	
        HttpSession session=userAuthMockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("password")))
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
		
		Circle circle=new Circle("Spring", null, null);
		UserCircle userCircle=new UserCircle("john", "Spring");
		
		when(userDAO.get("john")).thenReturn(null);
		when(circleDAO.get("spring")).thenReturn(circle);
		when(userCircleDAO.get("john", "spring")).thenReturn(userCircle);
		when(userCircleDAO.addUser("john", "spring")).thenReturn(true);
		
		userCircleMockMvc.perform(put("/api/usercircle/addToCircle/john/spring")
        		.session((MockHttpSession)session))
                .andExpect(status().isNotFound());
                
        verify(userDAO, times(2)).get("john");
        verify(circleDAO, times(0)).get("spring");
        verify(userCircleDAO, times(0)).get("john", "spring");
        verify(userCircleDAO, times(0)).addUser("john", "spring");
    }
	
	
	@Test
    public void testAddUserToCircleInvalidCircleFailure() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password")).thenReturn(true);
		when(userDAO.get("john")).thenReturn(user);
	
        HttpSession session=userAuthMockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("password")))
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
		
		Circle circle=new Circle("Spring", null, null);
		UserCircle userCircle=new UserCircle("john", "Spring");
		
		when(userDAO.get("john")).thenReturn(user);
		when(circleDAO.get("spring")).thenReturn(null);
		when(userCircleDAO.get("john", "spring")).thenReturn(userCircle);
		when(userCircleDAO.addUser("john", "spring")).thenReturn(true);
		
		userCircleMockMvc.perform(put("/api/usercircle/addToCircle/john/spring")
        		.session((MockHttpSession)session))
                .andExpect(status().isNotFound());
                
        verify(userDAO, times(2)).get("john");
        verify(circleDAO, times(1)).get("spring");
        verify(userCircleDAO, times(0)).get("john", "spring");
        verify(userCircleDAO, times(0)).addUser("john", "spring");
    }
	
	
	@Test
    public void testRemoveUserFromCircle() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password")).thenReturn(true);
		when(userDAO.get("john")).thenReturn(user);
	
        HttpSession session=userAuthMockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("password")))
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
		
		Circle circle=new Circle("Spring", null, null);
		UserCircle userCircle=new UserCircle("john", "Spring");
		
		when(userDAO.get("john")).thenReturn(user);
		when(circleDAO.get("spring")).thenReturn(circle);
		when(userCircleDAO.get("john", "spring")).thenReturn(null);
		when(userCircleDAO.removeUser("john", "spring")).thenReturn(true);
		
		userCircleMockMvc.perform(put("/api/usercircle/removeFromCircle/john/spring")
        		.session((MockHttpSession)session))
                .andExpect(status().isOk());
                
        verify(userDAO, times(1)).get("john");
        verify(circleDAO, times(0)).get("spring");
        verify(userCircleDAO, times(0)).get("john", "spring");
        verify(userCircleDAO, times(1)).removeUser("john", "spring");
    }
	
	@Test
    public void testRemoveUserFromCircleFailure() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password")).thenReturn(true);
		when(userDAO.get("john")).thenReturn(user);
	
        HttpSession session=userAuthMockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("password")))
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
		
		Circle circle=new Circle("Spring", null, null);
		UserCircle userCircle=new UserCircle("john", "Spring");
		
		when(userDAO.get("john")).thenReturn(user);
		when(circleDAO.get("spring")).thenReturn(circle);
		when(userCircleDAO.get("john", "spring")).thenReturn(null);
		when(userCircleDAO.removeUser("john", "spring")).thenReturn(false);
		
		userCircleMockMvc.perform(put("/api/usercircle/removeFromCircle/john/spring")
        		.session((MockHttpSession)session))
                .andExpect(status().isInternalServerError());
                
        verify(userDAO, times(1)).get("john");
        verify(userCircleDAO, times(1)).removeUser("john", "spring");
    }
	
	
	@Test
    public void testUserSubscriptionToCircle() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password")).thenReturn(true);
		when(userDAO.get("john")).thenReturn(user);
	
        HttpSession session=userAuthMockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("password")))
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
		
		Circle circle=new Circle("Spring", null, null);
		UserCircle userCircle=new UserCircle("john", "Spring");
		
		
		when(userCircleDAO.getMyCircles("john")).thenReturn(Arrays.asList("Spring","Angular"));
		
		userCircleMockMvc.perform(get("/api/usercircle/searchByUser/john")
        		.session((MockHttpSession)session))
                .andExpect(status().isOk());
                
        verify(userDAO, times(1)).get("john");
        verify(userCircleDAO, times(1)).getMyCircles("john");
    }
	
	
	@Test
    public void testUserSubscriptionToCircleFailure() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password2")).thenReturn(false);
		when(userDAO.get("john")).thenReturn(user);
	
        HttpSession session=userAuthMockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
		
		Circle circle=new Circle("Spring", null, null);
		UserCircle userCircle=new UserCircle("john", "Spring");
		
		
		when(userCircleDAO.getMyCircles("john")).thenReturn(Arrays.asList("Spring","Angular"));
		
		userCircleMockMvc.perform(get("/api/usercircle/searchByUser/john")
        		.session((MockHttpSession)session))
                .andExpect(status().isUnauthorized());
                
        verify(userDAO, times(0)).get("john");
        verify(userCircleDAO, times(0)).getMyCircles("john");
    }
	
	/*
     * converts a Java object into JSON representation
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
