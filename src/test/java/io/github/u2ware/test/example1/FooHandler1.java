package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.PartTreeQueryBuilder;
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
	protected void handleQueryBuilder(PartTreeQueryBuilder<Foo> builder) {
		logger.info("handleQueryBuilder1: "+ builder);
		logger.info("handleQueryBuilder1: "+ builder.getQueryParameters());
		logger.info("handleQueryBuilder1: "+ builder.getQueryParameters().get());
		logger.info("handleQueryBuilder1: "+ builder.getQueryParameters().get("age"));
		logger.info("handleQueryBuilder1: "+ builder.getQueryParameters().get("name"));
	}
	
	
}
