package com.stackroute.activitystream.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="message")
public class Message {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int messageId;
	private String senderName;
	private String receiverId;
	private String circleName;
	private Timestamp postedDate;
	private String streamType;
	private String message;
	private String tag;
	
	
	
	public Message(String senderName, String receiverId, String circleName, Timestamp postedDate, String streamType,
			String message, String tag) {
		super();
		this.senderName = senderName;
		this.receiverId = receiverId;
		this.circleName = circleName;
		this.postedDate = postedDate;
		this.streamType = streamType;
		this.message = message;
		this.tag = tag;
	}
	
	
	
	public Message() {
		
	}



	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getCircleName() {
		return circleName;
	}
	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	public Timestamp getPostedDate() {
		return postedDate;
	}
	public void setPostedDate() {
		this.postedDate = setCurrentDate();
	}
	public String getStreamType() {
		return streamType;
	}
	public void setStreamType(String streamType) {
		this.streamType = streamType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Timestamp setCurrentDate()
	{
		return new Timestamp(System.currentTimeMillis());
	}

}
