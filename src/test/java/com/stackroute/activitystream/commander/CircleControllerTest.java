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
import com.stackroute.activitystream.controller.UserController;
import com.stackroute.activitystream.dao.CircleDAO;
import com.stackroute.activitystream.dao.MessageDAO;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.daoimpl.MessageDAOImpl;
import com.stackroute.activitystream.model.Circle;
import com.stackroute.activitystream.model.Message;
import com.stackroute.activitystream.model.User;

import junit.framework.Assert;

import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpSession;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContextConfig.class})
@WebAppConfiguration
public class CircleControllerTest {

	private MockMvc circleMockMvc;
	private MockMvc userAuthMockMvc;
	
	
	@Mock
	private Circle circle;
	
	@Mock
	private CircleDAO circleDAO;
	
	@Mock
	private UserDAO userDAO;
	
	@InjectMocks
	private CircleController circleController=new CircleController();
	
	@InjectMocks
	private UserAuthController userAuthController=new UserAuthController();
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		circleMockMvc=MockMvcBuilders.standaloneSetup(circleController)
				.build();
		userAuthMockMvc=MockMvcBuilders.standaloneSetup(userAuthController)
				.build();
			
	}
	
	@Test
    public void testCreateCircle() throws Exception {
		
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
		
		List<User> users=Arrays.asList(
				new User("john","John","password"),
				new User("chris","Chris","password2"));
		
		
		Circle newCircle=new Circle("Spring", null, null);
		when(circleDAO.save(any())).thenReturn(true);
        circleMockMvc.perform(post("/api/circle")
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(asJsonString(newCircle))
        		.session((MockHttpSession)session))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.circleName", is("Spring")))
                .andExpect(jsonPath("$.creatorId", is("john")));
        verify(circleDAO, times(1)).save(any()); 
    }
	
	
	@Test
    public void testCreateDuplicateCircleFailure() throws Exception {
		
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
		
		List<User> users=Arrays.asList(
				new User("john","John","password"),
				new User("chris","Chris","password2"));
		
		
		Circle newCircle=new Circle("Spring", null, null);
		
		when(circleDAO.save(any())).thenReturn(true);
		when(circleDAO.get(newCircle.getCircleName())).thenReturn(newCircle);
        
		circleMockMvc.perform(post("/api/circle")
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(asJsonString(newCircle))
        		.session((MockHttpSession)session))
                .andExpect(status().isConflict())
                ;
        verify(circleDAO, times(1)).get(any());
        verify(circleDAO, times(0)).save(any());
    }
	
	@Test
    public void testCreateCircleFailure() throws Exception {
		
		User user=new User("john","","password2");
		
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
		
		
		Circle newCircle=new Circle("Spring", null, null);
		
		when(circleDAO.save(any())).thenReturn(true);
		when(circleDAO.get(newCircle.getCircleName())).thenReturn(newCircle);
        
		circleMockMvc.perform(post("/api/circle")
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(asJsonString(newCircle))
        		.session((MockHttpSession)session))
                .andExpect(status().isUnauthorized())
                ;
        verify(circleDAO, times(0)).get(any());
        verify(circleDAO, times(0)).save(any());
    }
	
	
	@Test
    public void testGetAllCircles() throws Exception {
		
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
		
		List<Circle> circles=Arrays.asList(
				new Circle("Angular","john",new Timestamp(System.currentTimeMillis())),
				new Circle("Spring","chris",new Timestamp(System.currentTimeMillis())));
		
		when(circleDAO.getAllCircles()).thenReturn(circles);
		
        circleMockMvc.perform(get("/api/circle")
        		.session((MockHttpSession)session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].circleName", is("Angular")))
                .andExpect(jsonPath("$[0].creatorId", is("john")))
                .andExpect(jsonPath("$[1].circleName", is("Spring")))
                .andExpect(jsonPath("$[1].creatorId", is("chris")));
        verify(circleDAO, times(1)).getAllCircles(); 
    }
	
	@Test
    public void testGetAllCirclesFailure() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password2")).thenReturn(false);
		
	
        HttpSession session=userAuthMockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
		
		List<Circle> circles=Arrays.asList(
				new Circle("Angular","john",new Timestamp(System.currentTimeMillis())),
				new Circle("Spring","chris",new Timestamp(System.currentTimeMillis())));
		
		when(circleDAO.getAllCircles()).thenReturn(circles);
		
        circleMockMvc.perform(get("/api/circle")
        		.session((MockHttpSession)session))
                .andExpect(status().isUnauthorized());
        
        verify(circleDAO, times(0)).getAllCircles();
    }
	
	
	@Test
    public void testGetAllMatchingCircles() throws Exception {
		
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
		
		List<Circle> circles=Arrays.asList(
				new Circle("Spring","chris",new Timestamp(System.currentTimeMillis())),
				new Circle("Spring Security","chris",new Timestamp(System.currentTimeMillis())));
		
		
		when(circleDAO.getAllCircles("spring")).thenReturn(circles);
		
        circleMockMvc.perform(get("/api/circle/search/spring")
        		.session((MockHttpSession)session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].circleName", is("Spring")))
                .andExpect(jsonPath("$[0].creatorId", is("chris")))
                .andExpect(jsonPath("$[1].circleName", is("Spring Security")))
                .andExpect(jsonPath("$[1].creatorId", is("chris")));
        verify(circleDAO, times(1)).getAllCircles(("spring")); 
    }
	
	@Test
    public void testGetAllMatchingCirclesFailure() throws Exception {
		
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
		
		List<Circle> circles=Arrays.asList(
				new Circle("Spring","chris",new Timestamp(System.currentTimeMillis())),
				new Circle("Spring Security","chris",new Timestamp(System.currentTimeMillis())));
		
		
		when(circleDAO.getAllCircles("spring")).thenReturn(circles);
		
        circleMockMvc.perform(get("/api/circle/search/spring")
        		.session((MockHttpSession)session))
                .andExpect(status().isUnauthorized());
                
        verify(circleDAO, times(0)).getAllCircles(("spring")); 
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
