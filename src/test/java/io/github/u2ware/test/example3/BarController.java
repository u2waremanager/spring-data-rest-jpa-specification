package io.github.u2ware.test.example3;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.AbstractRestController;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
	
	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired BarRepository barRepository;
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
	@RequestMapping(method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForCollectionResource() {
		logger.info("optionsForCollectionResource");
		return super.optionsForAllResource();
	}

	@RequestMapping(method = RequestMethod.HEAD)
	public ResponseEntity<?> headCollectionResource() throws HttpRequestMethodNotSupportedException {
		logger.info("headCollectionResource");
		return super.headForAllResource();
	}
	
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public Resources<?> getCollectionResource(DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler) {
		logger.info("getCollectionResource");
		Iterable<?> results = pageable.getPageable() != null ?  
				barRepository.findAll(pageable.getPageable()) :  
				barRepository.findAll(sort);
		return super.getCollectionResource(results, assembler, pageable);
	}

	
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> postCollectionResource(@RequestBody Bar entity, PersistentEntityResourceAssembler assembler) throws HttpRequestMethodNotSupportedException {
		logger.info("postCollectionResource");
		Object result = barRepository.save(entity);
		return super.getItemResource(result, assembler);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForItemResource() {
		logger.info("optionsForItemResource");
		return super.optionsForAllResource();
	}	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
	public ResponseEntity<?> headForItemResource() throws HttpRequestMethodNotSupportedException {
		logger.info("headForItemResource");
		return super.headForAllResource();
	}	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getItemResource(@PathVariable UUID id, PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers) throws HttpRequestMethodNotSupportedException {
		logger.info("getItemResource");
		return barRepository.findById(id).map(it -> {
			return super.getItemResource(it, assembler, headers);
		}).orElseGet(() ->
			ResponseEntity.notFound().build()
		);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> putItemResource(@RequestBody Bar entity, @PathVariable UUID id, PersistentEntityResourceAssembler assembler) throws HttpRequestMethodNotSupportedException {

		logger.info("putItemResource");
		return barRepository.findById(id).map(it -> {
			Object result = barRepository.save(entity);
			return super.getItemResource(result, assembler);
			
		}).orElseThrow(() -> 
			new ResourceNotFoundException()
		);
	}	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchItemResource(@RequestBody Bar entity, @PathVariable UUID id, PersistentEntityResourceAssembler assembler)throws HttpRequestMethodNotSupportedException, ResourceNotFoundException {
		logger.info("patchItemResource");
		return barRepository.findById(id).map(it -> {
			Object result = barRepository.save(entity);
			return super.getItemResource(result, assembler);
			
		}).orElseThrow(() -> 
			new ResourceNotFoundException()
		);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteItemResource(@PathVariable UUID id) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
		logger.info("deleteItemResource");
		return barRepository.findById(id).map(it -> {
			barRepository.deleteById(id);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		}).orElseThrow(() -> 
			new ResourceNotFoundException()
		);
	}	
	

}