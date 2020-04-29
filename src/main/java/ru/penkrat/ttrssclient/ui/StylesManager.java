package ru.penkrat.ttrssclient.ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

import javafx.scene.Scene;
import ru.penkrat.ttrssclient.ui.settings.SettingsService;

@Singleton
public class StylesManager {

	private SettingsService settings;

	private String commonCss = getClass().getResource("/styles/styles.css").toExternalForm();
	private String darkCss = getClass().getResource("/styles/dark.css").toExternalForm();

	private List<Scene> scenes = new ArrayList<>();

	Subscription darkModeSub;

	@Inject
	public StylesManager(SettingsService settings) {
		this.settings = settings;

		darkModeSub = EasyBind.subscribe(settings.darkModeProperty(), isDark -> {
			for (Scene scene : scenes) {
				if (isDark) {
					scene.getStylesheets().add(darkCss);
				} else {
					scene.getStylesheets().remove(darkCss);
				}
			}
		});
	}

	public void registerScene(Scene scene) {
		scenes.add(scene);

		scene.getStylesheets().add(commonCss);
		if (settings.darkModeProperty().getValue()) {
			scene.getStylesheets().add(darkCss);
		}
	}

}
