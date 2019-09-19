package org.springframework.data.rest.core.event;

public class BeforeReadEvent extends RepositoryEvent {

	private static final long serialVersionUID = 1932567984753687446L;

	private Object object;
	
	public BeforeReadEvent(Object source, Object object) {
		super(source);
		this.object = object;
	}
	
	public Object getObject() {
		return object;
	}

}