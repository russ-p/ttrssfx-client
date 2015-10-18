package ru.penkrat.ttrssclient.ui.view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.fxmisc.easybind.EasyBind;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import de.saxsys.mvvmfx.utils.viewlist.ViewListCellFactory;
import ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.ui.LoginDialog;
import ru.penkrat.ttrssclient.ui.viewmodel.ArticleListItemViewModel;
import ru.penkrat.ttrssclient.ui.viewmodel.MainViewModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.web.WebView;
import net.sf.image4j.codec.ico.ICODecoder;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;

public class MainView implements FxmlView<MainViewModel>, Initializable {

	@FXML
	TreeView<CategoryFeedTreeItem> treeView;

	@FXML
	Label statusLabel;

	@FXML
	ListView<ArticleListItemViewModel> articleView;

	@FXML
	WebView webView;

	@InjectViewModel
	MainViewModel viewModel;

	private TreeItem<CategoryFeedTreeItem> treeRoot;

	@FXML
	Hyperlink link;

	@FXML
	public void onUpdate() {
		viewModel.update();
	}

	@FXML
	public void onOpenLink() {
		viewModel.openInBrowser();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		treeRoot = new TreeItem<CategoryFeedTreeItem>();
		treeRoot.setExpanded(true);

		treeView.setRoot(treeRoot);
		treeView.setShowRoot(false);

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

		viewModel.getRootItems().addListener((ListChangeListener<CategoryFeedTreeItem>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					List<TreeItem<CategoryFeedTreeItem>> list = c.getAddedSubList().stream()
							.map(cat -> new TreeItem<CategoryFeedTreeItem>(cat)).peek(treeItem -> {
						if (!treeItem.getValue().isLeaf()) {
							ObservableList<TreeItem<CategoryFeedTreeItem>> chItems = EasyBind.map(
									treeItem.getValue().getChildren(), feed -> new TreeItem<>(feed, createIcon(feed)));
							EasyBind.listBind(treeItem.getChildren(), chItems);
						}
					}).collect(Collectors.toList());
					treeRoot.getChildren().addAll(c.getFrom(), list);
				}
				if (c.wasRemoved()) {
					c.getRemoved().stream().map((item) -> findInTree(item, treeRoot)).forEach(i -> {
						treeRoot.getChildren().remove(i);
					});
				}
			}
		});

		viewModel.selectedCategoryOrFeedProperty()
				.bind(EasyBind.monadic(treeView.getSelectionModel().selectedItemProperty()).map(TreeItem::getValue));

		EasyBind.subscribe(viewModel.selectedCategoryOrFeedProperty(), n -> {
			TreeItem<CategoryFeedTreeItem> selected = findInTree(n, treeRoot);
			if (selected != null) {
				selected.setExpanded(true);
				treeView.getSelectionModel().select(selected);
			} else {
				treeView.getSelectionModel().clearSelection();
			}
		});

		articleView.itemsProperty().setValue(viewModel.getArticles());
		viewModel.selectedArticleProperty().bind(articleView.getSelectionModel().selectedItemProperty());

		EasyBind.subscribe(viewModel.selectedArticleContentProperty(),
				content -> webView.getEngine().loadContent(content));

		link.textProperty().bind(viewModel.selectedArticleTitleProperty());
		link.setVisited(false);

		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(viewModel.selectedArticleLinkProperty());
		link.setTooltip(tooltip);

		statusLabel.textProperty().bind(viewModel.statusProperty());

		ViewListCellFactory<ArticleListItemViewModel> cellFactory = CachedViewModelCellFactory
				.create(vm -> FluentViewLoader.fxmlView(ArticleListItemView.class).viewModel(vm).load());

		articleView.setCellFactory(cellFactory);

		viewModel.subscribe("showLoginDialog", (key, payload) -> {
			LoginData loginData = (LoginData) payload[0];
			LoginDialog loginDialog = new LoginDialog(loginData, (ld) -> {
				return viewModel.checkLoginData(ld);
			});
			loginDialog.showAndWait().ifPresent(viewModel::acceptLoginData);
		});
	}

	private TreeItem<CategoryFeedTreeItem> findInTree(CategoryFeedTreeItem item, TreeItem<CategoryFeedTreeItem> root) {
		for (TreeItem<CategoryFeedTreeItem> treeItem : root.getChildren()) {
			if (Objects.equals(treeItem.getValue(), item)) {
				return treeItem;
			}
			TreeItem<CategoryFeedTreeItem> subTreeItem = findInTree(item, treeItem);
			if (subTreeItem != null)
				return subTreeItem;
		}
		return null;
	}

	@FXML
	public void onLogin() {
		viewModel.login();
	}

	private Node createIcon(CategoryFeedTreeItem feedItem) {
		ImageView imageView = new ImageView();
		String url = viewModel.getIconUrl(feedItem);
		if (url != null) {
			Image image = getImage(url);
			if (image != null)
				imageView.setImage(image);
		}
		imageView.setFitWidth(16);
		imageView.setFitHeight(16);
		return imageView;
	}

	private Image getImage(String url) {
		Image image = new Image(url, false);
		if (image.getException() != null) {
			try {
				try (InputStream istream = new URL(url).openStream();) {
					List<BufferedImage> images = ICODecoder.read(istream);
					if (!images.isEmpty())
						image = SwingFXUtils.toFXImage(images.get(0), new WritableImage(16, 16));
				}
			} catch (IOException e) {
				// TODO:
			}
		}
		return image;
	}
}
