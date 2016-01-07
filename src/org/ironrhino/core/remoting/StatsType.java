package org.ironrhino.core.remoting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.ironrhino.core.model.Displayable;
import org.ironrhino.core.struts.I18N;

public enum StatsType implements Displayable {

	SERVER_SIDE("server"), CLIENT_SIDE("client"), CLIENT_FAILED("cfailed");

	private ConcurrentHashMap<String, Map<String, AtomicInteger>> buffer = new ConcurrentHashMap<>();

	private String namespace;

	private StatsType(String namespace) {
		this.namespace = namespace;
	}

	public ConcurrentHashMap<String, Map<String, AtomicInteger>> getBuffer() {
		return buffer;
	}

	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getDisplayName() {
		try {
			return I18N.getText(getClass(), name());
		} catch (Exception e) {
			return name();
		}
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}