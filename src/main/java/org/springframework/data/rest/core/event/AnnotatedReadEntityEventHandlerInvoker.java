package org.springframework.data.rest.core.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AnnotatedReadEntityEventHandlerInvoker implements ApplicationListener<ReadEntityEvent>, BeanPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotatedReadEntityEventHandlerInvoker.class);
	private static final String PARAMETER_MISSING = "Invalid event handler method %s! At least a single argument is required to determine the domain type for which you are interested in events.";
	private final MultiValueMap<Class<? extends ReadEntityEvent>, EventHandlerMethod> handlerMethods = new LinkedMultiValueMap<Class<? extends ReadEntityEvent>, EventHandlerMethod>();
	

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
	

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public void onApplicationEvent(ReadEntityEvent event) {
		Class<? extends ReadEntityEvent> eventType = event.getClass();

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
				parameters.add(((BeforeReadEvent) event).getQuery());
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("Invoking {} handler for {}.", event.getClass().getSimpleName(), event.getSource());
			}

			ReflectionUtils.invokeMethod(handlerMethod.method, handlerMethod.handler, parameters.toArray());
		}
	}
	
	private <T extends Annotation> void inspect(Object handler, Method method, Class<T> annotationType, Class<? extends ReadEntityEvent> eventType) {
		T annotation = AnnotationUtils.findAnnotation(method, annotationType);

		if (annotation == null) {
			return;
		}

		if (method.getParameterCount() == 0) {
			throw new IllegalStateException(String.format(PARAMETER_MISSING, method));
		}

		ResolvableType parameter = ResolvableType.forMethodParameter(method, 0, handler.getClass());
		EventHandlerMethod handlerMethod = EventHandlerMethod.of(parameter.resolve(), handler, method);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Annotated handler method found: {}", handlerMethod);
		}

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

}
