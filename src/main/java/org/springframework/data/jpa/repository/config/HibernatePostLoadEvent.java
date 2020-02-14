package org.springframework.data.jpa.repository.config;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class HibernatePostLoadEvent extends ApplicationEvent{

	public HibernatePostLoadEvent(Object source) {
		super(source);
	}

}
