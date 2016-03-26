package ru.penkrat.ttrssclient.ui.viewmodel;

import javax.inject.Inject;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.App;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.service.generic.FunctionService;
import ru.penkrat.ttrssclient.ui.articles.ArticleScope;
import ru.penkrat.ttrssclient.ui.feedstree.FeedScope;
import ru.penkrat.ttrssclient.ui.login.LoginManager;

public class MainViewModel implements ViewModel {

	private StringProperty status = new SimpleStringProperty("");

	private final StringProperty selectedArticleContent = new SimpleStringProperty();

	private final StringProperty selectedArticleTitle = new SimpleStringProperty();

	private final StringProperty selectedArticleLink = new SimpleStringProperty();

	private final FunctionService<Article, String> loadArticleContentService;

	Subscription articleSelectionSubscription;

	private LoginManager loginManager;

	@Inject
	public MainViewModel(TTRSSClient client, FeedScope feedScope, ArticleScope articleScope,
			LoginManager loginManager) {
		this.loginManager = loginManager;

		loadArticleContentService = new FunctionService<>(client::getContent);
		loadArticleContentService.setOnSucceeded(t -> {
			selectedArticleContent.set(loadArticleContentService.getValue());
		});

		articleSelectionSubscription = EasyBind.subscribe(articleScope.selectedArticleProperty(), article -> {
			if (article != null) {
				loadArticleContentService.restart(article);
				selectedArticleTitle.set(article.getTitle());
				selectedArticleLink.set(article.getLink());
			}
		});

		status.bind(feedScope.loadingMessageProperty().concat(articleScope.loadingListMessageProperty()));

		loginManager.tryLoginWithSavedCredentionals();
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

	public void update() {
		if (loginManager.tryLoginWithSavedCredentionals()) {
			NotificationCenterFactory.getNotificationCenter().publish("UPDATE");
		} else {
			publish("showLoginDialog", loginManager.getSavedLoginData());
		}
	}

	public void acceptLoginData(LoginData loginData) {
		loginManager.acceptLoginData(loginData);
		NotificationCenterFactory.getNotificationCenter().publish("UPDATE");
	}

	public Boolean checkLoginData(LoginData loginData) {
		return loginManager.checkLoginData(loginData);
	}

	public void login() {
		publish("showLoginDialog", loginManager.getSavedLoginData());
	}

}
