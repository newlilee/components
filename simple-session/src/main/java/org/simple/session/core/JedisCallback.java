package org.simple.session.core;

import redis.clients.jedis.Jedis;

/**
 * Jedis Execute Callback
 * 
 * @author clx 2018/4/3.
 */
public interface JedisCallback<T> {
	/**
	 * Execute Callback
	 * 
	 * @param jedis
	 * @return
	 */
	T execute(Jedis jedis);
}
