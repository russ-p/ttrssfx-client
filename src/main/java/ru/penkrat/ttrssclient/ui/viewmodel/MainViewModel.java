package ru.penkrat.ttrssclient.ui.viewmodel;

import java.util.List;
import java.util.stream.Collectors;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.penkrat.ttrssclient.App;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.domain.Category;
import ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem;
import ru.penkrat.ttrssclient.domain.Feed;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.service.generic.BiFunctionService;
import ru.penkrat.ttrssclient.service.generic.FunctionService;
import ru.penkrat.ttrssclient.service.generic.SupplierService;
import ru.penkrat.ttrssclient.service.generic.delayed.DelayedConsumerService;

public class MainViewModel implements ViewModel {

	private StringProperty status = new SimpleStringProperty("");

	private final ObjectProperty<CategoryFeedTreeItem> selectedCategoryOrFeed = new SimpleObjectProperty<>(null);

	private final ObjectProperty<ArticleListItemViewModel> selectedArticle = new SimpleObjectProperty<>(null);

	private final StringProperty selectedArticleContent = new SimpleStringProperty();

	private final StringProperty selectedArticleTitle = new SimpleStringProperty();

	private final StringProperty selectedArticleLink = new SimpleStringProperty();

	private SupplierService<List<Category>> loadCategoriesService;

	private final ObservableList<CategoryFeedTreeItem> rootItems = FXCollections.observableArrayList();

	private final ObservableList<ArticleListItemViewModel> articles = FXCollections.observableArrayList();

	private FunctionService<Integer, List<Feed>> loadFeedsService;

	private BiFunctionService<Integer, Integer, List<Article>> loadArticlesService;

	private FunctionService<Article, String> loadArticleContentService;

	private DelayedConsumerService<ArticleListItemViewModel> markAsReadService;

	private BiFunctionService<Integer, Integer, List<Article>> loadAdditionalArticlesService;

	Subscription articleSelectionSubscription;

	private TTRSSClient client;

	public MainViewModel() {
		client = new TTRSSClient();

		loadCategoriesService = new SupplierService<>(client::getCategories, "Загружаю категории");
		loadCategoriesService.setOnSucceeded(t -> {
			rootItems.setAll(loadCategoriesService.getValue());
		});

		loadFeedsService = new FunctionService<>(client::getFeeds, "Загружаю фиды…");
		loadFeedsService.setOnSucceeded(t -> {
			List<Feed> feeds = loadFeedsService.getValue();
			getSelectedCategoryOrFeed().getChildren().setAll(feeds);
		});

		loadArticlesService = new BiFunctionService<>(client::getHeadlines, "Загружаю статьи…");
		loadArticlesService.setOnSucceeded(t -> {
			articles.setAll(loadArticlesService.getValue().stream().map(ArticleListItemViewModel::new)
					.collect(Collectors.toList()));
		});

		EasyBind.subscribe(selectedCategoryOrFeed, categoryOrFeed -> {
			if (categoryOrFeed instanceof Category) {
				if (categoryOrFeed.getChildren().isEmpty()) {
					loadFeedsService.restart(((Category) categoryOrFeed).getId());
				}
			} else if (categoryOrFeed instanceof Feed) {
				if (categoryOrFeed.getChildren().isEmpty()) {
					loadArticlesService.restart(((Feed) categoryOrFeed).getId(), 0);
				}
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
					loadAdditionalArticlesService.restart(((Feed) selectedCategoryOrFeed.getValue()).getId(),
							articles.size());
				}
			}
		});

		status.bind(loadCategoriesService.messageProperty().concat(loadFeedsService.messageProperty())
				.concat(loadArticlesService.messageProperty()));

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

	public ObservableList<CategoryFeedTreeItem> getRootItems() {
		return rootItems;
	}

	public final ObjectProperty<CategoryFeedTreeItem> selectedCategoryOrFeedProperty() {
		return this.selectedCategoryOrFeed;
	}

	public final ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem getSelectedCategoryOrFeed() {
		return this.selectedCategoryOrFeedProperty().get();
	}

	public final void setSelectedCategoryOrFeed(
			final ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem selectedCategoryOrFeed) {
		this.selectedCategoryOrFeedProperty().set(selectedCategoryOrFeed);
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
		loadCategoriesService.restart();
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

}
