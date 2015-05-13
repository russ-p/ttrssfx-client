package ru.penkrat.ttrssclient.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonHttpClient {

	private static final Logger log = LoggerFactory.getLogger(JsonHttpClient.class);

	static class JsonHttpRequesBuilder {

		private JsonObjectBuilder jsonObjectBuilder;
		private String url;
		private CloseableHttpClient httpclient;

		public JsonHttpRequesBuilder(String url, CloseableHttpClient httpclient) {
			this.url = url;
			this.httpclient = httpclient;
			jsonObjectBuilder = Json.createObjectBuilder();
		}

		public JsonHttpRequesBuilder add(String name, JsonValue value) {
			jsonObjectBuilder.add(name, value);
			return this;
		}

		public JsonHttpRequesBuilder add(String name, String value) {
			jsonObjectBuilder.add(name, value);
			return this;
		}

		public JsonHttpRequesBuilder add(String name, BigInteger value) {
			jsonObjectBuilder.add(name, value);
			return this;
		}

		public JsonHttpRequesBuilder add(String name, BigDecimal value) {
			jsonObjectBuilder.add(name, value);
			return this;
		}

		public JsonHttpRequesBuilder add(String name, int value) {
			jsonObjectBuilder.add(name, value);
			return this;
		}

		public JsonHttpRequesBuilder add(String name, long value) {
			jsonObjectBuilder.add(name, value);
			return this;
		}

		public JsonHttpRequesBuilder add(String name, double value) {
			jsonObjectBuilder.add(name, value);
			return this;
		}

		public JsonHttpRequesBuilder add(String name, boolean value) {
			jsonObjectBuilder.add(name, value);
			return this;
		}

		public JsonHttpRequesBuilder addNull(String name) {
			jsonObjectBuilder.addNull(name);
			return this;
		}

		public JsonHttpRequesBuilder add(String name, JsonObjectBuilder builder) {
			jsonObjectBuilder.add(name, builder);
			return this;
		}

		public JsonHttpRequesBuilder add(String name, JsonArrayBuilder builder) {
			jsonObjectBuilder.add(name, builder);
			return this;
		}

		private HttpUriRequest createJsonRequest() {
			JsonObject json = jsonObjectBuilder.build();
			HttpPost req = new HttpPost(url);
			req.setEntity(new StringEntity(json.toString(), JSON));
			return req;
		}

		private Optional<JsonObject> getJsonContent(CloseableHttpResponse response) throws ParseException, IOException {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				JsonObject object = Json.createReader(entity.getContent()).readObject();
				log.debug(object.toString());
				return Optional.of(object);
			}
			return Optional.empty();
		}

		public Optional<JsonObject> exec() {
			try (CloseableHttpResponse response = httpclient.execute(createJsonRequest())) {
				return getJsonContent(response);
			} catch (IOException e) {
				log.error("Error:", e);
			}
			return Optional.empty();
		}

	}

	private CloseableHttpClient httpclient = HttpClients.createDefault();

	private static final ContentType JSON = ContentType.create("application/json");

	public JsonHttpRequesBuilder createRequest(String url) {
		return new JsonHttpRequesBuilder(url, httpclient);
	}

}
