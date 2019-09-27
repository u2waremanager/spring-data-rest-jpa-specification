package org.springframework.data.jpa.repository.query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.MultiValueMap;

public class PredicateBuilder<T> {

	protected static Log logger = LogFactory.getLog(PredicateBuilder.class);
	
	private final CriteriaBuilderSupport<T> predicateBuilder;
	private final CriteriaQuerySupport<T> orderBuilder;
	
	
	private enum State{ AND, AND_START, AND_END, OR, OR_START, OR_END }
	private State state;
	
	private Predicate predicate;
	private Predicate subPredicate;
	private MultiValueMap<String,Object> requestParam;
	private T requestParamToEntity;
	
	public PredicateBuilder(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		this.predicateBuilder = new CriteriaBuilderSupport<>(root, query, builder, this);
		this.orderBuilder = new CriteriaQuerySupport<>(root, query, builder, this);
	}

	public void setRequestParam(MultiValueMap<String,Object> requestParam) {
		this.requestParam = requestParam;
	}
	public MultiValueMap<String,Object> getRequestParam() {
		return requestParam;
	}
	public T getRequestParamToEntity() {
		return requestParamToEntity;
	}
	public void setRequestParamToEntity(T requestParamToEntity) {
		this.requestParamToEntity = requestParamToEntity;
	}

	public Class<? extends T> getEntityType() {
		return predicateBuilder.getRoot().getJavaType();
	}
	
	public Root<T> getRoot() {
		return predicateBuilder.getRoot();
	}
	
	public CriteriaQuery<?> getCriteriaQuery() {
		return predicateBuilder.getCriteriaQuery();
	}

	public CriteriaBuilder getCriteriaBuilder() {
		return predicateBuilder.getCriteriaBuilder();
	}
	
	public CriteriaQuerySupport<T> order() { 
		return orderBuilder; 
	}

	public CriteriaBuilderSupport<T> and() { 
//		logger.info("and: "+subPredicate); logger.info("and: "+predicate);
		this.state = State.AND; 
		return predicateBuilder; 
	}
	public CriteriaBuilderSupport<T> andStart() { 
//		logger.info("andStart: "+subPredicate); logger.info("andStart: "+predicate);
		this.state = State.AND_START; 
		return predicateBuilder; 
	}
	public PredicateBuilder<T> andEnd() { 
		if(subPredicate != null ) {
			predicate = (predicate == null) ? subPredicate : join(State.AND, predicate, subPredicate);
		}
		subPredicate = null;		
//		logger.info("andEnd: "+subPredicate);logger.info("andEnd: "+predicate);
		this.state = State.AND_END; 
		return this; 
	}
	public CriteriaBuilderSupport<T> or() { 
//		logger.info("or: "+subPredicate);logger.info("or: "+predicate);
		this.state = State.OR; 
		return predicateBuilder; 
	}
	public CriteriaBuilderSupport<T> orStart() { 
//		logger.info("orStart: "+subPredicate);logger.info("orStart: "+predicate);
		this.state = State.OR_START; 
		return predicateBuilder; 
	}
	public PredicateBuilder<T> orEnd() { 
		if(subPredicate != null ) {
			predicate = (predicate == null) ? subPredicate : join(State.OR, predicate, subPredicate);
		}
		subPredicate = null;		
//		logger.info("orEnd: "+subPredicate);logger.info("orEnd: "+predicate);
		this.state = State.OR_END; 
		return this; 
	}
	
	private Predicate join(State state, Predicate... predicates) {
		if(State.AND.equals(state)) {
			return predicateBuilder.getCriteriaBuilder().and(predicates);
		}else if(State.OR.equals(state)) {
			return predicateBuilder.getCriteriaBuilder().or(predicates);
		}
		return null;
	}
	
	private PredicateBuilder<T> join(Predicate criteria) {
		
		if(criteria == null) return this;
		
		switch (state) {
			case AND:
				if(subPredicate != null) {
					subPredicate = join(State.AND, subPredicate, criteria);
				}else {
					predicate = (predicate == null) ? criteria : join(State.AND, predicate, criteria); 
				}
//				logger.info("AND: "+subPredicate);logger.info("AND: "+predicate);
				break;
				
			case AND_START:
				subPredicate = criteria;
//				logger.info("AND_START: "+subPredicate); logger.info("AND_START: "+predicate);
				break;
			case AND_END:
//				logger.info("AND_START: "+subPredicate); logger.info("AND_START: "+predicate);
				break;
			case OR:
				if(subPredicate != null) {
					subPredicate = join(State.OR, subPredicate, criteria);
				}else {
					predicate = (predicate == null) ? criteria : join(State.OR, predicate, criteria);
				}
//				logger.info("OR: "+subPredicate); logger.info("OR: "+predicate);
				break;
			case OR_START:
				subPredicate = criteria;
//				logger.info("OR_START: "+subPredicate);logger.info("OR_START: "+predicate);
				break;
			case OR_END:
//				logger.info("OR_END: "+subPredicate);logger.info("OR_END: "+predicate);
				break;
	
			default:
				break;
		}
		return this;
	}
	
	
	public Predicate build() {
		orderBuilder.orderBy();
		return predicate;
	}

	public static class CriteriaQuerySupport<T>{
		
		private final Root<T> root;
		private final CriteriaQuery<?> query;
		private final CriteriaBuilder builder;
		private final PredicateBuilder<T> chain;
		private List<Order> orders = new ArrayList<Order>();

		private CriteriaQuerySupport(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, PredicateBuilder<T> chain) {
			this.root = root;
			this.query = query;
			this.builder = builder;
			this.chain = chain;
		}

		private void orderBy() {
			this.query.orderBy(orders);
		}

		public PredicateBuilder<T> asc(String property) {
			Part part = new Part(property, root.getJavaType());
			orders.add(builder.asc(getTypedPath(root, part)));
			return chain;
		}
		public PredicateBuilder<T> desc(String property) {
			Part part = new Part(property, root.getJavaType());
			orders.add(builder.desc(getTypedPath(root, part)));
			return chain;
		}
		
		private <X> Expression<X> getTypedPath(Root<?> root, Part part) {
			return QueryUtils.toExpressionRecursively(root, part.getProperty());
		}
	}
	
	
	public static class CriteriaBuilderSupport<T>{
		
		private final Root<T> root;
		private final CriteriaQuery<?> query;
		private final CriteriaBuilder builder;
		private final PredicateBuilder<T> chain;
		
		private CriteriaBuilderSupport(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, PredicateBuilder<T> chain) {
			this.root = root;
			this.query = query;
			this.builder = builder;
			this.chain = chain;
		}
		
		private Root<T> getRoot() {
			return root;
		}
		private CriteriaQuery<?> getCriteriaQuery() {
			return query;
		}
		private CriteriaBuilder getCriteriaBuilder() {
			return builder;
		}

		public PredicateBuilder<T> partTree(String source){
			return partTree(source, chain.getRequestParam());
		}
		public PredicateBuilder<T> partTree(String source, T params){
			return partTree(source, new BeanWrapperImpl(params));
		}
		public PredicateBuilder<T> partTree(String source, Object... params){
			return partTree(source, new BeanWrapperObjectArray(params));
		}
		public PredicateBuilder<T> partTree(String source, MultiValueMap<String,Object> params){
			return partTree(source, new BeanWrapperMultiValue(params));
		}
		public PredicateBuilder<T> partTree(String source, BeanWrapper params){
			try {
				PartTree partTree = new PartTree(source, root.getJavaType());
				Predicate predicate = new PartTreePredicateBuilder<>(root, query, builder).build(partTree, params);
				return chain.join(predicate);
				
			}catch(Exception e) {
				logger.info(source+" -> "+e.getMessage());
				return chain.join((Predicate)null);
			}
		}

		
		public PredicateBuilder<T> part(String source, Object params){

			try {
				Part part = new Part(source, root.getJavaType());
				Predicate predicate = new PartTreePredicateBuilder<>(root, query, builder).build(part, params);
				return chain.join(predicate);
				
			}catch(Exception e) {
				logger.info(source+" -> "+e.getMessage());
				return chain.join((Predicate)null);
			}
		}


		public PredicateBuilder<T> isNull(String property){
			return part(property+"IsNull", "");
		}
		public PredicateBuilder<T> isNotNull(String property){
			return part(property+"IsNotNull", "");
		}
		public PredicateBuilder<T> eq(String property, Object value){
			return part(property, value);
		}
		public PredicateBuilder<T> notEq(String property, Object value){
			return part(property+"Not", value);
		}
		public PredicateBuilder<T> like(String property, Object value){
			return part(property+"ContainingIgnoreCase", value);
		}
		public PredicateBuilder<T> notLike(String property, Object value){
			return part(property+"NotContainingIgnoreCase", value);
		}
		public PredicateBuilder<T> between(String property, Object value) {
			return part(property+"IsBetween", value);
		}
		public PredicateBuilder<T> gt(String property, Object value) {
			return part(property+"IsGreaterThan", value);
		}
		public PredicateBuilder<T> gte(String property, Object value) {
			return part(property+"IsGreaterThanEqual", value);
		}
		public PredicateBuilder<T> lt(String property, Object value) {
			return part(property+"IsLessThan", value);
		}
		public PredicateBuilder<T> lte(String property, Object value) {
			return part(property+"IsLessThanEqual", value);
		}
		public PredicateBuilder<T> in(String property, Object value) {
			return part(property+"IsIn", value);
		}
		public PredicateBuilder<T> notIn(String property, Object value) {
			return part(property+"IsNotIn", value);
		}
		
		
		public PredicateBuilder<T> eq(String property){
			return part(property, chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> notEq(String property){
			return part(property+"Not", chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> like(String property){
			return part(property+"ContainingIgnoreCase", chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> notLike(String property){
			return part(property+"NotContainingIgnoreCase", chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> between(String property) {
			return part(property+"IsBetween", chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> gt(String property) {
			return part(property+"IsGreaterThan", chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> gte(String property) {
			return part(property+"IsGreaterThanEqual", chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> lt(String property) {
			return part(property+"IsLessThan", chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> lte(String property) {
			return part(property+"IsLessThanEqual", chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> in(String property) {
			return part(property+"IsIn", chain.getRequestParam().get(property));
		}
		public PredicateBuilder<T> notIn(String property) {
			return part(property+"IsNotIn", chain.getRequestParam().get(property));
		}
		
	}


}
