package org.springframework.data.rest.core.event.hibernate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

@SuppressWarnings("serial")
public class HibernateEventBroker implements ApplicationEventPublisherAware, PostLoadEventListener{

	@PersistenceUnit
	private EntityManagerFactory emf;

	private ApplicationEventPublisher publisher;
	private boolean enableHandleLoad = true;

	public boolean isEnableHandleLoad() {
		return enableHandleLoad;
	}

	public void setEnableHandleLoad(boolean enableHandleLoad) {
		this.enableHandleLoad = enableHandleLoad;
	}

	@PostConstruct
	protected void init() {
		SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
		EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
		registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(this);
	}

	@Override
	public void onPostLoad(PostLoadEvent event) {
		if (!isEnableHandleLoad()) return;
		publisher.publishEvent(new HibernatePostLoadEvent(event.getEntity()));
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}
	
}
