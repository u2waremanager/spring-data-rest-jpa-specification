package org.springframework.data.rest.webmvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.hateoas.Resources;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class ReporitoryReadEntityController {

	
	protected Log logger = LogFactory.getLog(getClass());

	
	
	@ResponseBody
	@RequestMapping(value = "/{repository}", method = RequestMethod.GET, headers = "u2ware=u2ware")
	public Resources<?> getCollectionResource(@QuerydslPredicate RootResourceInformation resourceInformation,
			PersistentEntityResource payload, DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		logger.info("11111111111111111111111111111111111111111");
		logger.info("11111111111111111111111111111111111111111");
		logger.info("11111111111111111111111111111111111111111");
		logger.info("11111111111111111111111111111111111111111");
		logger.info("11111111111111111111111111111111111111111");
		
		
		return null;
	}
//	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET)
//	public ResponseEntity<?> executeSearch(RootResourceInformation resourceInformation,
//			@RequestParam MultiValueMap<String, Object> parameters, @PathVariable String search, DefaultedPageable pageable,
//			Sort sort, PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers) {
//
//	}
//	
//	
//	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST)
//	public ResponseEntity<ResourceSupport> postCollectionResource(RootResourceInformation resourceInformation,
//			PersistentEntityResource payload, PersistentEntityResourceAssembler assembler,
//			@RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
//			throws HttpRequestMethodNotSupportedException {
//
//		resourceInformation.verifySupportedMethod(HttpMethod.POST, ResourceType.COLLECTION);
//
//		return createAndReturn(payload.getContent(), resourceInformation.getInvoker(), assembler,
//				config.returnBodyOnCreate(acceptHeader));
//	}
	
}
