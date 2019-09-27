package org.springframework.data.jpa.repository.query;

import java.util.Map;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

public class BeanWrapperMultiValue extends BeanWrapperImpl {
	
	private Map<String, ?> source;
	
	public BeanWrapperMultiValue(Map<String, ?> source) {
		this.source = source;
	}
	
	@Override
	public Object getPropertyValue(String propertyName) throws BeansException {
		return source.get(propertyName);
	}
}