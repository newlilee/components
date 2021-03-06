package org.simple.session.api;

import org.simple.session.api.impl.RedisHttpSession;

import java.util.Map;

/**
 * SessionManager
 *
 * @author clx 2018/4/3.
 */
public interface SessionManager {

	/**
	 * persist session to session store
	 *
	 * @param id                  session id
	 * @param snapshot            session attributes' snapshot
	 * @param maxInactiveInterval session max life(seconds)
	 * @return true if save successfully, or false
	 */
	Boolean persist(final String id, final Map<String, Object> snapshot, final int maxInactiveInterval);

	/**
	 * find session by id
	 *
	 * @param id session id
	 * @return
	 */
	Map<String, Object> loadById(String id);

	/**
	 * delete session physically
	 *
	 * @param id session id
	 */
	void deleteById(String id);

	/**
	 * expired session
	 *
	 * @param session             current session
	 * @param maxInactiveInterval max life(seconds)
	 */
	void expire(RedisHttpSession session, final int maxInactiveInterval);

	/**
	 * session manager destroy when filter destroy
	 */
	void destroy();

	/**
	 * get session id generator
	 *
	 * @return session id generator
	 */
	SessionIdGenerator getSessionIdGenerator();
}
