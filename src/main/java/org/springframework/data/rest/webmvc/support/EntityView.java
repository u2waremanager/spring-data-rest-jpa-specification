package org.springframework.data.rest.webmvc.support;

import java.io.Serializable;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface EntityView<T, ID extends Serializable> {

	@JsonIgnore @Transient 
	public ID getId();
	
	@JsonIgnore
	public void deserialize(T source) ;

	@JsonIgnore
	public T serialize();
	
}
