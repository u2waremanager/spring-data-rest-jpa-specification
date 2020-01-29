package org.springframework.data.rest.core.event;

import static org.springframework.core.GenericTypeResolver.resolveTypeArgument;

import org.springframework.context.ApplicationListener;

//AbstractRepositoryEventListener
public class AbstractReadEntityEventListener<T> implements ApplicationListener<RepositoryEvent> {

	private final Class<?> INTERESTED_TYPE = resolveTypeArgument(getClass(), AbstractRepositoryEventListener.class);
	
	@Override
	@SuppressWarnings("unchecked")
	public final void onApplicationEvent(RepositoryEvent event) {

		Class<?> srcType = event.getSource().getClass();

		if (null != INTERESTED_TYPE && !INTERESTED_TYPE.isAssignableFrom(srcType)) {
			return;
		}

		if (event instanceof AfterReadEvent) {
			onAfterRead((T) event.getSource());
		} else if (event instanceof BeforeReadEvent) {
			onBeforeRead((T) event.getSource(), ((BeforeReadEvent) event).getQuery());
		}		
	}
	
	protected void onAfterRead(T entity) {}

	protected void onBeforeRead(T entity, Object query) {}

}
