package org.springframework.data.rest.core.event;

public class AfterReadEvent extends RepositoryEvent {

	private static final long serialVersionUID = 1932567984753687446L;

	public AfterReadEvent(Object source) {
		super(source);
	}
}
