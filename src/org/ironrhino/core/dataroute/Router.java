package org.ironrhino.core.dataroute;

import java.util.List;

public interface Router {

	public int route(List<String> nodes, String routingKey);

}
