package org.simple.session.api.impl;

import org.simple.session.api.SessionManager;
import org.simple.session.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Redis HttpServletRequest
 *
 * @author clx 2018/4/3.
 */
public class RedisHttpServletRequest extends HttpServletRequestWrapper {

	private static final Logger logger = LoggerFactory.getLogger(RedisHttpServletRequest.class);

	private HttpServletRequest request;

	private HttpServletResponse response;

	private SessionManager sessionManager;

	private RedisHttpSession session;

	private String sessionCookieName;

	private String cookieDomain;

	private String cookieContextPath;

	private int maxInactiveInterval;

	private int cookieMaxAge;

	public RedisHttpServletRequest(HttpServletRequest request, HttpServletResponse response,
								   SessionManager sessionManager) {
		super(request);
		this.request = request;
		this.response = response;
		this.sessionManager = sessionManager;
	}

	@Override
	public HttpSession getSession(boolean create) {
		return doGetSession(create);
	}

	/**
	 * get session instance, create new one if not exsit
	 */
	@Override
	public HttpSession getSession() {
		return doGetSession(true);
	}

	/**
	 * get session id name in cookie
	 */
	public String getSessionCookieName() {
		return this.sessionCookieName;
	}

	/**
	 * set session id in cookie
	 *
	 * @param sessionCookieName session name in cookie
	 */
	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	/**
	 * get cookie cookie's domain
	 *
	 * @return cookie's store domain
	 */
	public String getCookieDomain() {
		return this.cookieDomain;
	}

	/**
	 * set cookie cookie's domain
	 */
	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	/**
	 * get cookie's store path
	 *
	 * @return cookie's store path
	 */
	public String getCookieContextPath() {
		return cookieContextPath;
	}

	/**
	 * set cookie's store path
	 */
	public void setCookieContextPath(String cookieContextPath) {
		this.cookieContextPath = cookieContextPath;
	}

	/**
	 * set session inactive life (seconds)
	 */
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	/**
	 * set cookie max age
	 *
	 * @param cookieMaxAge cookie max age
	 */
	public void setCookieMaxAge(int cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
	}

	/**
	 * get current session instance
	 *
	 * @return current session instance
	 */
	public RedisHttpSession currentSession() {
		return session;
	}

	/**
	 * get session from session cookie name
	 *
	 * @param create if true create
	 * @return session
	 */
	private HttpSession doGetSession(boolean create) {
		if (session == null) {
			Cookie cookie = WebUtils.findCookie(this, getSessionCookieName());
			if (cookie != null) {
				String value = cookie.getValue();
				logger.debug("Find session's id from cookie.[{}]", value);
				session = buildSession(value, false);
			} else {
				session = buildSession(create);
			}
		} else {
			logger.debug("Session[{}] was existed.", session.getId());
		}
		return session;
	}

	/**
	 * build a new session from session id
	 *
	 * @param id      session id
	 * @param refresh refresh cookie or not
	 * @return session
	 */
	private RedisHttpSession buildSession(String id, boolean refresh) {
		RedisHttpSession session = new RedisHttpSession(sessionManager, request, id);
		session.setMaxInactiveInterval(maxInactiveInterval);
		if (refresh) {
			WebUtils.addCookie(this, response, getSessionCookieName(), id, getCookieDomain(), getCookieContextPath(),
					cookieMaxAge, true);
		}
		return session;
	}

	/**
	 * build a new session
	 *
	 * @param create create session or not
	 * @return session
	 */
	private RedisHttpSession buildSession(boolean create) {
		if (create) {
			session = buildSession(sessionManager.getSessionIdGenerator().generate(request), true);
			logger.debug("Build new session[{}].", session.getId());
			return session;
		} else {
			return null;
		}
	}
}
