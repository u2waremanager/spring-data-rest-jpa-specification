package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.JpaSpecification;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Baz.class)
public class BazHandler2 {

	protected Log logger = LogFactory.getLog(getClass());
	
	@HandleBeforeCreate
	protected void handleBeforeCreate(Baz entity) {
		logger.info("handleBeforeCreate");
	}
	

	@HandleBeforeRead
	protected void handleBeforeRead(JpaSpecification<Baz> spec) {
		logger.info("handleBeforeRead: "+ spec.getPayload());
		
		Baz p = spec.getPayload();
		if(p == null) return;
		
		spec.getPredicateBuilder()
			.eq("name", p.getName())
		;
	}
	
}
