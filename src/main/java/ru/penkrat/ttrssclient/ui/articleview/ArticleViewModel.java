package ru.penkrat.ttrssclient.ui.articleview;

import javax.inject.Inject;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.App;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.service.generic.FunctionService;
import ru.penkrat.ttrssclient.ui.articles.ArticleScope;

public class ArticleViewModel implements ViewModel {

	private final StringProperty selectedArticleContent = new SimpleStringProperty();

	private final StringProperty selectedArticleTitle = new SimpleStringProperty();

	private final StringProperty selectedArticleLink = new SimpleStringProperty();

	private final FunctionService<Article, String> loadArticleContentService;

	Subscription articleSelectionSubscription;

	@Inject
	public ArticleViewModel(TTRSSClient client, ArticleScope articleScope) {
		loadArticleContentService = new FunctionService<>(client::getContent);
		loadArticleContentService.setOnSucceeded(t -> {
			selectedArticleContent.set(loadArticleContentService.getValue());
		});

		articleSelectionSubscription = EasyBind.subscribe(articleScope.selectedArticleProperty(), article -> {
			if (article != null) {
				loadArticleContentService.restart(article);
				selectedArticleTitle.set(article.getTitle());
				selectedArticleLink.set(article.getLink());
			}
		});
	}

	public final StringProperty selectedArticleContentProperty() {
		return this.selectedArticleContent;
	}

	public final StringProperty selectedArticleTitleProperty() {
		return this.selectedArticleTitle;
	}

	public final StringProperty selectedArticleLinkProperty() {
		return this.selectedArticleLink;
	}

	public void openInBrowser() {
		if (selectedArticleLink.get() != null)
			App.getInstance().getHostServices().showDocument(selectedArticleLink.get());
	}
}
