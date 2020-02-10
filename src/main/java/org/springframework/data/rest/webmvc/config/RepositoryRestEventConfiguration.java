package org.springframework.data.rest.webmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.AnnotatedReadEventHandlerInvoker;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
@Configuration
public class RepositoryRestEventConfiguration {


//	@Bean 
//	public RepositoryRestEventConfigure repositoryRestEventConfigure() {
//		return new RepositoryRestEventConfigure();
//	}
//	
//	@Bean 
//	public RepositoryRestEventHandlerInvoker repositoryRestEventHandlerInvoker() {
//		return new RepositoryRestEventHandlerInvoker();
//	}

	@Bean 
	public AnnotatedReadEventHandlerInvoker annotatedReadEventHandlerInvoker() {
		return new AnnotatedReadEventHandlerInvoker();
	}
	
}
