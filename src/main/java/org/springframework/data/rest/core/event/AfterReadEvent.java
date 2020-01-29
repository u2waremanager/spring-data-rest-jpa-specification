package org.springframework.data.rest.core.event;

public class AfterReadEvent extends ReadEntityEvent {

	private static final long serialVersionUID = -6090615345948638970L;

	public AfterReadEvent(Object source) {
		super(source);
	}
}
