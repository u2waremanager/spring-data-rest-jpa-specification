package org.springframework.data.jpa.repository.query;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

public class BeanWrapperObjectArray extends BeanWrapperImpl {
	
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
