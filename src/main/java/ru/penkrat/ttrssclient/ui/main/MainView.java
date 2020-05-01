package ru.penkrat.ttrssclient.ui.main;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import ru.penkrat.ttrssclient.ui.Utils;
import ru.penkrat.ttrssclient.ui.login.LoginDialogView;

@Component
public class MainView implements FxmlView<MainViewModel>, Initializable {

	@FXML
	Label statusLabel;

	@InjectViewModel
	MainViewModel viewModel;

	@Inject
	private ApplicationContext appContext;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		statusLabel.textProperty().bind(viewModel.statusProperty());

		viewModel.subscribe("showLoginDialog", (key, payload) -> {
			appContext.getBean(LoginDialogView.class)
					.withOwner(statusLabel.getScene().getWindow())
					.showAndWait();
		});
	}

	@FXML
	public void onUpdate() {
		viewModel.update();
	}

	@FXML
	public void onLogin() {
		viewModel.login();
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
		NotificationCenterFactory.getNotificationCenter().publish("SHOW_SETTINGS", Utils.parentWindowFromEvent(event));
	}
}
