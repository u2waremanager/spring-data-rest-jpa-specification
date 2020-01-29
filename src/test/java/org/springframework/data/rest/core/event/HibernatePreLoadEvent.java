package org.springframework.data.rest.core.event;

import org.springframework.data.rest.core.event.RepositoryEvent;

@SuppressWarnings("serial")
public class HibernatePreLoadEvent extends RepositoryEvent {

	public HibernatePreLoadEvent(Object source) {
		super(source);
	}
}
