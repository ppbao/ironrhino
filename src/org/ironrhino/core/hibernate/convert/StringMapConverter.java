package org.ironrhino.core.hibernate.convert;

import java.util.Map;

import javax.persistence.Converter;

@Converter
public class StringMapConverter extends JsonConverter<Map<String, String>> {

}