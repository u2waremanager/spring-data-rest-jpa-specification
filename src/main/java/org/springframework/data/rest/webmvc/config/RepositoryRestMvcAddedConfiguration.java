package org.springframework.data.rest.webmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.HibernateConfiguration;
import org.springframework.data.rest.core.event.AnnotatedReadEventHandlerInvoker;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.UriLinkBuilder;
import org.springframework.data.rest.webmvc.support.UriLinkParser;

@RepositoryRestController
@Configuration
public class RepositoryRestMvcAddedConfiguration {


	@Bean 
	public HibernateConfiguration hibernateConfiguration() {
		return new HibernateConfiguration();
	}

	@Bean 
	public AnnotatedReadEventHandlerInvoker annotatedReadEventHandlerInvoker() {
		return new AnnotatedReadEventHandlerInvoker();
	}
	
	
	@Bean 
	public UriLinkParser uriLinkParser() {
		return new UriLinkParser();
	}
	@Bean 
	public UriLinkBuilder uriLinkBuilder() {
		return new UriLinkBuilder();
	}
	
}
