package ru.penkrat.ttrssclient.ui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import org.controlsfx.control.Notifications;

import ru.penkrat.ttrssclient.MainApp;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.domain.Category;
import ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem;
import ru.penkrat.ttrssclient.domain.Feed;
import ru.penkrat.ttrssclient.service.generic.BiFunctionService;
import ru.penkrat.ttrssclient.service.generic.FunctionService;
import ru.penkrat.ttrssclient.service.generic.SupplierService;
import ru.penkrat.ttrssclient.service.generic.delayed.DelayedConsumerService;

public class MainWindowController implements Initializable {
	@FXML
	private Button loginButton;

	@FXML
	private TreeView<CategoryFeedTreeItem> treeView;

	@FXML
	private Button updateButton;

	@FXML
	private ListView<Article> articleView;

	@FXML
	private WebView webView;

	@FXML
	private Label statusLabel;

	@FXML
	private Hyperlink link;

	private TreeItem<CategoryFeedTreeItem> treeRoot;

	private SupplierService<List<Category>> loadCategoriesService;

	private FunctionService<Integer, List<Feed>> loadFeedsService;

	private BiFunctionService<Integer, Integer, List<Article>> loadArticlesService;

	private FunctionService<Article, String> loadArticleContentService;

	private BiFunctionService<Integer, Integer, List<Article>> loadAdditionalArticlesService;

	private DelayedConsumerService<Article> markAsReadService;

	@FXML
	void handleUpdate(ActionEvent event) {
		loadCategoriesService.restart();
	}

	@FXML
	void linkAction(ActionEvent event) {
		Article selectedItem = articleView.getSelectionModel().getSelectedItem();
		if (selectedItem != null)
			MainApp.getInstance().getHostServices().showDocument(selectedItem.getLink());
	}

	private void handleError(WorkerStateEvent t) {
		Notifications.create().title("Error").text(t.getSource().getException().getMessage()).showError();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		TTRSSClient client = MainApp.getInstance().getClient();

		loadCategoriesService = new SupplierService<>(client::getCategories, "Загружаю категории");
		loadCategoriesService.setOnSucceeded(t -> {
			List<TreeItem<CategoryFeedTreeItem>> list = loadCategoriesService.getValue().stream()
					.map(cat -> new TreeItem<CategoryFeedTreeItem>(cat)).collect(Collectors.toList());
			treeRoot.getChildren().clear();
			treeRoot.getChildren().addAll(list);
		});
		loadCategoriesService.setOnFailed(this::handleError);
		loadCategoriesService.restart();

		loadFeedsService = new FunctionService<>(client::getFeeds, "Загружаю фиды…");
		loadFeedsService.setOnSucceeded(t -> {
			List<Feed> feeds = loadFeedsService.getValue();
			List<TreeItem<CategoryFeedTreeItem>> list = feeds
					.stream()
					.map(feed -> new TreeItem<CategoryFeedTreeItem>(feed, createImageView(client.getIconURL(feed
							.getId())))).collect(Collectors.toList());
			TreeItem<CategoryFeedTreeItem> item = treeView.getSelectionModel().selectedItemProperty().get();
			item.getChildren().clear();
			item.getChildren().addAll(list);
			item.setExpanded(true);
		});
		loadFeedsService.setOnFailed(this::handleError);

		loadArticlesService = new BiFunctionService<>(client::getHeadlines, "Загружаю статьи…");
		loadArticlesService.setOnSucceeded(t -> {
			articleView.itemsProperty().get().clear();
			articleView.itemsProperty().get().addAll(loadArticlesService.getValue());
		});
		loadArticlesService.setOnFailed(this::handleError);

		markAsReadService = new DelayedConsumerService<>(article -> {
			client.updateArticle(article.getId(), 0, 2);
			article.setUnread(false);
		}, "Как прочитано", 2000);
		markAsReadService.setOnSucceeded(t -> {
			Article article = articleView.getSelectionModel().getSelectedItem();
			int selectedIndex = articleView.getSelectionModel().getSelectedIndex();
			articleView.getItems().set(selectedIndex, article);
		});

		loadAdditionalArticlesService = new BiFunctionService<>(client::getHeadlines, "Загружаю статьи…");
		loadAdditionalArticlesService.setOnSucceeded(t -> {
			articleView.itemsProperty().get().addAll(loadAdditionalArticlesService.getValue());
		});

		treeRoot = new TreeItem<CategoryFeedTreeItem>();
		treeRoot.setExpanded(true);
		treeView.setRoot(treeRoot);
		treeView.setShowRoot(false);
		treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newItem) -> {
			if (newItem == null)
				return;
			CategoryFeedTreeItem value = newItem.getValue();
			((Stage) statusLabel.getScene().getWindow()).setTitle("Tiny Tiny RSS — [" + value.getTitle() + "]");

			if (value instanceof Category) {
				if (newItem.getChildren().isEmpty()) {
					loadFeedsService.restart(((Category) value).getId());
				}
			} else if (value instanceof Feed) {
				if (newItem.getChildren().isEmpty()) {
					loadArticlesService.restart(((Feed) value).getId(), 0);
				}
			}
		});

		treeView.setCellFactory(tree -> {
			return new TreeCell<CategoryFeedTreeItem>() {
				@Override
				protected void updateItem(CategoryFeedTreeItem item, boolean empty) {
					super.updateItem(item, empty);

					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						setGraphic(getTreeItem().getGraphic());
						if (item.getUnread() > 0) {
							setText(item.getTitle() + " (" + item.getUnread() + ")");
							getStyleClass().add("list-cell-bold");
						} else {
							setText(item.getTitle());
							getStyleClass().remove("list-cell-bold");
						}
					}
				}
			};
		});

		loadArticleContentService = new FunctionService<>(client::getContent);
		loadArticleContentService.setOnSucceeded(t -> {
			webView.getEngine().loadContent(loadArticleContentService.getValue());
		});

		articleView
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> {
							if (newValue != null) {
								loadArticleContentService.restart(newValue);

								if (articleView.getItems().indexOf(newValue) + 1 >= articleView.getItems().size()) {
									loadAdditionalArticlesService.restart(((Feed) treeView.getSelectionModel()
											.getSelectedItem().getValue()).getId(), articleView.getItems().size());
								}
								markAsReadService.restart(newValue);
								link.setText(newValue.getTitle());
								link.setVisited(false);
							}
						});

		articleView.setCellFactory(value -> {
			return new ListCell<Article>() {

				private String CSS_BOLD = "list-cell-bold";

				@Override
				protected void updateItem(Article article, boolean empty) {
					super.updateItem(article, empty);
					getStyleClass().remove(CSS_BOLD);
					if (empty || article == null) {
						setGraphic(null);
					} else {
						setGraphic(new ArticleCellController().setArticle(article).getGraphic());
					}
				}
			};
		});

		statusLabel.textProperty().bind(
				loadCategoriesService.messageProperty().concat(loadFeedsService.messageProperty())
						.concat(loadArticlesService.messageProperty()));

		link.setText(null);
	}

	private Node createImageView(String url) {
		Image image = new Image(url, 16, 16, true, true, true);
		return new ImageView(image);
	}

}
