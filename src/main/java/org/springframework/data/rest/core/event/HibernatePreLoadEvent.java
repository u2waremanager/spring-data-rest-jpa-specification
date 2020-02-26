package org.springframework.data.rest.core.event;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class HibernatePreLoadEvent extends ApplicationEvent{

	public HibernatePreLoadEvent(Object source) {
		super(source);
	}

}
