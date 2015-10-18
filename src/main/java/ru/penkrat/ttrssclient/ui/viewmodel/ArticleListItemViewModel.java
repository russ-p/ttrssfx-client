package ru.penkrat.ttrssclient.ui.viewmodel;

import java.time.format.DateTimeFormatter;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import ru.penkrat.ttrssclient.domain.Article;

public class ArticleListItemViewModel implements ViewModel {

	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM HH:mm");

	private final Article article;

	private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper();

	private final StringProperty date = new SimpleStringProperty();

	private final BooleanProperty unread = new SimpleBooleanProperty();

	public ArticleListItemViewModel(Article article) {
		this.article = article;
		title.set(article.getTitle());
		unread.set(article.isUnread());
		date.set(dtf.format(article.getUpdated()));
	}

	public ObservableStringValue titleProperty() {
		return title.getReadOnlyProperty();
	}

	public Article getArticle() {
		return article;
	}

	public final BooleanProperty unreadProperty() {
		return this.unread;
	}

	public final StringProperty dateProperty() {
		return this.date;
	}

}
