package org.simple.session.core.filter;

import org.simple.session.api.SessionManager;
import org.simple.session.api.filter.AbstractSessionFilter;
import org.simple.session.core.RedisSessionManager;

import java.io.IOException;
import java.util.Properties;

/**
 * Redis Session Filter based on Redis
 *
 * @author clx 2018/4/3.
 */
public class RedisSessionFilter extends AbstractSessionFilter {

	private Properties properties;

	public RedisSessionFilter() {
	}

	public RedisSessionFilter(Properties properties) {
		this.properties = properties;
	}

	/**
	 * subclass create session manager
	 *
	 * @return session manager
	 */
	@Override
	protected SessionManager createSessionManager() throws IOException {
		if (properties != null) {
			return new RedisSessionManager(properties);
		}
		return new RedisSessionManager();
	}
}
