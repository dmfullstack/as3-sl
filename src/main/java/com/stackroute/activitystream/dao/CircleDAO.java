package com.stackroute.activitystream.dao;

import java.util.List;

import com.stackroute.activitystream.model.Circle;

public interface CircleDAO {
	
	public boolean save(Circle circle);
	
	public List<Circle> getAllCircles();
	
	public List<Circle> getAllCircles(String searchString);
	
	public Circle get(String circleName);
	
	public boolean update(Circle circle);
	
	public boolean delete(Circle circle);

}
