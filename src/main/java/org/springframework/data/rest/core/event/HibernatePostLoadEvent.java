package org.springframework.data.rest.core.event;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class HibernatePostLoadEvent extends ApplicationEvent{

	public HibernatePostLoadEvent(Object source) {
		super(source);
	}

}
