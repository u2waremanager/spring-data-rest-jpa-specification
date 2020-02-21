package io.github.u2ware.test.example4;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class ManyToOneSample3Processor implements ResourceProcessor<Resource<ManyToOneSample3>>{

	@Override
	public Resource<ManyToOneSample3> process(Resource<ManyToOneSample3> resource) {
		resource.add(new Link("hello", "world"));
		return resource;
	}

}
