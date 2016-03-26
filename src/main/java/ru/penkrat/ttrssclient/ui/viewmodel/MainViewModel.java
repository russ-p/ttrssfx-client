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

public class MainViewModel implements ViewModel {

	private StringProperty status = new SimpleStringProperty("");

	private final StringProperty selectedArticleContent = new SimpleStringProperty();

	private final StringProperty selectedArticleTitle = new SimpleStringProperty();

	private final StringProperty selectedArticleLink = new SimpleStringProperty();

	private final FunctionService<Article, String> loadArticleContentService;

	Subscription articleSelectionSubscription;

	private final TTRSSClient client;

	@Inject
	public MainViewModel(TTRSSClient client, FeedScope feedScope, ArticleScope articleScope) {
		this.client = client;

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

}
