package org.springframework.data.jpa.repository.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.StringPath;

@SuppressWarnings("unchecked")
public class JPAQueryBuilder<T> {

	protected Log logger = LogFactory.getLog(getClass());
	
	private Class<T> clazz ;
	private EntityPath<T> entity;
	private PathBuilder<T> builder;
	
	public JPAQueryBuilder(Class<T> clazz) {
		this.clazz = clazz;
		this.entity = new PathBuilderFactory().create(clazz);
		this.builder = new PathBuilder<>(entity.getType(), entity.getMetadata());
	}
	
	public PathBuilder<Object> get(String property) {
		Field field= ReflectionUtils.findField(clazz, property);
		if(field == null) {
			throw new RuntimeException(property + " is not field");
		}
    	return builder.get(property);
    }
    
	public <A extends Comparable<?>> ComparablePath<A>  getComparable(String property) {
		Field field= ReflectionUtils.findField(clazz, property);
		if(field == null) {
			throw new RuntimeException(property + " is not field");
		}
		if(! ClassUtils.isAssignable(Comparable.class, field.getType())) {
			throw new RuntimeException(property + " is not "+Comparable.class);
		}
		return builder.getComparable(property, (Class<A>)field.getType());
	}
	
	public StringPath getString(String property) {
		Field field= ReflectionUtils.findField(clazz, property);
		if(field == null) {
			throw new RuntimeException(property + " is not field");
		}
		return builder.getString(property);
	}

	
	
	
	
	
//  public <A, E> ArrayPath<A, E> getArray(String property) {
//		Field field= ReflectionUtils.findField(clazz, property);
//		if(field == null) {
//			throw new RuntimeException(property + " is not field");
//		}
//		Class<A> type = (Class<A>)field.getType();
//		return builder.getArray(property, type);
//  }
	
//	builder.getArray(property, type)
//	builder.getBoolean(propertyName)
//	builder.getCollection(property, type)
//	builder.getComparable(property, type)
//	builder.getEnum(property, type)
//	builder.getList(property, type)
//	builder.getMap(property, key, value)
//	builder.getNumber(property, type)
//	builder.getSimple(property, type)
//	builder.getString(property)
//	builder.get(property);

	public EntityPath<T> from() {
		return entity;
	}
	public WhereBuilder<T> where() {
		return new WhereBuilder<T>(this);
	}
	public OrderBuilder<T> orderBy() {
		return new OrderBuilder<T>(this);
	}
	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public JPAQueryBuilder<T> pageable(Pageable pageable) { 
//		for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
//			query.orderBy(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, path.get(order.getProperty())));
//		}
//
//		if (pageable.isUnpaged()) {
//			return this;
//		}
//		query.offset(pageable.getOffset());
//		query.limit(pageable.getPageSize());
//		return this;
//	}
//	
	
	
	public static class WhereBuilder<T> {
		
		private BooleanBuilder root = new BooleanBuilder();
		private BooleanBuilder builder ;
		private String state ;
		private JPAQueryBuilder<T> jpaQueryPath ;
		
		private WhereBuilder(JPAQueryBuilder<T> jpaQueryPath) {
			this.jpaQueryPath = jpaQueryPath;
			this.root = new BooleanBuilder();
		}
		
		public Predicate build() {
			return root;
		}

		public PredicateBuilder<T> and() {
			this.state = "and";
			return new PredicateBuilder<>(jpaQueryPath, this);
		}
		public PredicateBuilder<T> or() {
			this.state = "or";
			return new PredicateBuilder<>(jpaQueryPath, this);
		}

		public PredicateBuilder<T> orStart() {
			if(this.builder != null) throw new RuntimeException("orStart duplicated.");
			this.state = "or";
			this.builder = new BooleanBuilder();
			return new PredicateBuilder<>(jpaQueryPath, this);
		}
		public WhereBuilder<T> orEnd() {
			if(this.builder == null) throw new RuntimeException("orStart is not opend.");
			this.root.or(this.builder);
			this.builder = null;
			return this;
		}
		
		public PredicateBuilder<T> andStart() {
			if(this.builder != null) throw new RuntimeException("andStart duplicated.");
			this.state = "and";
			this.builder = new BooleanBuilder();
			return new PredicateBuilder<>(jpaQueryPath, this);
		}
		public WhereBuilder<T> andEnd() {
			if(this.builder == null) throw new RuntimeException("andStart is not opend.");
			this.root.and(this.builder);
			this.builder = null;
			return this;
		}
		
		private WhereBuilder<T> chain(BooleanExpression expression) {
			if(expression == null) return this;
			
			if(this.builder == null) {
				if("and".equals(state)) {
					root.and(expression);
					
				}else if("or".equals(state)) {
					root.or(expression);
				}
			}else {
				if("and".equals(state)) {
					builder.and(expression);
					
				}else if("or".equals(state)) {
					builder.or(expression);
				}
			}
			return this;
		}
		
		public static class PredicateBuilder<T> {
			
			private JPAQueryBuilder<T> jpaQueryPath;
			private WhereBuilder<T> whereBuilder;
			
			private PredicateBuilder(JPAQueryBuilder<T> jpaQueryPath, WhereBuilder<T> whereBuilder) {
				this.jpaQueryPath = jpaQueryPath;
				this.whereBuilder = whereBuilder;
			}
			
			public <O> WhereBuilder<T> eq(String name, O value) {
				if(value == null) return whereBuilder.chain(null);
				return whereBuilder.chain(jpaQueryPath.get(name).eq(value));
			}
		}
		
	}
	

	
	
	@SuppressWarnings("rawtypes")
	public static class OrderBuilder<T> {
		private JPAQueryBuilder<T> jpaQueryPath ;
		private List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
		
		private OrderBuilder(JPAQueryBuilder<T> jpaQueryPath) {
			this.jpaQueryPath = jpaQueryPath;
		}
		
		public OrderBuilder<T> order(String property, Order order) {
			orderSpecifiers.add(new OrderSpecifier(order, jpaQueryPath.getComparable(property)));
			return this;
		}
		
		public OrderBuilder<T> order(Sort sort) {
			for (org.springframework.data.domain.Sort.Order o : sort) {
				orderSpecifiers.add(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, jpaQueryPath.getComparable(o.getProperty())));
			}
			return this;
		}

		public OrderSpecifier[] build() {
			OrderSpecifier[] r = new OrderSpecifier[orderSpecifiers.size()];
			orderSpecifiers.toArray(r);
			return r;
		}
	}
	
	
	
	
	
	
	
	
}
