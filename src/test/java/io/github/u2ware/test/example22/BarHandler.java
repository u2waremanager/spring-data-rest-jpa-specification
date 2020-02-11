package io.github.u2ware.test.example22;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;


@Component
@RepositoryEventHandler
public class BarHandler {

	protected Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeCreate
	protected void handleBeforeCreate(Bar entity) {
		logger.info("handleBeforeCreate "+entity);
	}
	
	
}
