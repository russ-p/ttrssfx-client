package ru.penkrat.ttrssclient.ui.view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.fxmisc.easybind.EasyBind;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import de.saxsys.mvvmfx.utils.viewlist.ViewListCellFactory;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;
import net.sf.image4j.codec.ico.ICODecoder;
import ru.penkrat.ttrssclient.domain.CategoryFeedTreeItem;
import ru.penkrat.ttrssclient.domain.LoginData;
import ru.penkrat.ttrssclient.ui.LoginDialog;
import ru.penkrat.ttrssclient.ui.viewmodel.ArticleListItemViewModel;
import ru.penkrat.ttrssclient.ui.viewmodel.MainViewModel;

public class MainView implements FxmlView<MainViewModel>, Initializable {

	@FXML
	Label statusLabel;

	@FXML
	ListView<ArticleListItemViewModel> articleView;

	@FXML
	WebView webView;

	@InjectViewModel
	MainViewModel viewModel;

	private ScrollBar articleViewScrollBar;

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

		viewModel.getArticles().addListener((ListChangeListener<ArticleListItemViewModel>) c -> {
			initScrolls();
			int index = articleView.getSelectionModel().getSelectedIndex();
			articleView.scrollTo(index);
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

	private void initScrolls() {
		if (articleViewScrollBar != null)
			return;
		articleViewScrollBar = getVerticalScrollbar(articleView);
		articleViewScrollBar.valueProperty().addListener((ov, o, n) -> {
			double value = n.doubleValue();
			if (value == articleViewScrollBar.getMax()) {
				articleViewScrollBar.setValue(95);
				viewModel.preload();
			}
		});
	}

	private ScrollBar getVerticalScrollbar(Node table) {
		ScrollBar result = null;
		for (Node n : table.lookupAll(".scroll-bar")) {
			if (n instanceof ScrollBar) {
				ScrollBar bar = (ScrollBar) n;
				if (bar.getOrientation().equals(Orientation.VERTICAL)) {
					result = bar;
				}
			}
		}
		return result;
	}

	@FXML
	public void onPrev() {
		articleView.getSelectionModel().selectPrevious();
	}

	@FXML
	public void onNext() {
		articleView.getSelectionModel().selectNext();
	}
}
