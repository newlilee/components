package org.simple.session.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.LazilyParsedNumber;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author clx 2019-08-13
 */
public final class JsonUtils {
	private JsonUtils() {
	}

	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(
			LazilyParsedNumber.class, (JsonSerializer<LazilyParsedNumber>) (src, typeOfSrc, context) ->
					new JsonPrimitive(src)).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().create();

	/**
	 * 将json数据转换为本地对象数据
	 *
	 * @param jsonElement
	 * @return
	 */
	public static Object jsonToLocal(JsonElement jsonElement) {
		if (jsonElement.isJsonNull()) {
			return null;
		}
		if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Map<String, Object> map = new LinkedHashMap<>(jsonObject.size());
			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				map.put(entry.getKey(), jsonToLocal(entry.getValue()));
			}
			return map;
		}
		if (jsonElement.isJsonArray()) {
			JsonArray array = jsonElement.getAsJsonArray();
			int arraySize = array.size();
			List<Object> list = new ArrayList<>(arraySize);
			for (int i = 0; i < arraySize; i++) {
				list.add(jsonToLocal(array.get(i)));
			}
			return list;
		}
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isBoolean()) {
				return jsonPrimitive.getAsBoolean();
			}
			if (jsonPrimitive.isNumber()) {
				return jsonPrimitive.getAsNumber();
			}
			return jsonPrimitive.getAsString();
		}
		throw new IllegalArgumentException(jsonElement.toString());
	}

	/**
	 * json string to local object
	 *
	 * @param jsonStr
	 * @return
	 */
	public static Object jsonToLocal(String jsonStr) {
		return jsonToLocal(JsonUtils.GSON.fromJson(jsonStr, JsonElement.class));
	}

	/**
	 * to json string
	 *
	 * @param v
	 * @return
	 */
	public static String toJsonStr(Object v) {
		return JsonUtils.GSON.toJson(v);
	}
}
