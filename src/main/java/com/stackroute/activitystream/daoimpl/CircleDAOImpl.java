package com.stackroute.activitystream.daoimpl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.stackroute.activitystream.dao.CircleDAO;
import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.Circle;


@Repository("circleDAO")
@Transactional
public class CircleDAOImpl implements CircleDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private UserDAO userDAO;

	public CircleDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean save(Circle circle) {
		try {
			circle.setCreatedDate();
			if(userDAO.get(circle.getCreatorId())!=null) {
			getCurrentSession().save(circle);
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

	public boolean update(Circle circle) {
		try {
			getCurrentSession().update(circle);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(Circle circle) {
		try {
			getCurrentSession().delete(circle);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// redundant
	public List<Circle> list() {
		return getCurrentSession().createQuery("from Circle").list();
	}

	public Circle get(String circleName) {
		return (Circle) getCurrentSession().get(Circle.class, circleName);

	}
	

	public List<Circle> getAllCircles() {
		
		return getCurrentSession().createQuery("from Circle").list();
	}

	

	@SuppressWarnings("unchecked")
	public List<Circle> getAllCircles(String searchString) {
		return getCurrentSession().createCriteria(Circle.class).add(Restrictions.like("circleName", "%" + searchString + "%"))
				.list();

	}

	

}
