package ru.penkrat.ttrssclient.ui;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import ru.penkrat.ttrssclient.domain.Article;

public class ArticleCellController implements Initializable {

	private final static String CSS_UNREAD = "list-cell-unread";

	@FXML
	private Label date;

	@FXML
	private Label title;

	@FXML
	private AnchorPane pane;
	
	public ArticleCellController() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ArticleCell.fxml"));
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ArticleCellController setArticle(Article article) {
		title.setText(article.getTitle());
		if (article.isUnread())
			title.getStyleClass().add(CSS_UNREAD);
		else
			title.getStyleClass().remove(CSS_UNREAD);

		date.setText(DateTimeFormatter.ISO_DATE_TIME.format(article.getUpdated()));
		return this;
	}

	public Node getGraphic() {
		pane.getStylesheets().add("/styles/styles.css");
		return pane;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		date.getStyleClass().add("list-cell-small");
	}
}
