package io.github.u2ware.test.example4;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class DomainSampleHandler {

	protected Log logger = LogFactory.getLog(getClass());
	
	@HandleBeforeCreate
	protected void handleBeforeCreate(DomainSample e) {
		logger.info("handleBeforeCreate: "+ e);
		logger.info("handleBeforeCreate: "+ e.getBar1());
		logger.info("handleBeforeCreate: "+ e.getBar2());
		logger.info("handleBeforeCreate: "+ e.getBar3());
		logger.info("handleBeforeCreate: "+ e.getBar4());
	}

}
