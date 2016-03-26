package ru.penkrat.ttrssclient;

import java.util.List;

import com.google.inject.Module;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.penkrat.ttrssclient.ui.view.MainView;

public class App extends MvvmfxGuiceApplication {

	public static void main(String... args) {
		launch(args);
	}

	private static App instance;

	@Override
	public void startMvvmfx(final Stage stage) throws Exception {
		instance = this;
		stage.setTitle("Tiny-Tiny RSS FX");

		final Parent parent = FluentViewLoader.fxmlView(MainView.class).load().getView();

		Scene scene = new Scene(parent);
		scene.getStylesheets().add("/styles/styles.css");

		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void initGuiceModules(List<Module> modules) throws Exception {
	}

	public static Application getInstance() {
		return instance;
	}
}