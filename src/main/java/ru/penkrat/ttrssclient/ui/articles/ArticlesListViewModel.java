package ru.penkrat.ttrssclient.ui.articles;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.binding.PipeBinding;
import ru.penkrat.ttrssclient.binding.Subscription;
import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.domain.Feed;
import ru.penkrat.ttrssclient.service.generic.BiFunctionService;
import ru.penkrat.ttrssclient.service.generic.delayed.DelayedConsumerService;
import ru.penkrat.ttrssclient.ui.feedstree.FeedScope;

@Component
public class ArticlesListViewModel implements ViewModel {

	private BiFunctionService<Integer, Integer, List<Article>> loadArticlesService;

	private BiFunctionService<Integer, Integer, List<Article>> loadAdditionalArticlesService;

	private DelayedConsumerService<ArticleListItemViewModel> markAsReadService;

	private final ObservableList<ArticleListItemViewModel> articles = FXCollections.observableArrayList();

	private final ObjectProperty<ArticleListItemViewModel> selectedArticle = new SimpleObjectProperty<>(null);

	private ObjectProperty<Feed> selectedFeedProperty;

	Subscription articleSelectionSubscription; // f*ck GC

	Subscription feedSubscription; // f*ck GC

	@Inject
	public ArticlesListViewModel(TTRSSClient client, FeedScope feedScope, ArticleScope articleScope) {
		selectedFeedProperty = feedScope.selectedFeedProperty();

		loadArticlesService = new BiFunctionService<>(client::getHeadlines, "Загружаю статьи…");
		loadArticlesService.setOnSucceeded(t -> {
			articles.setAll(loadArticlesService.getValue().stream().map(ArticleListItemViewModel::new)
					.collect(Collectors.toList()));
		});

		loadAdditionalArticlesService = new BiFunctionService<>(client::getHeadlines, "Загружаю статьи…");
		loadAdditionalArticlesService.setOnSucceeded(t -> {
			articles.addAll(loadAdditionalArticlesService.getValue().stream().map(ArticleListItemViewModel::new)
					.collect(Collectors.toList()));
		});

		markAsReadService = new DelayedConsumerService<>(articleModel -> {
			client.updateArticle(articleModel.getArticle().getId(), 0, 2);
			articleModel.getArticle().setUnread(false);
			articleModel.unreadProperty().set(false);
		}, "Прочитано...", 1000);
		markAsReadService.setOnSucceeded(t -> {
			// TODO:
		});

		feedSubscription = PipeBinding.of(selectedFeedProperty)
				.subscribe(feed -> loadArticlesService.restart(feed.getId(), 0));

		articleSelectionSubscription = PipeBinding.of(selectedArticle)
				.subscribe(articleModel -> {
					articleScope.setSelectedArticle(articleModel.getArticle());
					markAsReadService.restart(articleModel);
					if (articles.indexOf(articleModel) + 1 >= articles.size()) {
						preload();
					}
				});

		articleScope.loadingListMessageProperty()
				.bind(loadArticlesService.messageProperty()
						.concat(loadAdditionalArticlesService.messageProperty())
						.concat(markAsReadService.messageProperty()));
	}

	public void preload() {
		loadAdditionalArticlesService.restart(selectedFeedProperty.getValue().getId(), articles.size());
	}

	public boolean isPreloadRunning() {
		return loadAdditionalArticlesService.getState().compareTo(State.RUNNING) == 0;
	}

	public ObservableList<ArticleListItemViewModel> getArticles() {
		return articles;
	}

	public ObjectProperty<ArticleListItemViewModel> selectedArticleProperty() {
		return selectedArticle;
	}

}
