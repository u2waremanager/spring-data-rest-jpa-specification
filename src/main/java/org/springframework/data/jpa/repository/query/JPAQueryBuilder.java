package org.springframework.data.jpa.repository.query;

import org.springframework.data.jpa.repository.query.JPAQueryBuilderFactory.FromBuilder;
import org.springframework.data.jpa.repository.query.JPAQueryBuilderFactory.OrderBuilder;
import org.springframework.data.jpa.repository.query.JPAQueryBuilderFactory.WhereBuilder;

public class JPAQueryBuilder<T> {

	private Class<T> entityType;
	
	public JPAQueryBuilder(Class<T> entityType) {
		this.entityType = entityType;
	}
	
	public FromBuilder<T> from(){
		return JPAQueryBuilderFactory.from(entityType);
	}
	
	public WhereBuilder<T> where(){
		return JPAQueryBuilderFactory.where(entityType);
	}

	public OrderBuilder<T> orderBy(){
		return JPAQueryBuilderFactory.orderBy(entityType);
	}
	
}
