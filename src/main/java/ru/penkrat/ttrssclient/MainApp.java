package ru.penkrat.ttrssclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.ui.LoginDialog;

public class MainApp extends Application {

	private static final Logger log = LoggerFactory.getLogger(MainApp.class);

	private static MainApp instance;

	private TTRSSClient client = new TTRSSClient();

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	public void start(Stage stage) throws Exception {
		instance = this;
		log.info("Starting JavaFX application");

		LoginData loginData = LoginData.load();
		LoginDialog loginDialog = new LoginDialog(loginData, (ld) -> {
			client.setLoginData(ld);
			return client.login();
		});

		if (loginDialog.showAndWait().isPresent()) {
			loginData.save();
			String fxmlFile = "/fxml/MainWindow.fxml";
			log.debug("Loading FXML for main view from: {}", fxmlFile);
			FXMLLoader loader = new FXMLLoader();
			Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

			log.debug("Showing JFX scene");
			Scene scene = new Scene(rootNode, 800, 600);
			scene.getStylesheets().add("/styles/styles.css");

			stage.setTitle("Tiny Tiny RSS");
			stage.setScene(scene);
			stage.show();
		}

	}

	public static MainApp getInstance() {
		return instance;
	}

	public TTRSSClient getClient() {
		return client;
	}

}
