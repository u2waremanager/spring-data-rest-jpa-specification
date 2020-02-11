package io.github.u2ware.test.example4;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.support.JPAQueryBuilder;
import org.springframework.data.rest.core.annotation.HandleAfterRead;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.querydsl.jpa.impl.JPAQuery;


@Component
@RepositoryEventHandler
public class BarHandler {

	protected Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeCreate
	protected void handleBeforeCreate(Bar entity) {
		logger.info("handleBeforeCreate "+entity);
	}
	
	@HandleAfterRead
	protected void handleAfterRead(Bar entity) {
		logger.info("handleAfterRead "+entity);
	}

	@HandleBeforeRead
	protected void handleBeforeRead(Bar entity, Object query) {
		logger.info("handleBeforeRead "+entity);
		logger.info("handleBeforeRead "+query);
		
		JPAQuery<Bar> q = (JPAQuery)query;
		JPAQueryBuilder.of(q).from(Bar.class).leftJoin("childs").build();
	}
	
	
}
