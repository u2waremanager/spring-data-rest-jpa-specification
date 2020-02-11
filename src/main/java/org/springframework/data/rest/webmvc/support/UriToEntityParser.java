package org.springframework.data.rest.webmvc.support;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

//@RepositoryRestController
public class UriToEntityParser implements InitializingBean{

	private @Autowired PersistentEntities entities;
	private @Autowired RepositoryInvokerFactory invokerFactory;
	private @Autowired Repositories repositories;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		UriToEntityParser.uriToEntityConverter = new UriToEntityConverter(entities, invokerFactory, repositories);
	}
	
	//////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////
	private static UriToEntityConverter uriToEntityConverter;
	
	public static List<String> resolveSegments(String link) {
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(link).build();
		List<String> segments = uriComponents.getPathSegments();
		return segments;
	}
	
	public static String resolveSegment(String link, int index) {
		return resolveSegments(link).get(index);
	}
	
	public static String resolveLastSegment(String link) {
		List<String> segments = resolveSegments(link);
		return segments.get(segments.size() - 1);
	}
	
	public static UUID resolveUuid(String link) {
		return UUID.fromString(resolveLastSegment(link));
	}
	public static String resolveString(String link) {
		return resolveLastSegment(link);
	}
	public static Long resolveLong(String link) {
		return Long.parseLong(resolveLastSegment(link));
	}
	public static Integer resolveInteger(String link) {
		return Integer.parseInt(resolveLastSegment(link));
	}

	@SuppressWarnings("unchecked")
	public static <T> T resolveEntity(String link, Class<T> domainType) {
		System.err.println(link);
		
		URI source = UriComponentsBuilder.fromUriString(link).build().toUri();
		TypeDescriptor sourceType = new TypeDescriptor(ResolvableType.forClass(URI.class), URI.class, null);
		TypeDescriptor targetType = new TypeDescriptor(ResolvableType.forClass(domainType), domainType, null);
		return (T)uriToEntityConverter.convert(source, sourceType, targetType);
	}
}