package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.JpaSpecification;
import org.springframework.data.rest.core.event.RepositoryRestEventHandler;
import org.springframework.stereotype.Component;

@Component
public class BazHandler1 extends RepositoryRestEventHandler<Baz>{

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	protected void handleBeforeCreate(Baz entity) {
		logger.info("handleBeforeCreate");
	}
	
	
	@Override
	protected void handleBeforeRead(JpaSpecification<Baz> spec) {
		logger.info("handleBeforeRead: "+ spec.getPayload());
		
		Baz p = spec.getPayload();
		if(p == null) return;
		
		spec.getPredicateBuilder()
			.eq("age", p.getAge())
		;
	}
	
}
