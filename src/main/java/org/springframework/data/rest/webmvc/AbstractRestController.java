package org.springframework.data.rest.webmvc;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.ETag;
import org.springframework.data.rest.webmvc.support.PersistentEntityProjector;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestHeader;


public abstract class AbstractRestController<T,ID extends Serializable> extends AbstractRepositoryController{

	private final Class<?> DOMAIN_TYPE = GenericTypeResolver.resolveTypeArguments(getClass(), AbstractRestController.class)[0];

	
	@SuppressWarnings("unchecked")
	protected Class<T> getDomainType(){
		return (Class<T>) DOMAIN_TYPE;
	}
	
	/////////////////////////////////////////////////////////////////////
	// RootResourceInformationHandlerMethodArgumentResolver
	// PersistentEntityResourceAssemblerArgumentResolver
	// ETagArgumentResolver
	////////////////////////////////////////////////////////////////////
	protected ResourceMetadata metadata() {
		return mappings.getMetadataFor(getDomainType());
	}
	protected PersistentEntity<?,?> entity() {
		return entities.getPersistentEntity(getDomainType()).get();
	}
	protected RepositoryInvoker invoker() {
		return invokerFactory.getInvokerFor(getDomainType());
	}
	protected RootResourceInformation information() {
		return new RootResourceInformation(metadata(), entity(), invoker());
	}
	protected String acceptHeader() {
		return null;
	}
	protected ETag eTag() {
		return ETag.NO_ETAG;
	}
	
	protected PersistentEntityResourceAssembler assembler() {
		PersistentEntityProjector projector = new PersistentEntityProjector(projectionDefinitions, projectionFactory, null, links.getMappings());
		return new PersistentEntityResourceAssembler(entities, projector, links, linkProvider);
	}
	
	/////////////////////////////////////////////////////////////////////
	// Support
	////////////////////////////////////////////////////////////////////

	protected Resources<?> getCollectionResource(Iterable<?> results, PersistentEntityResourceAssembler assembler, DefaultedPageable pageable){
		Optional<Link> baseLink = Optional.of(entityLinks.linkToPagedResource(getDomainType(), pageable.isDefault() ? null : pageable.getPageable()));
		return toResources(results, assembler, getDomainType(), baseLink);
	}

	protected ResponseEntity<?> getItemResource(Object result, PersistentEntityResourceAssembler assembler){
	
		PersistentEntityResource resource = assembler.toFullResource(result);
		
		HttpHeaders headers = headersPreparer.prepareHeaders(Optional.of(resource));
		addLocationHeader(headers, assembler, result);
		
		return new ResponseEntity<ResourceSupport>(resource, headers, HttpStatus.CREATED);
	}
	
	protected ResponseEntity<?> getItemResource(Object result, PersistentEntityResourceAssembler assembler, HttpHeaders headers){
		return getResourceStatus()
				.getStatusAndHeaders(headers, result, entity())
				.toResponseEntity(() -> assembler.toFullResource(result));
	}
	
	/////////////////////////////////////////////////////////////////////
	// Display
	////////////////////////////////////////////////////////////////////
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForCollectionResource(RootResourceInformation information) {
		return super.optionsForCollectionResource(information);
	}	
	
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.HEAD)
	public ResponseEntity<?> headCollectionResource(RootResourceInformation resourceInformation, DefaultedPageable pageable) throws HttpRequestMethodNotSupportedException {
		return super.headCollectionResource(resourceInformation, pageable);
	}
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET)
	public Resources<?> getCollectionResource(@QuerydslPredicate RootResourceInformation resourceInformation, DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
		return super.getCollectionResource(resourceInformation, pageable, sort, assembler);
	}
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET, produces = { "application/x-spring-data-compact+json", "text/uri-list" })
	public Resources<?> getCollectionResourceCompact(@QuerydslPredicate RootResourceInformation resourceinformation, DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
		return super.getCollectionResourceCompact(resourceinformation, pageable, sort, assembler);
	}
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST)
	public ResponseEntity<ResourceSupport> postCollectionResource(RootResourceInformation resourceInformation, PersistentEntityResource payload, PersistentEntityResourceAssembler assembler, @RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader) throws HttpRequestMethodNotSupportedException {
		return super.postCollectionResource(resourceInformation, payload, assembler, acceptHeader);
	}
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForItemResource(RootResourceInformation information) {
		return super.optionsForItemResource(information);
	}	
	
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.HEAD)
	public ResponseEntity<?> headForItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id, PersistentEntityResourceAssembler assembler) throws HttpRequestMethodNotSupportedException {
		return super.headForItemResource(resourceInformation, id, assembler);
	}	
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Resource<?>> getItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id, final PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers) throws HttpRequestMethodNotSupportedException {
		return super.getItemResource(resourceInformation, id, assembler, headers);
	}
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<? extends ResourceSupport> putItemResource(RootResourceInformation resourceInformation, PersistentEntityResource payload, @BackendId Serializable id, PersistentEntityResourceAssembler assembler, ETag eTag, @RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader) throws HttpRequestMethodNotSupportedException {
		return super.putItemResource(resourceInformation, payload, id, assembler, eTag, acceptHeader);
	}	
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<ResourceSupport> patchItemResource(RootResourceInformation resourceInformation, PersistentEntityResource payload, @BackendId Serializable id, PersistentEntityResourceAssembler assembler, ETag eTag, @RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)throws HttpRequestMethodNotSupportedException, ResourceNotFoundException {
		return super.patchItemResource(resourceInformation, payload, id, assembler, eTag, acceptHeader);
	}
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id, ETag eTag) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
		return super.deleteItemResource(resourceInformation, id, eTag);
	}	
}
