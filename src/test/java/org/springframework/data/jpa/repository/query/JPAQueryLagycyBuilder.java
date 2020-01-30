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
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

public class JPAQueryLagycyBuilder<T> {

	protected static Log logger = LogFactory.getLog(JPAQueryBuilder.class);
	
	private OrderBuilder<T> orderBuilder;
	private WhereBuilder<T> whereBuilder;
	
	private QueryParameters<T> queryParameters;
	
	private JPAQuery<T> query;
	private JPAQueryBuilder<T> path;
	private BooleanBuilder where;
	

	public static <X> JPAQueryLagycyBuilder<X> of(Class<X> domainClass) {
		return new JPAQueryLagycyBuilder<>(domainClass);
	}
	public static <X> JPAQueryLagycyBuilder<X> of(Class<X> domainClass, EntityManager em) {
		return new JPAQueryLagycyBuilder<>(domainClass, em);
	}
	public static <X> JPAQueryLagycyBuilder<X> of(Class<X> domainClass, JPAQuery<X> query) {
		return new JPAQueryLagycyBuilder<>(domainClass, query);
	}
	
	private JPAQueryLagycyBuilder(Class<T> domainClass) {
		this(domainClass, new JPAQuery<>());
	}
	private JPAQueryLagycyBuilder(Class<T> domainClass, EntityManager em) {
		this(domainClass, new JPAQuery<>(em));
	}
	
	private JPAQueryLagycyBuilder(Class<T> domainClass, JPAQuery<T> query) {
		this.path = new JPAQueryBuilder<>(domainClass);
		this.where = new BooleanBuilder();
		this.whereBuilder = new WhereBuilder<>(this, where);
		this.orderBuilder = new OrderBuilder<>(this);
		this.query = query;
		this.query.from(path.from());
	}
	
	private JPAQuery<T> getJPAQuery() {
		return query;
	}
	private JPAQueryBuilder<T> getJPAQueryPath() {
		return path;
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
	public JPAQueryLagycyBuilder<T> distinct() {
		this.query.distinct();
		return this;
	}
	public JPAQueryLagycyBuilder<T> join(String property) {
		this.query.leftJoin(path.get(property)).fetchJoin();
		return this;
	}
	public WhereBuilder<T> where() { 
		return whereBuilder; 
	}
	public WhereBuilder<T> where(Pageable pageable) { 
		pageable(pageable);
		return whereBuilder; 
	}
	
	public OrderBuilder<T> order() { 
		return orderBuilder; 
	}
	public OrderBuilder<T> order(Pageable pageable) { 
		pageable(pageable);
		return orderBuilder; 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JPAQueryBuilder<T> pageable(Pageable pageable) { 
		for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
			query.orderBy(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, path.get(order.getProperty())));
		}

		if (pageable.isUnpaged()) {
			return this;
		}
		query.offset(pageable.getOffset());
		query.limit(pageable.getPageSize());
		return this;
	}
	
	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public JPAQuery<T> build() { 
		query.where(where);
		return query;
	}
	public JPAQuery<T> build(EntityManager em) { 
		query.where(where);
		return query.clone(em);
	}
	
	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public static class WhereBuilder<T>{
		
		private JPAQueryLagycyBuilder<T> builder;
		private PredicateBuilder<T> predicateBuilder;
		
		private State state;
		private enum State{ AND, AND_START, AND_END, OR, OR_START, OR_END }

		private BooleanBuilder where;
		private BooleanBuilder sub;
		
		public WhereBuilder(JPAQueryLagycyBuilder<T> builder, BooleanBuilder where) {
			this.builder = builder;
			this.predicateBuilder = new PredicateBuilder<>(builder);
			this.where = where;
		}
		
		//////////////////////////////////////////
		//
		//////////////////////////////////////////
		public JPAQueryLagycyBuilder<T> pageable(Pageable pageable) { 
			return builder.pageable(pageable);
		}
		
		public JPAQuery<T> build() { 
			return builder.build();
		}
		public JPAQuery<T> build(EntityManager em) { 
			return builder.build(em);
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
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).isNull());
		}
		public WhereBuilder<T> isNotNull(String property){
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).isNotNull());
		}
		public WhereBuilder<T> eq(String property, Object value){
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).eq(value));
		}
		public WhereBuilder<T> notEq(String property, Object value){
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).ne(value));
		}
		public WhereBuilder<T> like(String property, Object value){
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().getString(property).toLowerCase().contains(value.toString().toLowerCase()));
		}
		public WhereBuilder<T> notLike(String property, Object value){
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().getString(property).notLike(value.toString()));
		}
		public <A extends Comparable<?>> WhereBuilder<T> gt(String property, A value) {
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().getComparable(property).gt(value));
		}
		public <A extends Comparable<?>> WhereBuilder<T> gte(String property, A value) {
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().getComparable(property).goe(value));
		}
		public <A extends Comparable<?>> WhereBuilder<T> lt(String property, A value) {
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().getComparable(property).lt(value));
		}
		public <A extends Comparable<?>> WhereBuilder<T> lte(String property, A value) {
			if(value == null) return builder.getWhereBuilder();
			return builder.getWhereBuilder().chain(builder.getJPAQueryPath().getComparable(property).loe(value));
		}
		public <A extends Comparable<?>> WhereBuilder<T> between(String property, Object value) {
			if(value == null) return builder.getWhereBuilder();
			if(ClassUtils.isAssignableValue(Collection.class, value)) {
				A from = (A)((Collection)value).iterator().next();
				A to = (A)((Collection)value).iterator().next();
				return builder.getWhereBuilder().chain(builder.getJPAQueryPath().getComparable(property).between(from, to));
				
			}else if(ObjectUtils.isArray(value)){
				Comparable[] objects = (Comparable[])value;
				A from = (A)objects[0];
				A to = (A)objects[0];
				return builder.getWhereBuilder().chain(builder.getJPAQueryPath().getComparable(property).between(from, to));
			}else {
				return builder.getWhereBuilder();
			}
		}
		public WhereBuilder<T> in(String property, Object value) {
			if(value == null) return builder.getWhereBuilder();
			if(ClassUtils.isAssignableValue(Collection.class, value)) {
				return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).in((Collection)value));
			}else if(ObjectUtils.isArray(value)){
				return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).in((Object[])value));
			}else {
				return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).in(value));
			}
		}
		public WhereBuilder<T> notIn(String property, Object value) {
			if(value == null) return builder.getWhereBuilder();
			if(ClassUtils.isAssignableValue(Collection.class, value)) {
				return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).notIn((Collection)value));
			}else if(ObjectUtils.isArray(value)){
				return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).notIn((Object[])value));
			}else {
				return builder.getWhereBuilder().chain(builder.getJPAQueryPath().get(property).notIn(value));
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
			builder.getJPAQuery().orderBy(new OrderSpecifier(Order.ASC, builder.getJPAQueryPath().get(property)));
			return this;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public OrderBuilder<T> desc(String property) {
			builder.getJPAQuery().orderBy(new OrderSpecifier(Order.DESC, builder.getJPAQueryPath().get(property)));
			return this;
		}
		
		public JPAQueryBuilder<T> pageable(Pageable pageable) { 
			return builder.pageable(pageable);
		}
		public JPAQuery<T> build() { 
			return builder.build();
		}
		public JPAQuery<T> build(EntityManager em) { 
			return builder.build(em);
		}
	}
}
