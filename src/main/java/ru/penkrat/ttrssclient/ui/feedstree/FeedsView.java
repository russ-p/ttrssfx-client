package ru.penkrat.ttrssclient.ui.feedstree;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.penkrat.ttrssclient.binding.ListBindings;
import ru.penkrat.ttrssclient.binding.PipeBinding;
import ru.penkrat.ttrssclient.binding.Subscription;
import ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem;
import ru.penkrat.ttrssclient.domain.Feed;

@Component
public class FeedsView implements FxmlView<FeedsViewModel>, Initializable {

	@FXML
	private TreeView<CategoryFeedTreeItem> treeView;

	@InjectViewModel
	private FeedsViewModel viewModel;

	@Inject
	private FeedIconProvider feedIconProvider;

	private TreeItem<CategoryFeedTreeItem> treeRoot;

	private List<Subscription> subs = new ArrayList<>();

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
							.map(cat -> new TreeItem<CategoryFeedTreeItem>(cat))
							.peek(treeItem -> {
								if (!treeItem.getValue().isLeaf()) {
									ObservableList<TreeItem<CategoryFeedTreeItem>> chItems = ListBindings.map(
											treeItem.getValue().getChildren(),
											feed -> new TreeItem<>(feed, createIcon(feed)));

									Subscription s = ListBindings.listBind(treeItem.getChildren(), chItems);
									subs.add(s);
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

		PipeBinding.of(treeView.getSelectionModel().selectedItemProperty())
				.map(TreeItem::getValue)
				.subscribe(viewModel.selectedCategoryOrFeedProperty());

		PipeBinding.of(viewModel.selectedCategoryOrFeedProperty())
				.subscribe(n -> {
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
		if (feedItem instanceof Feed) {
			Image image = feedIconProvider.getImage(String.valueOf(((Feed) feedItem).getId()));
			if (image != null) {
				ImageView imageView = new ImageView();
				imageView.setImage(image);
				imageView.setFitWidth(16);
				imageView.setFitHeight(16);
				return imageView;
			}
			return new FontIcon(MaterialDesign.MDI_IMAGE_BROKEN);
		}
		return new FontIcon(MaterialDesign.MDI_FOLDER);
	}

}
