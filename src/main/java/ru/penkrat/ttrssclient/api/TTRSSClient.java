package ru.penkrat.ttrssclient.api;

import static ru.penkrat.ttrssclient.api.TTRSSRequestBody.op;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.domain.Category;
import ru.penkrat.ttrssclient.domain.Feed;

@Component
public class TTRSSClient {

	private static final Logger log = LoggerFactory.getLogger(TTRSSClient.class);

	private String url = "";
	private URI apiURI;

	private String sid;

	private HttpClient client = HttpClient.newBuilder().build();

	public TTRSSClient() {
	}

	public void setUrl(String url) {
		if (url == null)
			return;
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 2);
		}
		this.url = url;
		this.apiURI = URI.create(url + "/api/");
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	private TTRSSResponseBody exec(TTRSSRequestBody request) {
		HttpRequest httpReq = HttpRequest.newBuilder()
				.uri(apiURI)
				.POST(BodyPublishers.ofString(request.toString()))
				.setHeader("Content-Type", "application/json")
				.build();
		try {
			if (log.isDebugEnabled()) {
				log.debug("Send request to {}", httpReq.uri());
				log.debug(">| {}", request);
			}
			HttpResponse<String> response = client.send(httpReq, BodyHandlers.ofString());
			final var body = response.body();
			log.debug("<| {}", body);
			return TTRSSResponseBody.parse(body);
		} catch (IOException | InterruptedException e) {
			log.error("Request error", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	// API https://git.tt-rss.org/git/tt-rss/wiki/ApiReference

	public String getVersion() {
		return exec(op("getVersion").sid(sid))
				.getContentAsObject()
				.map(j -> j.getString("version")).orElse("");
	}

	public boolean login(String username, String password) {
		sid = null;

		return exec(op("login") //
				.add("user", username) //
				.add("password", password) //
		)
				.getContentAsObject()
				.map(content -> {
					if (!content.containsKey("error")) {
						sid = content.getString("session_id");
						return true;
					} else {
						log.error("op: login, result = {}", content.getString("error"));
						return false;
					}
				}).orElse(false);
	}

	public boolean checkLogin() {
		return exec(op("getVersion").sid(sid)).getStatus() == 0;
	}

	public void getFeedTree() {
		exec(op("getFeedTree").sid(sid).add("include_empty", "false"))
				.getContentAsArray()
				.ifPresent(json -> {
					// TODO
					// json.getJsonArray("content")
				});
	}

	public List<Category> getCategories() {
		return exec(op("getCategories").sid(sid)
				.add("unread_only", "false") //
				.add("enable_nested", "false") //
				.add("include_empty", "false") //
		).getContentAsArray()
				.map(data -> data.stream()
						.map(Category::new)
						.collect(Collectors.toList()))
				.orElseGet(Collections::emptyList);
	}

	public List<Feed> getFeeds(Integer categoryId) {
		return exec(op("getFeeds").sid(sid)
				.add("cat_id", categoryId) //
				.add("unread_only", "false") //
				.add("limit", "50") //
				.add("offset", "0") //
				.add("include_nested", "false") //
		).getContentAsArray()
				.map(data -> data.stream()
						.map(Feed::new)
						.collect(Collectors.toList()))
				.orElseGet(Collections::emptyList);
	}

	public List<Article> getHeadlines(Integer feedId, int skip) {
		return exec(op("getHeadlines").sid(sid)
				.add("feed_id", feedId) //
				.add("limit", "25") //
				.add("skip", skip) //
				.add("filter", "") //
				.add("is_cat", "false") //
				.add("show_excerpt", "true") //
				.add("excerpt_length", "256") // excerpt_length
				.add("show_content", "false") //
				.add("view_mode", "adaptive") //
				.add("include_attachments", "false") //
				.add("since_id", "") //
				.add("include_nested", "false") //
				.add("order_by", "") //
				.add("sanitize", "false") //
				.add("force_update", "false") //
				.add("has_sandbox", "false") //
		).getContentAsArray()
				.map(data -> data.stream()
						.map(Article::new)
						.collect(Collectors.toList()))
				.orElseGet(Collections::emptyList);
	}

	public Optional<JsonValue> getArticle(int id) {
		return exec(op("getArticle").sid(sid)
				.add("article_id", id) //
		).getContentAsArray()
				.flatMap((JsonArray data) -> data.stream().findFirst());
	}

	public String getContent(Article article) {
		if (article.getContent() == null)
			getArticle(article.getId()).ifPresent(article::update);
		return article.getContent();
	}

	public String getIconURL(int id) {
		return url + "/feed-icons/" + id + ".ico";
	}

	public String getIconURL(String id) {
		return url + "/feed-icons/" + id + ".ico";
	}

	public void updateArticle(int id, int mode, int field) {
		exec(op("getHeadlines").sid(sid)
				.add("article_ids", id) //
				.add("mode", mode) //
				.add("field", field) //
				.add("data", "") //
		).getStatus();
	}

}
