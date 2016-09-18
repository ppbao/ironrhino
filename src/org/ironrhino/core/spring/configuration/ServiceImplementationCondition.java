package org.ironrhino.core.spring.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.util.AppInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

class ServiceImplementationCondition implements Condition {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static Set<String> set = new HashSet<>();

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		if (context.getEnvironment() != null) {
			Map<String, Object> attrs = metadata
					.getAnnotationAttributes(ServiceImplementationConditional.class.getName());
			if (attrs != null) {
				String serviceInterfaceName = null;
				String className = ((AnnotationMetadataReadingVisitor) metadata).getClassName();
				Class<?> serviceInterface = (Class<?>) attrs.get("serviceInterface");
				if (serviceInterface != void.class) {
					serviceInterfaceName = serviceInterface.getName();
				} else {
					serviceInterfaceName = findMostMatchedInterface(className);
				}
				if (serviceInterfaceName != null) {
					String implementationClassName = AppInfo.getApplicationContextProperties()
							.getProperty(serviceInterfaceName);
					if (StringUtils.isNotBlank(implementationClassName)) {
						boolean matched = implementationClassName.equals(className);
						if (matched) {
							String key = serviceInterfaceName + "=" + implementationClassName;
							if (!set.contains(key)) {
								logger.info("Select implementation {} for service {}", implementationClassName,
										serviceInterfaceName);
								set.add(key);
							}
						}
						return matched;
					}
				}
				String[] profiles = (String[]) attrs.get("profiles");
				if (profiles.length == 0 || context.getEnvironment().acceptsProfiles(profiles)) {
					return true;
				}
				return false;
			}
		}
		return true;
	}

	private String findMostMatchedInterface(String className) {
		List<String> interfaceNames = new ArrayList<>();
		try {
			ClassPool classPool = ClassPool.getDefault();
			classPool.insertClassPath(new ClassClassPath(getClass()));
			CtClass cc = classPool.get(className);
			while (!cc.getName().equals(Object.class.getName())) {
				CtClass[] interfaces = cc.getInterfaces();
				for (CtClass ct : interfaces)
					interfaceNames.add(ct.getName());
				cc = cc.getSuperclass();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		for (String interfaceName : interfaceNames) {
			String simpleInterfaceName = interfaceName.substring(interfaceName.lastIndexOf('.') + 1);
			String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
			String interfacePackage = interfaceName.substring(0, interfaceName.lastIndexOf('.'));
			String classPackage = className.substring(0, className.lastIndexOf('.'));
			if (classPackage.startsWith(interfacePackage) && simpleClassName.contains(simpleInterfaceName))
				return interfaceName;
		}
		for (String interfaceName : interfaceNames) {
			String simpleInterfaceName = interfaceName.substring(interfaceName.lastIndexOf('.') + 1);
			String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
			if (simpleClassName.contains(simpleInterfaceName))
				return interfaceName;
		}
		for (String interfaceName : interfaceNames) {
			String interfacePackage = interfaceName.substring(0, interfaceName.lastIndexOf('.'));
			String classPackage = className.substring(0, className.lastIndexOf('.'));
			if (classPackage.startsWith(interfacePackage))
				return interfaceName;
		}
		return null;
	}

}
