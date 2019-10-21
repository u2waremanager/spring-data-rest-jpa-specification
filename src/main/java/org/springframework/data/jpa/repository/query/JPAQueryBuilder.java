package org.springframework.data.jpa.repository.query;

import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;

public class JPAQueryBuilder<T> {

	protected static Log logger = LogFactory.getLog(JPAQueryBuilder.class);
	
	private OrderBuilder<T> orderBuilder;
	private WhereBuilder<T> whereBuilder;
	
	private QueryParameters<T> queryParameters;
	
	private EntityManager em;
	private EntityPath<T> root;
	private PathBuilder<T> path;
	private JPAQuery<T> query;
	private BooleanBuilder where;
	

	public static <X> JPAQueryBuilder<X> of(Class<X> domainClass, EntityManager em) {
		return new JPAQueryBuilder<>(domainClass, em);
	}
	
	private JPAQueryBuilder(Class<T> domainClass, EntityManager em) {
		this.em = em;
		this.root = new PathBuilderFactory().create(domainClass);
		this.path = new PathBuilder<>(this.root.getType(), this.root.getMetadata());
		this.query = new JPAQuery<>(em);
		this.query.from(this.root);
		
		this.where = new BooleanBuilder();
		this.whereBuilder = new WhereBuilder<>(this, where);
		this.orderBuilder = new OrderBuilder<>(this);
	}
	
	public <X> JPAQueryBuilder<X> newJPAQueryBuilder(Class<X> domainClass) {
		return new JPAQueryBuilder<>(domainClass, em);
	}
	
//	private EntityPath<T> getEntityPath() {
//		return root;
//	}
	private PathBuilder<T> getPathBuilder() {
		return path;
	}
	private JPAQuery<T> getJPAQuery() {
		return query;
	}
	private WhereBuilder<T> getWhereBuilder() {
		return whereBuilder;
	}
	private OrderBuilder<T> getOrderBuilder() {
		return orderBuilder;
	}
	
	private QueryParameters<T> getQueryParameters() {
		return queryParameters;
	}
	public void setQueryParameters(QueryParameters<T> queryParameters) {
		this.queryParameters = queryParameters;
	}
	public void setQueryParameters(T queryParameters) {
		this.queryParameters = new QueryParameters<>(queryParameters);
	}
	public void setQueryParameters(Object... queryParameters) {
		this.queryParameters = new QueryParameters<>(queryParameters);
	}
	public void setQueryParameters(Map<String,?> queryParameters) {
		this.queryParameters = new QueryParameters<>(queryParameters);
	}
	
	/////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////
	public JPAQueryBuilder<T> join(String property) {
		this.query.leftJoin(path.get(property)).fetchJoin();
		return this;
	}
	public WhereBuilder<T> where() { 
		return whereBuilder; 
	}
	public OrderBuilder<T> order() { 
		return orderBuilder; 
	}

	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public JPAQuery<T> build() { 
		query.where(where);
		return query;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JPAQuery<T> build(Pageable pageable) { 
		
		query.where(where);
		
		for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
			query.orderBy(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, path.get(order.getProperty())));
		}

		if (pageable.isUnpaged()) {
			return query;
		}
		query.offset(pageable.getOffset());
		query.limit(pageable.getPageSize());
		return query;
	}
	
	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public static class WhereBuilder<T>{
		
		private JPAQueryBuilder<T> builder;
		private PredicateBuilder<T> predicateBuilder;
		
		private State state;
		private enum State{ AND, AND_START, AND_END, OR, OR_START, OR_END }

		private BooleanBuilder where;
		private BooleanBuilder sub;
		
		public WhereBuilder(JPAQueryBuilder<T> builder, BooleanBuilder where) {
			this.builder = builder;
			this.predicateBuilder = new PredicateBuilder<>(builder);
			this.where = where;
		}
		
		//////////////////////////////////////////
		//
		//////////////////////////////////////////
		public JPAQuery<T> build() { 
			return builder.build();
		}
		
		public JPAQuery<T> build(Pageable pageable) { 
			return builder.build(pageable);
		}
		
		
		public OrderBuilder<T> order() { 
			return builder.getOrderBuilder(); 
		}
		
		public PredicateBuilder<T> and() { 
			this.state = State.AND; 
			return predicateBuilder;
		}
		public PredicateBuilder<T> andStart() { 
			this.state = State.AND_START; 
			return predicateBuilder;
		}
		public WhereBuilder<T> andEnd() { 
			this.state = State.AND_END; 
			return chain(null);
		}
		public PredicateBuilder<T> or() { 
			this.state = State.OR; 
			return predicateBuilder;
		}
		public PredicateBuilder<T> orStart() { 
			this.state = State.OR_START; 
			return predicateBuilder;
		}
		public WhereBuilder<T> orEnd() { 
			this.state = State.OR_END; 
			return chain(null);
		}

		
		private WhereBuilder<T> chain(Predicate criteria) { 
			switch (this.state) {
				case AND:       
					if(sub != null) sub.and(criteria); else where.and(criteria); 
					break;
				case AND_START: 
					if(sub == null) sub = new BooleanBuilder().and(criteria);
					break;
				case AND_END:   
					if(sub != null) where.and(sub); sub = null;
					break;
				case OR:        
					if(sub != null) sub.or(criteria); else where.or(criteria);
					break;
				case OR_START:  
					if(sub == null) sub = new BooleanBuilder().and(criteria);
					break;
				case OR_END:    
					if(sub != null) where.or(sub); sub = null;
					break;
			}
			return this;
		}
	}
	
	@SuppressWarnings({"unchecked","rawtypes"})
	public static class PredicateBuilder<T>{
		
		private JPAQueryBuilder<T> builder;
		
		public PredicateBuilder(JPAQueryBuilder<T> builder) {
			this.builder = builder;
		}
		
		public WhereBuilder<T> isNull(String property){
			return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).isNull());
		}
		public WhereBuilder<T> isNotNull(String property){
			return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).isNotNull());
		}
		public WhereBuilder<T> eq(String property, Object value){
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).eq(value));
		}
		public WhereBuilder<T> notEq(String property, Object value){
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).ne(value));
		}
		public WhereBuilder<T> like(String property, Object value){
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getPathBuilder().getString(property).toLowerCase().contains(value.toString().toLowerCase()));
		}
		public WhereBuilder<T> notLike(String property, Object value){
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getPathBuilder().getString(property).notLike(value.toString()));
		}
		public <A extends Comparable<?>> WhereBuilder<T> gt(String property, A value) {
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getPathBuilder().getComparable(property, (Class<A>) value.getClass()).gt(value));
		}
		public <A extends Comparable<?>> WhereBuilder<T> gte(String property, A value) {
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getPathBuilder().getComparable(property, (Class<A>) value.getClass()).goe(value));
		}
		public <A extends Comparable<?>> WhereBuilder<T> lt(String property, A value) {
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getPathBuilder().getComparable(property, (Class<A>) value.getClass()).lt(value));
		}
		public <A extends Comparable<?>> WhereBuilder<T> lte(String property, A value) {
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getPathBuilder().getComparable(property, (Class<A>) value.getClass()).loe(value));
		}
		public <A extends Comparable<?>> WhereBuilder<T> between(String property, Object value) {
			if(value == null) return builder.getWhereBuilder();
			if(ClassUtils.isAssignableValue(Collection.class, value)) {
				A from = (A)((Collection)value).iterator().next();
				A to = (A)((Collection)value).iterator().next();
				return builder.getWhereBuilder().chain(builder.getPathBuilder().getComparable(property, (Class<A>) from.getClass()).between(from, to));
				
			}else if(ObjectUtils.isArray(value)){
				Comparable[] objects = (Comparable[])value;
				A from = (A)objects[0];
				A to = (A)objects[0];
				return builder.getWhereBuilder().chain(builder.getPathBuilder().getComparable(property, (Class<A>) from.getClass()).between(from, to));
			}else {
				return builder.getWhereBuilder();
			}
		}
		public WhereBuilder<T> in(String property, Object value) {
			if(value == null) return builder.getWhereBuilder();
			if(ClassUtils.isAssignableValue(Collection.class, value)) {
				return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).in((Collection)value));
			}else if(ObjectUtils.isArray(value)){
				return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).in((Object[])value));
			}else {
				return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).in(value));
			}
		}
		public WhereBuilder<T> notIn(String property, Object value) {
			if(value == null) return builder.getWhereBuilder();
			if(ClassUtils.isAssignableValue(Collection.class, value)) {
				return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).notIn((Collection)value));
			}else if(ObjectUtils.isArray(value)){
				return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).notIn((Object[])value));
			}else {
				return builder.getWhereBuilder().chain(builder.getPathBuilder().get(property).notIn(value));
			}
		}
		
		
		
		
		public WhereBuilder<T> eq(String property){
			return eq(property, builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> notEq(String property){
			return notEq(property, builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> like(String property){
			return like(property, builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> notLike(String property){
			return notLike(property, builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> between(String property) {
			return between(property, builder.getQueryParameters().get(property));
		}
		public <A extends Comparable<?>> WhereBuilder<T> gt(String property) {
			return gt(property, (A)builder.getQueryParameters().get(property));
		}
		public <A extends Comparable<?>> WhereBuilder<T> gte(String property) {
			return gte(property, (A)builder.getQueryParameters().get(property));
		}
		public <A extends Comparable<?>> WhereBuilder<T> lt(String property) {
			return lt(property, (A)builder.getQueryParameters().get(property));
		}
		public <A extends Comparable<?>> WhereBuilder<T> lte(String property) {
			return lte(property, (A)builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> in(String property) {
			return in(property, builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> notIn(String property) {
			return notIn(property, builder.getQueryParameters().get(property));
		}		
	}
	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public static class OrderBuilder<T>{

		private JPAQueryBuilder<T> builder;
		
		private OrderBuilder(JPAQueryBuilder<T> builder) {
			this.builder = builder;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public OrderBuilder<T> asc(String property) {
			builder.getJPAQuery().orderBy(new OrderSpecifier(Order.ASC, builder.getPathBuilder().get(property)));
			return this;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public OrderBuilder<T> desc(String property) {
			builder.getJPAQuery().orderBy(new OrderSpecifier(Order.DESC, builder.getPathBuilder().get(property)));
			return this;
		}
		
		public JPAQuery<T> build() { 
			return builder.build();
		}
		
		public JPAQuery<T> build(Pageable pageable) { 
			return builder.build(pageable);
		}
	}
}
