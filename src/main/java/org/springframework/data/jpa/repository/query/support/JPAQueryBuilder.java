package org.springframework.data.jpa.repository.query.support;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.support.AbstractWhereBuilder.BaseBuilder;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;

public class JPAQueryBuilder<T> {

	public static Log logger = LogFactory.getLog(JPAQueryBuilder.class);
	
	
	public static <X> JPAQueryBuilder<X> of(JPAQuery<X> query){
		return new JPAQueryBuilder<>(query);
	}
	
	public static <X> JPAQueryBuilder<X> of(EntityManager em){
		return new JPAQueryBuilder<X>(em);
	}
	
	
	private JPAQuery<T> query;
	
	public JPAQueryBuilder(JPAQuery<T> query) {
		this.query = query;
	}
	
	public JPAQueryBuilder(EntityManager em) {
		this.query = new JPAQuery<>(em);
	}
	
	@SuppressWarnings("unchecked")
	public FromBuilder<T> from(Class<?> entityType) {
		return from((PathBuilder<T>)new PathBuilderFactory().create(entityType));
	}

	public FromBuilder<T> from(PathBuilder<T> type) {
		query.from(type);
		return new FromBuilder<>(query, new BaseBuilder(type));
	}
	
	public static class FromBuilder<T>{
		
		private JPAQuery<T> query;
		private BaseBuilder builder;
		
		private FromBuilder(JPAQuery<T> query, BaseBuilder builder) {
			this.query = query;
			this.builder = builder;
		}
		
		public FromBuilder<T> leftJoin(String... property) {		
			for(String p : property) {
				this.query.leftJoin(builder.getPath().get(p)).fetchJoin();
			}
			return this;
		}
		public FromBuilder<T> rightJoin(String... property) {		
			for(String p : property) {
				this.query.rightJoin(builder.getPath().get(p)).fetchJoin();
			}
			return this;
		}
		
		public WhereBuilder<T> where() {			
			return new WhereBuilder<>(query, builder);
		}
		public OrderBuilder<T> orderBy() {			
			return new OrderBuilder<>(query, builder);
		}

		public JPAQuery<T> build() {			
			return query;
		}
	}
	

	public static class WhereBuilder<T> extends AbstractWhereBuilder<WhereBuilder<T>>{

		private JPAQuery<T> query;

		private WhereBuilder(JPAQuery<T> query, BaseBuilder builder) {
			super(builder);
			this.query = query;
		}
	
		public JPAQuery<T> build(){
			return query.where(builder.getBase());
		}
		
		public OrderBuilder<T> orderBy(){
			query.where(builder.getBase());
			return new OrderBuilder<>(query, builder);
		}
	}
	
	
	public static class OrderBuilder<T>{
		
		private JPAQuery<T> query;
		private BaseBuilder builder;
		
		private OrderBuilder(JPAQuery<T> query, BaseBuilder builder) {
			this.query = query;
			this.builder = builder;
		}
		
		public OrderBuilder<T> asc(String property) {
			query.orderBy(new OrderSpecifier<>(Order.ASC, builder.getPath().getComparable(property, Comparable.class)));
			return this;
		}

		public OrderBuilder<T> desc(String property) {
			query.orderBy(new OrderSpecifier<>(Order.DESC, builder.getPath().getComparable(property, Comparable.class)));
			return this;
		}
		
		public JPAQuery<T> build(){
			return query;
		}
	}
}
