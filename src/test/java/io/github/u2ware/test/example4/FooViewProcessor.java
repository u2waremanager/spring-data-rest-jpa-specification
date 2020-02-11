package io.github.u2ware.test.example4;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class FooViewProcessor implements ResourceProcessor<Resource<FooView>>{

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	public Resource<FooView> process(Resource<FooView> resource) {

		if(! resource.hasLinks()) {
			//resource.add(UriLinkBuilder.linkTo(FooView.class).slash(resource.getContent().getId()).withSelfRel());
		}
		
		return resource;
	}

}
