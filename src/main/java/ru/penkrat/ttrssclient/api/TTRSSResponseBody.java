package ru.penkrat.ttrssclient.api;

import java.io.StringReader;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;

/**
 * All API methods return JSON data like this:
 * <pre>
 * {
 * 	"seq":0,
 * 	"status":0,
 * 	"content":{"version":"1.4.3.1"}
 * }
 * <pre>
 * 
 * @author ruslan
 *
 */
public class TTRSSResponseBody {

	private final int status;

	private final Optional<JsonArray> jsonArrayContent;
	private final Optional<JsonObject> jsonObjectContent;

	public static TTRSSResponseBody parse(String input) {
		JsonObject msg = Json.createReader(new StringReader(input)).readObject();

		var status = msg.getInt("status");
		var content = msg.get("content");

		if (content.getValueType() == ValueType.ARRAY) {
			return new TTRSSResponseBody(status, Optional.empty(), Optional.ofNullable(msg.getJsonArray("content")));
		} else if (content.getValueType() == ValueType.OBJECT) {
			return new TTRSSResponseBody(status, Optional.ofNullable(msg.getJsonObject("content")), Optional.empty());
		} else {
			throw new IllegalStateException("'content' not array nor object");
		}
	}

	public TTRSSResponseBody(int status, Optional<JsonObject> jsonObject, Optional<JsonArray> jsonArray) {
		super();
		this.status = status;
		this.jsonObjectContent = jsonObject;
		this.jsonArrayContent = jsonArray;
	}

	public int getStatus() {
		return status;
	}

	public Optional<JsonObject> getContentAsObject() {
		return jsonObjectContent;
	}

	public Optional<JsonArray> getContentAsArray() {
		return jsonArrayContent;
	}

}
