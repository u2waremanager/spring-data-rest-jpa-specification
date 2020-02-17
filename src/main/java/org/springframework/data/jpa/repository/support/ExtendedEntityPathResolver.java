package org.springframework.data.jpa.repository.support;

import org.springframework.data.querydsl.SimpleEntityPathResolver;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.PathBuilderFactory;

public class ExtendedEntityPathResolver extends SimpleEntityPathResolver{

	private PathBuilderFactory factory = new PathBuilderFactory();
	
	public ExtendedEntityPathResolver() {
		super("");
	}
	
	@Override
	public <T> EntityPath<T> createPath(Class<T> domainClass) {
		try {
			return super.createPath(domainClass);
		}catch(Exception e) {
			return factory.create(domainClass);
		}
	}
}
