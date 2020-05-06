package ru.penkrat.ttrssclient.ui.login;

import java.util.prefs.Preferences;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.service.generic.SupplierService;
import ru.penkrat.ttrssclient.ui.settings.SettingsService;

@Component
public class LoginManager implements ViewModel {

	private final BooleanProperty isLoggedIn = new SimpleBooleanProperty(false);

	private final TTRSSClient client;
	private final SettingsService settings;

	private final Preferences prefs = Preferences.userNodeForPackage(LoginManager.class);
	private final StringProperty username = new SimpleStringProperty("");
	private final StringProperty password = new SimpleStringProperty("");

	private final SupplierService<Boolean> checkLoginService;

	@Inject
	public LoginManager(TTRSSClient client, SettingsService settings) {
		this.client = client;
		this.settings = settings;
		client.setUrl(settings.urlProperty().getValue());
		settings.urlProperty().addListener((ov, o, n) -> client.setUrl(n));

		checkLoginService = new SupplierService<Boolean>(client::checkLogin);
		checkLoginService.valueProperty().addListener((ov, o, n) -> isLoggedIn.setValue(n));
	}

	public final BooleanProperty isLoggedInProperty() {
		return isLoggedIn;
	}

	public boolean isLoggedIn() {
		return isLoggedIn.get();
	}

	public ReadOnlyObjectProperty<Boolean> tryLoginWithSavedCredentionals() {
		String sid = prefs.get("sid", "");
		if (sid.isBlank()) {
			isLoggedIn.setValue(false);
			return new SimpleObjectProperty<Boolean>(true);
		}
		client.setSid(sid);
		Platform.runLater(() -> checkLoginService.restart());
		return checkLoginService.valueProperty();
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
