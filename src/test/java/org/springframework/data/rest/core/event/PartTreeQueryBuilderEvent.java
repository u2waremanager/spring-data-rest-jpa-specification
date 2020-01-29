package org.springframework.data.rest.core.event;

@SuppressWarnings("serial")
public class PartTreeQueryBuilderEvent extends RepositoryEvent {
	
	private Class<?> entityType;
	
	public PartTreeQueryBuilderEvent(Object source, Class<?> entityType) {
		super(source);
		this.entityType = entityType;
	}

	public Class<?> getEntityType() {
		return entityType;
	}


}
