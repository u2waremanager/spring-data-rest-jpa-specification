package org.springframework.data.jpa.repository.query;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.MultiValueMap;

public class PartTreeQueryBuilder<T> {

	protected static Log logger = LogFactory.getLog(PartTreeQueryBuilder.class);
	
	private OrderBuilder<T> orderBuilder;
	private WhereBuilder<T> whereBuilder;
	
	private QueryParameters<T> queryParameters;
	
	private Root<T> root;
	private CriteriaQuery<?> criteriaQuery;
	private CriteriaBuilder criteriaBuilder;
	
	
	public static <X> PartTreeQueryBuilder<X> of(Root<X> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		return new PartTreeQueryBuilder<>(root, criteriaQuery, criteriaBuilder);
	}
	
	private PartTreeQueryBuilder(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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
	
	public QueryParameters<T> getQueryParameters() {
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
	public WhereBuilder<T> where() { 
		return whereBuilder; 
	}
	public OrderBuilder<T> order() { 
		return orderBuilder; 
	}

	public Predicate build() {
		return whereBuilder.build();
	}
	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public static class WhereBuilder<T>{
		
		private PartTreeQueryBuilder<T> builder;
		private PredicateBuilder<T> predicateBuilder;
		
		private enum State{ AND, AND_START, AND_END, OR, OR_START, OR_END }
		private State state;
		
		
		private Predicate where;
		private Predicate sub;
		
		public WhereBuilder(PartTreeQueryBuilder<T> builder) {
			this.builder = builder;
			this.predicateBuilder = new PredicateBuilder<>(builder);
		}

		//////////////////////////////////////////
		//
		//////////////////////////////////////////
		public Predicate build() {
			return where;
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
			this.state = State.AND_START; 
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
					if(sub != null) {
						sub = builder.getCriteriaBuilder().and(sub, criteria);
					}else {
						where = (where == null) ? criteria : builder.getCriteriaBuilder().and(where, criteria); 
					}
					break;
				case AND_START: 
					sub = criteria;
					break;
				case AND_END: 
					if(sub != null ) {
						where = (where == null) ? sub : builder.getCriteriaBuilder().and(where, sub);
					}
					sub = null;
					break;
				case OR: 
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
	
	public static class PredicateBuilder<T>{
		
		private PartTreeQueryBuilder<T> builder;
		
		public PredicateBuilder(PartTreeQueryBuilder<T> builder) {
			this.builder = builder;
		}
		
		public WhereBuilder<T> partTree(String source){
			return partTree(source, builder.getQueryParameters());
		}
		public WhereBuilder<T> partTree(String source, T params){
			return partTree(source, new QueryParameters<T>(params));
		}
		public WhereBuilder<T> partTree(String source, Object... params){
			return partTree(source, new QueryParameters<T>(params));
		}
		public WhereBuilder<T> partTree(String source, MultiValueMap<String,Object> params){
			return partTree(source, new QueryParameters<T>(params));
		}
		public WhereBuilder<T> partTree(String source, QueryParameters<T> params){
			try {
				PartTree partTree = new PartTree(source, builder.getRoot().getJavaType());
				Predicate predicate = new PartTreeQueryBuilderSupport<>(builder.getRoot(), builder.getCriteriaQuery(), builder.getCriteriaBuilder())
						.build(partTree, params);
				return builder.getWhereBuilder().chain(predicate);
			}catch(Exception e) {
				logger.info(source+" -> "+e.getMessage());
				return builder.getWhereBuilder();
			}
		}

		public WhereBuilder<T> part(String source, Object value){
			if(value == null) return builder.getWhereBuilder();
			try {
				Part part = new Part(source, builder.getRoot().getJavaType());
				Predicate predicate = new PartTreeQueryBuilderSupport<T>(builder.getRoot(), builder.getCriteriaQuery(), builder.getCriteriaBuilder())
						.build(part, value);
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
		public WhereBuilder<T> eq(String property){
			return part(property, builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> notEq(String property){
			return part(property+"Not", builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> like(String property){
			return part(property+"ContainingIgnoreCase", builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> notLike(String property){
			return part(property+"NotContainingIgnoreCase", builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> between(String property) {
			return part(property+"IsBetween", builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> gt(String property) {
			return part(property+"IsGreaterThan", builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> gte(String property) {
			return part(property+"IsGreaterThanEqual", builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> lt(String property) {
			return part(property+"IsLessThan", builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> lte(String property) {
			return part(property+"IsLessThanEqual", builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> in(String property) {
			return part(property+"IsIn", builder.getQueryParameters().get(property));
		}
		public WhereBuilder<T> notIn(String property) {
			return part(property+"IsNotIn", builder.getQueryParameters().get(property));
		}
	}

	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public static class OrderBuilder<T>{
		
		private PartTreeQueryBuilder<T> builder;
		
		public OrderBuilder(PartTreeQueryBuilder<T> builder){
			this.builder = builder;
		}
		
		public OrderBuilder<T> asc(String property) {
			builder.getCriteriaQuery().orderBy(builder.getCriteriaBuilder().asc(PartTreeQueryBuilderSupport.getTypedPath(builder.getRoot(), property)));
			return this;
		}
		public OrderBuilder<T> desc(String property) {
			builder.getCriteriaQuery().orderBy(builder.getCriteriaBuilder().desc(PartTreeQueryBuilderSupport.getTypedPath(builder.getRoot(), property)));
			return this;
		}
		
		public Predicate build() {
			return builder.build();
		}
		
	}
	
}