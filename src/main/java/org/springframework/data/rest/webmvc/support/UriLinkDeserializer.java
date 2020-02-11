package org.springframework.data.rest.webmvc.support;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

//@JsonComponent
public class UriLinkDeserializer<T> extends JsonDeserializer<T> implements ContextualDeserializer{

	protected Log logger = LogFactory.getLog(getClass());
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private Class<?> rawClass;
	
	public UriLinkDeserializer() {
	}
	public UriLinkDeserializer(Class<?> rawClass) {
		this.rawClass = rawClass;
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		JavaType type = ctxt.getContextualType();
		this.rawClass = type.getRawClass();
		return new UriLinkDeserializer<>(type.getRawClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		logger.debug("rawClass: "+rawClass);
		
		if (p.getCurrentToken() == JsonToken.START_OBJECT) {
			JsonNode node = p.getCodec().readTree(p);
//			logger.debug("node: "+node);
			try {
				T object = (T)objectMapper.treeToValue(node, rawClass);
//				logger.debug("object: "+ object);
				return object;
			} catch (Exception e) {
//				logger.debug("", e);
			}
		}else {
			String text = p.getValueAsString();
			logger.debug("text: "+text);
			
			try {
				T object = (T) UriLinkParser.resolveEntity(text, rawClass);
//				logger.debug("object: "+ object);
				return object;
			} catch (Exception e) {
//				logger.debug("", e);
			}
		}
		return null;
	}
	
	
}
