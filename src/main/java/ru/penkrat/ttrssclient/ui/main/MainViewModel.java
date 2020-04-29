package ru.penkrat.ttrssclient.ui.main;

import javax.inject.Inject;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.ui.articles.ArticleScope;
import ru.penkrat.ttrssclient.ui.feedstree.FeedScope;
import ru.penkrat.ttrssclient.ui.login.LoginManager;

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
