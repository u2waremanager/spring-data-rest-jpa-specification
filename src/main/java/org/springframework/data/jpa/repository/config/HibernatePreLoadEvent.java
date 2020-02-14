package org.springframework.data.jpa.repository.config;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class HibernatePreLoadEvent extends ApplicationEvent{

	public HibernatePreLoadEvent(Object source) {
		super(source);
	}

}
