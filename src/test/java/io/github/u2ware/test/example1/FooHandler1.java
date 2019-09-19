package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.JpaSpecification;
import org.springframework.data.rest.core.event.RepositoryRestEventHandler;
import org.springframework.stereotype.Component;

@Component
public class FooHandler1 extends RepositoryRestEventHandler<Foo>{

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	protected void handleBeforeCreate(Foo entity) {
		logger.info("handleBeforeCreate");
	}
	
	
	@Override
	protected void handleBeforeRead(JpaSpecification<Foo> spec) {
		logger.info("handleBeforeRead: "+ spec.getPayload());
	}
	
}
