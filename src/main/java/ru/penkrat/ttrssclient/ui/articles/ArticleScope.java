package ru.penkrat.ttrssclient.ui.articles;

import javax.inject.Singleton;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.domain.Article;

@Singleton
public class ArticleScope {

	private final ObjectProperty<Article> selectedArticle = new SimpleObjectProperty<>(null);

	private final StringProperty loadingListMessage = new SimpleStringProperty();

	public final ObjectProperty<Article> selectedArticleProperty() {
		return this.selectedArticle;
	}

	public final ru.penkrat.ttrssclient.domain.Article getSelectedArticle() {
		return this.selectedArticleProperty().get();
	}

	public final void setSelectedArticle(final ru.penkrat.ttrssclient.domain.Article selectedArticle) {
		this.selectedArticleProperty().set(selectedArticle);
	}

	public final StringProperty loadingListMessageProperty() {
		return this.loadingListMessage;
	}

}
