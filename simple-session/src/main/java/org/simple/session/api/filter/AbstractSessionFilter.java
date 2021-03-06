package org.simple.session.api.filter;

import com.google.common.base.Strings;
import org.simple.session.api.SessionManager;
import org.simple.session.api.impl.RedisHttpServletRequest;
import org.simple.session.api.impl.RedisHttpSession;
import org.simple.session.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Abstract Session Filter
 *
 * @author clx 2018/4/3.
 */
public abstract class AbstractSessionFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSessionFilter.class);

	protected final static String SESSION_COOKIE_NAME = "sessionCookieName";

	protected final static String DEFAULT_SESSION_COOKIE_NAME = "rsid";

	/**
	 * session cookie name
	 */
	protected String sessionCookieName;

	protected final static String MAX_INACTIVE_INTERVAL = "maxInactiveInterval";

	/**
	 * default 30 mins
	 */
	protected final static int DEFAULT_MAX_INACTIVE_INTERVAL = 30 * 60;

	/**
	 * max inactive interval
	 */
	protected int maxInactiveInterval;

	protected final static String COOKIE_DOMAIN = "cookieDomain";

	/**
	 * cookie name
	 */
	protected String cookieDomain;

	protected final static String COOKIE_CONTEXT_PATH = "cookieContextPath";

	protected final static String DEFAULT_COOKIE_CONTEXT_PATH = "/";

	/**
	 * cookie's context path
	 */
	protected String cookieContextPath;

	protected final static String COOKIE_MAX_AGE = "cookieMaxAge";

	protected final static int DEFAULT_COOKIE_MAX_AGE = -1;
	/**
	 * cookie's life
	 */
	protected int cookieMaxAge;

	/**
	 * session manager
	 */
	protected SessionManager sessionManager;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			sessionManager = createSessionManager();
			initAttrs(filterConfig);
		} catch (Exception ex) {
			logger.error("failed to init session filter.", ex);
			throw new ServletException(ex);
		}
	}

	/**
	 * subclass create session manager
	 *
	 * @return session manager
	 * @throws IOException
	 */
	protected abstract SessionManager createSessionManager() throws IOException;

	/**
	 * init basic attribute
	 *
	 * @param config the filter config
	 */
	private final void initAttrs(FilterConfig config) {
		String param = config.getInitParameter(SESSION_COOKIE_NAME);
		sessionCookieName = Strings.isNullOrEmpty(param) ? DEFAULT_SESSION_COOKIE_NAME : param;

		param = config.getInitParameter(MAX_INACTIVE_INTERVAL);
		maxInactiveInterval = Strings.isNullOrEmpty(param) ? DEFAULT_MAX_INACTIVE_INTERVAL : Integer.parseInt(param);

		cookieDomain = config.getInitParameter(COOKIE_DOMAIN);

		param = config.getInitParameter(COOKIE_CONTEXT_PATH);
		cookieContextPath = Strings.isNullOrEmpty(param) ? DEFAULT_COOKIE_CONTEXT_PATH : param;

		param = config.getInitParameter(COOKIE_MAX_AGE);
		cookieMaxAge = Strings.isNullOrEmpty(param) ? DEFAULT_COOKIE_MAX_AGE : Integer.parseInt(param);

		logger.info("SessionFilter (sessionCookieName={}, maxInactiveInterval={}, cookieDomain={})", sessionCookieName,
				maxInactiveInterval, cookieDomain);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof RedisHttpServletRequest) {
			chain.doFilter(request, response);
			return;
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		RedisHttpServletRequest redisHttpServletRequest = new RedisHttpServletRequest(httpRequest, httpResponse,
				sessionManager);
		redisHttpServletRequest.setSessionCookieName(sessionCookieName);
		redisHttpServletRequest.setMaxInactiveInterval(maxInactiveInterval);
		redisHttpServletRequest.setCookieDomain(cookieDomain);
		redisHttpServletRequest.setCookieContextPath(cookieContextPath);
		redisHttpServletRequest.setCookieMaxAge(cookieMaxAge);

		// do other filter
		chain.doFilter(redisHttpServletRequest, response);

		RedisHttpSession session = redisHttpServletRequest.currentSession();
		if (session != null) {
			if (!session.isValid()) {
				// if invalidate , delete login cookie
				logger.debug("delete login cookie");
				WebUtils.failureCookie(httpRequest, httpResponse, sessionCookieName, cookieDomain, cookieContextPath);
			} else if (session.isDirty()) {
				// should flush to store
				if (logger.isDebugEnabled()) {
					logger.debug("try to flush session to session store");
				}
				Map<String, Object> snapshot = session.snapshot();
				if (sessionManager.persist(session.getId(), snapshot, maxInactiveInterval)) {
					if (logger.isDebugEnabled()) {
						logger.debug("succeed to flush session {} to store, key is:{}", snapshot, session.getId());
					}
				} else {
					logger.error("failed to save session to redis");
					WebUtils.failureCookie(httpRequest, httpResponse, sessionCookieName, cookieDomain,
							cookieContextPath);
				}
			} else {
				// refresh expire time
				sessionManager.expire(session, maxInactiveInterval);
			}
		}
	}

	@Override
	public void destroy() {
		logger.info("filter is destroy.");
		sessionManager.destroy();
	}
}
