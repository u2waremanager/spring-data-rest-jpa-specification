package org.springframework.data.rest.webmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.HibernateConfiguration;
import org.springframework.data.rest.core.event.AnnotatedReadEventHandlerInvoker;
import org.springframework.data.rest.webmvc.RepositoryRestController;

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
	
}
