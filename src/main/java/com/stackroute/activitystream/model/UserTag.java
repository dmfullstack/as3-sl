package com.stackroute.activitystream.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="user_tag")
public class UserTag {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long userTagId;
	private String username;
	private String tag;
	
	public long getUserTagId() {
		return userTagId;
	}
	public void setUserTagId(long userTagId) {
		this.userTagId = userTagId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	

}
