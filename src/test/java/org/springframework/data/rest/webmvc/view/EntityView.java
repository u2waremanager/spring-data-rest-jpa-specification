package org.springframework.data.rest.webmvc.view;

import java.io.Serializable;

import org.springframework.hateoas.Identifiable;

public interface EntityView<T, ID extends Serializable> extends Identifiable<ID> {
	
	public void deserialize(T source) ;
	
	public T serialize() ;
	
}
