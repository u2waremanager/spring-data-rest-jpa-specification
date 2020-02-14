package org.springframework.data.jpa.repository.query.querydsl;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;

public class JPAQueryBuilder<T> {

	protected final Log logger = LogFactory.getLog(getClass());
	
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
		return new FromBuilder<>(query, type);
	}
	
	public static class FromBuilder<T>{
		
		private JPAQuery<T> query;
		private PathBuilder<T> type;
		
		private FromBuilder(JPAQuery<T> query, PathBuilder<T> type) {
			this.query = query;
			this.type = type;
		}
		
		public FromBuilder<T> leftJoin(String... property) {		
			for(String p : property) {
				this.query.leftJoin(type.get(p)).fetchJoin();
			}
			return this;
		}
		public FromBuilder<T> rightJoin(String... property) {		
			for(String p : property) {
				this.query.rightJoin(type.get(p)).fetchJoin();
			}
			return this;
		}
		
		public WhereBuilder<T> where() {			
			return new WhereBuilder<>(query, type);
		}
		public OrderBuilder<T> orderBy() {			
			return new OrderBuilder<>(query, type);
		}

		public JPAQuery<T> build() {			
			return query;
		}
	}
	

	public static class WhereBuilder<T> extends AbstractCriteriaBuilder<WhereBuilder<T>, JPAQuery<T>>{

		private JPAQuery<T> query;
		private PathBuilder<T> path;

		private WhereBuilder(JPAQuery<T> query, PathBuilder<T> path) {
			super(new BooleanBuilder(), path);
			this.query = query;
			this.path = path;
		}
	
		public JPAQuery<T> build(){
			return query.where(super.getBase());
		}
		
		public OrderBuilder<T> orderBy(){
			query.where(super.getBase());
			return new OrderBuilder<>(query, path);
		}
	}
	
	
	public static class OrderBuilder<T>{
		
		private JPAQuery<T> query;
		private PathBuilder<T> type;
		
		private OrderBuilder(JPAQuery<T> query, PathBuilder<T> type) {
			this.query = query;
			this.type = type;
		}
		
		public OrderBuilder<T> asc(String property) {
			query.orderBy(new OrderSpecifier<>(Order.ASC, type.getComparable(property, Comparable.class)));
			return this;
		}

		public OrderBuilder<T> desc(String property) {
			query.orderBy(new OrderSpecifier<>(Order.DESC, type.getComparable(property, Comparable.class)));
			return this;
		}
		
		public JPAQuery<T> build(){
			return query;
		}
	}
}
