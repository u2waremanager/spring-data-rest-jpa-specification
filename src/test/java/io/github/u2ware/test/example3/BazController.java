package io.github.u2ware.test.example3;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.AbstractRestController;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@BasePathAwareController
@RequestMapping("/bazes")
public class BazController extends AbstractRestController<Baz, UUID>{
	

	@RequestMapping(method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForCollectionResource() {
		return super.optionsFor();
	}

	@RequestMapping(method = RequestMethod.HEAD)
	public ResponseEntity<?> headForCollectionResource() throws HttpRequestMethodNotSupportedException {
		return super.headFor();
	}
	
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public Resources<?> getCollectionResource(DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler) throws Exception {
		ArrayList<Baz> source = new ArrayList<>();
		source.add(new Baz(UUID.randomUUID(), "a", 1));
		source.add(new Baz(UUID.randomUUID(), "b", 1));
		source.add(new Baz(UUID.randomUUID(), "c", 1));
		
		//return toResources(source, assembler, Baz.class, Optional.empty());
		return new Resources<>(source, getDefaultSelfLink());
	}
}
