package org.springframework.data.jpa.repository.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.StringPath;

public class JPAQueryBuilderFactory {
	
	private static <T> EntityPath<T> entity(Class<T> entityType){
		return new PathBuilderFactory().create(entityType);
	}

	private static <T> PathBuilder<T> path(Class<T> entityType){
		return path(entity(entityType));
	}
	
	private static <T> PathBuilder<T> path(EntityPath<T> entityPath){
		return new PathBuilder<>(new com.querydsl.core.types.dsl.PathBuilder<>(entityPath.getType(), entityPath.getMetadata()));
	}
	
	public static <T> FromBuilder<T> from(Class<T> entityType){
		return new FromBuilder<>(entityType);
	}
	
	public static <T> WhereBuilder<T> where(Class<T> entityType){
		return where(path(entityType));
	}
	public static <T> WhereBuilder<T> where(PathBuilder<T> pathBuilder){
		return new WhereBuilder<>(pathBuilder);
	}

	public static <T> OrderBuilder<T> orderBy(Class<T> entityType){
		return orderBy(path(entityType));
	}
	public static <T> OrderBuilder<T> orderBy(PathBuilder<T> pathBuilder){
		return new OrderBuilder<>(pathBuilder);
	}

	@SuppressWarnings("unchecked")
	private static class PathBuilder<Z>{
		
		private com.querydsl.core.types.dsl.PathBuilder<Z> pathBuilder;
		private Class<Z> entityType;
		
		private PathBuilder(com.querydsl.core.types.dsl.PathBuilder<Z> pathBuilder) {
			this.pathBuilder = pathBuilder;
			this.entityType = (Class<Z>) pathBuilder.getRoot().getType();
		}
		
		private com.querydsl.core.types.dsl.PathBuilder<Object> get(String property) {
			Field field= ReflectionUtils.findField(entityType, property);
			if(field == null) {
				throw new RuntimeException(property + " is not field");
			}
	    	return pathBuilder.get(property);
	    }
		
		private <A extends Comparable<?>> ComparablePath<A> getComparable(String property) {
			Field field= ReflectionUtils.findField(entityType, property);
			if(field == null) {
				throw new RuntimeException(property + " is not field");
			}
			if(! ClassUtils.isAssignable(Comparable.class, field.getType())) {
				throw new RuntimeException(property + " is not "+Comparable.class);
			}
			return pathBuilder.getComparable(property, (Class<A>)field.getType());
		}
		
		private StringPath getString(String property) {
			Field field= ReflectionUtils.findField(entityType, property);
			if(field == null) {
				throw new RuntimeException(property + " is not field");
			}
			return pathBuilder.getString(property);
		}

	//  public <A, E> ArrayPath<A, E> getArray(String property) {
//			Field field= ReflectionUtils.findField(clazz, property);
//			if(field == null) {
//				throw new RuntimeException(property + " is not field");
//			}
//			Class<A> type = (Class<A>)field.getType();
//			return builder.getArray(property, type);
	//  }
		
//		builder.getArray(property, type)
//		builder.getBoolean(propertyName)
//		builder.getCollection(property, type)
//		builder.getComparable(property, type)
//		builder.getEnum(property, type)
//		builder.getList(property, type)
//		builder.getMap(property, key, value)
//		builder.getNumber(property, type)
//		builder.getSimple(property, type)
//		builder.getString(property)
//		builder.get(property);
		
	}
	
	
	public static class FromBuilder<Z>{
	
		private Class<Z> entityType;
		
		private FromBuilder(Class<Z> entityType) {
			this.entityType = entityType;
		}
		
		public EntityPath<Z> build() {
			return new PathBuilderFactory().create(entityType);
		}
	}
	
	
	public static class WhereBuilder<X>{
		
		private PathBuilder<X> pathBuilder;
		private BooleanBuilder root = new BooleanBuilder();
		private BooleanBuilder current ;
		private String state ;
		
		private WhereBuilder(PathBuilder<X> pathBuilder) {
			this.pathBuilder = pathBuilder;
		}
		
		public PredicateBuilder<X> and() {
			this.state = "and";
			return new PredicateBuilder<>(pathBuilder, this);
		}
		public PredicateBuilder<X> or() {
			this.state = "or";
			return new PredicateBuilder<>(pathBuilder, this);
		}

		public PredicateBuilder<X> orStart() {
			if(this.current != null) throw new RuntimeException("orStart duplicated.");
			this.state = "or";
			this.current = new BooleanBuilder();
			return new PredicateBuilder<>(pathBuilder, this);
		}
		public WhereBuilder<X> orEnd() {
			if(this.current == null) throw new RuntimeException("orStart is not opend.");
			this.root.or(this.current);
			this.current = null;
			return this;
		}
		
		public PredicateBuilder<X> andStart() {
			if(this.current != null) throw new RuntimeException("andStart duplicated.");
			this.state = "and";
			this.current = new BooleanBuilder();
			return new PredicateBuilder<>(pathBuilder, this);
		}
		public WhereBuilder<X> andEnd() {
			if(this.current == null) throw new RuntimeException("andStart is not opend.");
			this.root.and(this.current);
			this.current = null;
			return this;
		}
		
		private WhereBuilder<X> chain(BooleanExpression expression) {
			if(expression == null) return this;
			
			if(this.current == null) {
				if("and".equals(state)) {
					root.and(expression);
					
				}else if("or".equals(state)) {
					root.or(expression);
				}
			}else {
				if("and".equals(state)) {
					current.and(expression);
					
				}else if("or".equals(state)) {
					current.or(expression);
				}
			}
			return this;
		}
		
		public BooleanBuilder build() {
			return root;
		}
	}
	
	public static class PredicateBuilder<W>{
		private PathBuilder<W> pathBuilder;
		private WhereBuilder<W> whereBuilder;
		
		private PredicateBuilder(PathBuilder<W> pathBuilder, WhereBuilder<W> whereBuilder) {
			this.pathBuilder = pathBuilder;
			this.whereBuilder = whereBuilder;
		}
		
		public <O> WhereBuilder<W> eq(String name, O value) {
			if(value == null) return whereBuilder.chain(null);
			return whereBuilder.chain(pathBuilder.get(name).eq(value));
		}

		public WhereBuilder<W> like(String name, String value) {
			if(value == null) return whereBuilder.chain(null);
			return whereBuilder.chain(pathBuilder.getString(name).like(value));
		}
		
		public <O extends Comparable<?>> WhereBuilder<W> goe(String name, Comparable<O> value) {
			if(value == null) return whereBuilder.chain(null);
			return whereBuilder.chain(pathBuilder.getComparable(name).goe(value));
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static class OrderBuilder<Y>{
		
		private PathBuilder<Y> pathBuilder;
		private List<OrderSpecifier<?>> orderSpecifiers;
		
		private OrderBuilder(PathBuilder<Y> pathBuilder) {
			this.pathBuilder = pathBuilder;
			this.orderSpecifiers = new ArrayList<>();
		}
		
		public OrderBuilder<Y> desc(String property){
			orderSpecifiers.add(new OrderSpecifier(com.querydsl.core.types.Order.DESC, pathBuilder.get(property)));
			return this;
		}
		public OrderBuilder<Y> asc(String property){
			orderSpecifiers.add(new OrderSpecifier(com.querydsl.core.types.Order.ASC, pathBuilder.get(property)));
			return this;
		}
		
		public OrderSpecifier<?>[] build() {
			OrderSpecifier[] result = new OrderSpecifier[orderSpecifiers.size()];
			orderSpecifiers.toArray(result);
			return result;
		}
	}
}
