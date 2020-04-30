package ru.penkrat.ttrssclient.ui.articleview;

import javax.inject.Inject;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;
import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.ViewModel;
import javafx.application.HostServices;
import javafx.beans.binding.Binding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.service.generic.FunctionService;
import ru.penkrat.ttrssclient.ui.articles.ArticleScope;

@Component
public class ArticleViewModel implements ViewModel {

	private final Binding<String> selectedArticleContent;

	private final StringProperty selectedArticleTitle = new SimpleStringProperty();

	private final StringProperty selectedArticleLink = new SimpleStringProperty();

	private final FunctionService<Article, String> loadArticleContentService;

	Subscription articleSelectionSubscription;

	private HostServices hostServices;

	@Inject
	public ArticleViewModel(TTRSSClient client, ArticleScope articleScope, HostServices hostServices, HtmlContentWrapper contentWrapper) {
		this.hostServices = hostServices;
		
		loadArticleContentService = new FunctionService<>(client::getContent);
		selectedArticleContent = contentWrapper.bind(loadArticleContentService.valueProperty(), articleScope.selectedArticleProperty());

		articleSelectionSubscription = EasyBind.subscribe(articleScope.selectedArticleProperty(), article -> {
			if (article != null) {
				loadArticleContentService.restart(article);
				selectedArticleTitle.set(article.getTitle());
				selectedArticleLink.set(article.getLink());
			}
		});
	}

	public final Binding<String> selectedArticleContentProperty() {
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
			hostServices.showDocument(selectedArticleLink.get());
	}
}
