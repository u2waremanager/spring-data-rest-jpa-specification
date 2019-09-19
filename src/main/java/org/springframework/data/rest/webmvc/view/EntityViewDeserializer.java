package org.springframework.data.rest.webmvc.view;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

public class EntityViewDeserializer<T> extends JsonDeserializer<T> implements ContextualDeserializer{

	protected Log logger = LogFactory.getLog(getClass());
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private Class<?> rawClass;
	
	public EntityViewDeserializer() {
	}
	public EntityViewDeserializer(Class<?> rawClass) {
		this.rawClass = rawClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		logger.debug("rawClass: "+rawClass);
		
		if (p.getCurrentToken() == JsonToken.START_OBJECT) {
			JsonNode node = p.getCodec().readTree(p);
			logger.debug("node: "+node);
			try {
				T object = (T)objectMapper.treeToValue(node, rawClass);
				
				logger.debug("object: "+ object);
				return object;
			} catch (Exception e) {
				logger.debug("", e);
			}
		}else {
			String text = p.getValueAsString();
			logger.debug("text: "+text);
			
			
			if(ClassUtils.isAssignable(EntityView.class, rawClass)) {
				
				try {
				
					Class<?> entityType = GenericTypeResolver.resolveTypeArguments(rawClass, EntityView.class)[0];
					T entity = (T) UriLinkParser.resolveEntity(text, entityType);
					logger.debug("entity: "+ entity);
					
					
					Constructor<?> c = ClassUtils.getConstructorIfAvailable(rawClass);
					T object = (T)c.newInstance();
					
					Method m = ClassUtils.getMethod(rawClass, "deserialize", entityType);
					m.invoke(object, entity);
					
					
					logger.debug("object: "+ object);
					return object;
				} catch (Exception e) {
					logger.debug("", e);
				}
				
			}else {
				try {
					T object = (T) UriLinkParser.resolveEntity(text, rawClass);
					
					logger.debug("object: "+ object);
					return object;
				} catch (Exception e) {
					logger.debug("", e);
				}
			}

		}
		return null;
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		JavaType type = ctxt.getContextualType();
		return new EntityViewDeserializer<>(type.getRawClass());
	}
}