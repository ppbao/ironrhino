package org.ironrhino.security.service;

import java.util.List;

import org.ironrhino.core.spring.security.ConcreteUserDetailsService;
import org.ironrhino.core.util.BeanUtils;
import org.ironrhino.security.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class UserServiceImpl implements UserService {

	@Autowired(required = false)
	private List<ConcreteUserDetailsService> userDetailsServices;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) {
		if (username == null)
			throw new IllegalArgumentException("username shouldn't be null");
		UserDetails ud = null;
		if (userDetailsServices != null)
			for (ConcreteUserDetailsService uds : userDetailsServices) {
				if (uds.accepts(username))
					try {
						ud = uds.loadUserByUsername(username);
						if (ud != null) {
							User user = new User();
							BeanUtils.copyProperties(ud, user, false);
							return user;
						}
					} catch (UsernameNotFoundException unfe) {
						continue;
					}
			}
		return null;
	}

}
