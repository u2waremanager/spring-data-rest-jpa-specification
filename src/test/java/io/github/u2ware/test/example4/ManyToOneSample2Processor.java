package io.github.u2ware.test.example4;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class ManyToOneSample2Processor implements ResourceProcessor<Resource<ManyToOneSample2>>{

	@Override
	public Resource<ManyToOneSample2> process(Resource<ManyToOneSample2> resource) {
		resource.add(new Link("hello", "world"));
		return resource;
	}

}
