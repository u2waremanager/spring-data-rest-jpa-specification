package org.springframework.data.rest.core.event;

public class BeforeReadEvent extends ReadEntityEvent {

	private static final long serialVersionUID = -6090615345948638970L;

	private Object query;
	
	public BeforeReadEvent(Object source, Object query) {
		super(source);
		this.query = query;
	}
	
	public Object getQuery() {
		return query;
	}
}
