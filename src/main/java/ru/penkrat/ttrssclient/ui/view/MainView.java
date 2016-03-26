package ru.penkrat.ttrssclient.ui.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.fxmisc.easybind.EasyBind;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.web.WebView;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.ui.LoginDialog;
import ru.penkrat.ttrssclient.ui.viewmodel.MainViewModel;

public class MainView implements FxmlView<MainViewModel>, Initializable {

	@FXML
	Label statusLabel;

	@FXML
	WebView webView;

	@InjectViewModel
	MainViewModel viewModel;

	@FXML
	Hyperlink link;

	@FXML
	public void onUpdate() {
		viewModel.update();
	}

	@FXML
	public void onOpenLink() {
		viewModel.openInBrowser();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		EasyBind.subscribe(viewModel.selectedArticleContentProperty(),
				content -> webView.getEngine().loadContent(content));

		link.textProperty().bind(viewModel.selectedArticleTitleProperty());
		link.setVisited(false);

		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(viewModel.selectedArticleLinkProperty());
		link.setTooltip(tooltip);

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
