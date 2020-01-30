package org.springframework.data.rest.webmvc.view;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.BaseUriLinkBuilder;
import org.springframework.data.rest.webmvc.support.RepositoryLinkBuilder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

//@RepositoryRestController
public class UriLinkBuilder implements InitializingBean{

	private @Autowired RepositoryRestConfiguration config;
	private @Autowired ResourceMappings mappings;
	//private @Autowired EntityLinks entityLinks;

	@Override
	public void afterPropertiesSet() throws Exception {
		UriLinkBuilder.defaultRepositoryRestConfiguration = config;
		UriLinkBuilder.defaultResourceMappings = mappings;
		//UriLinkBuilder.defaultEntityLinks = entityLinks;
	}	

	
	//////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////
	private static RepositoryRestConfiguration defaultRepositoryRestConfiguration;
	private static ResourceMappings defaultResourceMappings;
	//private static EntityLinks defaultEntityLinks;

	public static LinkBuilder linkTo() {
		BaseUri baseUri = new BaseUri(defaultRepositoryRestConfiguration.getBaseUri());
		return new ExtendedBaseUriLinkBuilder(baseUri);
	}

	public static LinkBuilder linkTo(boolean fromCurrentRequest) {
		if(fromCurrentRequest) {
			return new BaseUriLinkBuilder(ServletUriComponentsBuilder.fromCurrentRequest());
		}
		return linkTo();
	}
	
	public static LinkBuilder linkTo(Class<?> clazz) {
		BaseUri baseUri = new BaseUri(defaultRepositoryRestConfiguration.getBaseUri());
		ResourceMetadata metadata = defaultResourceMappings.getMetadataFor(clazz) ;
		if(metadata != null) {
			return new RepositoryLinkBuilder(metadata, baseUri);
		}else {
			return new ExtendedBaseUriLinkBuilder(ClassUtils.getShortNameAsProperty(clazz), baseUri);
		}
	}
	public static LinkBuilder linkTo(String pathSegment) {
		BaseUri baseUri = new BaseUri(defaultRepositoryRestConfiguration.getBaseUri());
		return new ExtendedBaseUriLinkBuilder(pathSegment, baseUri);
	}

	public static Link getDefaultSelfLink() {
		return linkTo(true).withSelfRel();
	}
	public static Link getSelfLink(Object source, Class<?> domainType) {
		return linkTo(domainType).slash(source).withSelfRel();
	}
	public static Link getSelfLink(Object source, String pathSegment) {
		return linkTo(pathSegment).slash(source).withSelfRel();
	}
	public static Link getSelfLink(Object source, PersistentEntityResourceAssembler assembler) {
		return assembler.getSelfLinkFor(source);
	}
	
	private static class ExtendedBaseUriLinkBuilder extends BaseUriLinkBuilder{

		public ExtendedBaseUriLinkBuilder(String pathSegment, BaseUri baseUri) {
			this(baseUri.getUriComponentsBuilder().pathSegment(pathSegment));
		}

		public ExtendedBaseUriLinkBuilder(BaseUri baseUri) {
			super(baseUri.getUriComponentsBuilder());
		}

		public ExtendedBaseUriLinkBuilder(UriComponentsBuilder builder) {
			super(builder);
		}
	}
	
}