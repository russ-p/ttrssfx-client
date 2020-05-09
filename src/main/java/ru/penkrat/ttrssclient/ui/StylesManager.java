package ru.penkrat.ttrssclient.ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import javafx.scene.Scene;
import ru.penkrat.ttrssclient.binding.PipeBinding;
import ru.penkrat.ttrssclient.binding.Subscription;
import ru.penkrat.ttrssclient.ui.settings.SettingsService;

@Component
public class StylesManager {

	private SettingsService settings;

	private String commonCss = getClass().getResource("/styles/styles.css").toExternalForm();
	private String darkCss = getClass().getResource("/styles/dark.css").toExternalForm();
	private String scale150Css = getClass().getResource("/styles/150.css").toExternalForm();

	private List<Scene> scenes = new ArrayList<>();

	Subscription darkModeSub, scaledSub;

	@Inject
	public StylesManager(SettingsService settings) {
		this.settings = settings;

		scaledSub = PipeBinding.of(settings.scaledProperty())
				.subscribe( isScaled -> {
			for (Scene scene : scenes) {
				if (isScaled) {
					scene.getStylesheets().add(scale150Css);
				} else {
					scene.getStylesheets().remove(scale150Css);
				}
			}
		});
		darkModeSub = PipeBinding.of(settings.darkModeProperty())
				.subscribe(isDark -> {
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
		if (settings.scaledProperty().getValue()) {
			scene.getStylesheets().add(scale150Css);
		}
	}

}
