package com.stackroute.activitystream.daoimpl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.stackroute.activitystream.dao.UserDAO;
import com.stackroute.activitystream.model.User;

@Repository("userDAO")
@Transactional
public class UserDAOImpl implements UserDAO{
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public UserDAOImpl(SessionFactory sessionFactory)
	{
		this.sessionFactory=sessionFactory;
	}
	
	private Session getCurrentSession()
	{
		return sessionFactory.getCurrentSession();
	}

	public boolean save(User user) {
		try {
			getCurrentSession().save(user);
			getCurrentSession().flush();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean update(User user) {
		try {
			getCurrentSession().update(user);
			getCurrentSession().flush();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(User user) {
		try {
			getCurrentSession().delete(user);
			getCurrentSession().flush();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public List<User> list() {
		return getCurrentSession().createQuery("from User").list();
	}

	public boolean validate(String id, String password) {
	User user=	(User) getCurrentSession().createQuery("from User where id = ? and password = ?")
		.setString(0,id)
		.setString(1,password)
		.uniqueResult();
	
	if(user==null)
	{
		return false;
	}
	else
	{
		return true;
	}
	}

	public User get(String id) {
		return (User) getCurrentSession().get(User.class, id);
			
	}
	
	public boolean exists(String id) {
		User user=(User) getCurrentSession().get(User.class, id);
		if(user!=null)
			return true;
		else
			return false;
			
	}

}
