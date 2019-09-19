package org.springframework.data.rest.core.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.rest.core.annotation.HandleAfterRead;
import org.springframework.data.rest.core.annotation.HandleBeforeRead;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.AnnotatedEventHandlerInvoker.EventHandlerMethod;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

//AnnotatedEventHandlerInvoker
public class RepositoryRestEventHandlerInvoker implements ApplicationListener<RepositoryEvent>, BeanPostProcessor {

	protected Log logger = LogFactory.getLog(getClass());
	
	private MultiValueMap<Class<? extends RepositoryEvent>, EventHandlerMethod> handlerMethods = new LinkedMultiValueMap<Class<? extends RepositoryEvent>, EventHandlerMethod>();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> beanType = ClassUtils.getUserClass(bean);

		RepositoryEventHandler typeAnno = AnnotationUtils.findAnnotation(beanType, RepositoryEventHandler.class);
		if (typeAnno == null) {
			return bean;
		}
		
		
		for (Method method : ReflectionUtils.getUniqueDeclaredMethods(beanType)) {
			inspect(bean, method, HandleAfterRead.class, AfterReadEvent.class);
			inspect(bean, method, HandleBeforeRead.class, BeforeReadEvent.class);
		}

		return bean;
	}

	private <T extends Annotation> void inspect(Object handler, Method method, Class<T> annotationType, Class<? extends RepositoryEvent> eventType) {

		
		T annotation = AnnotationUtils.findAnnotation(method, annotationType);

		if (annotation == null) {
			return;
		}

		if (method.getParameterTypes().length == 0) {
			throw new IllegalStateException("method.getParameterTypes().length == 0");
		}
		
		logger.info(handler);
		logger.info(method);

		ResolvableType parameter = ResolvableType.forMethodParameter(method, 0, handler.getClass());
		EventHandlerMethod handlerMethod = EventHandlerMethod.of(parameter.resolve(), handler, method);


		List<EventHandlerMethod> events = handlerMethods.get(eventType);
		if (events == null) {
			events = new ArrayList<EventHandlerMethod>();
		}
		if (events.isEmpty()) {
			handlerMethods.add(eventType, handlerMethod);
			return;
		}
		events.add(handlerMethod);
		Collections.sort(events);
		handlerMethods.put(eventType, events);
	}

	@Override
	public void onApplicationEvent(RepositoryEvent event) {

		Class<? extends RepositoryEvent> eventType = event.getClass();

		if (!handlerMethods.containsKey(eventType)) {
			return;
		}

		for (EventHandlerMethod handlerMethod : handlerMethods.get(eventType)) {

			Object src = event.getSource();

			if (!ClassUtils.isAssignable(handlerMethod.targetType, src.getClass())) {
				continue;
			}
			
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(src);
			if (event instanceof BeforeReadEvent) {
				parameters.add(((BeforeReadEvent)event).getObject());
			}
			logger.info(handlerMethod.handler);
			logger.info(handlerMethod.method);
			logger.info(parameters);
			
			ReflectionUtils.invokeMethod(handlerMethod.method, handlerMethod.handler, parameters.toArray());
		}
	}
}

