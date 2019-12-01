package org.springframework.data.jpa.repository.query;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.StringPath;

@SuppressWarnings("unchecked")
public class JPAQueryPath<T> {

	protected Log logger = LogFactory.getLog(getClass());
	
	private Class<T> clazz ;
	private EntityPath<T> entity;
	private PathBuilder<T> builder;

	public JPAQueryPath(Class<T> clazz) {
		this.clazz = clazz;
		this.entity = new PathBuilderFactory().create(clazz);
		this.builder = new PathBuilder<>(entity.getType(), entity.getMetadata());
	}


	public EntityPath<?> get() {
		return entity;
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
	
//    public <A, E> ArrayPath<A, E> getArray(String property) {
//		Field field= ReflectionUtils.findField(clazz, property);
//		if(field == null) {
//			throw new RuntimeException(property + " is not field");
//		}
//		Class<A> type = (Class<A>)field.getType();
//		return builder.getArray(property, type);
//    }
	
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
	
	
}
