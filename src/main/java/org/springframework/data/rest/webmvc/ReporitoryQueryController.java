package org.springframework.data.rest.webmvc;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.SpecificationBuilder;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.PartTreeSpecification;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.Repository;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.querydsl.core.BooleanBuilder;

@RepositoryRestController
public class ReporitoryQueryController extends AbstractRepositoryController{

	protected Log logger = LogFactory.getLog(getClass());

	private static final String QUERY = "/query";
	private static final String BASE_MAPPING = "/{repository}" + QUERY;
	
	
	/**
	 * <code>OPTIONS /{repository}/query</code>.
	 *
	 * @param resourceInformation
	 * @return
	 */
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.OPTIONS)
	public HttpEntity<?> optionsForQueries(RootResourceInformation resourceInformation) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAllow(Collections.singleton(HttpMethod.GET));
		return ResponseEntity.ok().headers(headers).build();
	}
	
	
	/**
	 * <code>HEAD /{repository}/query</code> - Checks whether the query resource is present.
	 *
	 * @param resourceInformation
	 * @return
	 */
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.HEAD)
	public HttpEntity<?> headForQueries(RootResourceInformation resourceInformation) {
		return ResponseEntity.noContent().build();
	}
	
	

	/**
	 * <code>GET /{repository}/query</code> - Exposes links to the individual query resources exposed by the backing
	 * repository.
	 *
	 * @param resourceInformation
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET)
	public <T> Resources<?> executeQuery(@QuerydslPredicate RootResourceInformation resourceInformation,
			@RequestHeader(name = "partTree", required = false) String partTree,
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

		
		Repository repository = getRepositoryFor(resourceInformation);
		Iterable<?> results = null;
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		if(ClassUtils.isAssignableValue(QuerydslPredicateExecutor.class, repository)) {

			QuerydslPredicateExecutor executor = (QuerydslPredicateExecutor)repository;
			logger.info("executor: " + executor);

			BooleanBuilder predicate = new BooleanBuilder();
			//....................
			publisher.publishEvent(new BeforeReadEvent(payload.getContent(), predicate));
			//...................
			if(unpaged) {
				results = executor.findAll(predicate, sort) ;
			}else {
				results = executor.findAll(predicate, pageable.getPageable()) ;
			}
			
			
		}else if(ClassUtils.isAssignableValue(JpaSpecificationExecutor.class, repository)) {
			
			
			JpaSpecificationExecutor executor = (JpaSpecificationExecutor)repository;
			logger.info("executor: " + executor);
			
			Specification specification = null;
			if(StringUtils.hasText(partTree)) {
				specification = new PartTreeSpecification(partTree, payload.getContent());
			}else {
				specification = new SpecificationBuilder();
				//....................
				publisher.publishEvent(new BeforeReadEvent(payload.getContent(), specification));
				//...................
			}
			if(unpaged) {
				results = executor.findAll(specification, sort) ;
			}else {
				results = executor.findAll(specification, pageable.getPageable()) ;
			}
			
		}else {
			throw new ResourceNotFoundException();
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
	
	
	@RequestMapping(value = BASE_MAPPING+"/{id}", method = RequestMethod.GET, headers = "read=specification")
	public ResponseEntity<Resource<?>> getItemResource1(RootResourceInformation resourceInformation,
			@BackendId Serializable id, 
			final PersistentEntityResourceAssembler assembler, 
			@RequestHeader HttpHeaders headers)
			throws HttpRequestMethodNotSupportedException {

		return super.getItemResource(resourceInformation, id).map(it -> {

			//....................
			publisher.publishEvent(new AfterReadEvent(it));
			//...................
			
			PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();

			return getResourceStatus().getStatusAndHeaders(headers, it, entity).toResponseEntity(//
					() -> assembler.toFullResource(it));

		}).orElseGet(() -> new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND));
	}
	

}
