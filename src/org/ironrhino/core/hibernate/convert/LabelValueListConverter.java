package org.ironrhino.core.hibernate.convert;

import java.util.List;

import javax.persistence.Converter;

import org.ironrhino.core.model.LabelValue;

@Converter
public class LabelValueListConverter extends JsonConverter<List<LabelValue>> {

}