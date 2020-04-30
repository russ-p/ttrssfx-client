package ru.penkrat.ttrssclient;

import javax.inject.Inject;

import org.fxmisc.easybind.EasyBind;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.spring.MvvmfxSpringApplication;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.penkrat.ttrssclient.domain.Feed;
import ru.penkrat.ttrssclient.ui.StylesManager;
import ru.penkrat.ttrssclient.ui.feedstree.FeedScope;
import ru.penkrat.ttrssclient.ui.main.MainView;

@SpringBootApplication
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
		stage.show();
	}

}