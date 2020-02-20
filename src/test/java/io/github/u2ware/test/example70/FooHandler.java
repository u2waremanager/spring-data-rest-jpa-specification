package io.github.u2ware.test.example70;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.domain.Specification;
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
	public void handleBeforeCreate(ManyToOnePhysicalColumn5 foo) {
		logger.info("handleBeforeCreate: "+foo);
	}
	@HandleBeforeRead
	public void handleBeforeRead(ManyToOnePhysicalColumn5 foo, Object obj) {
		logger.info("handleBeforeRead: "+foo);
		
		JPAQuery<ManyToOnePhysicalColumn5> query = (JPAQuery)obj;
		
//		Specification s;
//		s.and(other);
		
		
		logger.info("handleBeforeRead: "+query.getClass());
	}
	@HandleBeforeSave
	public void handleBeforeSave(ManyToOnePhysicalColumn5 foo) {
		logger.info("handleBeforeSave: "+foo);
	}
	@HandleBeforeDelete
	public void handleBeforeDelete(ManyToOnePhysicalColumn5 foo) {
		logger.info("handleBeforeDelete: "+foo);
	}
	

	@HandleAfterCreate
	public void handleAfterCreate(ManyToOnePhysicalColumn5 foo) {
		logger.info("handleAfterCreate: "+foo);
	}
	@HandleAfterRead
	public void handleAfterRead(ManyToOnePhysicalColumn5 foo) {
		logger.info("handleAfterRead: "+foo);
	}
	@HandleAfterSave
	public void handleAfterSave(ManyToOnePhysicalColumn5 foo) {
		logger.info("handleAfterSave: "+foo);
	}
	@HandleAfterDelete
	public void handleAfterDelete(ManyToOnePhysicalColumn5 foo) {
		logger.info("handleAfterDelete: "+foo);
	}
	
	
}
