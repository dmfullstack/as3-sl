package com.stackroute.activitystream.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="circle")
public class Circle {

	@Id
	private String circleName;
	private String creatorId;
	private Timestamp createdDate;
	
	
	
	public Circle(String circleName, String creatorId, Timestamp createdDate) {
		super();
		this.circleName = circleName;
		this.creatorId = creatorId;
		this.createdDate = createdDate;
	}
	
	
	public Circle() {
		
	}


	public String getCircleName() {
		return circleName;
	}
	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public Timestamp getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate() {
		this.createdDate = setCurrentDate();
	}
	
	public Timestamp setCurrentDate()
	{
		return new Timestamp(System.currentTimeMillis());
	}
	
	
}
