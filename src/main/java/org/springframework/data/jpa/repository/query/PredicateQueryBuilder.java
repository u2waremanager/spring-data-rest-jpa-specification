package org.springframework.data.jpa.repository.query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.repository.query.parser.Part;

public class PredicateQueryBuilder<T> {

	public static <X> PredicateQueryBuilder<X> of(Root<X> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		return new PredicateQueryBuilder<>(root, criteriaQuery, criteriaBuilder);
	}
	
	private PredicateBuilder<T> builder;
	
	private PredicateQueryBuilder(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		this.builder = new PredicateBuilder<>(root, query, builder);
	}
	
	public WhereBuilder<T> where() {			
		return new WhereBuilder<>(builder);
	}
	public OrderBuilder<T> orderBy() {			
		return  new OrderBuilder<>(builder);
	}

	public Predicate build() {			
		return builder.getPredicate();
	}

	
	public static class WhereBuilder<T>{

		private PredicateBuilder<T> builder;
		
		protected WhereBuilder(PredicateBuilder<T> builder) {
			this.builder = builder;
		}

		public WhereBuilder<T> and(Predicate right) {
			builder.and(right);
			return this;
		}
		public AndBuilder<T, WhereBuilder<T>> and() { 
			return new AndBuilder<T, WhereBuilder<T>>(this, builder) {};
		}
		public AndStartBuilder<T> andStart() { 
			return new AndStartBuilder<>(this, new PredicateBuilder<T>(builder));
		}

		public WhereBuilder<T> or(Predicate right) {
			builder.or(right);
			return this;
		}
		public OrBuilder<T, WhereBuilder<T>> or() { 
			return new OrBuilder<T, WhereBuilder<T>>(this, builder) {};
		}
		public OrStartBuilder<T> orStart() { 
			return new OrStartBuilder<>(this, new PredicateBuilder<T>(builder));
		}
	
		public OrderBuilder<T> orderBy(){
			return new OrderBuilder<>(builder);
		}
		
		public Predicate build() {
			return builder.getPredicate();
		}
		
		public static class AndStartBuilder<T> {

			private WhereBuilder<T> where;
			private PredicateBuilder<T> builder;
			
			private AndStartBuilder(WhereBuilder<T> where, PredicateBuilder<T> builder) {
				this.where = where;
				this.builder = builder;
			}
			
			public AndStartBuilder<T> and(Predicate right) {
				builder.and(right);
				return this;
			}
			public AndBuilder<T, AndStartBuilder<T>> and() { 
				return new AndBuilder<T,AndStartBuilder<T>>(this, builder) {};
			}
			public AndStartBuilder<T> or(Predicate right) {
				builder.or(right);
				return this;
			}
			public OrBuilder<T, AndStartBuilder<T>> or() { 
				return new OrBuilder<T,AndStartBuilder<T>>(this, builder) {};
			}
			
			public WhereBuilder<T> andEnd() {
				return where.and(builder.getPredicate());
			}
		}
		
		
		public static class OrStartBuilder<T> {

			private WhereBuilder<T> where;
			private PredicateBuilder<T> builder;
			
			private OrStartBuilder(WhereBuilder<T> where, PredicateBuilder<T> builder) {
				this.where = where;
				this.builder = builder;
			}
			
			public OrStartBuilder<T> and(Predicate right) {
				builder.and(right);
				return this;
			}
			public AndBuilder<T, OrStartBuilder<T>> and() { 
				return new AndBuilder<T,OrStartBuilder<T>>(this, builder) {};
			}
			public OrStartBuilder<T> or(Predicate right) {
				builder.or(right);
				return this;
			}
			public OrBuilder<T, OrStartBuilder<T>> or() { 
				return new OrBuilder<T,OrStartBuilder<T>>(this, builder) {};
			}
			
			public WhereBuilder<T> orEnd() {
				return where.or(builder.getPredicate());
			}
		}
		
		
		
		public abstract static class AndBuilder<T,Z> extends OperationBuilder<T,Z>{

			private Z where;
			
			private AndBuilder(Z where, PredicateBuilder<T> builder) {
				super(builder);
				this.where = where;
			}

			@Override
			protected Z add(Predicate right) {
				builder.and(right);
				return where;
			}
		}
		
		public abstract static class OrBuilder<T,Z> extends OperationBuilder<T,Z>{

			private Z where;
			
			private OrBuilder(Z where, PredicateBuilder<T> builder) {
				super(builder);
				this.where = where;
			}

			@Override
			protected Z add(Predicate right) {
				builder.or(right);
				return where;
			}
		}
		
		private abstract static class OperationBuilder<T, Z>{
			
			protected abstract Z add(Predicate right);
			
			protected PredicateBuilder<T> builder;
			
			protected OperationBuilder(PredicateBuilder<T> builder) {
				this.builder = builder;
			}
			
			private Z part(String source, Object value){
				if(value == null) return add(null);
				try {
					Part part = new Part(source, builder.getRoot().getJavaType());
					Predicate predicate = new PartTreePredicate<T>(builder.getRoot(), builder.getQuery(), builder.getBuilder()).build(part, value);
					return add(predicate);
				}catch(Exception e) {
//					logger.info(source+" -> "+e.getMessage());
					return add(null);
				}
			}
			public Z isNull(String property){
				return part(property+"IsNull", true);
			}
			public Z isNotNull(String property){
				return part(property+"IsNotNull", false);
			}
			public Z eq(String property, Object value){
				return part(property, value);
			}
			public Z notEq(String property, Object value){
				return part(property+"Not", value);
			}
			public Z like(String property, Object value){
				return part(property+"ContainingIgnoreCase", value);
			}
			public Z notLike(String property, Object value){
				return part(property+"NotContainingIgnoreCase", value);
			}
			public Z between(String property, Object value) {
				return part(property+"IsBetween", value);
			}
			public Z gt(String property, Object value) {
				return part(property+"IsGreaterThan", value);
			}
			public Z gte(String property, Object value) {
				return part(property+"IsGreaterThanEqual", value);
			}
			public Z lt(String property, Object value) {
				return part(property+"IsLessThan", value);
			}
			public Z lte(String property, Object value) {
				return part(property+"IsLessThanEqual", value);
			}
			public Z in(String property, Object value) {
				return part(property+"IsIn", value);
			}
			public Z notIn(String property, Object value) {
				return part(property+"IsNotIn", value);
			}
			
		}
	}
	
	
	
	public static class OrderBuilder<T>{

		private PredicateBuilder<T> builder;
		private List<Order> orders;
		
		protected OrderBuilder(PredicateBuilder<T> builder) {
			this.builder = builder;
			this.orders = new ArrayList<>();
		}

		public OrderBuilder<T> asc(String property) {
			orders.add(builder.getBuilder().asc(PartTreePredicate.getTypedPath(builder.getRoot(), property)));
			return this;
		}

		public OrderBuilder<T> desc(String property) {
			orders.add(builder.getBuilder().desc(PartTreePredicate.getTypedPath(builder.getRoot(), property)));
			return this;
		}
		
		public Predicate build() {
			builder.getQuery().orderBy(orders);
			return builder.getPredicate();
		}
	}
	
	
	public static class PredicateBuilder<T>{
		
		private Root<T> root;
		private CriteriaQuery<?> query;
		private CriteriaBuilder builder;
		private Predicate predicate;

		protected PredicateBuilder(PredicateBuilder<T> builder) {
			this(builder.getRoot(), builder.getQuery(), builder.getBuilder());
		}
		
		protected PredicateBuilder(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
			this.root = root;
			this.query = query;
			this.builder = builder;
		}
		public PredicateBuilder<T> and(Predicate right) {
			predicate = (predicate == null) ? right : builder.and(predicate, right);
			return this;
		}
		public PredicateBuilder<T> or(Predicate right) {
			predicate = (predicate == null) ? right : builder.or(predicate, right);
			return this;
		}
		
		public Root<T> getRoot() {
			return root;
		}
		public CriteriaQuery<?> getQuery() {
			return query;
		}
		public CriteriaBuilder getBuilder() {
			return builder;
		}
		public Predicate getPredicate() {
			return predicate;
		}
	}
	
}
