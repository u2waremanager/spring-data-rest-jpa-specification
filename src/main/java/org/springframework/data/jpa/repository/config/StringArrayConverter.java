package org.springframework.data.jpa.repository.config;

import javax.persistence.AttributeConverter;

import org.springframework.util.StringUtils;

public class StringArrayConverter implements AttributeConverter<String[],String>{

	@Override
	public String convertToDatabaseColumn(String[] attribute) {
		return StringUtils.arrayToCommaDelimitedString(attribute);
	}

	@Override
	public String[] convertToEntityAttribute(String dbData) {
		return StringUtils.commaDelimitedListToStringArray(dbData);
	}
}
