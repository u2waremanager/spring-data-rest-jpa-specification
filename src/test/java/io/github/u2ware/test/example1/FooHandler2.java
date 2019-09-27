package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.PredicateBuilder;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandlePredicateBuilder;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Foo.class)
public class FooHandler2 {

	protected Log logger = LogFactory.getLog(getClass());
	
	@HandleBeforeCreate
	protected void handleBeforeCreate(Foo entity) {
		logger.info("handleBeforeCreate");
	}
	
	
	@HandlePredicateBuilder
	protected void handlePredicateBuilder(PredicateBuilder<Foo> builder) {
		
		
		logger.info("handleBeforeRead2: "+ builder.getRequestParamToEntity());
		logger.info("handleBeforeRead2: "+ builder.getRequestParam());
		
		
		builder.and().eq("name");
		builder.and().like("name");
		builder.and().gte("name");
		builder.and().in("name");
		builder.and().between("name");
		
		
		builder.and().eq("longValue");
		builder.and().like("longValue"); //-> do not working
		builder.and().gte("longValue");
		builder.and().in("longValue");
		builder.and().between("longValue");
		
		builder.and().eq("uriValue");
		builder.and().like("uriValue"); //-> do not working
		builder.and().gte("uriValue");
		builder.and().in("uriValue");
		builder.and().between("uriValue");
		
	}
	
}
