package ru.penkrat.ttrssclient.api;

import org.junit.Test;

public class TTRSSRequestBodyTest {
	private final static String EXAMPLE = "{\"op\":\"getVersion\",\"sid\":\"1234567890abcdfe\",\"key\":\"0\"}";

	@Test
	public void testAddStringString() throws Exception {
		String json = TTRSSRequestBody.op("getVersion").sid("1234567890abcdfe").add("key", "0").toString();
		org.assertj.core.api.Assertions.assertThat(json).isEqualTo(EXAMPLE);
	}

	@Test
	public void testAddStringObject() throws Exception {
		String json = TTRSSRequestBody.op("getVersion").sid("1234567890abcdfe").add("key", 0).toString();
		org.assertj.core.api.Assertions.assertThat(json).isEqualTo(EXAMPLE);
	}

	@Test
	public void testToString() throws Exception {
		final var body = TTRSSRequestBody.op("getVersion").sid("1234567890abcdfe").add("key", "0");
		
		System.out.println(body); // implicit call toString()
		
		String json = body.toString();

		org.assertj.core.api.Assertions.assertThat(json).isEqualTo(EXAMPLE);
	}

}
