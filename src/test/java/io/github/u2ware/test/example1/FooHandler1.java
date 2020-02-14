package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.querydsl.PredicateBuilder;
import org.springframework.data.rest.core.event.AbstractRepositoryReadEventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.querydsl.core.types.Predicate;

@Component
public class FooHandler1 extends AbstractRepositoryReadEventListener<Foo>{

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	protected void onBeforeRead(Foo foo, Object query) {
		
		if(! ClassUtils.isAssignableValue(Predicate.class, query)) return;
		logger.info("FooHandler1: "+ foo);
		logger.info("FooHandler1: "+ query);
		
		Predicate q = (Predicate)query;
		PredicateBuilder.of(q)
			.where(Foo.class).and().eq("name", foo.get_name())
			.build();		
	}
	
	
}
