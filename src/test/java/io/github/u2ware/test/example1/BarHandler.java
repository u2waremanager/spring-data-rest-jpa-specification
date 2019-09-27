package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.PredicateBuilder;
import org.springframework.data.rest.core.annotation.HandlePredicateBuilder;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;


@Component
@RepositoryEventHandler
public class BarHandler {

	protected Log logger = LogFactory.getLog(getClass());

	
	@HandlePredicateBuilder
	protected void handlePredicateBuilder(PredicateBuilder<Bar> builder) {
		
		
		logger.info("handleBeforeRead: "+ builder.getRequestParam());
		logger.info("handleBeforeRead: "+ builder.getRequestParam().get("age"));
		logger.info("handleBeforeRead: "+ builder.getRequestParam().get("name"));
	}
	
	
}
