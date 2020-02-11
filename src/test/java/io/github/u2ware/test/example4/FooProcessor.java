package io.github.u2ware.test.example4;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.webmvc.support.UriLinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class FooProcessor implements ResourceProcessor<Resource<Foo>>{

	protected Log logger = LogFactory.getLog(getClass());
	
	
	@Override
	public Resource<Foo> process(Resource<Foo> resource) {

		if(! resource.hasLinks()) {
			resource.add(UriLinkBuilder.linkTo(Foo.class).slash(resource.getContent().getId()).withSelfRel());
		}
		
		return resource;
	}

}
