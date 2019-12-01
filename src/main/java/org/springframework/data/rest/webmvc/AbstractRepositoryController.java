package org.springframework.data.rest.webmvc;

import static org.springframework.data.rest.webmvc.ControllerUtils.EMPTY_RESOURCE_LIST;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterDeleteEvent;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.core.mapping.SupportedHttpMethods;
import org.springframework.data.rest.core.projection.ProjectionDefinitions;
import org.springframework.data.rest.core.support.SelfLinkProvider;
import org.springframework.data.rest.webmvc.mapping.Associations;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.ETag;
import org.springframework.data.rest.webmvc.support.ETagDoesntMatchException;
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
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({ "unchecked" , "rawtypes"})
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
	
	
	protected @Autowired SelfLinkProvider linkProvider;
	protected ProjectionDefinitions projectionDefinitions;
	protected ProjectionFactory projectionFactory;
	protected @Autowired Associations links;

	
	private ResourceStatus resourceStatus;
	
	
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
	
	protected <R> R getRepositoryFor(RootResourceInformation information, Class<R> returnType) {
		return repositories.getRepositoryFor(information.getDomainType()).map(repository -> {
			if (repository == null || !ClassUtils.isAssignableValue(returnType, repository)) {
				throw new ResourceNotFoundException(returnType+ " is not Found: "+information);
			}
			return (R)repository;
		}).orElseThrow(() -> new ResourceNotFoundException(returnType+ " is not Found: "+information));
	}
	
	/////////////////////////////////////////////////////////////////////
	// Addon
	////////////////////////////////////////////////////////////////////
	public ResponseEntity<?> optionsForAllResource() {
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}
	public ResponseEntity<?> headForAllResource() {
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}
	
	
	/////////////////////////////////////////////////////////////////////
	// RepositoryEntityController 
	////////////////////////////////////////////////////////////////////
	/**
	 * <code>OPTIONS /{repository}</code>.
	 *
	 * @param information
	 * @return
	 * @since 2.2
	 */
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForCollectionResource(RootResourceInformation information) {

		HttpHeaders headers = new HttpHeaders();
		SupportedHttpMethods supportedMethods = information.getSupportedMethods();

		headers.setAllow(supportedMethods.getMethodsFor(ResourceType.COLLECTION).toSet());

		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}

	/**
	 * <code>HEAD /{repository}</code>
	 *
	 * @param resourceInformation
	 * @return
	 * @throws HttpRequestMethodNotSupportedException
	 * @since 2.2
	 */
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.HEAD)
	public ResponseEntity<?> headCollectionResource(RootResourceInformation resourceInformation,
			DefaultedPageable pageable) throws HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.HEAD, ResourceType.COLLECTION);

		RepositoryInvoker invoker = resourceInformation.getInvoker();

		if (null == invoker) {
			throw new ResourceNotFoundException();
		}

		List<Link> links = getCollectionResourceLinks(resourceInformation, pageable);
		links.add(0, getDefaultSelfLink());

		HttpHeaders headers = new HttpHeaders();
		headers.add(LINK_HEADER, new Links(links).toString());

		return new ResponseEntity<Object>(headers, HttpStatus.NO_CONTENT);
	}

	/**
	 * <code>GET /{repository}</code> - Returns the collection resource (paged or unpaged).
	 *
	 * @param resourceInformation
	 * @param pageable
	 * @param sort
	 * @param assembler
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws HttpRequestMethodNotSupportedException
	 */
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET)
	public Resources<?> getCollectionResource(@QuerydslPredicate RootResourceInformation resourceInformation,
			DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.COLLECTION);

		RepositoryInvoker invoker = resourceInformation.getInvoker();

		if (null == invoker) {
			throw new ResourceNotFoundException();
		}

		Iterable<?> results = pageable.getPageable() != null ? invoker.invokeFindAll(pageable.getPageable())
				: invoker.invokeFindAll(sort);

		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
		Optional<Link> baseLink = Optional.of(entityLinks.linkToPagedResource(resourceInformation.getDomainType(),
				pageable.isDefault() ? null : pageable.getPageable()));

		Resources<?> result = toResources(results, assembler, metadata.getDomainType(), baseLink);
		result.add(getCollectionResourceLinks(resourceInformation, pageable));
		return result;
	}

	private List<Link> getCollectionResourceLinks(RootResourceInformation resourceInformation,
			DefaultedPageable pageable) {

		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
		SearchResourceMappings searchMappings = metadata.getSearchResourceMappings();

		List<Link> links = new ArrayList<Link>();
		links.add(new Link(ProfileController.getPath(this.config, metadata), ProfileResourceProcessor.PROFILE_REL));

		if (searchMappings.isExported()) {
			links.add(entityLinks.linkFor(metadata.getDomainType()).slash(searchMappings.getPath())
					.withRel(searchMappings.getRel()));
		}

		return links;
	}

//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET,
//			produces = { "application/x-spring-data-compact+json", "text/uri-list" })
	public Resources<?> getCollectionResourceCompact(@QuerydslPredicate RootResourceInformation resourceinformation,
			DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		Resources<?> resources = getCollectionResource(resourceinformation, pageable, sort, assembler);
		List<Link> links = new ArrayList<Link>(resources.getLinks());

		for (Resource<?> resource : ((Resources<Resource<?>>) resources).getContent()) {
			PersistentEntityResource persistentEntityResource = (PersistentEntityResource) resource;
			links.add(resourceLink(resourceinformation, persistentEntityResource));
		}
		if (resources instanceof PagedResources) {
			return new PagedResources<Object>(Collections.emptyList(), ((PagedResources<?>) resources).getMetadata(), links);
		} else {
			return new Resources<Object>(Collections.emptyList(), links);
		}
	}

	/**
	 * <code>POST /{repository}</code> - Creates a new entity instances from the collection resource.
	 *
	 * @param resourceInformation
	 * @param payload
	 * @param assembler
	 * @param acceptHeader
	 * @return
	 * @throws HttpRequestMethodNotSupportedException
	 */
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST)
	public ResponseEntity<ResourceSupport> postCollectionResource(RootResourceInformation resourceInformation,
			PersistentEntityResource payload, PersistentEntityResourceAssembler assembler,
			@RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
			throws HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.POST, ResourceType.COLLECTION);

		return createAndReturn(payload.getContent(), resourceInformation.getInvoker(), assembler,
				config.returnBodyOnCreate(acceptHeader));
	}

	/**
	 * <code>OPTIONS /{repository}/{id}<code>
	 *
	 * @param information
	 * @return
	 * @since 2.2
	 */
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForItemResource(RootResourceInformation information) {

		HttpHeaders headers = new HttpHeaders();
		SupportedHttpMethods supportedMethods = information.getSupportedMethods();

		headers.setAllow(supportedMethods.getMethodsFor(ResourceType.ITEM).toSet());
		headers.put("Accept-Patch", ACCEPT_PATCH_HEADERS);

		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}

	/**
	 * <code>HEAD /{repository}/{id}</code>
	 *
	 * @param resourceInformation
	 * @param id
	 * @return
	 * @throws HttpRequestMethodNotSupportedException
	 * @since 2.2
	 */
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.HEAD)
	public ResponseEntity<?> headForItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id,
			PersistentEntityResourceAssembler assembler) throws HttpRequestMethodNotSupportedException {

		return getItemResource(resourceInformation, id).map(it -> {

			Links links = new Links(assembler.toResource(it).getLinks());

			HttpHeaders headers = headersPreparer.prepareHeaders(resourceInformation.getPersistentEntity(), it);
			headers.add(LINK_HEADER, links.toString());

			return new ResponseEntity<Object>(headers, HttpStatus.NO_CONTENT);

		}).orElseThrow(() -> new ResourceNotFoundException());
	}

	/**
	 * <code>GET /{repository}/{id}</code> - Returns a single entity.
	 *
	 * @param resourceInformation
	 * @param id
	 * @return
	 * @throws HttpRequestMethodNotSupportedException
	 */
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Resource<?>> getItemResource(RootResourceInformation resourceInformation,
			@BackendId Serializable id, final PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers)
			throws HttpRequestMethodNotSupportedException {

		return getItemResource(resourceInformation, id).map(it -> {

			PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();

			return getResourceStatus().getStatusAndHeaders(headers, it, entity).toResponseEntity(//
					() -> assembler.toFullResource(it));

		}).orElseGet(() -> new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND));
	}

	/**
	 * <code>PUT /{repository}/{id}</code> - Updates an existing entity or creates one at exactly that place.
	 *
	 * @param resourceInformation
	 * @param payload
	 * @param id
	 * @param assembler
	 * @param eTag
	 * @param acceptHeader
	 * @return
	 * @throws HttpRequestMethodNotSupportedException
	 */
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<? extends ResourceSupport> putItemResource(RootResourceInformation resourceInformation,
			PersistentEntityResource payload, @BackendId Serializable id, PersistentEntityResourceAssembler assembler,
			ETag eTag, @RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
			throws HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.PUT, ResourceType.ITEM);

		if (payload.isNew()) {
			resourceInformation.verifyPutForCreation();
		}

		RepositoryInvoker invoker = resourceInformation.getInvoker();
		Object objectToSave = payload.getContent();
		eTag.verify(resourceInformation.getPersistentEntity(), objectToSave);

		return payload.isNew() ? createAndReturn(objectToSave, invoker, assembler, config.returnBodyOnCreate(acceptHeader))
				: saveAndReturn(objectToSave, invoker, PUT, assembler, config.returnBodyOnUpdate(acceptHeader));
	}

	/**
	 * <code>PATCH /{repository}/{id}</code> - Updates an existing entity or creates one at exactly that place.
	 *
	 * @param resourceInformation
	 * @param payload
	 * @param id
	 * @param assembler
	 * @param eTag,
	 * @param acceptHeader
	 * @return
	 * @throws HttpRequestMethodNotSupportedException
	 * @throws ResourceNotFoundException
	 * @throws ETagDoesntMatchException
	 */
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<ResourceSupport> patchItemResource(RootResourceInformation resourceInformation,
			PersistentEntityResource payload, @BackendId Serializable id, PersistentEntityResourceAssembler assembler,
			ETag eTag, @RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
			throws HttpRequestMethodNotSupportedException, ResourceNotFoundException {

		resourceInformation.verifySupportedMethod(HttpMethod.PATCH, ResourceType.ITEM);

		Object domainObject = payload.getContent();

		eTag.verify(resourceInformation.getPersistentEntity(), domainObject);

		return saveAndReturn(domainObject, resourceInformation.getInvoker(), PATCH, assembler,
				config.returnBodyOnUpdate(acceptHeader));
	}

	/**
	 * <code>DELETE /{repository}/{id}</code> - Deletes the entity backing the item resource.
	 *
	 * @param resourceInformation
	 * @param id
	 * @param eTag
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws HttpRequestMethodNotSupportedException
	 * @throws ETagDoesntMatchException
	 */
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id,
			ETag eTag) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.DELETE, ResourceType.ITEM);

		RepositoryInvoker invoker = resourceInformation.getInvoker();
		Optional<Object> domainObj = invoker.invokeFindById(id);

		return domainObj.map(it -> {

			PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();

			eTag.verify(entity, it);

			publisher.publishEvent(new BeforeDeleteEvent(it));
			invoker.invokeDeleteById(entity.getIdentifierAccessor(it).getIdentifier());
			publisher.publishEvent(new AfterDeleteEvent(it));

			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);

		}).orElseThrow(() -> new ResourceNotFoundException());
	}

	/**
	 * Merges the given incoming object into the given domain object.
	 *
	 * @param domainObject
	 * @param invoker
	 * @param httpMethod
	 * @return
	 */
	protected ResponseEntity<ResourceSupport> saveAndReturn(Object domainObject, RepositoryInvoker invoker,
			HttpMethod httpMethod, PersistentEntityResourceAssembler assembler, boolean returnBody) {

		publisher.publishEvent(new BeforeSaveEvent(domainObject));
		Object obj = invoker.invokeSave(domainObject);
		publisher.publishEvent(new AfterSaveEvent(obj));

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

	/**
	 * Triggers the creation of the domain object and renders it into the response if needed.
	 *
	 * @param domainObject
	 * @param invoker
	 * @return
	 */
	protected ResponseEntity<ResourceSupport> createAndReturn(Object domainObject, RepositoryInvoker invoker,
			PersistentEntityResourceAssembler assembler, boolean returnBody) {

		publisher.publishEvent(new BeforeCreateEvent(domainObject));
		Object savedObject = invoker.invokeSave(domainObject);
		publisher.publishEvent(new AfterCreateEvent(savedObject));

		Optional<PersistentEntityResource> resource = Optional
				.ofNullable(returnBody ? assembler.toFullResource(savedObject) : null);

		HttpHeaders headers = headersPreparer.prepareHeaders(resource);
		addLocationHeader(headers, assembler, savedObject);

		return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, resource);
	}

	/**
	 * Sets the location header pointing to the resource representing the given instance. Will make sure we properly
	 * expand the URI template potentially created as self link.
	 *
	 * @param headers must not be {@literal null}.
	 * @param assembler must not be {@literal null}.
	 * @param source must not be {@literal null}.
	 */
	protected void addLocationHeader(HttpHeaders headers, PersistentEntityResourceAssembler assembler, Object source) {

		String selfLink = assembler.getSelfLinkFor(source).getHref();
		headers.setLocation(new UriTemplate(selfLink).expand());
	}

	/**
	 * Returns the object backing the item resource for the given {@link RootResourceInformation} and id.
	 *
	 * @param resourceInformation
	 * @param id
	 * @return
	 * @throws HttpRequestMethodNotSupportedException
	 * @throws {@link ResourceNotFoundException}
	 */
	protected Optional<Object> getItemResource(RootResourceInformation resourceInformation, Serializable id)
			throws HttpRequestMethodNotSupportedException, ResourceNotFoundException {

		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.ITEM);

		return resourceInformation.getInvoker().invokeFindById(id);
	}
	
	
	/////////////////////////////////////////////////////////////////////
	// AbstractRepositoryRestController
	////////////////////////////////////////////////////////////////////
	protected Link resourceLink(RootResourceInformation resourceLink, Resource resource) {

		ResourceMetadata repoMapping = resourceLink.getResourceMetadata();

		Link selfLink = resource.getLink("self");
		String rel = repoMapping.getItemResourceRel();

		return new Link(selfLink.getHref(), rel);
	}

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

	protected Resources<?> entitiesToResources(Page<Object> page, PersistentEntityResourceAssembler assembler,
			Class<?> domainType, Optional<Link> baseLink) {

		if (page.getContent().isEmpty()) {
			return baseLink.<PagedResources<?>> map(it -> pagedResourcesAssembler.toEmptyResource(page, domainType, it))//
					.orElseGet(() -> pagedResourcesAssembler.toEmptyResource(page, domainType));
		}

		return baseLink.map(it -> pagedResourcesAssembler.toResource(page, assembler, it))//
				.orElseGet(() -> pagedResourcesAssembler.toResource(page, assembler));
	}

	protected Resources<?> entitiesToResources(Iterable<Object> entities, PersistentEntityResourceAssembler assembler,
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

	protected Link getDefaultSelfLink() {
		return new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
	}
	
	/////////////////////////////////////////////////////////////////////
	// RepositorySearchController
	////////////////////////////////////////////////////////////////////
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

			return resourceStatus.getStatusAndHeaders(headers, it, entity).toResponseEntity(//
					() -> assembler.toFullResource(it));

		}).orElseThrow(() -> new ResourceNotFoundException());
	}
	
	
}
