package ru.penkrat.ttrssclient.api;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

@Ignore
public class TTRSSClientTest {

	TTRSSClient client;

	@BeforeClass
	public static void before() {
		ch.qos.logback.classic.Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TTRSSClient.class);

		log.setLevel(Level.ALL);
	}

	@Before
	public void setup() {
		client = new TTRSSClient();
		client.setUrl("*");
		client.login("*", "*");
	}

	@Test
	public void testGetVersion() throws Exception {
		client.getVersion();
	}

}
