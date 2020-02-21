package io.github.u2ware.test.example4;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class ManyToOneSample4Processor implements ResourceProcessor<Resource<ManyToOneSample4>>{

	@Override
	public Resource<ManyToOneSample4> process(Resource<ManyToOneSample4> resource) {
		resource.add(new Link("hello", "world"));
		return resource;
	}

}
