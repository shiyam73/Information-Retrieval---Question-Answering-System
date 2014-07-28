package com.ir.qa.login;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ir.qa.QueryProcessor.QueryProcessor;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class QAServletContextListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Initializing the Query Processor context");
		QueryProcessor.initialize();
		System.out.println("Context Initialization succeeded");
	}
}
