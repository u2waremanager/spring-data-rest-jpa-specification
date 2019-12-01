package org.springframework.data.rest.core.event;

@SuppressWarnings("serial")
public class AfterLoadEvent extends RepositoryEvent {

	public AfterLoadEvent(Object source) {
		super(source);
	}
}

