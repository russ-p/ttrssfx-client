package ru.penkrat.ttrssclient.ui.settings;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.view.renderer.FormRenderer;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import ru.penkrat.ttrssclient.ui.settings.SettingsService.Font;

@Component
@Scope(SCOPE_PROTOTYPE)
public class SettingsViewDialog extends Dialog<ButtonType> {

	private ListProperty<Font> fontFamilies = new SimpleListProperty<>(
			FXCollections.observableArrayList(Font.values()));
	private ListProperty<String> fontSizes = new SimpleListProperty<>(
			FXCollections.observableArrayList(SettingsService.FONT_SIZES));
	private ListProperty<String> themes = new SimpleListProperty<>(
			FXCollections.observableArrayList(SettingsService.THEMES));

	@Inject
	public SettingsViewDialog(SettingsService viewModel) {
		final Form form = Form.of(
				Group.of(
						Field.ofStringType(viewModel.urlProperty()).label("TT-RSS URL").span(12),
						Field.ofStringType(viewModel.getUsername()).label("Username").span(6),
						Field.ofPasswordType(viewModel.getPassword()).label("Password").tooltip("Leave blank").span(6)),
				Group.of(
						Field.ofBooleanType(viewModel.darkModeProperty()).label("Dark mode").span(6)),
				Group.of(
						Field.ofSingleSelectionType(themes, viewModel.themeProperty())
								.label("Theme").span(12),
						Field.ofSingleSelectionType(fontFamilies, viewModel.fontFamilyProperty())
								.label("Font Family")
								.span(6),
						Field.ofSingleSelectionType(fontSizes, viewModel.fontSizeProperty())
								.label("Font Size")
								.span(6)))
				.title("Settings")
				.binding(BindingMode.CONTINUOUS);

		VBox content = new FormRenderer(form);
		content.setPadding(new javafx.geometry.Insets(8));

		getDialogPane().setContent(content);
		getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		setTitle(form.getTitle());
		Button closeButton = (Button) getDialogPane().lookupButton(ButtonType.CLOSE);
		closeButton.addEventHandler(ActionEvent.ACTION, evt -> viewModel.storeSettings());
	}

	public SettingsViewDialog withOwner(Window window) {
		this.initOwner(window);
		return this;
	}
}
