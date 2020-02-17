package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.PredicateQueryBuilder;
import org.springframework.data.rest.core.annotation.HandleAfterRead;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;


@Component
@RepositoryEventHandler
public class BarHandler {

	protected Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeRead
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void handleBeforeRead(Bar bar, Object query) {
		
		logger.info("handleBeforeRead: "+ bar);
		logger.info("handleBeforeRead: "+ query);
		
		if(! ClassUtils.isAssignableValue(Specification.class, query)) return;
		Specification<Bar> spec = (Specification)query;
		spec.and((r,c,b)->{
			return PredicateQueryBuilder.of(r,c,b).where().and().eq("name", bar.getName()).build();
		});
		
	}
	
	@HandleAfterRead
	protected void handleAfterRead(Bar bar) {
		logger.info("handleAfterRead: "+ bar);
	}
	
}
