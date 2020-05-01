package ru.penkrat.ttrssclient.api;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * <pre>
 * {
 *   "sid":"...",
 *   "op":"getHeadlines",
 *   "feed_id":"0",
 *   "is_cat":"1"
 * }
 * </pre>
 * 
 * @author ruslan
 *
 */
public class TTRSSRequestBody {

	private String json;

	public static TTRSSRequestBody op(String operation) {
		return new TTRSSRequestBody().add("op", operation);
	}

	private final JsonObjectBuilder body;

	public TTRSSRequestBody() {
		body = Json.createObjectBuilder();
	}

	public TTRSSRequestBody sid(String sid) {
		return add("sid", sid);
	}

	public TTRSSRequestBody add(String key, String value) {
		body.add(key, value);
		return this;
	}

	public TTRSSRequestBody add(String key, Object value) {
		if (value != null) {
			return add(key, value.toString());
		} else {
			body.addNull(key);
		}
		return this;
	}

	public String toString() {
		if (json != null)
			return json;
		json = body.build().toString();
		return json;
	}

}
