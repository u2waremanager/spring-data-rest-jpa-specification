package org.springframework.data.jpa.repository.query;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryParameters<T> {

	private ObjectMapper objectMapper = new ObjectMapper();
	private BeanWrapper beanWrapper;
	private T entity;
	
	public QueryParameters(T source) {
		this.beanWrapper = new BeanWrapperImpl(source);
		this.entity = source;
	}
	
	public QueryParameters(Object... source) {
		this.beanWrapper = new BeanWrapperObjectArray(source);
	}
	
	public QueryParameters(Map<String, ?> source) {
		this.beanWrapper = new BeanWrapperMultiValue(source);
	}
	
	public QueryParameters(MultiValueMap<String, Object> source, Class<T> clazz) {
		this.beanWrapper = new BeanWrapperMultiValue(source);

		Map<String, Object> temp = new HashMap<String, Object>();
		BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
		for (String name : source.keySet()) {
			try {
				PropertyDescriptor pd = beanWrapper.getPropertyDescriptor(name);
				if (pd != null) {

					Class<?> type = pd.getPropertyType();
					List<?> list = source.get(name);
					Object value = null;
					if (list != null) {
						if (ClassUtils.isAssignableValue(type, list) || type.isArray()) {
							value = list;
						} else {
							value = list.size() > 0 ? list.get(0) : null;
						}
					}
					if (value != null) {
						temp.put(name, value);
					}
				}
			} catch (Exception e) {

			}
		}
		this.entity = objectMapper.convertValue(temp, clazz);
	}
	
	public T get() {
		return entity;
	}
	
	public Object get(String propertyName) {
		return beanWrapper.getPropertyValue(propertyName);
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
