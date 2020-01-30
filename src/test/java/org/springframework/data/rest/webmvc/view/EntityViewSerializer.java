package org.springframework.data.rest.webmvc.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

@RepositoryRestController
public class EntityViewSerializer<T> implements ResourceProcessor<Resource<EntityView<T,?>>>{

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	public Resource<EntityView<T,?>> process(Resource<EntityView<T,?>> resource) {
		
		try {
			EntityView<T,?> content = resource.getContent();
			logger.debug("content "+content);
			
			
			T target = content.serialize();
			logger.debug("target "+target);
			
			Link link = UriLinkBuilder.linkTo(target.getClass()).slash(content.getId()).withSelfRel();
			logger.debug("target "+link);
			
			resource.add(link);
		}catch(Exception e) {
			logger.debug("", e);
		}
		return resource;
	}
}
