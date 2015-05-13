package ru.penkrat.ttrssclient.domain;

import java.util.prefs.Preferences;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginData {

	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String URL_KEY = "url";

	private final StringProperty url = new SimpleStringProperty(this, URL_KEY);
	private final StringProperty username = new SimpleStringProperty(this, USERNAME_KEY);
	private final StringProperty password = new SimpleStringProperty(this, PASSWORD_KEY);

	public static LoginData load() {
		Preferences prefs = Preferences.userNodeForPackage(LoginData.class);
		return new LoginData(prefs.get(USERNAME_KEY, "admin"), prefs.get(PASSWORD_KEY, "admin"), prefs.get(URL_KEY,
				"http://tt-rss.domain"));
	}

	public LoginData() {
	}

	public LoginData(String username, String password, String url) {
		setUrl(url);
		setUsername(username);
		setPassword(password);
	}

	@Override
	public String toString() {
		return "LoginData [url=" + url + ", username=" + username + "]";
	}

	public final StringProperty urlProperty() {
		return this.url;
	}

	public final java.lang.String getUrl() {
		return this.urlProperty().get();
	}

	public final void setUrl(final java.lang.String url) {
		this.urlProperty().set(url);
	}

	public final StringProperty usernameProperty() {
		return this.username;
	}

	public final java.lang.String getUsername() {
		return this.usernameProperty().get();
	}

	public final void setUsername(final java.lang.String username) {
		this.usernameProperty().set(username);
	}

	public final StringProperty passwordProperty() {
		return this.password;
	}

	public final java.lang.String getPassword() {
		return this.passwordProperty().get();
	}

	public final void setPassword(final java.lang.String password) {
		this.passwordProperty().set(password);
	}

	public void save() {
		Preferences prefs = Preferences.userNodeForPackage(LoginData.class);
		prefs.put(USERNAME_KEY, this.getUsername());
		prefs.put(PASSWORD_KEY, this.getPassword());
		prefs.put(URL_KEY, this.getUrl());
	}

}
