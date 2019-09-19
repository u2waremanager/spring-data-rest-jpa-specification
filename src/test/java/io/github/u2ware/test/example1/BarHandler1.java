package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.JpaSpecification;
import org.springframework.data.rest.core.event.RepositoryRestEventHandler;
import org.springframework.stereotype.Component;

@Component
public class BarHandler1 extends RepositoryRestEventHandler<Bar>{

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	protected void handleBeforeCreate(Bar entity) {
		logger.info("handleBeforeCreate");
	}
	
	
	@Override
	protected void handleBeforeRead(JpaSpecification<Bar> spec) {
		logger.info("handleBeforeRead: "+ spec);
	}
	
}
