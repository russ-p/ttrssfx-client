package ru.penkrat.ttrssclient.ui.main;

import javax.inject.Inject;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.ui.articles.ArticleScope;
import ru.penkrat.ttrssclient.ui.articleview.HtmlContentWrapper;
import ru.penkrat.ttrssclient.ui.articleview.HtmlContentWrapper.Font;
import ru.penkrat.ttrssclient.ui.feedstree.FeedScope;
import ru.penkrat.ttrssclient.ui.login.LoginManager;

public class MainViewModel implements ViewModel {

	private StringProperty status = new SimpleStringProperty("");

	private LoginManager loginManager;

	private HtmlContentWrapper contentWrapper;

	private final ObservableList<Font> fontFamilies = FXCollections.observableArrayList(Font.values());

	private final ObservableList<String> fontSizes = FXCollections.observableArrayList(HtmlContentWrapper.FONT_SIZES);

	private final ObservableList<String> themes = FXCollections.observableArrayList(HtmlContentWrapper.THEMES);

	@Inject
	public MainViewModel(FeedScope feedScope, ArticleScope articleScope, LoginManager loginManager,
			HtmlContentWrapper contentWrapper) {
		this.loginManager = loginManager;
		this.contentWrapper = contentWrapper;
		status.bind(feedScope.loadingMessageProperty().concat(articleScope.loadingListMessageProperty()));
		loginManager.tryLoginWithSavedCredentionals();
	}

	public final StringProperty statusProperty() {
		return this.status;
	}

	public final Property<Font> fontFamilyProperty() {
		return contentWrapper.fontFamilyProperty();
	}

	public final StringProperty fontSizeProperty() {
		return contentWrapper.fontSizeProperty();
	}

	public final StringProperty themeProperty() {
		return contentWrapper.themeProperty();
	}

	public ObservableList<Font> getFontFamilies() {
		return fontFamilies;
	}

	public ObservableList<String> getFontSizes() {
		return fontSizes;
	}

	public ObservableList<String> getThemes() {
		return themes;
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
