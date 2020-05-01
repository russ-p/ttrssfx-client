package ru.penkrat.ttrssclient.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.json.Json;
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
	private String apiUrl = "";

	private String sid;

	private JsonHttpClient httpclient = new JsonHttpClient();

	public TTRSSClient() {
	}

	public void setUrl(String url) {
		if (url == null)
			return;
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 2);
		}
		this.url = url;
		this.apiUrl = url + "/api/";
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public boolean checkLogin() {
		return httpclient.createRequest(apiUrl).add("op", "getVersion") //
				.add("sid", sid) // session_id
				.exec().map(json -> json.getInt("status")).map(status -> status == 0).orElse(false);
	}

	public boolean login(String username, String password) {
		sid = null;

		httpclient.createRequest(apiUrl).add("op", "login") //
				.add("user", username) //
				.add("password", password) //
				.exec().ifPresent(json -> {
					final var content = json.getJsonObject("content");
					if (!content.containsKey("error")) {
						sid = content.getString("session_id");
					} else {
						log.error("op: login, result = {}", content.getString("error"));
					}
				});

		return sid != null;
	}

	public void getFeedTree() {
		httpclient.createRequest(apiUrl).add("sid", sid) // session_id
				.add("op", "getFeedTree") // operation
				.add("include_empty", "false") //
				.exec().ifPresent(json -> {
					// TODO
					// json.getJsonArray("content")
				});
	}

	public List<Category> getCategories() {
		JsonArray jsonArray = httpclient.createRequest(apiUrl).add("sid", sid) // session_id
				.add("op", "getCategories") // operation
				.add("unread_only", "false") //
				.add("enable_nested", "false") //
				.add("include_empty", "false") //
				.exec().map(js -> js.getJsonArray("content")).get();
		return jsonArray.stream().map(Category::new).collect(Collectors.toList());
	}

	public List<Feed> getFeeds(Integer categoryId) {
		JsonArray jsonArray = httpclient.createRequest(apiUrl).add("sid", sid) // session_id
				.add("op", "getFeeds") // operation
				.add("cat_id", categoryId) //
				.add("unread_only", "false") //
				.add("limit", "50") //
				.add("offset", "0") //
				.add("include_nested", "false") //
				.exec().map(js -> js.getJsonArray("content")).get();

		return jsonArray.stream().map(Feed::new).collect(Collectors.toList());
	}

	public List<Article> getHeadlines(Integer feedId, int skip) {
		JsonArray jsonArray = httpclient.createRequest(apiUrl).add("sid", sid) // session_id
				.add("op", "getHeadlines") // operation
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
				.exec().map(js -> js.getJsonArray("content")).get();

		return jsonArray.stream().map(Article::new).collect(Collectors.toList());
	}

	public Optional<JsonValue> getArticle(int id) {
		return httpclient.createRequest(apiUrl).add("sid", sid).add("op", "getArticle").add("article_id", id)
				.exec().map(js -> js.getJsonArray("content")).orElse(Json.createArrayBuilder().build()).stream()
				.findFirst();
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
		httpclient.createRequest(apiUrl).add("sid", sid) // session_id
				.add("op", "updateArticle") // operation
				.add("article_ids", id) //
				.add("mode", mode) //
				.add("field", field) //
				.add("data", "") //
				.exec().isPresent();
	}

}
