package org.springframework.data.rest.webmvc.support;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

public class EntityViewSerializer<T, ID extends Serializable> implements ResourceProcessor<Resource<EntityView<T, ID>>>{

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	public Resource<EntityView<T,ID>> process(Resource<EntityView<T,ID>> resource) {
		
		try {
			EntityView<T,ID> content = resource.getContent();
			logger.info("content "+content);
			
			
			T target = content.serialize();
			logger.info("target "+target);
			
			Link link = UriLinkBuilder.linkTo(target.getClass()).slash(content.getId()).withSelfRel();
			logger.info("link "+link);
			
			resource.add(link);
		}catch(Exception e) {
			logger.debug("", e);
		}
		return resource;
	}
}