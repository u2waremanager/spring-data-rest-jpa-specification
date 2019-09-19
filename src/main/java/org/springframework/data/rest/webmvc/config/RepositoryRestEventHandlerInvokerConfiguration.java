package org.springframework.data.rest.webmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.RepositoryRestEventHandlerInvoker;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
@Configuration
public class RepositoryRestEventHandlerInvokerConfiguration {

	@Bean 
	public RepositoryRestEventHandlerInvoker repositoryRestEventHandlerInvoker() {
		return new RepositoryRestEventHandlerInvoker();
	}
	
}
