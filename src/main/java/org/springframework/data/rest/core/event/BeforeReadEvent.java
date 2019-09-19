package org.springframework.data.rest.core.event;

public class BeforeReadEvent extends RepositoryEvent {

	private static final long serialVersionUID = 1932567984753687446L;

	private Class<?> type;
	
	public BeforeReadEvent(Object source, Class<?> type) {
		super(source);
		this.type = type;
	}
	
	public Class<?> getSourceType() {
		return type;
	}
}