package org.springframework.data.jpa.repository.query.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.MultiValueMap;

public class PredicateBuilder<T> {

	protected static Log logger = LogFactory.getLog(PredicateBuilder.class);
	
	private OrderBuilder<T> orderBuilder;
	private WhereBuilder<T> whereBuilder;
	
	private Root<T> root;
	private CriteriaQuery<?> criteriaQuery;
	private CriteriaBuilder criteriaBuilder;
	
	
	public static <X> PredicateBuilder<X> of(Root<X> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		return new PredicateBuilder<>(root, criteriaQuery, criteriaBuilder);
	}
	
	private PredicateBuilder(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		this.root = root;
		this.criteriaQuery = criteriaQuery;
		this.criteriaBuilder = criteriaBuilder;
		
		
		this.whereBuilder = new WhereBuilder<>(this);
		this.orderBuilder = new OrderBuilder<>(this);
	}
	
	/////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////
	private Root<T> getRoot() {
		return root;
	}
	private CriteriaQuery<?> getCriteriaQuery() {
		return criteriaQuery;
	}
	private CriteriaBuilder getCriteriaBuilder() {
		return criteriaBuilder;
	}
	private WhereBuilder<T> getWhereBuilder() {
		return whereBuilder;
	}
	private OrderBuilder<T> getOrderBuilder() {
		return orderBuilder;
	}
	

	
	/////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////
	public WhereBuilder<T> where() { 
		return whereBuilder; 
	}
	public OrderBuilder<T> orderBy() { 
		return orderBuilder; 
	}

	public Predicate build() {
		return whereBuilder.build();
	}
	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public static class WhereBuilder<T>{
		
		private PredicateBuilder<T> builder;
		private PartTreeBuilder<T> partTreeBuilder;
		
		private enum State{ AND, AND_START, AND_END, OR, OR_START, OR_END }
		private State state;
		
		
		private Predicate where;
		private Predicate sub;
		
		public WhereBuilder(PredicateBuilder<T> builder) {
			this.builder = builder;
			this.partTreeBuilder = new PartTreeBuilder<>(builder);
		}

		//////////////////////////////////////////
		//
		//////////////////////////////////////////
		public Predicate build() {
			return where;
		}

		
		public OrderBuilder<T> orderBy() { 
			return builder.getOrderBuilder(); 
		}
		
		public PartTreeBuilder<T> and() { 
			this.state = State.AND; 
			return partTreeBuilder;
		}
		public PartTreeBuilder<T> andStart() { 
			this.state = State.AND_START; 
			return partTreeBuilder;
		}
		public WhereBuilder<T> andEnd() { 
			this.state = State.AND_END; 
			return chain(null);
		}
		public PartTreeBuilder<T> or() { 
			this.state = State.OR; 
			return partTreeBuilder;
		}
		public PartTreeBuilder<T> orStart() { 
			this.state = State.OR_START; 
			return partTreeBuilder;
		}
		public WhereBuilder<T> orEnd() { 
			this.state = State.OR_END; 
			return chain(null);
		}
		
		
		private WhereBuilder<T> chain(Predicate criteria) { 

			switch (this.state) {
				case AND: 
					if(sub != null) {
						sub = builder.getCriteriaBuilder().and(sub, criteria);
					}else {
						where = (where == null) ? criteria : builder.getCriteriaBuilder().and(where, criteria); 
					}
					break;
				case AND_START: 
					sub = criteria;
					logger.info("AND_START " +sub.getAlias());
					break;
				case AND_END: 
					logger.info("AND_END "+sub);
					if(sub != null ) {
						where = (where == null) ? sub : builder.getCriteriaBuilder().and(where, sub);
					}
					sub = null;
					break;
				case OR: 
					logger.info("OR "+sub);
					if(sub != null) {
						sub = builder.getCriteriaBuilder().or(sub, criteria);
					}else {
						where = (where == null) ? criteria : builder.getCriteriaBuilder().or(where, criteria); 
					}
					break;
				case OR_START: 
					sub = criteria;
					break;
				case OR_END: 
					if(sub != null ) {
						where = (where == null) ? sub : builder.getCriteriaBuilder().or(where, sub);
					}
					sub = null;
					break;
			}
			return this;
		}
	}
	
	public static class PartTreeBuilder<T>{
		
		private PredicateBuilder<T> builder;
		
		public PartTreeBuilder(PredicateBuilder<T> builder) {
			this.builder = builder;
		}
		
		public WhereBuilder<T> partTree(String source, T params){
			return partTree(source, BeanWrapperFactory.getInstance(params));
		}
		public WhereBuilder<T> partTree(String source, Object... params){
			return partTree(source, BeanWrapperFactory.getInstance(params));
		}
		public WhereBuilder<T> partTree(String source, MultiValueMap<String,Object> params){
			return partTree(source, BeanWrapperFactory.getInstance(params));
		}
		private WhereBuilder<T> partTree(String source, BeanWrapper params){
			try {
				PartTree partTree = new PartTree(source, builder.getRoot().getJavaType());
				Predicate predicate = new PartTreePredicate<>(builder.getRoot(), builder.getCriteriaQuery(), builder.getCriteriaBuilder()).build(partTree, params);
				return builder.getWhereBuilder().chain(predicate);
			}catch(Exception e) {
				logger.info(source+" -> "+e.getMessage());
				return builder.getWhereBuilder();
			}
		}

		private WhereBuilder<T> part(String source, Object value){
			if(value == null) return builder.getWhereBuilder();
			try {
				Part part = new Part(source, builder.getRoot().getJavaType());
				Predicate predicate = new PartTreePredicate<T>(builder.getRoot(), builder.getCriteriaQuery(), builder.getCriteriaBuilder()).build(part, value);
				return builder.getWhereBuilder().chain(predicate);
			}catch(Exception e) {
				logger.info(source+" -> "+e.getMessage());
				return builder.getWhereBuilder();
			}
		}
		
		public WhereBuilder<T> isNull(String property){
			return part(property+"IsNull", true);
		}
		public WhereBuilder<T> isNotNull(String property){
			return part(property+"IsNotNull", false);
		}
		public WhereBuilder<T> eq(String property, Object value){
			return part(property, value);
		}
		public WhereBuilder<T> notEq(String property, Object value){
			return part(property+"Not", value);
		}
		public WhereBuilder<T> like(String property, Object value){
			return part(property+"ContainingIgnoreCase", value);
		}
		public WhereBuilder<T> notLike(String property, Object value){
			return part(property+"NotContainingIgnoreCase", value);
		}
		public WhereBuilder<T> between(String property, Object value) {
			return part(property+"IsBetween", value);
		}
		public WhereBuilder<T> gt(String property, Object value) {
			return part(property+"IsGreaterThan", value);
		}
		public WhereBuilder<T> gte(String property, Object value) {
			return part(property+"IsGreaterThanEqual", value);
		}
		public WhereBuilder<T> lt(String property, Object value) {
			return part(property+"IsLessThan", value);
		}
		public WhereBuilder<T> lte(String property, Object value) {
			return part(property+"IsLessThanEqual", value);
		}
		public WhereBuilder<T> in(String property, Object value) {
			return part(property+"IsIn", value);
		}
		public WhereBuilder<T> notIn(String property, Object value) {
			return part(property+"IsNotIn", value);
		}
		
		///////////////////////////////////////////////////////////////
		//
		///////////////////////////////////////////////////////////////
	}

	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public static class OrderBuilder<T>{
		
		private PredicateBuilder<T> builder;
		private List<Order> orders;
		
		public OrderBuilder(PredicateBuilder<T> builder){
			this.builder = builder;
			this.orders = new ArrayList<>();
		}
		
		public OrderBuilder<T> asc(String property) {
			orders.add(builder.getCriteriaBuilder().asc(PartTreePredicate.getTypedPath(builder.getRoot(), property)));
			return this;
		}
		public OrderBuilder<T> desc(String property) {
			orders.add(builder.getCriteriaBuilder().desc(PartTreePredicate.getTypedPath(builder.getRoot(), property)));
			return this;
		}
		
		public Predicate build() {
			builder.getCriteriaQuery().orderBy(orders);
			return builder.build();
		}
		
	}
}