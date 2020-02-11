package io.github.u2ware.test.example4;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.webmvc.support.UriLinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import io.github.u2ware.test.example4.Bar.Child;

@Component
public class BarChildProcessor implements ResourceProcessor<Resource<Child>>{

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	public Resource<Child> process(Resource<Child> resource) {

		if(! resource.hasLinks()) {
			resource.add(UriLinkBuilder.linkTo(Child.class).slash(resource.getContent().getSeq()).withSelfRel());
		}
		
		return resource;
	}

}
