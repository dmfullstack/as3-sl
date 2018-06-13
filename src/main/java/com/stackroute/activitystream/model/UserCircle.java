package com.stackroute.activitystream.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name ="user_circle")
public class UserCircle {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int userCircleId;
	private String username;
	private String circleName;
	
	public UserCircle(String username, String circleName) {
		super();
		this.username = username;
		this.circleName = circleName;
	}
	
	
	
	public UserCircle() {
		
	}



	public int getUserCircleId() {
		return userCircleId;
	}
	public void setUserCircleId(int userCircleId) {
		this.userCircleId = userCircleId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCircleName() {
		return circleName;
	}
	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
	
	
}
