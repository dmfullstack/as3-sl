package com.stackroute.activitystream.test;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.activitystream.config.ApplicationContextConfig;
import com.stackroute.activitystream.controller.UserAuthController;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.User;

import junit.framework.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContextConfig.class})
@WebAppConfiguration
public class UserAuthControllerTest {

	private MockMvc mockMvc;
	
	@Mock
	private User user;
	
	@Mock
	private UserDAO userDAO;
	
	@InjectMocks
	private UserAuthController userAuthController=new UserAuthController();
	
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(userAuthController)
				.build();
			
	}
	
	@Test
    public void testAuthenticate() throws Exception {
			
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password")).thenReturn(true);
		when(userDAO.get("john")).thenReturn(user);
	
        mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("password")));
        verify(userDAO, times(1)).validate("john", "password");
        verify(userDAO, times(1)).get("john");
        verifyNoMoreInteractions(userDAO);
        
                
    }
	
	
	@Test
    public void testAuthenticateIncorrectPasswordFailure() throws Exception {
			
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password2")).thenReturn(false);
		when(userDAO.get("john")).thenReturn(user);
	
        mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isUnauthorized());
        verify(userDAO, times(1)).validate("john", "password");
        verifyNoMoreInteractions(userDAO);           
    }
	
	@Test
    public void testAuthenticateIncorrectUsernameFailure() throws Exception {
			
		User user=new User("john","","password");
		
		when(userDAO.validate("john2", "password")).thenReturn(false);
		when(userDAO.get("john")).thenReturn(user);
	
        mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isUnauthorized());
        verify(userDAO, times(1)).validate("john", "password");
        verifyNoMoreInteractions(userDAO);           
    }
	
	@Test
    public void testLogout() throws Exception {
		
		User user=new User("john","","password");
		
		when(userDAO.validate("john", "password")).thenReturn(true);
		when(userDAO.get("john")).thenReturn(user);
	
        HttpSession session=mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("password")))
                .andReturn()
                .getRequest()
                .getSession();
        
        Assert.assertNotNull(session);
        
        verify(userDAO, times(1)).validate("john", "password");
        verify(userDAO, times(1)).get("john");
        verifyNoMoreInteractions(userDAO);
		
        mockMvc.perform(put("/api/logout").session((MockHttpSession)session))
                .andExpect(status().isOk());
                  
    }
	
	
	@Test
    public void testLogoutFailure() throws Exception {
	
        mockMvc.perform(put("/api/logout"))
                .andExpect(status().isBadRequest());
                  
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
