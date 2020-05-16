package ru.penkrat.ttrssclient.ui.articleview;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.ViewModel;
import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.binding.PipeBinding;
import ru.penkrat.ttrssclient.binding.Subscription;
import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.service.generic.FunctionService;
import ru.penkrat.ttrssclient.ui.articles.ArticleScope;

@Component
public class ArticleViewModel implements ViewModel {

	private final ObservableValue<String> selectedArticleContent;

	private final StringProperty selectedArticleTitle = new SimpleStringProperty();

	private final StringProperty selectedArticleLink = new SimpleStringProperty();

	private final FunctionService<Article, String> loadArticleContentService;

	Subscription articleSelectionSubscription;

	private HostServices hostServices;

	private FunctionService<String, Map.Entry<Integer, String>> ampSearcheService;

	@Inject
	public ArticleViewModel(TTRSSClient client, ArticleScope articleScope, HostServices hostServices,
			HtmlContentWrapper contentWrapper, AmpSearcher ampSearcher) {
		this.hostServices = hostServices;

		loadArticleContentService = new FunctionService<>(client::getContent);
		selectedArticleContent = contentWrapper.bind(loadArticleContentService.valueProperty(),
				articleScope.selectedArticleProperty());

		articleSelectionSubscription = PipeBinding.of(articleScope.selectedArticleProperty())
				.subscribe(article -> {
					loadArticleContentService.restart(article);
					selectedArticleTitle.set(article.getTitle());
					selectedArticleLink.set(article.getLink());
				});

		ampSearcheService = new FunctionService<>(link -> {
			try {
				return ampSearcher.findLink(link);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
		ampSearcheService.setOnSucceeded(e -> {
			final var result = ampSearcheService.getValue();
			if (result.getKey() == 0) {
				publish("open_url", result.getValue());
			} else {
				publish("set_content", result.getValue());
			}
		});
	}

	public final ObservableValue<String> selectedArticleContentProperty() {
		return this.selectedArticleContent;
	}

	public final StringProperty selectedArticleTitleProperty() {
		return this.selectedArticleTitle;
	}

	public final StringProperty selectedArticleLinkProperty() {
		return this.selectedArticleLink;
	}

	public void openInBrowser() {
		if (selectedArticleLink.get() != null) {
			hostServices.showDocument(selectedArticleLink.get());
		}
	}

	public void showAMP() {
		ampSearcheService.restart(selectedArticleLink.get());
	}
}
