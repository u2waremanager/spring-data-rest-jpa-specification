package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.PredicateBuilder;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;


@Component
@RepositoryEventHandler
public class BarHandler {

	protected Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeRead
	protected void handleBeforeRead(Bar entity, PredicateBuilder<Bar> builder) {
		
		logger.info("handleBeforeRead : "+ entity);
		logger.info("handleBeforeRead: "+ builder.getParameters());
		logger.info("handleBeforeRead: "+ builder.getParameters().get("age"));
		logger.info("handleBeforeRead: "+ builder.getParameters().get("name"));
	}
	
	
}
