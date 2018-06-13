package com.stackroute.activitystream.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/*This class WebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer
 * class. In Servlet 3.0+ environments in order to configure the ServletContext 
 * programmatically -- as opposed to the traditional web.xml-based approach. By overriding the methods of
 * class, we can define the Configuration classes and root mapping so that our application can gets
 * into spring.
 */
public class WebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{

	@Override
	protected Class<?>[] getRootConfigClasses() {
		// TODO Auto-generated method stub
		return new Class[] {ApplicationContextConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		// TODO Auto-generated method stub
		return new String[] {"/"};
	}
	
	

}
