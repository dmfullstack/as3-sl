package com.stackroute.activitystream.daoimpl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.stackroute.activitystream.dao.CircleDAO;
import com.stackroute.activitystream.dao.UserCircleDAO;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.UserCircle;

@Repository("userCircleDAO")
@Transactional
public class UserCircleDAOImpl implements UserCircleDAO {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private CircleDAO circleDAO;

	@Autowired
	private SessionFactory sessionFactory;

	public UserCircleDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean addUser(String username, String circleName) {

		UserCircle userCircle = new UserCircle();
		userCircle.setCircleName(circleName);
		userCircle.setUsername(username);

		try {
			if (userDAO.get(username) != null && circleDAO.get(circleName) != null) {
				if(get(username,circleName)==null) {
				getCurrentSession().save(userCircle);
				}
			}
			else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean removeUser(String username, String circleName) {
		UserCircle userCircle = get(username, circleName);
		try {
			getCurrentSession().delete(userCircle);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public UserCircle get(String username, String circleName) {
		return (UserCircle) getCurrentSession().createQuery("from UserCircle where username= ? and circleName= ?")
				.setString(0, username).setString(1, circleName).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<String> getMyCircles(String username) {
		return getCurrentSession().createQuery("select circleName from UserCircle where username= ?")
				.setString(0, username).list();
	}

}
