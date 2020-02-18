package io.github.u2ware.test.example4;

import org.springframework.data.rest.webmvc.support.UriLinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class ManyToOnePhysicalColumn4Processor implements ResourceProcessor<Resource< ManyToOnePhysicalColumn4>>{

	@Override
	public Resource<ManyToOnePhysicalColumn4> process(Resource<ManyToOnePhysicalColumn4> resource) {

		if(! resource.hasLinks()) {
			resource.add(UriLinkBuilder.linkTo(ManyToOnePhysicalColumn4.class).slash(resource.getContent().getId()).withSelfRel());
		}
		
		return resource;
	}

}
