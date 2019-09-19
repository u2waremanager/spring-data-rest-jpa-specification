package org.springframework.data.rest.webmvc;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.PredicateBuilder;
import org.springframework.data.rest.core.event.BeforeReadEvent;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

@RepositoryRestController
public class RepositorySpecificationController extends AbstractRepositoryController{

	protected Log logger = LogFactory.getLog(getClass());
	
	private static final String QUERY = "!q";
	private static final String BASE_MAPPING = "/{repository}/" + QUERY;

	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.OPTIONS)
	public HttpEntity<?> optionsForSpecification() {
		return super.optionsForAllResource();
	}
	
	@RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.OPTIONS)
	public HttpEntity<?> optionsForSpecification(@PathVariable String search) {
		return super.optionsForAllResource();
	}
	
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.HEAD)
	public HttpEntity<?> headForSpecification() throws HttpRequestMethodNotSupportedException {
		return super.headForAllResource();
	}

	@RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.HEAD)
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
				T source = (T) convertValue(parameters, domainType);
				PredicateBuilder<T> pd = new PredicateBuilder<T>(root, query, builder, parameters);
				if (StringUtils.hasLength(search)) {
					pd.and().partTree(search, source);
				}else {
					publisher.publishEvent(new BeforeReadEvent(source, pd));
				}
				return pd.build();
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

	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private <T> T convertValue(MultiValueMap<String, Object> parameters, Class<T> clazz) {

		Map<String, Object> temp = new HashMap<String, Object>();

		BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
		for (String name : parameters.keySet()) {
			try {
				PropertyDescriptor pd = beanWrapper.getPropertyDescriptor(name);
				if (pd != null) {

					Class<?> type = pd.getPropertyType();
					List<?> source = parameters.get(name);
					Object value = null;
					if (source != null) {
						if (ClassUtils.isAssignableValue(type, source) || type.isArray()) {
							value = source;
						} else {
							value = source.size() > 0 ? source.get(0) : null;
						}
					}
					if (value != null) {
						temp.put(name, value);
					}
				}
			} catch (Exception e) {

			}
		}
		return objectMapper.convertValue(temp, clazz);
	}	
	

}


