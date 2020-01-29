package org.springframework.data.rest.core.event;

import org.springframework.context.ApplicationListener;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.repository.query.JPAQueryLagycyBuilder;
import org.springframework.data.jpa.repository.query.PartTreeQueryBuilder;

//AbstractRepositoryEventListener
public abstract class RepositoryRestEventHandler<T> implements ApplicationListener<RepositoryEvent> {

//	protected Log logger = LogFactory.getLog(getClass());

	private final Class<?> INTERESTED_TYPE = GenericTypeResolver.resolveTypeArgument(getClass(), RepositoryRestEventHandler.class);

	@Override
	@SuppressWarnings("unchecked")
	public final void onApplicationEvent(RepositoryEvent event) {

		Class<?> srcType = event.getSource().getClass();
		if (event instanceof JPAQueryBuilderEvent) {
			JPAQueryBuilderEvent e = (JPAQueryBuilderEvent)event;
			srcType = e.getEntityType();
		}else if(event instanceof PartTreeQueryBuilderEvent) {
			PartTreeQueryBuilderEvent e = (PartTreeQueryBuilderEvent)event;
			srcType = e.getEntityType();
		}
		
		if (!INTERESTED_TYPE.isAssignableFrom(srcType)) {
			return;
		}

		if (event instanceof BeforeCreateEvent) {
			handleBeforeCreate((T) event.getSource());

		} else if (event instanceof AfterCreateEvent) {
			handleAfterCreate((T) event.getSource());

		} else if (event instanceof BeforeSaveEvent) {
			handleBeforeSave((T) event.getSource());

		} else if (event instanceof AfterSaveEvent) {
			handleAfterSave((T) event.getSource());

		} else if (event instanceof BeforeDeleteEvent) {
			handleBeforeDelete((T) event.getSource());

		} else if (event instanceof AfterDeleteEvent) {
			handleAfterDelete((T) event.getSource());
		
			
		}else if (event instanceof HibernatePreLoadEvent) {
			handleHibernatePreLoad((T) event.getSource());

		}else if (event instanceof HibernatePostLoadEvent) {
			handleHibernatePostLoad((T) event.getSource());
			
		}else if (event instanceof JPAQueryBuilderEvent) {
			handleQueryBuilder((JPAQueryLagycyBuilder<T>) event.getSource());
		
		}else if (event instanceof PartTreeQueryBuilderEvent) {
			handleQueryBuilder((PartTreeQueryBuilder<T>) event.getSource());
		}
	}
	

	protected void handleBeforeCreate(T entity) {}
	
	protected void handleAfterCreate(T entity) {}

	protected void handleBeforeSave(T entity) {}
	
	protected void handleAfterSave(T entity) {}
	
	protected void handleBeforeDelete(T entity) {}
	
	protected void handleAfterDelete(T entity) {}
	
	protected void handleHibernatePreLoad(T entity){}

	protected void handleHibernatePostLoad(T entity){}
	
	protected void handleQueryBuilder(JPAQueryLagycyBuilder<T> builder) {}
	
	protected void handleQueryBuilder(PartTreeQueryBuilder<T> builder) {}
	
	// @EventListener
	// @RepositoryEventHandler
	// @HandleBeforeCreate
	// @HandleAfterCreate
	// @HandleBeforeSave
	// @HandleAfterSave
	// @HandleBeforeDelete
	// @HandleAfterDelete
}