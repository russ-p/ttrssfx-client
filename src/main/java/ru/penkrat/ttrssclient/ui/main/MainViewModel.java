package ru.penkrat.ttrssclient.ui.main;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.ui.articles.ArticleScope;
import ru.penkrat.ttrssclient.ui.feedstree.FeedScope;
import ru.penkrat.ttrssclient.ui.login.LoginManager;

@Component
public class MainViewModel implements ViewModel {

	private StringProperty status = new SimpleStringProperty("");

	private LoginManager loginManager;

	@Inject
	public MainViewModel(FeedScope feedScope, ArticleScope articleScope, LoginManager loginManager) {
		this.loginManager = loginManager;
		status.bind(feedScope.loadingMessageProperty().concat(articleScope.loadingListMessageProperty()));
		loginManager.tryLoginWithSavedCredentionals();
	}

	public final StringProperty statusProperty() {
		return this.status;
	}

	public void update() {
		if (loginManager.tryLoginWithSavedCredentionals()) {
			NotificationCenterFactory.getNotificationCenter().publish("UPDATE");
		} else {
			publish("showLoginDialog");
		}
	}

	public void login() {
		publish("showLoginDialog");
	}

}
