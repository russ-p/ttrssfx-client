package ru.penkrat.ttrssclient.domain;

import java.util.prefs.Preferences;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginData {

	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String URL_KEY = "url";

	private static final String SID = "sid";
	private static final String SAVE_PASS = "save_pass";

	private final StringProperty url = new SimpleStringProperty(this, URL_KEY);
	private final StringProperty username = new SimpleStringProperty(this, USERNAME_KEY);
	private final StringProperty password = new SimpleStringProperty(this, PASSWORD_KEY);

	private final StringProperty sid = new SimpleStringProperty(this, SID);
	private final BooleanProperty savePass = new SimpleBooleanProperty(this, SAVE_PASS, false);

	public static LoginData load() {
		Preferences prefs = Preferences.userNodeForPackage(LoginData.class);
		return new LoginData(prefs.get(USERNAME_KEY, "admin"), prefs.get(PASSWORD_KEY, "admin"),
				prefs.get(URL_KEY, "http://tt-rss.domain"), prefs.get(SID, ""));
	}

	public LoginData() {
	}

	public LoginData(String username, String password, String url) {
		setUrl(url);
		setUsername(username);
		setPassword(password);
	}

	public LoginData(String username, String password, String url, String sid) {
		setUrl(url);
		setUsername(username);
		setPassword(password);
		setSid(sid);
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
		prefs.put(SID, this.getSid());
		prefs.put(SAVE_PASS, this.isSavePass() ? "1" : "0");
	}

	public final StringProperty sidProperty() {
		return this.sid;
	}

	public final java.lang.String getSid() {
		return this.sidProperty().get();
	}

	public final void setSid(final java.lang.String sid) {
		this.sidProperty().set(sid);
	}

	public final BooleanProperty savePassProperty() {
		return this.savePass;
	}

	public final boolean isSavePass() {
		return this.savePassProperty().get();
	}

	public final void setSavePass(final boolean savePass) {
		this.savePassProperty().set(savePass);
	}

}
