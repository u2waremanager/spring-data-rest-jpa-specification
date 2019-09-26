package org.springframework.data.jpa.repository.query;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

public interface Parameters {

	public Object getPropertyValue(String aa);
	
	
	public static class Entity<T> extends BeanWrapperImpl implements Parameters{
		public Entity(T source) {
			super(source);
		}
	}
	
	public static class MultiValueMap extends BeanWrapperImpl implements Parameters{
		
		private Map<String, ?> source;
		
		public MultiValueMap(Map<String, ?> source) {
			this.source = source;
		}
		
		@Override
		public Object getPropertyValue(String propertyName) throws BeansException {
			return source.get(propertyName);
		}
	}
	
	public static class ObjectArray extends BeanWrapperImpl implements Parameters{
		
		private Object[] source;
		private AtomicInteger index;
		
		public ObjectArray(Object... source ) {
			this.source = source;
			this.index = new AtomicInteger(0);
		}
		
		@Override
		public Object getPropertyValue(String propertyName) throws BeansException {
			return source[index.getAndAdd(1)];
		}
	}
	
	

}
