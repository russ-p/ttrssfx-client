package ru.penkrat.ttrssclient.ui.feedstree;

import java.util.List;

import javax.inject.Inject;

import org.fxmisc.easybind.EasyBind;
import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.Category;
import ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem;
import ru.penkrat.ttrssclient.domain.Feed;
import ru.penkrat.ttrssclient.service.generic.FunctionService;
import ru.penkrat.ttrssclient.service.generic.SupplierService;
import ru.penkrat.ttrssclient.ui.login.LoginManager;

@Component
public class FeedsViewModel implements ViewModel {

	private TTRSSClient client;

	private SupplierService<List<Category>> loadCategoriesService;

	private FunctionService<Integer, List<Feed>> loadFeedsService;

	private final ObjectProperty<CategoryFeedTreeItem> selectedCategoryOrFeed = new SimpleObjectProperty<>(null);

	private final ObservableList<CategoryFeedTreeItem> rootItems = FXCollections.observableArrayList();

	@Inject
	public FeedsViewModel(TTRSSClient client, FeedScope feedScope, LoginManager loginManager,
			LoadFeedsTaskFactory loadFeedsTaskFactory) {
		this.client = client;
		loadCategoriesService = new SupplierService<>(client::getCategories, "Загружаю категории");
		loadCategoriesService.setOnSucceeded(t -> {
			rootItems.setAll(loadCategoriesService.getValue());
			loadCategoriesService.getValue().parallelStream()
				.forEach(task -> loadFeedsTaskFactory.runTask(task));
		});
		loadCategoriesService.setOnFailed(e -> {
			loadCategoriesService.getException().printStackTrace();
		});

		loadFeedsService = new FunctionService<>(client::getFeeds, "Загружаю фиды…");
		loadFeedsService.setOnSucceeded(t -> {
			List<Feed> feeds = loadFeedsService.getValue();
			getSelectedCategoryOrFeed().getChildren().setAll(feeds);
		});

		EasyBind.subscribe(selectedCategoryOrFeed, categoryOrFeed -> {
			if (categoryOrFeed instanceof Category) {
				if (categoryOrFeed.getChildren().isEmpty()) {
					loadFeedsService.restart(((Category) categoryOrFeed).getId());
				}
			} else if (categoryOrFeed instanceof Feed) {
				if (categoryOrFeed.getChildren().isEmpty()) {
					feedScope.setSelectedFeed((Feed) categoryOrFeed);
				}
			}
		});

		NotificationCenterFactory.getNotificationCenter().subscribe("UPDATE", (a, b) -> {
			loadCategoriesService.restart();
		});

		loginManager.isLoggedInProperty().addListener((ov, o, n) -> {
			if (n != null && n) {
				loadCategoriesService.restart();
			}
		});
		if (loginManager.isLoggedIn()) {
			loadCategoriesService.restart();
		}

		feedScope.loadingMessageProperty()
				.bind(loadCategoriesService.messageProperty().concat(loadFeedsService.messageProperty()));
	}

	public final ObjectProperty<CategoryFeedTreeItem> selectedCategoryOrFeedProperty() {
		return this.selectedCategoryOrFeed;
	}

	public final ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem getSelectedCategoryOrFeed() {
		return this.selectedCategoryOrFeedProperty().get();
	}

	public final void setSelectedCategoryOrFeed(
			final ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem selectedCategoryOrFeed) {
		this.selectedCategoryOrFeedProperty().set(selectedCategoryOrFeed);
	}

	public ObservableList<CategoryFeedTreeItem> getRootItems() {
		return rootItems;
	}

	public String getIconUrl(CategoryFeedTreeItem catFeedItem) {
		if (catFeedItem instanceof Feed) {
			Feed feed = (Feed) catFeedItem;
			return client.getIconURL(feed.getId());
		}
		return null;
	}

}
