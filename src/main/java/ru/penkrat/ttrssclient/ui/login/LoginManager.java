package ru.penkrat.ttrssclient.ui.login;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.LoginData;

@Singleton
public class LoginManager {

	private final BooleanProperty isLoggedIn = new SimpleBooleanProperty(false);

	private TTRSSClient client;

	@Inject
	public LoginManager(TTRSSClient client) {
		this.client = client;
	}

	public final BooleanProperty isLoggedInProperty() {
		return isLoggedIn;
	}

	public boolean tryLoginWithSavedCredentionals() {
		LoginData loginData = LoginData.load();
		client.setLoginData(loginData);
		client.setSid(loginData.getSid());
		boolean result = client.checkLogin();
		isLoggedIn.setValue(result);
		return result;
	}

	public void acceptLoginData(LoginData loginData) {
		loginData.setSid(client.getSid());
		loginData.save();
	}

	public Boolean checkLoginData(LoginData ld) {
		client.setLoginData(ld);
		return client.login();
	}

	public LoginData getSavedLoginData() {
		return LoginData.load();
	}

	public boolean isLoggedIn() {
		return isLoggedIn.get();
	}

}
