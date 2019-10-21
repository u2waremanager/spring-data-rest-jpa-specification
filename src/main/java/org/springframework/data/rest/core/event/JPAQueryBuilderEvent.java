package org.springframework.data.rest.core.event;

@SuppressWarnings("serial")
public class JPAQueryBuilderEvent extends RepositoryEvent {

	
	private Class<?> entityType;
	
	public JPAQueryBuilderEvent(Object source, Class<?> entityType) {
		super(source);
		this.entityType = entityType;
	}

	public Class<?> getEntityType() {
		return entityType;
	}

}
