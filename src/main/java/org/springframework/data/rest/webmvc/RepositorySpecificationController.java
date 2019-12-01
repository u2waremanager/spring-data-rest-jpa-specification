package org.springframework.data.rest.webmvc;

import java.io.Serializable;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.PartTreeQueryBuilder;
import org.springframework.data.jpa.repository.query.QueryParameters;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.rest.core.event.AfterLoadEvent;
import org.springframework.data.rest.core.event.PartTreeQueryBuilderEvent;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class RepositorySpecificationController extends AbstractRepositoryController{

	protected Log logger = LogFactory.getLog(getClass());
	
	private static final String QUERY = "/!q";
	private static final String BASE_MAPPING = "/{repository}" + QUERY;

	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.OPTIONS)
	public HttpEntity<?> optionsForSpecification() {
		return super.optionsForAllResource();
	}
	
	@RequestMapping(value = BASE_MAPPING + "/{search:.+}", method = RequestMethod.OPTIONS)
	public HttpEntity<?> optionsForSpecification(@PathVariable String search) {
		return super.optionsForAllResource();
	}
	
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.HEAD)
	public HttpEntity<?> headForSpecification() throws HttpRequestMethodNotSupportedException {
		return super.headForAllResource();
	}

	@RequestMapping(value = BASE_MAPPING + "/{search:.+}", method = RequestMethod.HEAD)
	public HttpEntity<?> headForSpecification(@PathVariable String search) throws HttpRequestMethodNotSupportedException {
		return super.headForAllResource();
	}

	//RepositorySearchController v;
	@ResponseBody
	@RequestMapping(value = BASE_MAPPING, method =  {RequestMethod.GET,RequestMethod.POST})
	public <T> ResponseEntity<?> executeSpecificationByParam(RootResourceInformation resourceInformation,
			@RequestParam MultiValueMap<String, Object> parameters, 
			@RequestParam(name= QUERY,  required=false) String search, 
			@RequestParam(name="paged", required=false, defaultValue="true") Boolean paged,
			DefaultedPageable pageable,
			Sort sort, PersistentEntityResourceAssembler assembler, 
			@RequestHeader HttpHeaders headers) throws Exception{
		return executeSpecification(resourceInformation, parameters, search, paged, pageable, sort, assembler, headers);
	}
	
	@ResponseBody
	@RequestMapping(value = BASE_MAPPING + "/{search:.+}", method = {RequestMethod.GET,RequestMethod.POST})
	public <T> ResponseEntity<?> executeSpecificationByPath(RootResourceInformation resourceInformation,
			@RequestParam MultiValueMap<String, Object> parameters, 
			@PathVariable String search, 
			@RequestParam(name="paged", required=false, defaultValue="true") Boolean paged,
			DefaultedPageable pageable,
			Sort sort, PersistentEntityResourceAssembler assembler, 
			@RequestHeader HttpHeaders headers) throws Exception{
		return executeSpecification(resourceInformation, parameters, search, paged, pageable, sort, assembler, headers);
	}
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method =  {RequestMethod.POST})
//	public <T> ResponseEntity<?> executeSpecificationByParam(RootResourceInformation resourceInformation,
//			PersistentEntityResource payload, 
//			@RequestParam MultiValueMap<String, Object> parameters, 
//			@RequestParam(name= QUERY,  required=false) String search, 
//			@RequestParam(name="paged", required=false, defaultValue="true") Boolean paged,
//			DefaultedPageable pageable,
//			Sort sort, PersistentEntityResourceAssembler assembler, 
//			@RequestHeader HttpHeaders headers) throws Exception{
//		return executeSpecification(resourceInformation, parameters, payload, search, paged, pageable, sort, assembler, headers);
//	}
	
	
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING + "/{search:.+}", method = {RequestMethod.POST})
//	public <T> ResponseEntity<?> executeSpecificationByPath(RootResourceInformation resourceInformation,
//			PersistentEntityResource payload, 
//			@RequestParam MultiValueMap<String, Object> parameters, 
//			@PathVariable String search, 
//			@RequestParam(name="paged", required=false, defaultValue="true") Boolean paged,
//			DefaultedPageable pageable,
//			Sort sort, PersistentEntityResourceAssembler assembler, 
//			@RequestHeader HttpHeaders headers) throws Exception{
//		return executeSpecification(resourceInformation, parameters, payload, search, paged, pageable, sort, assembler, headers);
//	}
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> ResponseEntity<?> executeSpecification(RootResourceInformation resourceInformation,
			MultiValueMap<String, Object> parameters, 
			//PersistentEntityResource payload, 
			String search, 
			Boolean paged,
			DefaultedPageable pageable,
			Sort sort, PersistentEntityResourceAssembler assembler, 
			HttpHeaders headers) {
		
		try {
			logger.info("search: " + search);
			logger.info("parameters: " + parameters);
			
			Class<?> domainType = resourceInformation.getDomainType();
			logger.info("domain: " + domainType.getName());

			JpaSpecificationExecutor executor = getRepositoryFor(resourceInformation, JpaSpecificationExecutor.class);
			logger.info("executor: " + executor);

			
			Specification specification = (root, query, builder) -> {
				PartTreeQueryBuilder<T> pb = PartTreeQueryBuilder.of(root, query, builder);
				pb.setQueryParameters(new QueryParameters<>(parameters, domainType));
				
				if (StringUtils.hasLength(search)) {
					pb.where().and().partTree(search);
				}else {
					publisher.publishEvent(new PartTreeQueryBuilderEvent(pb, domainType));
				}
				return pb.build();
			};
			logger.info("specification: "+specification);

			
			Object result = null;
			if (StringUtils.startsWithIgnoreCase(search, "count")) {
				result = executor.count(specification);

			} else if (StringUtils.startsWithIgnoreCase(search, "exists")) {
				result = executor.count(specification) > 0;
			} else {
				if(paged) {
					result = executor.findAll(specification, pageable.getPageable()) ;
				}else {
					result = executor.findAll(specification, sort) ;
				}
			}
			return super.toResource(Optional.of(result), assembler, resourceInformation.getDomainType(), Optional.empty(), headers, resourceInformation);
			
		}catch(Exception e) {
			logger.info("", e);
			throw new ResourceNotFoundException(e.getMessage());
		}
	}

		

}


