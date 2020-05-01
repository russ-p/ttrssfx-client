package ru.penkrat.ttrssclient.ui.main;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ru.penkrat.ttrssclient.ui.login.LoginDialogView;
import ru.penkrat.ttrssclient.ui.settings.SettingsViewDialog;

@Component
public class MainView implements FxmlView<MainViewModel>, Initializable {

	@FXML
	Label statusLabel;

	@FXML
	Button updateButton;

	@FXML
	Button loginButton;

	@FXML
	Button settingsButton;

	@FXML
	Button prevButton;

	@FXML
	Button nextButton;

	@InjectViewModel
	MainViewModel viewModel;

	@Inject
	private ApplicationContext appContext;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		statusLabel.textProperty().bind(viewModel.statusProperty());

		updateButton.setGraphic(new FontIcon());
		loginButton.setGraphic(new FontIcon());
		settingsButton.setGraphic(new FontIcon());
		prevButton.setGraphic(new FontIcon());
		nextButton.setGraphic(new FontIcon());

		viewModel.subscribe("showLoginDialog", (key, payload) -> {
			onLogin();
		});
	}

	@FXML
	public void onUpdate() {
		viewModel.update();
	}

	@FXML
	public void onLogin() {
		appContext.getBean(LoginDialogView.class)
				.withOwner(statusLabel.getScene().getWindow())
				.showAndWait();
	}

	@FXML
	public void onPrev() {
		NotificationCenterFactory.getNotificationCenter().publish("PREV_ARTICLE");
	}

	@FXML
	public void onNext() {
		NotificationCenterFactory.getNotificationCenter().publish("NEXT_ARTICLE");
	}

	@FXML
	public void onSettings(ActionEvent event) {
		appContext.getBean(SettingsViewDialog.class)
				.withOwner(statusLabel.getScene().getWindow())
				.showAndWait();
	}
}
