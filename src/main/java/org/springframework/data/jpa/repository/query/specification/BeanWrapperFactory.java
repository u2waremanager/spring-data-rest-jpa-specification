package org.springframework.data.jpa.repository.query.specification;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.util.MultiValueMap;

class BeanWrapperFactory {

	private BeanWrapperFactory() {}
	
	static <T> BeanWrapper getInstance(T params){
		return new BeanWrapperImpl(params);
	}
	static BeanWrapper getInstance(Object... params){
		return new BeanWrapperObjectArray(params);
	}
	static BeanWrapper getInstance(MultiValueMap<String,Object> params){
		return new BeanWrapperMultiValue(params);
	}

	static class BeanWrapperMultiValue extends BeanWrapperImpl {
		
		private Map<String, ?> source;
		
		protected BeanWrapperMultiValue(Map<String, ?> source) {
			this.source = source;
		}
		
		@Override
		public Object getPropertyValue(String propertyName) throws BeansException {
			return source.get(propertyName);
		}
	}
	
	static class BeanWrapperObjectArray extends BeanWrapperImpl {
		
		private Object[] source;
		private AtomicInteger index;
		
		BeanWrapperObjectArray(Object... source ) {
			this.source = source;
			this.index = new AtomicInteger(0);
		}
		
		@Override
		public Object getPropertyValue(String propertyName) throws BeansException {
			return source[index.getAndAdd(1)];
		}
	}
	
	
	
}
