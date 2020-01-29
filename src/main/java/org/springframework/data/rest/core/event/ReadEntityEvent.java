package org.springframework.data.rest.core.event;

@SuppressWarnings("serial")
public abstract class ReadEntityEvent extends RepositoryEvent {

	protected ReadEntityEvent(Object source) {
		super(source);
	}
}
