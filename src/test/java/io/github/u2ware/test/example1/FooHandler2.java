package io.github.u2ware.test.example1;

import static org.assertj.core.api.Assertions.offset;

import javax.persistence.criteria.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.specification.PredicateBuilder;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Component
@RepositoryEventHandler(Foo.class)
public class FooHandler2 {

	protected Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeCreate
	protected void handleBeforeCreate(Foo entity) {
		logger.info("handleBeforeCreate");
	}
	
	
	
	@HandleBeforeRead
	protected void handleBeforeRead(Foo foo, Object query) {
		
		
		if(! ClassUtils.isAssignableValue(Specification.class, query)) return;
		logger.info("FooHandler2: "+ foo);
		logger.info("FooHandler2: "+ query);
		
		Specification<Foo> spec = (Specification)query;
		spec.and((r,c,b)->{
			return BaseBuilder.of(r,c,b).where().and().eq("name", foo.get_name()).build();
		});
	}

}
