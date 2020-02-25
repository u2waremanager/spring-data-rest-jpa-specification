package io.github.u2ware.test.example5;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;


@Component
@RepositoryEventHandler
public class DomainSampleHandler {

	protected Log logger = LogFactory.getLog(getClass());
	
	@HandleBeforeCreate
	protected void handleBeforeCreate(DomainSample e) {
		logger.info("handleBeforeCreate: "+ e);
//		e.setSample2Response(e.getSample2Request());
//		e.setSample4Response(e.getSample4Request());
	}

	@HandleBeforeSave
	protected void handleBeforeSave(DomainSample e) {
		logger.info("handleBeforeSave: "+ e);
//		e.setSample2Response(e.getSample2Request());
//		e.setSample4Response(e.getSample4Request());
	}
	
	
	@HandleBeforeRead
	protected void handleBeforeRead(DomainSample e, Object base) {
		logger.info("handleBeforeRead: "+ e);
//		
//		PredicateBuilder.of((BooleanBuilder)base, DomainSample.class)
//			.where()
//			.and().in("name", e.getNames())
//			.build();
	}
	
}
