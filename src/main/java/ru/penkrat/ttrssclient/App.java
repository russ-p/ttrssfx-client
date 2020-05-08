package ru.penkrat.ttrssclient;

import javax.inject.Inject;

import org.fxmisc.easybind.EasyBind;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.spring.MvvmfxSpringApplication;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ru.penkrat.ttrssclient.domain.Feed;
import ru.penkrat.ttrssclient.ui.StylesManager;
import ru.penkrat.ttrssclient.ui.feedstree.FeedScope;
import ru.penkrat.ttrssclient.ui.main.MainView;

@SpringBootApplication
@ComponentScan(basePackages = "ru.penkrat.ttrssclient")
public class App extends MvvmfxSpringApplication {

	@Inject
	private FeedScope feedScope;

	@Inject
	private StylesManager stylesManager;

	public static void main(String... args) {
		launch(args);
	}

	@Override
	public void startMvvmfx(final Stage stage) throws Exception {
		stage.titleProperty().bind(EasyBind.monadic(feedScope.selectedFeedProperty())
				.map(Feed::getTitle)
				.map(feedTitle -> "Tiny-Tiny RSS FX - " + feedTitle)
				.orElse("Tiny-Tiny RSS FX"));

		final Parent parent = FluentViewLoader.fxmlView(MainView.class).load().getView();

		Scene scene = new Scene(parent);
		stylesManager.registerScene(scene);
		stage.setScene(scene);

		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		stage.setWidth(screenBounds.getWidth());
		stage.setHeight(screenBounds.getHeight() - 50);
		stage.show();
	}

}