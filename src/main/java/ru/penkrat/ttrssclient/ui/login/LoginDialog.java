package ru.penkrat.ttrssclient.ui.login;


import java.net.URL;

import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import ru.penkrat.ttrssclient.domain.LoginData;

public class LoginDialog extends Dialog<LoginData> {

	private final ButtonType loginButtonType;
	private final CustomTextField txUserName;
	private final CustomPasswordField txPassword;
	private CustomTextField txbaseUrl;

	public LoginDialog(final LoginData loginApplicationData, final Callback<LoginData, Boolean> authenticator) {
		final DialogPane dialogPane = getDialogPane();

		setTitle(getString("login.dlg.title"));
		dialogPane.setHeaderText(getString("login.dlg.header"));
		dialogPane.getStyleClass().add("login-dialog");
//		dialogPane.getStylesheets().add(
//				LoginDialog.class.getResource("/org/controlsfx/dialog/dialogs.css").toExternalForm());
		dialogPane.getStylesheets().add("/styles/styles.css");
		dialogPane.getButtonTypes().addAll(ButtonType.CANCEL);

		txUserName = (CustomTextField) TextFields.createClearableTextField();
//		txUserName.setLeft(new ImageView(getImage("/org/controlsfx/dialog/user.png")));
		txUserName.textProperty().bindBidirectional(loginApplicationData.usernameProperty());
		String userNameCation = getString("login.dlg.user.caption");
		txUserName.setPromptText(userNameCation);

		txPassword = (CustomPasswordField) TextFields.createClearablePasswordField();
//		txPassword.setLeft(new ImageView(getImage("/org/controlsfx/dialog/lock.png")));
		txPassword.textProperty().bindBidirectional(loginApplicationData.passwordProperty());
		String passwordCaption = getString("login.dlg.pswd.caption");
		txPassword.setPromptText(passwordCaption);

		txbaseUrl = (CustomTextField) TextFields.createClearableTextField();
		txbaseUrl.textProperty().bindBidirectional(loginApplicationData.urlProperty());

		Label lbMessage = new Label("");
		lbMessage.getStyleClass().addAll("message-banner");
		lbMessage.setVisible(false);

		final VBox content = new VBox(10);
		content.getChildren().add(lbMessage);
		content.getChildren().add(txUserName);
		content.getChildren().add(txPassword);
		content.getChildren().add(new HBox(8, new Label("URL:"), txbaseUrl));

		dialogPane.setContent(content);

		loginButtonType = new javafx.scene.control.ButtonType(getString("login.dlg.login.button"), ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(loginButtonType);
		Button loginButton = (Button) dialogPane.lookupButton(loginButtonType);
		loginButton.addEventFilter(ActionEvent.ACTION, event -> {
			try {
				if (authenticator != null) {
					authenticator.call(loginApplicationData);
				}
				lbMessage.setManaged(false);
				lbMessage.setVisible(false);
			} catch (Throwable ex) {
				lbMessage.setManaged(true);
				lbMessage.setVisible(true);
				lbMessage.setText(ex.getMessage());
				event.consume();
			}
		});

		ValidationSupport validationSupport = new ValidationSupport();
		Platform.runLater(() -> {
			String requiredFormat = "'%s' is required";
			validationSupport.registerValidator(txUserName,
					Validator.createEmptyValidator(String.format(requiredFormat, userNameCation)));
			validationSupport.registerValidator(txPassword,
					Validator.createEmptyValidator(String.format(requiredFormat, passwordCaption)));
			loginButton.disableProperty().bind(validationSupport.invalidProperty());
			txUserName.requestFocus();
		});

		setResultConverter(dialogButton -> dialogButton == loginButtonType ? loginApplicationData : null);
	}

	private String getString(String string) {
		return string;
	}

	private Image getImage(String resourceName) {
		URL url = LoginDialog.class.getResource(resourceName);
		return new Image(url.toString());
	}

}