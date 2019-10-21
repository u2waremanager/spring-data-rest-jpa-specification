package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.PartTreeQueryBuilder;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleHibernatePostLoad;
import org.springframework.data.rest.core.annotation.HandleHibernatePreLoad;
import org.springframework.data.rest.core.annotation.HandlePartTreeQueryBuilder;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Foo.class)
public class FooHandler2 {

	protected Log logger = LogFactory.getLog(getClass());

	@HandleHibernatePreLoad
	protected void handleHibernatePreLoad(Foo entity) {
		logger.info("handleHibernatePreLoad Foo "+entity.getSeq());
	}
	@HandleHibernatePostLoad
	protected void handleHibernatePostLoad(Foo entity) {
		logger.info("handleHibernatePostLoad Foo "+entity.getSeq());
	}
	
	@HandleBeforeCreate
	protected void handleBeforeCreate(Foo entity) {
		logger.info("handleBeforeCreate");
	}
	
	
	
	@HandlePartTreeQueryBuilder
	protected void handleQueryBuilder(PartTreeQueryBuilder<Foo> builder) {
		
		
		logger.info("handleQueryBuilder2: "+ builder);
		logger.info("handleQueryBuilder2: "+ builder);
		
		
//		builder.and().eq("name");
//		builder.and().like("name");
//		builder.and().gte("name");
//		builder.and().in("name");
//		builder.and().between("name");
//		
//		
//		builder.and().eq("longValue");
//		builder.and().like("longValue"); //-> do not working
//		builder.and().gte("longValue");
//		builder.and().in("longValue");
//		builder.and().between("longValue");
//		
//		builder.and().eq("uriValue");
//		builder.and().like("uriValue"); //-> do not working
//		builder.and().gte("uriValue");
//		builder.and().in("uriValue");
//		builder.and().between("uriValue");
		
	}
	
}
