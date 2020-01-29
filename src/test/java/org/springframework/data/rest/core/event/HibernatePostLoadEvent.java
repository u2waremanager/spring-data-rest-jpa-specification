package org.springframework.data.rest.core.event;

import org.springframework.data.rest.core.event.RepositoryEvent;

@SuppressWarnings("serial")
public class HibernatePostLoadEvent extends RepositoryEvent {

	public HibernatePostLoadEvent(Object source) {
		super(source);
	}
}
