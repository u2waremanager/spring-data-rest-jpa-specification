package io.github.u2ware.test.example4;

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
//import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.rest.core.event.hibernate.HibernatePostLoadEvent;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class HibernateEventBroker extends EmptyInterceptor 
implements ApplicationContextAware, ApplicationEventPublisherAware, 
PostLoadEventListener, PreLoadEventListener {

	private static final long serialVersionUID = 2787103521260283735L;

	protected Log logger = LogFactory.getLog(getClass());

	@PersistenceUnit
	private EntityManagerFactory emf;

	private ApplicationContext context;
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
		SessionFactoryOptionsBuilder options = (SessionFactoryOptionsBuilder) sessionFactory.getSessionFactoryOptions();
		options.applyInterceptor(this);
		
		EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
		registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(this);
		registry.getEventListenerGroup(EventType.PRE_LOAD).appendListener(this);
		//registry.getEventListenerGroup(EventType.PRE_COLLECTION_RECREATE).appendListener(this);
	}

	@Override
	public void onPostLoad(PostLoadEvent event) {
		if (!isEnableHandleLoad()) return;
		publisher.publishEvent(new HibernatePostLoadEvent(event.getEntity()));
	}
	@Override
	public void onPreLoad(PreLoadEvent event) {
		logger.info("onPreLoad 1"+event.getEntityName());
	}


	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
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


	private String PREPARE_STATEMENT_REGEX = "\\{(.*?)\\}";
	
	private @Autowired HibernatePrepareStatementAware event;
	@Override
	public String onPrepareStatement(String sql) {
		
		String expressionString = sql;
		Object rootObject = event;
		
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression(expressionString, ParserContext.TEMPLATE_EXPRESSION);
		EvaluationContext ctx = new StandardEvaluationContext(rootObject);
		String result = exp.getValue(ctx, String.class);		
		
		logger.info(sql+" -> "+result);

//		Pattern pattern = Pattern.compile(PREPARE_STATEMENT_REGEX);
//		Matcher matcher = pattern.matcher(sql);
//		while (matcher.find()){
//			String target = matcher.group();
//			String beanName = matcher.group(1);
//			try {
//				HibernatePrepareStatementAware aware = context.getBean(beanName, HibernatePrepareStatementAware.class);
//				String replacement = aware.getStatement();
//				result = result.replace(target, replacement);
//				logger.info(target+" -> "+replacement);
//			}catch(Exception e) {
//				logger.info(target, e);
//			}
//		}
//		return result;
		
		return result;
	}


}