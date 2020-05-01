package ru.penkrat.ttrssclient.ui.login;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import javax.inject.Inject;

import org.controlsfx.control.Notifications;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.view.renderer.FormRenderer;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

@Component
@Scope(SCOPE_PROTOTYPE)
public class LoginDialogView extends Dialog<ButtonType> {

	@Inject
	public LoginDialogView(LoginManager viewModel) {
		viewModel.prepare();
		Form form = Form.of(
				Group.of(
						Field.ofStringType(viewModel.usernameProperty()).required(true).label("Username").span(6),
						Field.ofPasswordType(viewModel.passwordProperty()).required(true).label("Password").span(6)))
				.title("Login")
				.binding(BindingMode.CONTINUOUS);
		getDialogPane().setContent(new FormRenderer(form));

		ButtonType loginButtonType = new javafx.scene.control.ButtonType("Login", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
		Button loginButton = (Button) getDialogPane().lookupButton(loginButtonType);
		loginButton.disableProperty().bind(form.validProperty().not());
		loginButton.addEventFilter(ActionEvent.ACTION, event -> {
			if (viewModel.checkLoginData()) {
				viewModel.handleLoginSuccess();
			} else {
				event.consume();
				Notifications.create()
						.title("Произошла ошибка")
						.text("Неправильный логин или пароль")
						.position(Pos.BOTTOM_CENTER)
						.darkStyle()
						.showError();
			}
		});
	}

	public LoginDialogView withOwner(Window window) {
		this.initOwner(window);
		return this;
	}
}
