package org.springframework.data.rest.core.event.hibernate;

import org.springframework.data.rest.core.event.RepositoryEvent;

public class HibernatePostLoadEvent extends RepositoryEvent {

	private static final long serialVersionUID = 1932567984753687446L;

	public HibernatePostLoadEvent(Object source) {
		super(source);
	}
}
