package org.springframework.data.jpa.repository.support;

import java.lang.reflect.Field;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.StringPath;

public class JPAQueryType<T> {

	private Class<T> entityType;
	private PathBuilder<T> pathBuilder;
	
	public JPAQueryType(Class<T> entityType) {
		this.entityType = entityType;
		this.pathBuilder =  new PathBuilderFactory().create(entityType);
	}

	public Class<T> getType(){
		return entityType;
	}
	public EntityPath<T> getRoot(){
		return pathBuilder;
	}

	
	public PathBuilder<Object> get(String property) {
		Field field= ReflectionUtils.findField(entityType, property);
		if(field == null) {
			throw new RuntimeException(property + " is not field");
		}
    	return pathBuilder.get(property);
    }
	
	@SuppressWarnings("unchecked")
	public <A extends Comparable<?>> ComparablePath<A> getComparable(String property) {
		Field field= ReflectionUtils.findField(entityType, property);
		if(field == null) {
			throw new RuntimeException(property + " is not field");
		}
		if(! ClassUtils.isAssignable(Comparable.class, field.getType())) {
			throw new RuntimeException(property + " is not "+Comparable.class);
		}
		return pathBuilder.getComparable(property, (Class<A>)field.getType());
	}
	
	public StringPath getString(String property) {
		Field field= ReflectionUtils.findField(entityType, property);
		if(field == null) {
			throw new RuntimeException(property + " is not field");
		}
		if(! ClassUtils.isAssignable(String.class, field.getType())) {
			throw new RuntimeException(property + " is not "+String.class);
		}
		return pathBuilder.getString(property);
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
	
//	
//	public BooleanExpression eq(String property, Object right) {
//		return get(property).eq(right);
//	}
//
//	public BooleanExpression goe(String property, Comparable<?> right) {
//		return getComparable(property).goe(right);
//	}
}
