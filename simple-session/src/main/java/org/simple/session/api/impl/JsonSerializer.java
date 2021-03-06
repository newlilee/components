package org.simple.session.api.impl;

import com.google.common.base.Throwables;
import org.simple.session.exception.SerializeException;
import org.simple.session.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Serializer
 *
 * @author clx 2018/4/3.
 */
public class JsonSerializer implements org.simple.session.api.JsonSerializer {

	private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

	@Override
	public String serialize(Object o) {
		try {
			return JsonUtils.toJsonStr(o);
		} catch (Exception e) {
			logger.error("failed to serialize http session {} to json,cause:{}", o, Throwables.getStackTraceAsString(e));
			throw new SerializeException("failed to serialize http session to json", e);
		}
	}

	@Override
	public Map<String, Object> deserialize(String o) {
		try {
			return (Map<String, Object>) JsonUtils.jsonToLocal(o);
		} catch (Exception e) {
			logger.error("failed to deserialize string  {} to http session,cause:{} ", o,
					Throwables.getStackTraceAsString(e));
			throw new SerializeException("failed to deserialize string to http session", e);
		}
	}
}
