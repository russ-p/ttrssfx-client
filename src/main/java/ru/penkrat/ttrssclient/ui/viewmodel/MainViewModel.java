package ru.penkrat.ttrssclient.ui.viewmodel;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.penkrat.ttrssclient.App;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem;
import ru.penkrat.ttrssclient.domain.Feed;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.service.generic.BiFunctionService;
import ru.penkrat.ttrssclient.service.generic.FunctionService;
import ru.penkrat.ttrssclient.service.generic.delayed.DelayedConsumerService;
import ru.penkrat.ttrssclient.ui.feedstree.FeedScope;

public class MainViewModel implements ViewModel {

	private StringProperty status = new SimpleStringProperty("");

	private final ObjectProperty<ArticleListItemViewModel> selectedArticle = new SimpleObjectProperty<>(null);

	private final StringProperty selectedArticleContent = new SimpleStringProperty();

	private final StringProperty selectedArticleTitle = new SimpleStringProperty();

	private final StringProperty selectedArticleLink = new SimpleStringProperty();

	private final ObservableList<ArticleListItemViewModel> articles = FXCollections.observableArrayList();

	private BiFunctionService<Integer, Integer, List<Article>> loadArticlesService;

	private FunctionService<Article, String> loadArticleContentService;

	private DelayedConsumerService<ArticleListItemViewModel> markAsReadService;

	private BiFunctionService<Integer, Integer, List<Article>> loadAdditionalArticlesService;

	Subscription articleSelectionSubscription;

	private TTRSSClient client;

	private ObjectProperty<Feed> selectedFeedProperty;

	@Inject
	public MainViewModel(TTRSSClient client, FeedScope feedScope) {
		this.client = client;
		selectedFeedProperty = feedScope.selectedFeedProperty();

		loadArticlesService = new BiFunctionService<>(client::getHeadlines, "Загружаю статьи…");
		loadArticlesService.setOnSucceeded(t -> {
			articles.setAll(loadArticlesService.getValue().stream().map(ArticleListItemViewModel::new)
					.collect(Collectors.toList()));
		});

		EasyBind.subscribe(selectedFeedProperty, feed -> {
			if (feed != null) {
				loadArticlesService.restart(feed.getId(), 0);
			}
		});

		loadArticleContentService = new FunctionService<>(client::getContent);
		loadArticleContentService.setOnSucceeded(t -> {
			selectedArticleContent.set(loadArticleContentService.getValue());
		});
		markAsReadService = new DelayedConsumerService<>(articleModel -> {
			client.updateArticle(articleModel.getArticle().getId(), 0, 2);
			articleModel.getArticle().setUnread(false);
			articleModel.unreadProperty().set(false);
		} , "Как прочитано", 1000);
		markAsReadService.setOnSucceeded(t -> {
			// TODO:
		});

		loadAdditionalArticlesService = new BiFunctionService<>(client::getHeadlines, "Загружаю статьи…");
		loadAdditionalArticlesService.setOnSucceeded(t -> {
			articles.addAll(loadAdditionalArticlesService.getValue().stream().map(ArticleListItemViewModel::new)
					.collect(Collectors.toList()));
		});

		articleSelectionSubscription = EasyBind.subscribe(selectedArticle, articleModel -> {
			if (articleModel != null) {
				Article article = articleModel.getArticle();
				loadArticleContentService.restart(article);
				selectedArticleTitle.set(article.getTitle());
				selectedArticleLink.set(article.getLink());
				markAsReadService.restart(articleModel);

				if (articles.indexOf(articleModel) + 1 >= articles.size()) {
					preload();
				}
			}
		});

		status.bind(feedScope.loadingMessageProperty().concat(loadArticlesService.messageProperty()));

		tryLogin();
	}

	public final StringProperty statusProperty() {
		return this.status;
	}

	public final java.lang.String getStatus() {
		return this.statusProperty().get();
	}

	public final void setStatus(final java.lang.String status) {
		this.statusProperty().set(status);
	}

	public ObservableList<ArticleListItemViewModel> getArticles() {
		return articles;
	}

	public final ObjectProperty<ArticleListItemViewModel> selectedArticleProperty() {
		return this.selectedArticle;
	}

	public final StringProperty selectedArticleContentProperty() {
		return this.selectedArticleContent;
	}

	public final java.lang.String getSelectedArticleContent() {
		return this.selectedArticleContentProperty().get();
	}

	public final void setSelectedArticleContent(final java.lang.String selectedArticleContent) {
		this.selectedArticleContentProperty().set(selectedArticleContent);
	}

	public final StringProperty selectedArticleTitleProperty() {
		return this.selectedArticleTitle;
	}

	public final java.lang.String getSelectedArticleTitle() {
		return this.selectedArticleTitleProperty().get();
	}

	public final void setSelectedArticleTitle(final java.lang.String selectedArticleTitle) {
		this.selectedArticleTitleProperty().set(selectedArticleTitle);
	}

	public final StringProperty selectedArticleLinkProperty() {
		return this.selectedArticleLink;
	}

	public final java.lang.String getSelectedArticleLink() {
		return this.selectedArticleLinkProperty().get();
	}

	public final void setSelectedArticleLink(final java.lang.String selectedArticleLink) {
		this.selectedArticleLinkProperty().set(selectedArticleLink);
	}

	public void openInBrowser() {
		if (getSelectedArticleLink() != null)
			App.getInstance().getHostServices().showDocument(getSelectedArticleLink());
	}

	private void doUpdate() {
		NotificationCenterFactory.getNotificationCenter().publish("UPDATE");
	}

	public void update() {
		LoginData loginData = LoginData.load();
		client.setLoginData(loginData);
		client.setSid(loginData.getSid());
		if (client.checkLogin()) {
			doUpdate();
		} else {
			publish("showLoginDialog", loginData);
		}
	}

	private void tryLogin() {
		LoginData loginData = LoginData.load();
		client.setLoginData(loginData);
		client.setSid(loginData.getSid());
		if (client.checkLogin()) {
			doUpdate();
		}
	}

	public void acceptLoginData(LoginData loginData) {
		loginData.setSid(client.getSid());
		loginData.save();
		doUpdate();
	}

	public Boolean checkLoginData(LoginData ld) {
		client.setLoginData(ld);
		return client.login();
	}

	public void login() {
		LoginData loginData = LoginData.load();
		publish("showLoginDialog", loginData);
	}

	public String getIconUrl(CategoryFeedTreeItem catFeedItem) {
		if (catFeedItem instanceof Feed) {
			Feed feed = (Feed) catFeedItem;
			return client.getIconURL(feed.getId());
		}
		return null;
	}

	public void preload() {
		loadAdditionalArticlesService.restart(selectedFeedProperty.getValue().getId(), articles.size());
	}

}
