package ru.penkrat.ttrssclient.ui.articleview;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class AmpSearcher {

	private HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();

	final Pattern linkPattern = Pattern.compile("<link(.+?)>", Pattern.DOTALL);
	final Pattern relPattern = Pattern.compile("rel=\"(.*?)\"", Pattern.DOTALL);
	final Pattern hrefPattern = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);

	public Map.Entry<Integer, String> findLink(String srcUrl) throws IOException, InterruptedException {
		HttpRequest httpReq = HttpRequest.newBuilder()
				.uri(URI.create(srcUrl))
				.GET()
				.build();

		HttpResponse<String> response = client.send(httpReq, BodyHandlers.ofString());

		final var body = response.body();
		Matcher matcher = linkPattern.matcher(body);
		while (matcher.find()) {
			Entry<String, String> relHref = parseRelHref(matcher.group(1));
			if (relHref != null && "amphtml".equalsIgnoreCase(relHref.getKey())) {
				return new AbstractMap.SimpleEntry<>(0, relHref.getValue());
			}
		}

		return new AbstractMap.SimpleEntry<>(1, body);
	}

	private Map.Entry<String, String> parseRelHref(String link) {
		Matcher m1 = relPattern.matcher(link);
		Matcher m2 = hrefPattern.matcher(link);

		if (m1.find() && m2.find()) {
			return new AbstractMap.SimpleEntry<>(m1.group(1), m2.group(1));
		}
		return null;
	}

}
