package ru.penkrat.ttrssclient.ui.settings;

import java.util.prefs.Preferences;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
	private final BooleanProperty scaled = new SimpleBooleanProperty(false);
	private final StringProperty url = new SimpleStringProperty("http://example.com/tt-rss");
	private final StringProperty username = new SimpleStringProperty("");
	private final StringProperty password = new SimpleStringProperty("");



	private Preferences prefs = Preferences.userNodeForPackage(SettingsService.class);

	@Inject
	public SettingsService() {
		loadSettings();
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
	
	public BooleanProperty scaledProperty() {
		return this.scaled;
	}

	public StringProperty urlProperty() {
		return this.url;
	}

	public String getUsername() {
		return this.username.getValue();
	}

	public String getPassword() {
		return this.password.getValue();
	}

	private void loadSettings() {
		fontFamily.setValue(Font.values()[prefs.getInt("fontFamily", 0)]);
		fontSize.setValue(prefs.get("fontSize", "15px"));
		theme.setValue(prefs.get("theme", "Default"));
		darkMode.setValue(prefs.getBoolean("darkMode", false));
		scaled.setValue(prefs.getBoolean("scaled", false));
		url.setValue(prefs.get("url", url.getValue()));
		username.setValue(prefs.get("username", username.getValue()));
		password.setValue(prefs.get("password", password.getValue()));
	}

	void storeSettings() {
		prefs.putInt("fontFamily", fontFamily.getValue().ordinal());
		prefs.put("fontSize", fontSize.getValue());
		prefs.put("theme", theme.getValue());
		prefs.putBoolean("darkMode", darkMode.getValue());
		prefs.putBoolean("scaled", scaled.getValue());
		prefs.put("url", url.getValue());
		prefs.put("username", username.getValue());
		prefs.put("password", password.getValue()); // TODO: not safe
	}
}
