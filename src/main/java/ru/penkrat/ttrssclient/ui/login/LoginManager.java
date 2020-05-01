package ru.penkrat.ttrssclient.ui.login;

import java.util.prefs.Preferences;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.ui.settings.SettingsService;

@Component
public class LoginManager implements ViewModel {

	private final BooleanProperty isLoggedIn = new SimpleBooleanProperty(false);

	private final TTRSSClient client;
	private final SettingsService settings;

	private final Preferences prefs = Preferences.userNodeForPackage(LoginManager.class);
	private final StringProperty username = new SimpleStringProperty("");
	private final StringProperty password = new SimpleStringProperty("");

	@Inject
	public LoginManager(TTRSSClient client, SettingsService settings) {
		this.client = client;
		this.settings = settings;
		client.setUrl(settings.urlProperty().getValue());
		settings.urlProperty().addListener((ov, o, n) -> client.setUrl(n));
	}

	public final BooleanProperty isLoggedInProperty() {
		return isLoggedIn;
	}

	public boolean isLoggedIn() {
		return isLoggedIn.get();
	}

	public boolean tryLoginWithSavedCredentionals() {
		String sid = prefs.get("sid", "");
		if (sid.isBlank()) {
			isLoggedIn.setValue(false);
			return false;
		}
		client.setSid(sid);
		boolean result = client.checkLogin();
		isLoggedIn.setValue(result);
		return result;
	}

	void handleLoginSuccess() {
		final var sid = client.getSid();
		if (sid != null) {
			prefs.put("sid", sid);
			isLoggedIn.setValue(true);
			NotificationCenterFactory.getNotificationCenter().publish("UPDATE");
		}
	}

	void prepare() {
		username.setValue(settings.getUsername());
		password.setValue(settings.getPassword());
	}

	Boolean checkLoginData() {
		final var isOk = client.login(username.getValue(), password.getValue());
		if (isOk) {
			handleLoginSuccess();
		}
		return isOk;
	}

	StringProperty usernameProperty() {
		return username;
	}

	StringProperty passwordProperty() {
		return password;
	}
}
