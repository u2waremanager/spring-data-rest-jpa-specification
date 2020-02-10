package org.springframework.data.rest.webmvc;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.SpecificationBuffer;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.core.event.AfterReadEvent;
import org.springframework.data.rest.core.event.BeforeReadEvent;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;

@RepositoryRestController
public class ReporitoryReadEntityController extends AbstractRepositoryController{

	protected Log logger = LogFactory.getLog(getClass());

	private @PersistenceContext EntityManager entityManager;

	
	@RequestMapping(value = "/{repository}/{id}", method = RequestMethod.GET, headers = "read=specification")
	public ResponseEntity<Resource<?>> getItemResource1(RootResourceInformation resourceInformation,
			@BackendId Serializable id, 
			final PersistentEntityResourceAssembler assembler, 
			@RequestHeader HttpHeaders headers)
			throws HttpRequestMethodNotSupportedException {

		return super.getItemResource(resourceInformation, id).map(it -> {

			publisher.publishEvent(new AfterReadEvent(it));
			
			PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();

			return getResourceStatus().getStatusAndHeaders(headers, it, entity).toResponseEntity(//
					() -> assembler.toFullResource(it));

		}).orElseGet(() -> new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND));
	}
	
	
	
	@RequestMapping(value = "/{repository}/{id}", method = RequestMethod.GET, headers = "read=querydsl")
	public ResponseEntity<Resource<?>> getItemResource2(RootResourceInformation resourceInformation,
			@BackendId Serializable id, 
			final PersistentEntityResourceAssembler assembler, 
			@RequestHeader HttpHeaders headers)
			throws HttpRequestMethodNotSupportedException {

		return super.getItemResource(resourceInformation, id).map(it -> {

			publisher.publishEvent(new AfterReadEvent(it));
			
			PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();

			return getResourceStatus().getStatusAndHeaders(headers, it, entity).toResponseEntity(//
					() -> assembler.toFullResource(it));

		}).orElseGet(() -> new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping(value = "/{repository}", method = RequestMethod.GET, headers = "read=specification")
	public <T> Resources<?> getReadCollectionResource1(@QuerydslPredicate RootResourceInformation resourceInformation,
			@RequestParam(name = "unpaged", required = false) boolean unpaged,
			PersistentEntityResource payload, DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		
		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.COLLECTION);

		RepositoryInvoker invoker = resourceInformation.getInvoker();

		if (null == invoker) {
			throw new ResourceNotFoundException();
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		Class<?> domainType = resourceInformation.getDomainType();
		logger.info("domain: " + domainType.getName());

		JpaSpecificationExecutor executor = getRepositoryFor(resourceInformation, JpaSpecificationExecutor.class);
		logger.info("executor: " + executor);		
		
//		Specification specification = Specification.where((root, query, builder) -> {return null;});		
		
		Specification specification = new SpecificationBuffer();
		
		//....................
		publisher.publishEvent(new BeforeReadEvent(payload.getContent(), specification));
		//...................
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		
		Iterable<?> results = null;
		if(unpaged) {
			results = executor.findAll(specification, sort) ;
		}else {
			results = executor.findAll(specification, pageable.getPageable()) ;
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
		Optional<Link> baseLink = Optional.of(entityLinks.linkToPagedResource(resourceInformation.getDomainType(),
				pageable.isDefault() ? null : pageable.getPageable()));

		Resources<?> result = toResources(results, assembler, metadata.getDomainType(), baseLink);
		result.add(getCollectionResourceLinks(resourceInformation, pageable));
		return result;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "/{repository}", method = RequestMethod.GET, headers = "read=querydsl")
	public <T> Resources<?> getReadCollectionResource2(@QuerydslPredicate RootResourceInformation resourceInformation,
			@RequestParam(name = "unpaged", required = false) boolean unpaged,
			PersistentEntityResource payload, DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		
		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.COLLECTION);

		RepositoryInvoker invoker = resourceInformation.getInvoker();

		if (null == invoker) {
			throw new ResourceNotFoundException();
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		EntityPath<?> entityPath = new PathBuilderFactory().create(resourceInformation.getDomainType());
		PathBuilder<?> pathBuilder = new PathBuilder<>(entityPath.getType(), entityPath.getMetadata());
		Querydsl querydsl = new Querydsl(entityManager, pathBuilder); 
		JPQLQuery<?> query = querydsl.createQuery();

		
		//....................
		publisher.publishEvent(new BeforeReadEvent(payload.getContent(), query));
		//...................
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		if(query.getMetadata().getJoins().isEmpty()) {
			query.from(entityPath);
		}

		logger.info("pathBuilder: "+pathBuilder.getRoot().getType());
		logger.info("query: "+query.getType());
		
		Iterable<?> results = null;
		if(unpaged) {
			querydsl.applySorting(sort, query);
			results = query.fetch();
		}else {
			querydsl.applyPagination(pageable.getPageable(), query);
			results = PageableExecutionUtils.getPage(query.fetch(), pageable.getPageable(), query::fetchCount);
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
		Optional<Link> baseLink = Optional.of(entityLinks.linkToPagedResource(resourceInformation.getDomainType(),
				pageable.isDefault() ? null : pageable.getPageable()));

		Resources<?> result = toResources(results, assembler, metadata.getDomainType(), baseLink);
		result.add(getCollectionResourceLinks(resourceInformation, pageable));
		return result;
	}
}
