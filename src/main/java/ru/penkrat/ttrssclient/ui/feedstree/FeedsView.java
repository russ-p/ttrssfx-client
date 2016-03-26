package ru.penkrat.ttrssclient.ui.feedstree;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.fxmisc.easybind.EasyBind;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import net.sf.image4j.codec.ico.ICODecoder;
import ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem;

public class FeedsView implements FxmlView<FeedsViewModel>, Initializable {

	@FXML
	private TreeView<CategoryFeedTreeItem> treeView;

	@InjectViewModel
	private FeedsViewModel viewModel;

	private TreeItem<CategoryFeedTreeItem> treeRoot;

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
							getStyleClass().removeAll("list-cell-bold");
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
