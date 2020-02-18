package io.github.u2ware.test.example4;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.support.PredicateBuilder;
import org.springframework.data.rest.core.annotation.HandleAfterRead;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.querydsl.core.BooleanBuilder;


@Component
@RepositoryEventHandler
public class DomainSampleHandler {

	protected Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeCreate
	protected void handleBeforeCreate(DomainSample e) {
		logger.info("handleBeforeCreate "+e);
	}
	
	@HandleAfterRead
	protected void handleAfterRead(DomainSample e) {
		logger.info("handleAfterRead "+e);
	}

	@HandleBeforeRead
	protected void handleBeforeRead(DomainSample e, Object q) {
		logger.info("handleBeforeRead "+e);
		logger.info("handleBeforeRead "+q);
		
		BooleanBuilder root = (BooleanBuilder)q;
		PredicateBuilder.of(root, DomainSample.class).where().and().eq("name", e.getName()).build();
	}
	
	
}
