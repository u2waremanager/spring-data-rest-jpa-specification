package org.springframework.data.jpa.repository.support;

import javax.persistence.EntityManager;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;

public class JPAQueryBuilder<T> {

//	protected final Log logger = LogFactory.getLog(getClass());
	
	public static <X> JPAQueryBuilder<X> of(JPAQuery<X> query){
		return new JPAQueryBuilder<>(query);
	}
	
	public static <X> JPAQueryBuilder<X> of(EntityManager em){
		return new JPAQueryBuilder<X>(em);
	}
	
	
	private JPAQuery<T> query;
	private PathBuilder<T> type;
	
	public JPAQueryBuilder(JPAQuery<T> query) {
		this.query = query;
	}
	
	public JPAQueryBuilder(EntityManager em) {
		this.query = new JPAQuery<>(em);
	}
	
	@SuppressWarnings("unchecked")
	public FromBuilder<T> from(Class<?> entityType) {
		this.type = (PathBuilder<T>) new PathBuilderFactory().create(entityType);
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
	
	
	
	public static class WhereBuilder<T>{
		
		private JPAQuery<T> query;
		private PathBuilder<T> type;
		
		private PredicateBuilder<T> predicate; 
		private String state; 
		private BooleanBuilder where; 
		private BooleanBuilder current; 
		
		private WhereBuilder(JPAQuery<T> query, PathBuilder<T> type) {
			this.query = query;
			this.type = type;
			this.predicate = new PredicateBuilder<>(type, this);
			this.where = new BooleanBuilder();
		}
		
		public PredicateBuilder<T> and(){
			this.state = "and";
			return predicate;
		}
		public PredicateBuilder<T> or(){
			this.state = "or";
			return predicate;
		}
		public PredicateBuilder<T> andStart(){
			if(this.current != null) throw new RuntimeException("current is not finished."); 
			this.current = new BooleanBuilder();
			return predicate;
		}
		public WhereBuilder<T> andEnd(){
			if(this.current == null) throw new RuntimeException("current is not started."); 
			this.where.and(this.current);
			this.current = null;
			return this;
		}
		public PredicateBuilder<T> orStart(){
			if(this.current != null) throw new RuntimeException("current is not finished."); 
			this.current = new BooleanBuilder();
			return predicate;
		}
		public WhereBuilder<T> orEnd(){
			if(this.current == null) throw new RuntimeException("current is not started."); 
			this.where.or(this.current);
			this.current = null;
			return this;
		}

		private WhereBuilder<T> chain(BooleanExpression right){
			if("or".equals(state)) {
				if(this.current != null) {
					current.or(right);
				}else {
					where.or(right);
				}
				
			}else {
				if(this.current != null) {
					current.and(right);
				}else {
					where.and(right);
				}
			}
			return this;
		}

		public WhereBuilder<T> and(BooleanExpression right){
			this.state = "and";
			return chain(right);
		}
		public WhereBuilder<T> or(BooleanExpression right){
			this.state = "or";
			return chain(right);
		}
		
		
		public JPAQuery<T> build(){
			query.where(where);
			return query;
		}
		
		public OrderBuilder<T> orderBy(){
			query.where(where);
			return new OrderBuilder<>(query, type);
		}
	}
	
	public static class PredicateBuilder<T>{
		
		private PathBuilder<T> type;
		private WhereBuilder<T> where;
		
		private PredicateBuilder(PathBuilder<T> type, WhereBuilder<T> where) {
			this.type = type;
			this.where = where;
		}
		
		public WhereBuilder<T> eq(String property, Object right) {
			return where.chain(type.get(property).eq(right));
		}

		public WhereBuilder<T> goe(String property, Comparable<?> right) {
			return where.chain(type.getComparable(property, Comparable.class).goe(right));
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
