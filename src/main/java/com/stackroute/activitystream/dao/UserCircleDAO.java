package com.stackroute.activitystream.dao;

import java.util.List;

import com.stackroute.activitystream.model.UserCircle;

public interface UserCircleDAO {

	public boolean addUser(String username, String circleName);

	public boolean removeUser(String username, String circleName);
	
	public UserCircle get(String username, String circleName);
	
	public List<String> getMyCircles(String username);

}
