package org.springframework.data.jpa.repository.query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.repository.query.parser.Part;

public class PredicateBuilder<T> {

	public static <X> PredicateBuilder<X> of(Root<X> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		return new PredicateBuilder<>(root, criteriaQuery, criteriaBuilder);
	}
	
	public static class BaseBuilder<T>{
		
		private Root<T> root;
		private CriteriaQuery<?> query;
		private CriteriaBuilder builder;
		private Predicate predicate;

		private BaseBuilder(BaseBuilder<T> builder) {
			this(builder.getRoot(), builder.getQuery(), builder.getBuilder());
		}
		
		public BaseBuilder(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
			this.root = root;
			this.query = query;
			this.builder = builder;
		}
		public BaseBuilder<T> and(Predicate right) {
			predicate = (predicate == null) ? right : builder.and(predicate, right);
			return this;
		}
		public BaseBuilder<T> or(Predicate right) {
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
		public Predicate getBase() {
			return predicate;
		}
	}
	
	
	private BaseBuilder<T> builder;
	
	private PredicateBuilder(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		this.builder = new BaseBuilder<>(root, query, builder);
	}
	
	public WhereBuilder<T> where() {			
		return new WhereBuilder<>(builder);
	}
	public OrderBuilder<T> orderBy() {			
		return  new OrderBuilder<>(builder);
	}

	public Predicate build() {			
		return builder.getBase();
	}

	public static class OrderBuilder<T>{

		private BaseBuilder<T> builder;
		private List<Order> orders;
		
		private OrderBuilder(BaseBuilder<T> builder) {
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
			return builder.getBase();
		}
	}
	
	

	public static class WhereBuilder<T>{

		private BaseBuilder<T> builder;
		
		private WhereBuilder(BaseBuilder<T> builder) {
			this.builder = builder;
		}

		public WhereBuilder<T> and(Predicate right) {
			builder.and(right); return this;
		}
		public WhereBuilder<T> or(Predicate right) {
			builder.or(right); return this;
		}
		
		public AndBuilder<WhereBuilder<T>, T> and() { 
			return new AndBuilder<WhereBuilder<T>, T>(this, builder) {};
		}
		public OrBuilder<WhereBuilder<T>, T> or() { 
			return new OrBuilder<WhereBuilder<T>, T>(this, builder) {};
		}
		
		public AndStartBuilder<WhereBuilder<T>, T> andStart() { 
			return new AndStartBuilder<WhereBuilder<T>, T>(this, builder) {};
		}

		public OrStartBuilder<WhereBuilder<T>, T> orStart() { 
			return new OrStartBuilder<WhereBuilder<T>, T>(this, builder){};
		}
	
		public OrderBuilder<T> orderBy(){
			return new OrderBuilder<>(builder);
		}
		
		public Predicate build() {
			return builder.getBase();
		}
		
		
		public abstract static class AndStartBuilder<W, T> {

			private W where;
			private BaseBuilder<T> builder;
			private BaseBuilder<T> sub;
			
			private AndStartBuilder(W where, BaseBuilder<T> builder) {
				this.where = where;
				this.builder = builder;
				this.sub = new BaseBuilder<>(builder);
			}
			
			public AndStartBuilder<W,T> and(Predicate right) {
				sub.and(right); return this;
			}
			public AndStartBuilder<W,T> or(Predicate right) {
				sub.or(right); return this;
			}
			
			public AndBuilder<AndStartBuilder<W,T>, T> and() { 
				return new AndBuilder<AndStartBuilder<W,T>, T>(this, sub) {};
			}
			public OrBuilder<AndStartBuilder<W,T>, T> or() { 
				return new OrBuilder<AndStartBuilder<W,T>, T>(this, sub) {};
			}

			public AndStartBuilder<AndStartBuilder<W,T>, T> andStart() { 
				return new AndStartBuilder<AndStartBuilder<W,T>, T>(this, sub) {};
			}
			public OrStartBuilder<AndStartBuilder<W,T>, T> orStart() { 
				return new OrStartBuilder<AndStartBuilder<W,T>, T>(this, sub) {};
			}
			
			
			public W andEnd() {
				builder.and(sub.getBase()); return where;
			}
			
		}
		
		
		public abstract static class OrStartBuilder<W,T> {

			private W where;
			private BaseBuilder<T> builder;
			private BaseBuilder<T> sub;
			
			private OrStartBuilder(W where, BaseBuilder<T> builder) {
				this.where = where;
				this.builder = builder;
				this.sub = new BaseBuilder<>(builder);
			}
			
			public OrStartBuilder<W,T> and(Predicate right) {
				sub.and(right); return this;
			}
			public OrStartBuilder<W,T> or(Predicate right) {
				sub.or(right); return this;
			}
			
			public AndBuilder<OrStartBuilder<W,T>, T> and() { 
				return new AndBuilder<OrStartBuilder<W,T>, T>(this, sub) {};
			}
			public OrBuilder<OrStartBuilder<W,T>, T> or() { 
				return new OrBuilder<OrStartBuilder<W,T>, T>(this, sub) {};
			}

			public AndStartBuilder<OrStartBuilder<W,T>, T> andStart() { 
				return new AndStartBuilder<OrStartBuilder<W,T>, T>(this, sub) {};
			}
			public OrStartBuilder<OrStartBuilder<W,T>, T> orStart() { 
				return new OrStartBuilder<OrStartBuilder<W,T>, T>(this, sub) {};
			}
			
			public W orEnd() {
				builder.or(sub.getBase()); return where;
			}
		}
		
		
		
		public abstract static class AndBuilder<W,T> extends OperationBuilder<W,T>{

			private AndBuilder(W where, BaseBuilder<T> builder) {
				super(where, builder);
			}

			@Override
			protected W add(Predicate right) {
				builder.and(right); return where;
			}
		}
		
		public abstract static class OrBuilder<W,T> extends OperationBuilder<W,T>{

			private OrBuilder(W where, BaseBuilder<T> builder) {
				super(where, builder);
			}

			@Override
			protected W add(Predicate right) {
				builder.or(right); return where;
			}
		}
		
		public abstract static class OperationBuilder<W,T>{
			
			
			protected W where;
			protected BaseBuilder<T> builder;
			
			protected OperationBuilder(W where, BaseBuilder<T> builder) {
				this.where = where;
				this.builder = builder;
			}

			protected abstract W add(Predicate right);

			private W part(String source, Object value){
				if(value == null) return where;
				try {
					Part part = new Part(source, builder.getRoot().getJavaType());
					Predicate predicate = new PartTreePredicate<T>(builder.getRoot(), builder.getQuery(), builder.getBuilder()).build(part, value);
					return add(predicate);
				}catch(Exception e) {
//					logger.info(source+" -> "+e.getMessage());
					return where;
				}
			}
			public W isNull(String property){
				return part(property+"IsNull", true);
			}
			public W isNotNull(String property){
				return part(property+"IsNotNull", false);
			}
			public W eq(String property, Object value){
				return part(property, value);
			}
			public W notEq(String property, Object value){
				return part(property+"Not", value);
			}
			public W like(String property, Object value){
				return part(property+"ContainingIgnoreCase", value);
			}
			public W notLike(String property, Object value){
				return part(property+"NotContainingIgnoreCase", value);
			}
			public W between(String property, Object value) {
				return part(property+"IsBetween", value);
			}
			public W gt(String property, Object value) {
				return part(property+"IsGreaterThan", value);
			}
			public W gte(String property, Object value) {
				return part(property+"IsGreaterThanEqual", value);
			}
			public W lt(String property, Object value) {
				return part(property+"IsLessThan", value);
			}
			public W lte(String property, Object value) {
				return part(property+"IsLessThanEqual", value);
			}
			public W in(String property, Object value) {
				return part(property+"IsIn", value);
			}
			public W notIn(String property, Object value) {
				return part(property+"IsNotIn", value);
			}
			
		}
	}
	
	
	
	
}
