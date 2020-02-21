package io.github.u2ware.test.example5;


import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.support.PredicateBuilder;
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
		logger.info("handleBeforeCreate: "+ e);
	}

	@HandleBeforeRead
	protected void handleBeforeRead(DomainSample e, Object base) {
		logger.info("handleBeforeRead: "+ e);
		logger.info("handleBeforeRead: "+ e.get_sample4());
//		
//		PredicateBuilder.of((BooleanBuilder)base, DomainSample.class)
//			.where()
//			.and().in("name", e.getNames())
//			.build();
	}
	
}
