package org.simple.session.api;

import javax.servlet.http.HttpServletRequest;

/**
 * SessionId Generator
 *
 * @author clx 2018/4/3.
 */
public interface SessionIdGenerator {

	/**
	 * 生成Session Id
	 *
	 * @param request
	 * @return session id
	 */
	String generate(HttpServletRequest request);
}
