package com.smart.service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import io.swagger.jaxrs.config.BeanConfig;

public class SwaggerConfiguration extends HttpServlet{
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setTitle("iTrack-SmartService");
		beanConfig.setVersion("1.0");
		
		//For local
		//beanConfig.setSchemes(new String[]{"http"});
		//beanConfig.setHost("localhost:8080");
		//beanConfig.setBasePath("/SmartApp/rest");
		//For azure deployment
		beanConfig.setSchemes(new String[]{"https"});
		beanConfig.setHost("webapp-smartapp.azurewebsites.net");
		beanConfig.setBasePath("/rest");
		
		beanConfig.setResourcePackage("com.smart.service");
		beanConfig.setScan(true);
		beanConfig.setDescription("Services for iTrack App");
	}
}