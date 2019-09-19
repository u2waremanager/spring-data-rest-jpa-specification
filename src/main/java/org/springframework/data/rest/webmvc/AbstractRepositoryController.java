package org.springframework.data.rest.webmvc;

import static org.springframework.data.rest.webmvc.ControllerUtils.EMPTY_RESOURCE_LIST;
import static org.springframework.http.HttpMethod.PUT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.core.mapping.SupportedHttpMethods;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractRepositoryController {

	protected Log logger = LogFactory.getLog(getClass());
	
	protected @Autowired ApplicationEventPublisher publisher;
	protected @Autowired RepositoryEntityLinks entityLinks;
	protected @Autowired RepositoryRestConfiguration config;
	protected @Autowired HttpHeadersPreparer headersPreparer;
	protected @Autowired ResourceMappings mappings;
	protected @Autowired Repositories repositories;
	protected @Autowired RepositoryInvokerFactory invokerFactory;
	protected @Autowired PagedResourcesAssembler<Object> pagedResourcesAssembler;
	protected @Autowired ObjectMapper objectMapper;
	
	protected @Autowired ResourceLoader resourceLoader;
	protected @Autowired PersistentEntities entities;
	
	private ResourceStatus resourceStatus;
	
//	private @Autowired Associations associationLinks;
//	PersistentEntities entities;
//	RepositoryInvokerFactory invokerFactory,
//	Repositories repositories

//	private RepositoryEntityController entityController;
//	private RepositorySearchController searchController;
//	private static UriToEntityConverter uriToEntityConverter;

	protected static final EmbeddedWrappers WRAPPERS = new EmbeddedWrappers(false);
	protected static final String ACCEPT_HEADER = "Accept";
	protected static final String LINK_HEADER = "Link";
	protected static final List<String> ACCEPT_PATCH_HEADERS = Arrays.asList(//
			RestMediaTypes.MERGE_PATCH_JSON.toString(), //
			RestMediaTypes.JSON_PATCH_JSON.toString(), //
			MediaType.APPLICATION_JSON_VALUE);
	
	
	protected ResourceStatus getResourceStatus() {
		if(this.resourceStatus == null) {
			this.resourceStatus = ResourceStatus.of(headersPreparer);
		}
		return this.resourceStatus;
	}
	
	
	@SuppressWarnings("unchecked")
	protected <R> R getRepositoryFor(RootResourceInformation information, Class<R> returnType) {
		return repositories.getRepositoryFor(information.getDomainType()).map(repository -> {
			if (repository == null || !ClassUtils.isAssignableValue(returnType, repository)) {
				throw new ResourceNotFoundException(returnType+ " is not Found: "+information);
			}
			return (R)repository;
		}).orElseThrow(() -> new ResourceNotFoundException(returnType+ " is not Found: "+information));
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
	protected ResponseEntity<?> toResource(Optional<Object> source, final PersistentEntityResourceAssembler assembler,
			Class<?> domainType, Optional<Link> baseLink, HttpHeaders headers, RootResourceInformation information) {

		return source.map(it -> {

			if (it instanceof Iterable) {
				return ResponseEntity.ok(toResources((Iterable<?>) it, assembler, domainType, baseLink));
			} else if (ClassUtils.isPrimitiveOrWrapper(it.getClass())) {
				return ResponseEntity.ok(it);
			}

			PersistentEntity<?, ?> entity = information.getPersistentEntity();

			// Returned value is not of the aggregates type - probably some projection
			if (!entity.getType().isInstance(it)) {
				return ResponseEntity.ok(it);
			}

			return getResourceStatus().getStatusAndHeaders(headers, it, entity).toResponseEntity(//
					() -> assembler.toFullResource(it));

		}).orElseThrow(() -> new ResourceNotFoundException());
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Resources<?> toResources(Iterable<?> source, PersistentEntityResourceAssembler assembler,
			Class<?> domainType, Optional<Link> baseLink) {

		if (source instanceof Page) {
			Page<Object> page = (Page<Object>) source;
			return entitiesToResources(page, assembler, domainType, baseLink);
		} else if (source instanceof Iterable) {
			return entitiesToResources((Iterable<Object>) source, assembler, domainType);
		} else {
			return new Resources(EMPTY_RESOURCE_LIST);
		}
	}

	private Resources<?> entitiesToResources(Page<Object> page, PersistentEntityResourceAssembler assembler,
			Class<?> domainType, Optional<Link> baseLink) {

		if (page.getContent().isEmpty()) {
			return baseLink.<PagedResources<?>> map(it -> pagedResourcesAssembler.toEmptyResource(page, domainType, it))//
					.orElseGet(() -> pagedResourcesAssembler.toEmptyResource(page, domainType));
		}

		return baseLink.map(it -> pagedResourcesAssembler.toResource(page, assembler, it))//
				.orElseGet(() -> pagedResourcesAssembler.toResource(page, assembler));
	}

	private Resources<?> entitiesToResources(Iterable<Object> entities, PersistentEntityResourceAssembler assembler,
			Class<?> domainType) {

		if (!entities.iterator().hasNext()) {

			List<Object> content = Arrays.<Object> asList(WRAPPERS.emptyCollectionOf(domainType));
			return new Resources<Object>(content, getDefaultSelfLink());
		}

		List<Resource<Object>> resources = new ArrayList<Resource<Object>>();

		for (Object obj : entities) {
			resources.add(obj == null ? null : assembler.toResource(obj));
		}

		return new Resources<Resource<Object>>(resources, getDefaultSelfLink());
	}
	
	
	protected List<Link> getCollectionResourceLinks(ResourceMetadata metadata, DefaultedPageable pageable) {
		List<Link> links = new ArrayList<Link>();
		if(metadata == null) return links;
		
		SearchResourceMappings searchMappings = metadata.getSearchResourceMappings();
		links.add(new Link(ProfileController.getPath(this.config, metadata), ProfileResourceProcessor.PROFILE_REL));
		if (searchMappings.isExported()) {
			links.add(entityLinks.linkFor(metadata.getDomainType()).slash(searchMappings.getPath()).withRel(searchMappings.getRel()));
		}
		return links;
	}
	
	protected Link getDefaultSelfLink() {
		return new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
	}

	
//	private static final String BASE_MAPPING = "/{repository}/!s";
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////

//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.OPTIONS)
//	protected ResponseEntity<?> optionsForCollectionResource(RootResourceInformation information) {
//		return optionsForCollectionResource(information.getResourceMetadata());
//	}
	protected ResponseEntity<?> optionsForCollectionResource(ResourceMetadata metadata) {
		HttpHeaders headers = new HttpHeaders();
		SupportedHttpMethods supportedMethods = metadata.getSupportedHttpMethods();
		headers.setAllow(supportedMethods.getMethodsFor(ResourceType.COLLECTION).toSet());
		//return ResponseEntity.ok().headers(headers).build();
		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}
	
	protected ResponseEntity<?> optionsFor() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAllow(Collections.singleton(HttpMethod.GET));
		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.HEAD)
//	protected ResponseEntity<?> headForCollectionResource(RootResourceInformation resourceInformation, DefaultedPageable pageable) throws HttpRequestMethodNotSupportedException {
//
//		resourceInformation.verifySupportedMethod(HttpMethod.HEAD, ResourceType.COLLECTION);
//		RepositoryInvoker invoker = resourceInformation.getInvoker();
//		if (null == invoker) {
//			throw new ResourceNotFoundException();
//		}
//		return headForCollectionResource(pageable, resourceInformation.getResourceMetadata());
//	}
	
	protected ResponseEntity<?> headForCollectionResource(DefaultedPageable pageable, ResourceMetadata metadata) throws HttpRequestMethodNotSupportedException {
		List<Link> links = getCollectionResourceLinks(metadata, pageable);
		links.add(0, getDefaultSelfLink());

		HttpHeaders headers = new HttpHeaders();
		headers.add(LINK_HEADER, new Links(links).toString());

		return new ResponseEntity<Object>(headers, HttpStatus.NO_CONTENT);
	}
	
	protected ResponseEntity<?> headFor() throws HttpRequestMethodNotSupportedException {
		return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		//return ResponseEntity.noContent().build();
	}

	
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET)
//	protected Resources<?> getCollectionResource(/*@QuerydslPredicate*/ RootResourceInformation resourceInformation, DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
//		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.COLLECTION);
//		RepositoryInvoker invoker = resourceInformation.getInvoker();
//		if (null == invoker) {
//			throw new ResourceNotFoundException();
//		}
//		
//		Iterable<?> results = pageable.getPageable() != null ? invoker.invokeFindAll(pageable.getPageable()) : invoker.invokeFindAll(sort);
//		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
//		return getCollectionResource(pageable, assembler, results, metadata);
//	}
	
	protected Resources<?> getCollectionResource(DefaultedPageable pageable, PersistentEntityResourceAssembler assembler, Iterable<?> results, ResourceMetadata metadata) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
		
		if(metadata == null) {
			
			logger.info(pageable);
			logger.info(assembler);
			logger.info(results);
			logger.info(metadata);
			
			Resources<?> result = toResources(results, assembler, Map.class, Optional.empty());
			result.add(getCollectionResourceLinks(metadata, pageable));
			return result;
		}
		
		Optional<Link> baseLink = Optional.of(entityLinks.linkToPagedResource(metadata.getDomainType(), pageable.isDefault() ? null : pageable.getPageable()));
		Resources<?> result = toResources(results, assembler, metadata.getDomainType(), baseLink);
		result.add(getCollectionResourceLinks(metadata, pageable));
		return result;
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.OPTIONS)
//	public ResponseEntity<?> optionsForItemResource(RootResourceInformation information) {
//		return optionsForItemResource(information.getResourceMetadata());
//	}
	
	public ResponseEntity<?> optionsForItemResource(ResourceMetadata metadata) {

		HttpHeaders headers = new HttpHeaders();
		SupportedHttpMethods supportedMethods = metadata.getSupportedHttpMethods();

		headers.setAllow(supportedMethods.getMethodsFor(ResourceType.ITEM).toSet());
		headers.put("Accept-Patch", ACCEPT_PATCH_HEADERS);

		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.GET)
//	public ResponseEntity<Resource<?>> getItemResource(RootResourceInformation resourceInformation,
//			@BackendId Serializable id, final PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers)
//			throws HttpRequestMethodNotSupportedException {
//
//		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.ITEM);
//		
//		Optional<?> result = resourceInformation.getInvoker().invokeFindById(id);
//		
//		return getItemResource(assembler, headers, result, resourceInformation.getPersistentEntity());
//	}
	
	public ResponseEntity<Resource<?>> getItemResource(PersistentEntityResourceAssembler assembler, HttpHeaders headers, Optional<?> result, final PersistentEntity<?,?> entity) throws HttpRequestMethodNotSupportedException {
		return result.map(it -> {
			return getResourceStatus().getStatusAndHeaders(headers, it, entity).toResponseEntity(() -> assembler.toFullResource(it));

		}).orElseGet(() -> new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND));
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.HEAD)
//	public ResponseEntity<?> headForItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id, PersistentEntityResourceAssembler assembler) throws HttpRequestMethodNotSupportedException {
//		Optional<?> result = resourceInformation.getInvoker().invokeFindById(id);
//		return headForItemResource(assembler, result, resourceInformation.getPersistentEntity());
//	}
	
	public ResponseEntity<?> headForItemResource(PersistentEntityResourceAssembler assembler, Optional<?> result, final PersistentEntity<?,?> entity) throws HttpRequestMethodNotSupportedException {
		return result.map(it -> {
			Links links = new Links(assembler.toResource(it).getLinks());
			HttpHeaders headers = headersPreparer.prepareHeaders(entity, it);
			headers.add(LINK_HEADER, links.toString());
			return new ResponseEntity<Object>(headers, HttpStatus.NO_CONTENT);
		}).orElseThrow(() -> new ResourceNotFoundException());
	}

	
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.DELETE)
//	public ResponseEntity<?> deleteItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id,
//			ETag eTag) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
//
//		resourceInformation.verifySupportedMethod(HttpMethod.DELETE, ResourceType.ITEM);
//
//		RepositoryInvoker invoker = resourceInformation.getInvoker();
//
//		Object domainObj = invoker.invokeFindById(id).map(it -> {
//			
//			PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();
//			eTag.verify(entity, it);
//
//			publisher.publishEvent(new BeforeDeleteEvent(it));
//			invoker.invokeDeleteById(entity.getIdentifierAccessor(it).getIdentifier());
//			publisher.publishEvent(new AfterDeleteEvent(it));
//			
//			return it;
//		}).orElseThrow(()-> null);
//		
//		return deleteItemResource(Optional.of(domainObj));
//	}

	
	public ResponseEntity<?> deleteItemResource(Object result) {
		return Optional.of(result).map(it -> {
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		}).orElseThrow(() -> new ResourceNotFoundException());
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST)
//	public ResponseEntity<ResourceSupport> postCollectionResource(RootResourceInformation resourceInformation,
//			PersistentEntityResource payload, PersistentEntityResourceAssembler assembler,
//			@RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
//			throws HttpRequestMethodNotSupportedException {
//
//		resourceInformation.verifySupportedMethod(HttpMethod.POST, ResourceType.COLLECTION);
//
//		Object domainObject = payload.getContent();
//		publisher.publishEvent(new BeforeCreateEvent(domainObject));
//		Object savedObject = resourceInformation.getInvoker().invokeSave(domainObject);
//		publisher.publishEvent(new AfterCreateEvent(savedObject));
//
//		return createAndReturn(savedObject, assembler, config.returnBodyOnCreate(acceptHeader));
//	}

	public ResponseEntity<ResourceSupport> postCollectionResource(
			PersistentEntityResourceAssembler assembler,
			@RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader, Object savedObject)
			throws HttpRequestMethodNotSupportedException {
		return createAndReturn(savedObject, assembler, config.returnBodyOnCreate(acceptHeader));
	}
	
	
	private ResponseEntity<ResourceSupport> createAndReturn(Object savedObject, PersistentEntityResourceAssembler assembler, boolean returnBody) {
		Optional<PersistentEntityResource> resource = Optional
				.ofNullable(returnBody ? assembler.toFullResource(savedObject) : null);

		HttpHeaders headers = headersPreparer.prepareHeaders(resource);
		addLocationHeader(headers, assembler, savedObject);

		return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, resource);
	}
	
	private ResponseEntity<ResourceSupport> saveAndReturn(Object obj, 
			HttpMethod httpMethod, PersistentEntityResourceAssembler assembler, boolean returnBody) {

		PersistentEntityResource resource = assembler.toFullResource(obj);
		HttpHeaders headers = headersPreparer.prepareHeaders(Optional.of(resource));

		if (PUT.equals(httpMethod)) {
			addLocationHeader(headers, assembler, obj);
		}

		if (returnBody) {
			return ControllerUtils.toResponseEntity(HttpStatus.OK, headers, resource);
		} else {
			return ControllerUtils.toEmptyResponse(HttpStatus.NO_CONTENT, headers);
		}
	}
	
	private void addLocationHeader(HttpHeaders headers, PersistentEntityResourceAssembler assembler, Object source) {
		String selfLink = assembler.getSelfLinkFor(source).getHref();
		headers.setLocation(new UriTemplate(selfLink).expand());
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PUT)
//	public ResponseEntity<? extends ResourceSupport> putItemResource(RootResourceInformation resourceInformation,
//			PersistentEntityResource payload, @BackendId Serializable id, PersistentEntityResourceAssembler assembler,
//			ETag eTag, @RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
//			throws HttpRequestMethodNotSupportedException {
//
//		resourceInformation.verifySupportedMethod(HttpMethod.PUT, ResourceType.ITEM);
//
//		if (payload.isNew()) {
//			resourceInformation.verifyPutForCreation();
//		}
//
//		RepositoryInvoker invoker = resourceInformation.getInvoker();
//		Object objectToSave = payload.getContent();
//		eTag.verify(resourceInformation.getPersistentEntity(), objectToSave);
//
//		if(payload.isNew()) {
//			
//			publisher.publishEvent(new BeforeCreateEvent(objectToSave));
//			Object savedObject = resourceInformation.getInvoker().invokeSave(objectToSave);
//			publisher.publishEvent(new AfterCreateEvent(savedObject));
//
//			return createAndReturn(savedObject, assembler, config.returnBodyOnCreate(acceptHeader));
//			
//		} else {
//			publisher.publishEvent(new BeforeSaveEvent(objectToSave));
//			Object obj = invoker.invokeSave(objectToSave);
//			publisher.publishEvent(new AfterSaveEvent(obj));
//
//			return saveAndReturn(obj, HttpMethod.PUT, assembler, config.returnBodyOnCreate(acceptHeader));
//		}
//	}
	
	public ResponseEntity<ResourceSupport> putItemResource(
			PersistentEntityResourceAssembler assembler,
			@RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader, Object savedObject)
			throws HttpRequestMethodNotSupportedException {
		return saveAndReturn(savedObject, HttpMethod.PUT, assembler, config.returnBodyOnCreate(acceptHeader));
	}
	

	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PATCH)
//	public ResponseEntity<ResourceSupport> patchItemResource(RootResourceInformation resourceInformation,
//			PersistentEntityResource payload, @BackendId Serializable id, PersistentEntityResourceAssembler assembler,
//			ETag eTag, @RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
//			throws HttpRequestMethodNotSupportedException, ResourceNotFoundException {
//
//		resourceInformation.verifySupportedMethod(HttpMethod.PATCH, ResourceType.ITEM);
//
//		Object domainObject = payload.getContent();
//
//		eTag.verify(resourceInformation.getPersistentEntity(), domainObject);
//
//		publisher.publishEvent(new BeforeSaveEvent(domainObject));
//		Object obj = resourceInformation.getInvoker().invokeSave(domainObject);
//		publisher.publishEvent(new AfterSaveEvent(obj));
//		
//		return saveAndReturn(obj, HttpMethod.PATCH, assembler, config.returnBodyOnUpdate(acceptHeader));
//	}
	
	
	public ResponseEntity<ResourceSupport> patchItemResource(
			PersistentEntityResourceAssembler assembler,
			@RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader, Object savedObject)
			throws HttpRequestMethodNotSupportedException {
		return saveAndReturn(savedObject, HttpMethod.PATCH, assembler, config.returnBodyOnCreate(acceptHeader));
	}
}
