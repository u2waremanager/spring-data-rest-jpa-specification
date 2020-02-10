package org.springframework.data.jpa.repository.query;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.util.MultiValueMap;

public class BeanWrapperFactory {

	private BeanWrapperFactory() {}
	
	public static <T> BeanWrapper getInstance(T params){
		return new BeanWrapperImpl(params);
	}
	public static BeanWrapper getInstance(Object... params){
		return new BeanWrapperObjectArray(params);
	}
	public static BeanWrapper getInstance(MultiValueMap<String,Object> params){
		return new BeanWrapperMultiValue(params);
	}

	public static class BeanWrapperMultiValue extends BeanWrapperImpl {
		
		private Map<String, ?> source;
		
		public BeanWrapperMultiValue(Map<String, ?> source) {
			this.source = source;
		}
		
		@Override
		public Object getPropertyValue(String propertyName) throws BeansException {
			return source.get(propertyName);
		}
	}
	
	public static class BeanWrapperObjectArray extends BeanWrapperImpl {
		
		private Object[] source;
		private AtomicInteger index;
		
		public BeanWrapperObjectArray(Object... source ) {
			this.source = source;
			this.index = new AtomicInteger(0);
		}
		
		@Override
		public Object getPropertyValue(String propertyName) throws BeansException {
			return source[index.getAndAdd(1)];
		}
	}
	
	
	
}
