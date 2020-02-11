package org.springframework.data.jpa.repository.support;

import javax.persistence.EntityManager;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

public class JPAQueryBuilder<T> {

//	protected final Log logger = LogFactory.getLog(getClass());
	
	public static <X> JPAQueryBuilder<X> of(EntityManager em){
		return new JPAQueryBuilder<>(new JPAQuery<>(em));
	}
	
	public static <X> JPAQueryBuilder<X> of(JPAQuery<X> query){
		return new JPAQueryBuilder<>(query);
	}
	
	
	private JPAQuery<T> query;
	private JPAQueryType<T> type;
	
	private JPAQueryBuilder(JPAQuery<T> query) {
		this.query = query;
	}
	
	public FromBuilder<T> from(Class<T> entityType) {
		this.type = new JPAQueryType<>(entityType);
		query.from(type.getRoot());
		return new FromBuilder<>(query, type);
	}
	

	
	public static class FromBuilder<T>{
		
		private JPAQuery<T> query;
		private JPAQueryType<T> type;
		
		private FromBuilder(JPAQuery<T> query, JPAQueryType<T> type) {
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
		private JPAQueryType<T> type;
		
		private PredicateBuilder<T> predicate; 
		private String state; 
		private BooleanBuilder where; 
		private BooleanBuilder current; 
		
		private WhereBuilder(JPAQuery<T> query, JPAQueryType<T> type) {
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
		
		private JPAQueryType<T> type;
		private WhereBuilder<T> where;
		
		private PredicateBuilder(JPAQueryType<T> type, WhereBuilder<T> where) {
			this.type = type;
			this.where = where;
		}
		
		public WhereBuilder<T> eq(String property, Object right) {
			return where.chain(type.get(property).eq(right));
		}

		public WhereBuilder<T> goe(String property, Comparable<?> right) {
			return where.chain(type.getComparable(property).goe(right));
		}
		
	}
	
	public static class OrderBuilder<T>{
		
		private JPAQuery<T> query;
		private JPAQueryType<T> type;
		
		private OrderBuilder(JPAQuery<T> query, JPAQueryType<T> type) {
			this.query = query;
			this.type = type;
		}
		
		public OrderBuilder<T> asc(String property) {
			query.orderBy(new OrderSpecifier<>(Order.ASC, type.getComparable(property)));
			return this;
		}

		public OrderBuilder<T> desc(String property) {
			query.orderBy(new OrderSpecifier<>(Order.DESC, type.getComparable(property)));
			return this;
		}
		
		public JPAQuery<T> build(){
			return query;
		}
	}
}
