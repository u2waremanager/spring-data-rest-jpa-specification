package io.github.u2ware.test.example1;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.support.PredicateBuilder;
import org.springframework.data.rest.core.event.AbstractRepositoryReadEventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.querydsl.core.BooleanBuilder;

@Component
public class FooHandler extends AbstractRepositoryReadEventListener<Foo>{

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	protected void onBeforeRead(Foo foo, Object query) {
		
		logger.info("onBeforeRead: "+ foo);
		logger.info("onBeforeRead: "+ query);
		if(! ClassUtils.isAssignableValue(BooleanBuilder.class, query)) return;
		PredicateBuilder.of((BooleanBuilder)query, Foo.class)
			.where()
				.and().eq("name", foo.get_name())
			.build();
	}
	
	@Override
	protected void onAfterRead(Foo bar) {
		logger.info("onAfterRead: "+ bar);
	}

}
