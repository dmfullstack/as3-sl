package com.stackroute.activitystream.daoimpl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.stackroute.activitystream.dao.CircleDAO;
import com.stackroute.activitystream.dao.MessageDAO;
import com.stackroute.activitystream.dao.UserCircleDAO;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.Message;
import com.stackroute.activitystream.model.UserTag;

@Repository("messageDAO")
@Transactional
public class MessageDAOImpl implements MessageDAO {

	private static int pageSize = 8;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private CircleDAO circleDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private UserCircleDAO userCircleDAO;

	public MessageDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public List<Message> getMessagesFromCircle(String circleName, int pageNumber) {
		Query query = getCurrentSession().createQuery("from Message where circleName=? order by postedDate desc")
				.setString(0, circleName);
		query.setFirstResult(pageSize * (pageNumber - 1));
		query.setMaxResults(pageSize);

		return query.list();
	}

	public List<Message> getMessagesFromUser(String username, String otherUsername, int pageNumber) {
		return getCurrentSession().createQuery(
				"from Message where (receiverID=? and senderID=?) or (receiverID=? and senderID=?) order by postedDate desc")
				.setString(0, username).setString(1, otherUsername).setString(2, otherUsername).setString(3, username)
				.setFirstResult(pageSize * (pageNumber - 1)).setMaxResults(pageSize).list();
	}

	public List<Message> getMessages(String username, int pageNumber) {
		List<String> myCircles = userCircleDAO.getMyCircles(username);
		List<Message> allMessages = new ArrayList<Message>();
		List<Message> circleStream;
		for (String circleName : myCircles) {
			circleStream = getMessagesFromCircle(circleName, pageNumber);
			if (circleStream != null) {
				allMessages.addAll(circleStream);
			}

		}

		return allMessages;
	}

	public boolean sendMessageToCircle(String circleName, Message message) {
		try {
			message.setPostedDate();
			message.setCircleName(circleName);
			

			if (circleDAO.get(circleName) == null || !(userCircleDAO.getMyCircles(message.getSenderName()).contains(circleName))) {
				return false;
			}
			
			
			else {
				getCurrentSession().save(message);
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

	public boolean sendMessageToUser(String username, Message message) {
		try {
			message.setPostedDate();
			message.setReceiverId(username);
			if((userDAO.get(message.getSenderName())!=null) && (userDAO.get(message.getReceiverId())!=null)) {
				getCurrentSession().save(message);
				return true;
			}
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendMessageToCircles(List<String> circleNames, Message message) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<String> listTags() {
		return getCurrentSession().createQuery("select distinct m.tag from Message m").list();
	}

	public List<String> listMyTags(String username) {
		return getCurrentSession().createQuery("select tag from UserTag where username= ?").setString(0, username)
				.list();
	}

	public List<Message> showMessagesWithTag(String tag, int pageNumber) {
		return getCurrentSession().createQuery("from Message where tag like ? order by postedDate desc")
				.setString(0, "%" + tag + "%").setFirstResult(pageSize * (pageNumber - 1)).setMaxResults(pageSize)
				.list();
	}

	public boolean subscribeUserToTag(String username, String tag) {
		UserTag userTag = new UserTag();
		userTag.setTag(tag);
		userTag.setUsername(username);

		try {
			if(!(listMyTags(username).contains(tag))) {
				getCurrentSession().save(userTag);
				return true;
			}
			else
				return false;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean unsubscribeUserToTag(String username, String tag) {
		UserTag userTag = getUserTag(username, tag);
		try {
			if(listMyTags(username).contains(tag)) {
			getCurrentSession().delete(userTag);
			return true;
			}
			else 
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public UserTag getUserTag(String username, String tag) {
		return (UserTag) getCurrentSession().createQuery("from UserTag where username= ? and tag= ?")
				.setString(0, username).setString(1, tag).uniqueResult();
	}

	@Override
	public boolean removeMessage(Message message) {
		
		try {
		getCurrentSession().delete(message);
		getCurrentSession().flush();
		return true;
		}catch(Exception e) {
			return false;
		}
		
	}

}
