package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.PredicateBuilder;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
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
	
	
	@HandleBeforeRead
	protected void handleBeforeRead(Foo entity, PredicateBuilder<Foo> builder) {
		
		logger.info("handleBeforeRead: "+ entity);
		logger.info("handleBeforeRead: "+ builder.getParameters());

		builder.and().eq("name", builder.getParameters().get("name"));
		builder.and().like("name", builder.getParameters().get("name"));
		builder.and().gte("name", builder.getParameters().get("name"));
		builder.and().in("name", builder.getParameters().get("name"));
		builder.and().between("name", builder.getParameters().get("name"));
		

		
		builder.and().eq("longValue", builder.getParameters().get("longValue"));
		builder.and().like("longValue", builder.getParameters().get("longValue")); //-> do not working
		builder.and().gte("longValue", builder.getParameters().get("longValue"));
		builder.and().in("longValue", builder.getParameters().get("longValue"));
		builder.and().between("longValue", builder.getParameters().get("longValue"));
		
		builder.and().eq("uriValue", builder.getParameters().get("uriValue"));
		builder.and().like("uriValue", builder.getParameters().get("uriValue")); //-> do not working
		builder.and().gte("uriValue", builder.getParameters().get("uriValue"));
		builder.and().in("uriValue", builder.getParameters().get("uriValue"));
		builder.and().between("uriValue", builder.getParameters().get("uriValue"));
		
	}
	
}
