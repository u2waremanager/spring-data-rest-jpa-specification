package io.github.u2ware.test.example3;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.AbstractRestController;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@BasePathAwareController
@RequestMapping("/bars")
public class BarController extends AbstractRestController<Bar, UUID>{
	
	private @Autowired BarRepository barRepository;
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
	@RequestMapping(method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForCollectionResource() {
		return super.optionsForCollectionResource();
	}

	@RequestMapping(method = RequestMethod.HEAD)
	public ResponseEntity<?> headForCollectionResource(DefaultedPageable pageable) throws HttpRequestMethodNotSupportedException {
		return super.headForCollectionResource(pageable);
	}
	
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public Resources<?> getCollectionResource(DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler) throws Exception {
		Iterable<?> results = pageable.getPageable() != null ?  barRepository.findAll(pageable.getPageable()) :  barRepository.findAll(sort);
		return getCollectionResource(pageable, assembler, results);
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ResourceSupport> postFor(@RequestBody Bar bar, PersistentEntityResourceAssembler assembler,  @RequestHeader HttpHeaders headers) throws HttpRequestMethodNotSupportedException {
		Object result = barRepository.save(bar);
		return postCollectionResource(assembler, headers, result);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForItemResource() {
		return super.optionsForItemResource();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
	public ResponseEntity<?> headForItemResource(@PathVariable UUID id, PersistentEntityResourceAssembler assembler) throws HttpRequestMethodNotSupportedException {
		Optional<?> result = barRepository.findById(id);
		return headForItemResource(assembler, result);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Resource<?>> getItemResource(@PathVariable UUID id, PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers) throws HttpRequestMethodNotSupportedException {
		Optional<?> result = barRepository.findById(id);
		return getItemResource(assembler, headers, result);
	}
	

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> putFor(@RequestBody Bar bar, PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers) throws HttpRequestMethodNotSupportedException {
		Object savedObject = barRepository.save(bar);
		return super.putItemResource(assembler, headers, savedObject);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchFor(@RequestBody Bar bar, PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers) throws HttpRequestMethodNotSupportedException {
		Object savedObject = barRepository.save(bar);
		return super.patchItemResource(assembler, headers, savedObject);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteFor(@PathVariable UUID id, PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers) throws HttpRequestMethodNotSupportedException {
		Object deletedObject = barRepository.findById(id).map(it->{ barRepository.deleteById(id); return it; }).orElseThrow(()-> null);
		return deleteItemResource(deletedObject);
	}
}