package io.github.u2ware.test.example4;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class ManyToOneSample1Processor implements ResourceProcessor<Resource<ManyToOneSample1>>{

	@Override
	public Resource<ManyToOneSample1> process(Resource<ManyToOneSample1> resource) {
		resource.add(new Link("hello", "world"));
		return resource;
	}

}
