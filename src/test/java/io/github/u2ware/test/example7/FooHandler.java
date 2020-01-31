package io.github.u2ware.test.example7;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterRead;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.querydsl.jpa.impl.JPAQuery;

@Component
@RepositoryEventHandler
@SuppressWarnings({ "rawtypes", "unchecked" })
public class FooHandler {

	protected Log logger = LogFactory.getLog(getClass());

	@HandleBeforeCreate
	public void handleBeforeCreate(Foo foo) {
		logger.info("handleBeforeCreate: "+foo);
	}
	@HandleBeforeRead
	public void handleBeforeRead(Foo foo, Object obj) {
		logger.info("handleBeforeRead: "+foo);
		
		JPAQuery<Foo> query = (JPAQuery)obj;
		
		
		
		
		
		logger.info("handleBeforeRead: "+query.getClass());
	}
	@HandleBeforeSave
	public void handleBeforeSave(Foo foo) {
		logger.info("handleBeforeSave: "+foo);
	}
	@HandleBeforeDelete
	public void handleBeforeDelete(Foo foo) {
		logger.info("handleBeforeDelete: "+foo);
	}
	

	@HandleAfterCreate
	public void handleAfterCreate(Foo foo) {
		logger.info("handleAfterCreate: "+foo);
	}
	@HandleAfterRead
	public void handleAfterRead(Foo foo) {
		logger.info("handleAfterRead: "+foo);
	}
	@HandleAfterSave
	public void handleAfterSave(Foo foo) {
		logger.info("handleAfterSave: "+foo);
	}
	@HandleAfterDelete
	public void handleAfterDelete(Foo foo) {
		logger.info("handleAfterDelete: "+foo);
	}
	
	
}
