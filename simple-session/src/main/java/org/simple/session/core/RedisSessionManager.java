package org.simple.session.core;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.simple.session.api.AbstractSessionManager;
import org.simple.session.api.SessionIdGenerator;
import org.simple.session.api.impl.RedisHttpSession;
import org.simple.session.exception.SessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Session Manager based on Redis
 *
 * @author clx 2018/4/3.
 */
public class RedisSessionManager extends AbstractSessionManager {

	private static final Logger logger = LoggerFactory.getLogger(RedisSessionManager.class);

	private static final String SENTINEL_MODE = "sentinel";

	private String sessionPrefix;

	private volatile JedisPoolExecutor executor;

	public RedisSessionManager() throws IOException {
	}

	public RedisSessionManager(Properties prop) throws IOException {
		super(prop);
	}

	/**
	 * @param propertiesFile properties file in classpath, default is session.properties
	 */
	public RedisSessionManager(String propertiesFile) throws IOException {
		super(propertiesFile);
	}

	/**
	 * init subclass
	 */
	@Override
	protected void init(Properties props) {
		this.sessionPrefix = props.getProperty("session.redis.prefix", "rsession");
		initJedisPool(props);
	}

	/**
	 * init jedis pool with properties
	 *
	 * @param props
	 */
	private void initJedisPool(Properties props) {
		JedisPoolConfig config = new JedisPoolConfig();

		config.setTestOnBorrow(true);

		int maxIdle = Integer.parseInt(props.getProperty("session.redis.pool.max.idle", "2"));
		config.setMaxIdle(maxIdle);

		int maxTotal = Integer.parseInt(props.getProperty("session.redis.pool.max.total", "5"));
		config.setMaxTotal(maxTotal);

		final String mode = props.getProperty("session.redis.mode");
		if (Objects.equal(mode, SENTINEL_MODE)) {
			// sentinel
			this.executor = new JedisPoolExecutor(config, true, props);
		} else {
			// standalone
			this.executor = new JedisPoolExecutor(config, false, props);
		}
	}

	/**
	 * persist session to session store
	 *
	 * @param id                  session id
	 * @param snapshot            session attributes' snapshot
	 * @param maxInactiveInterval session max life(seconds)
	 * @return true if save successfully, or false
	 */
	@Override
	public Boolean persist(final String id, final Map<String, Object> snapshot, final int maxInactiveInterval) {
		final String sid = sessionPrefix + ":" + id;
		try {
			this.executor.execute((JedisCallback<Void>) jedis -> {
				if (snapshot.isEmpty()) {
					// delete session
					jedis.del(sid);
				} else {
					// set session
					jedis.setex(sid, maxInactiveInterval, jsonSerializer.serialize(snapshot));
				}
				return null;
			});
			return Boolean.TRUE;
		} catch (Exception e) {
			logger.error("failed to delete session(key={}) in redis,cause:{}", sid,
					Throwables.getStackTraceAsString(e));
			return Boolean.FALSE;
		}
	}

	/**
	 * find session by id
	 *
	 * @param id session id
	 * @return session map object
	 */
	@Override
	public Map<String, Object> loadById(String id) {
		final String sid = sessionPrefix + ":" + id;
		try {
			return this.executor.execute(jedis -> {
				String session = jedis.get(sid);
				if (!Strings.isNullOrEmpty(session)) {
					return jsonSerializer.deserialize(session);
				}
				return Collections.emptyMap();
			});
		} catch (Exception e) {
			logger.error("failed to find session(key={}) in redis, cause:{}", sid, Throwables.getStackTraceAsString(e));
			throw new SessionException("get session failed", e);
		}
	}

	/**
	 * delete session physically
	 *
	 * @param id session id
	 */
	@Override
	public void deleteById(String id) {
		final String sid = sessionPrefix + ":" + id;
		try {
			this.executor.execute(new JedisCallback<Void>() {
				@Override
				public Void execute(Jedis jedis) {
					jedis.del(sid);
					return null;
				}
			});
		} catch (Exception e) {
			logger.error("failed to delete session(key={}) in redis,cause:{}", sid,
					Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * expired session
	 *
	 * @param session             current session
	 * @param maxInactiveInterval max life(seconds)
	 */
	@Override
	public void expire(RedisHttpSession session, final int maxInactiveInterval) {
		final String sessionId = sessionPrefix + ":" + session.getId();
		try {
			this.executor.execute(new JedisCallback<Void>() {
				@Override
				public Void execute(Jedis jedis) {
					jedis.expire(sessionId, maxInactiveInterval);
					return null;
				}
			});
		} catch (Exception e) {
			logger.error("failed to refresh expire time session(key={}) in redis,cause:{}", sessionId,
					Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * session manager destroy when filter destroy destroy executor
	 */
	@Override
	public void destroy() {
		if (executor != null) {
			executor.getJedisPool().destroy();
		}
	}

	/**
	 * get session id generator
	 *
	 * @return session id generator
	 */
	@Override
	public SessionIdGenerator getSessionIdGenerator() {
		return this.sessionIdGenerator;
	}
}
