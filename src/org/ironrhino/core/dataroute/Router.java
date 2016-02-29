package org.ironrhino.core.dataroute;

import java.util.List;

public interface Router {

	public String route(List<String> nodes, String routingKey);

}
