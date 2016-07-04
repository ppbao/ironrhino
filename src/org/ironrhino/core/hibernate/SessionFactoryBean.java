package org.ironrhino.core.hibernate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.ironrhino.core.hibernate.dialect.MyDialectResolver;
import org.ironrhino.core.util.ClassScanner;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

public class SessionFactoryBean extends org.springframework.orm.hibernate4.LocalSessionFactoryBean {

	@Autowired
	private Logger logger;

	@Autowired(required = false)
	private IdentifierGeneratorFactory identifierGeneratorFactory;

	private Class<?>[] annotatedClasses;

	private String excludeFilter;

	public void setExcludeFilter(String excludeFilter) {
		this.excludeFilter = excludeFilter;
	}

	@Override
	public void setAnnotatedClasses(Class<?>... annotatedClasses) {
		this.annotatedClasses = annotatedClasses;
	}

	@Override
	public void afterPropertiesSet() throws IOException {
		Properties properties = getHibernateProperties();
		if (StringUtils.isBlank(properties.getProperty(AvailableSettings.DIALECT_RESOLVERS)))
			properties.put(AvailableSettings.DIALECT_RESOLVERS, MyDialectResolver.class.getName());
		Map<String, Class<?>> added = new HashMap<String, Class<?>>();
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Collection<Class<?>> scaned = ClassScanner.scanAnnotated(ClassScanner.getAppPackages(), Entity.class);
		if (annotatedClasses != null)
			for (Class<?> c : annotatedClasses)
				if (!added.containsKey(c.getSimpleName()) || !c.isAssignableFrom(added.get(c.getSimpleName()))) {
					classes.add(c);
					added.put(c.getSimpleName(), c);
				}
		for (Class<?> c : scaned)
			if (!added.containsKey(c.getSimpleName()) || !c.isAssignableFrom(added.get(c.getSimpleName()))) {
				classes.add(c);
				added.put(c.getSimpleName(), c);
			}
		if (StringUtils.isNotBlank(excludeFilter)) {
			Collection<Class<?>> temp = classes;
			classes = new ArrayList<Class<?>>();
			String[] arr = excludeFilter.split("\\s*,\\s*");
			for (Class<?> clz : temp) {
				boolean exclude = false;
				for (String s : arr) {
					if (org.ironrhino.core.util.StringUtils.matchesWildcard(clz.getName(), s)) {
						exclude = true;
						break;
					}
				}
				if (!exclude)
					classes.add(clz);
			}
		}
		Collections.sort(classes, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> a, Class<?> b) {
				return a.getName().compareTo(b.getName());
			}
		});
		annotatedClasses = classes.toArray(new Class<?>[0]);
		logger.info("annotatedClasses: ");
		for (Class<?> clz : annotatedClasses)
			logger.info(clz.getName());
		super.setAnnotatedClasses(annotatedClasses);
		super.afterPropertiesSet();
	}

	@Override
	protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
		if (identifierGeneratorFactory != null) {
			try {
				Field f = Configuration.class.getDeclaredField("identifierGeneratorFactory");
				f.setAccessible(true);
				f.set(sfb, identifierGeneratorFactory);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sfb.buildSessionFactory();
	}
}
