package ru.penkrat.ttrssclient.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.domain.Category;
import ru.penkrat.ttrssclient.domain.Feed;
import ru.penkrat.ttrssclient.domain.LoginData;

public class TTRSSClient {

	private static final Logger log = LoggerFactory.getLogger(TTRSSClient.class);

	private LoginData loginData;

	private String sid;

	private JsonHttpClient httpclient = new JsonHttpClient();

	public TTRSSClient() {

	}

	public void setLoginData(LoginData loginData) {
		this.loginData = loginData;
	}

	public LoginData getLoginData() {
		return loginData;
	}

	private String getApiUrl() {
		return loginData.getUrl() + "/api/";
	}

	public boolean login() {
		if (loginData == null)
			return false;

		sid = null;

		httpclient.createRequest(getApiUrl()).add("op", "login") //
				.add("user", loginData.getUsername()) //
				.add("password", loginData.getPassword()) //
				.exec().ifPresent(json -> {
					sid = json.getJsonObject("content").getString("session_id");
				});

		if (sid == null)
			log.error("Login error.");

		return sid != null;
	}

	public void getFeedTree() {
		httpclient.createRequest(getApiUrl()).add("sid", sid) // session_id
				.add("op", "getFeedTree") // operation
				.add("include_empty", "false") //
				.exec().ifPresent(json -> {
					// TODO
					// json.getJsonArray("content")
					});
	}

	public List<Category> getCategories() {
		JsonArray jsonArray = httpclient.createRequest(getApiUrl()).add("sid", sid) // session_id
				.add("op", "getCategories") // operation
				.add("unread_only", "false") //
				.add("enable_nested", "false") //
				.add("include_empty", "false") //
				.exec().map(js -> js.getJsonArray("content")).get();
		return jsonArray.stream().map(Category::new).collect(Collectors.toList());
	}

	public List<Feed> getFeeds(Integer categoryId) {
		JsonArray jsonArray = httpclient.createRequest(getApiUrl()).add("sid", sid) // session_id
				.add("op", "getFeeds") // operation
				.add("cat_id", categoryId) //
				.add("unread_only", "false") //
				.add("limit", "10") //
				.add("offset", "0") //
				.add("include_nested", "false") //
				.exec().map(js -> js.getJsonArray("content")).get();

		return jsonArray.stream().map(Feed::new).collect(Collectors.toList());
	}

	public List<Article> getHeadlines(Integer feedId, int skip) {
		JsonArray jsonArray = httpclient.createRequest(getApiUrl()).add("sid", sid) // session_id
				.add("op", "getHeadlines") // operation
				.add("feed_id", feedId) //
				.add("limit", "25") //
				.add("skip", skip) //
				.add("filter", "") //
				.add("is_cat", "false") //
				.add("show_excerpt", "false") //
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
		return httpclient.createRequest(getApiUrl()).add("sid", sid)
				.add("op", "getArticle")
				.add("article_id", id)
				.exec().map(js -> js.getJsonArray("content")).orElse(Json.createArrayBuilder().build()).stream()
				.findFirst();
	}
	
	public String getContent(Article article) {
		if (article.getContent() == null)
			getArticle(article.getId()).ifPresent(article::update);
		return article.getContent();
	}

	public String getIconURL(int id) {
		return getApiUrl() + "/feed-icons/" + id + ".ico";
	}

	public void updateArticle(int id, int mode, int field) {
		httpclient.createRequest(getApiUrl()).add("sid", sid) // session_id
				.add("op", "updateArticle") // operation
				.add("article_ids", id) //
				.add("mode", mode) //
				.add("field", field) //
				.add("data", "") //
				.exec().isPresent();
	}

}
