package org.springframework.data.rest.core.event;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
//import org.hibernate.EntityMode;
//import org.hibernate.Transaction;
import org.hibernate.boot.internal.SessionFactoryOptionsBuilder;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class RepositoryRestEventConfigure extends EmptyInterceptor implements ApplicationEventPublisherAware, PostLoadEventListener, PreLoadEventListener {

	private static final long serialVersionUID = 2787103521260283735L;

	protected Log logger = LogFactory.getLog(getClass());

	private @PersistenceUnit EntityManagerFactory emf;
	private @Autowired(required = false) PrepareStatementContext hibernatePrepareStatement;

	private ApplicationEventPublisher publisher;
	private boolean enableHandleLoad = true;

	public boolean isEnableHandleLoad() {
		return enableHandleLoad;
	}

	public void setEnableHandleLoad(boolean enableHandleLoad) {
		this.enableHandleLoad = enableHandleLoad;
	}
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}
	

	@PostConstruct
	protected void init() {
		SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
		SessionFactoryOptionsBuilder options = (SessionFactoryOptionsBuilder) sessionFactory.getSessionFactoryOptions();
		options.applyInterceptor(this);
		
		EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
		registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(this);
		registry.getEventListenerGroup(EventType.PRE_LOAD).appendListener(this);
	}

	@Override
	public void onPostLoad(PostLoadEvent event) {
		if (!isEnableHandleLoad()) return;
		publisher.publishEvent(new HibernatePostLoadEvent(event.getEntity()));
	}
	@Override
	public void onPreLoad(PreLoadEvent event) {
		if (!isEnableHandleLoad()) return;
		publisher.publishEvent(new HibernatePreLoadEvent(event.getEntity()));
	}


	@Override
	public String onPrepareStatement(String sql) {
		
		if(hibernatePrepareStatement == null) return sql;
		
		String expressionString = sql;
		Object rootObject = hibernatePrepareStatement;
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression(expressionString, ParserContext.TEMPLATE_EXPRESSION);
		EvaluationContext ctx = new StandardEvaluationContext(rootObject);
		String result = exp.getValue(ctx, String.class);		
		return result;
	}
	
	

	//////////////////////////////////////
	//
	//////////////////////////////////////
//	@Override
//	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
//		logger.info("onLoad");
//		return false;
//	}
//
//	@Override
//	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
//		logger.info("onFlushDirty");
//		return false;
//	}
//
//	@Override
//	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
//		logger.info("onSave");
//		return false;
//	}
//
//	@Override
//	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
//		logger.info("onDelete");
//	}
//
//	@Override
//	public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
//		logger.info("onCollectionRecreate");
//	}
//
//	@Override
//	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
//		logger.info("onCollectionRemove");
//	}
//
//	@Override
//	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
//		logger.info("onCollectionUpdate");
//	}
//
//	@Override
//	public void preFlush(Iterator entities) throws CallbackException {
//		logger.info("preFlush");
//	}
//
//	@Override
//	public void postFlush(Iterator entities) throws CallbackException {
//		logger.info("postFlush");
//	}
//
//	@Override
//	public Boolean isTransient(Object entity) {
//		logger.info("isTransient");
//		return null;
//	}
//
//	@Override
//	public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
//		logger.info("findDirty");
//		return null;
//	}
//
//	@Override
//	public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException {
//		logger.info("instantiate: "+entityName);
//		return null;
//	}
//
//	@Override
//	public String getEntityName(Object object) throws CallbackException {
//		logger.info("getEntityName: "+object);
//		return null;
//	}
//
//	@Override
//	public Object getEntity(String entityName, Serializable id) throws CallbackException {
//		logger.info("getEntity: "+entityName);
//		return null;
//	}
//
//	@Override
//	public void afterTransactionBegin(Transaction tx) {
//		logger.info("afterTransactionBegin");
//		
//	}
//
//	@Override
//	public void beforeTransactionCompletion(Transaction tx) {
//		logger.info("beforeTransactionCompletion");
//		
//	}
//
//	@Override
//	public void afterTransactionCompletion(Transaction tx) {
//		logger.info("afterTransactionCompletion");
//		
//	}


}