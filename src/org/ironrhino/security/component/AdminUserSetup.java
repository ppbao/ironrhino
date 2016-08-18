package org.ironrhino.security.component;

import org.ironrhino.core.metadata.Setup;
import org.ironrhino.core.metadata.SetupParameter;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.security.model.User;
import org.ironrhino.security.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class AdminUserSetup {

	@Autowired
	private UserManager userManager;

	@Setup
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public User setup(
			@SetupParameter(defaultValue = "admin", displayOrder = Ordered.HIGHEST_PRECEDENCE, label = "admin.username", cssClass = "span2") String username,
			@SetupParameter(defaultValue = "password", displayOrder = Ordered.HIGHEST_PRECEDENCE
					+ 1, label = "admin.password", cssClass = "span2") String password)
			throws Exception {
		if (userManager.countAll() > 0)
			return null;
		User admin = new User();
		admin.setUsername(username);
		admin.setLegiblePassword(password);
		admin.setEnabled(true);
		admin.getRoles().add(UserRole.ROLE_ADMINISTRATOR);
		userManager.save(admin);
		return admin;
	}

}
