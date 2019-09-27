package org.springframework.data.rest.webmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.RepositoryRestEventConfigure;
import org.springframework.data.rest.core.event.RepositoryRestEventHandlerInvoker;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
@Configuration
public class RepositoryRestEventConfiguration {


	@Bean 
	public RepositoryRestEventConfigure repositoryRestEventConfigure() {
		return new RepositoryRestEventConfigure();
	}
	
	@Bean 
	public RepositoryRestEventHandlerInvoker repositoryRestEventHandlerInvoker() {
		return new RepositoryRestEventHandlerInvoker();
	}
	
}
