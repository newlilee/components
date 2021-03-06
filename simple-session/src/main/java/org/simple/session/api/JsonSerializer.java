package org.simple.session.api;

import java.util.Map;

/**
 * Json Serializer
 *
 * @author clx 2018/4/3.
 */
public interface JsonSerializer {
	/**
	 * Serialize object to json string
	 *
	 * @param o object
	 * @return json string
	 */
	String serialize(Object o);

	/**
	 * deserialize json string to map
	 *
	 * @param json json string
	 * @return map
	 */
	Map<String, Object> deserialize(String json);
}
