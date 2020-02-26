package io.github.u2ware.test.example4;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.support.PredicateBuilder;
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
		
				
		PredicateBuilder.of((BooleanBuilder)base, DomainSample.class)
			.where()
			.and().eq("sample4", e.getSample4())
			.and().eq("sample3.name", e.getSample3Name())
			.and().in("sample3.name", e.getSample3Names())
			.build();
	}
}
