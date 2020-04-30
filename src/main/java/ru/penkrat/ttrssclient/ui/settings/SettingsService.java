package ru.penkrat.ttrssclient.ui.settings;

import java.util.prefs.Preferences;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.view.renderer.FormRenderer;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

@Component
public class SettingsService {

	public enum Font {
		PT_SANS("PT Sans", "https://fonts.googleapis.com/css?family=PT+Sans&subset=latin,cyrillic"),
		OPEN_SANS("Open Sans", "https://fonts.googleapis.com/css?family=Open+Sans&subset=latin,cyrillic"),
		UBUNTU("Ubuntu", "https://fonts.googleapis.com/css?family=Ubuntu&subset=latin,cyrillic"),
		NOTO_SANS("Noto Sans", "https://fonts.googleapis.com/css?family=Noto+Sans&subset=latin,cyrillic"),
		PHILOSOPHER("Philosopher",
				"https://fonts.googleapis.com/css?family=Philosopher:400,400italic,700,700italic&subset=latin,cyrillic"),
		ROBOTO("Roboto", "https://fonts.googleapis.com/css?family=Roboto&subset=latin,cyrillic"),
		;

		String name, link;

		Font(String name, String link) {
			this.name = name;
			this.link = link;
		}

		public String toString() {
			return name;
		}
	}

	public static final String[] FONT_SIZES = new String[] { "13px", "15px", "18px", "24px" };

	public static final String[] THEMES = new String[] { "Default", "Light", "Dark", "Solarized", "Solarized Dark",
			"Dracula" };

	private final ObjectProperty<Font> fontFamily = new SimpleObjectProperty<>(Font.OPEN_SANS);
	private final ObjectProperty<String> fontSize = new SimpleObjectProperty<>("15px");
	private final ObjectProperty<String> theme = new SimpleObjectProperty<>("Default");
	private final BooleanProperty darkMode = new SimpleBooleanProperty(false);

	private ListProperty<Font> fontFamilies = new SimpleListProperty<>(
			FXCollections.observableArrayList(Font.values()));
	private ListProperty<String> fontSizes = new SimpleListProperty<>(FXCollections.observableArrayList(FONT_SIZES));
	private ListProperty<String> themes = new SimpleListProperty<>(FXCollections.observableArrayList(THEMES));

	private Preferences prefs = Preferences.userNodeForPackage(SettingsService.class);

	@Inject
	public SettingsService() {
		fontFamily.setValue(Font.values()[prefs.getInt("fontFamily", 0)]);
		fontSize.setValue(prefs.get("fontSize", "15px"));
		theme.setValue(prefs.get("theme", "Default"));
		darkMode.setValue(prefs.getBoolean("darkMode", false));

		NotificationCenterFactory.getNotificationCenter().subscribe("SHOW_SETTINGS",
				(key, payload) -> this.showDialog((Window) payload[0]));
	}

	public void showDialog(Window owner) {
		final Form form = createForm();
		VBox content = new FormRenderer(form);
		content.setPadding(new javafx.geometry.Insets(8));

		Dialog<Void> dialog = new javafx.scene.control.Dialog<Void>();
		dialog.initOwner(owner);
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		dialog.setTitle(form.getTitle());
		dialog.setOnHidden(evt -> {
			prefs.putInt("fontFamily", fontFamily.getValue().ordinal());
			prefs.put("fontSize", fontSize.getValue());
			prefs.put("theme", theme.getValue());
			prefs.putBoolean("darkMode", darkMode.getValue());
		});
		dialog.showAndWait();
	}

	private Form createForm() {
		return Form.of(
				Group.of(
						Field.ofSingleSelectionType(themes, theme).label("Theme").span(12),

						Field.ofSingleSelectionType(fontFamilies, fontFamily).label("Font Family").span(6),

						Field.ofSingleSelectionType(fontSizes, fontSize).label("Font Size").span(6)),
				Group.of(
						Field.ofBooleanType(darkMode).label("Dark mode").span(6)))
				.title("Settings")
				.binding(BindingMode.CONTINUOUS);
	}

	public ObjectProperty<Font> fontFamilyProperty() {
		return this.fontFamily;
	}

	public ObjectProperty<String> fontSizeProperty() {
		return this.fontSize;
	}

	public ObjectProperty<String> themeProperty() {
		return this.theme;
	}

	public BooleanProperty darkModeProperty() {
		return this.darkMode;
	}

}
