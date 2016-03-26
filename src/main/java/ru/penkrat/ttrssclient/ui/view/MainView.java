package ru.penkrat.ttrssclient.ui.view;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.ui.login.LoginDialog;
import ru.penkrat.ttrssclient.ui.viewmodel.MainViewModel;

public class MainView implements FxmlView<MainViewModel>, Initializable {

	@FXML
	Label statusLabel;

	@InjectViewModel
	MainViewModel viewModel;

	@FXML
	public void onUpdate() {
		viewModel.update();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		statusLabel.textProperty().bind(viewModel.statusProperty());

		viewModel.subscribe("showLoginDialog", (key, payload) -> {
			LoginData loginData = (LoginData) payload[0];
			LoginDialog loginDialog = new LoginDialog(loginData, (ld) -> {
				return viewModel.checkLoginData(ld);
			});
			loginDialog.showAndWait().ifPresent(viewModel::acceptLoginData);
		});
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
}
