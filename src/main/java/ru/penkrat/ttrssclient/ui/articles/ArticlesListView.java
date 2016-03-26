package ru.penkrat.ttrssclient.ui.articles;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenterFactory;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import de.saxsys.mvvmfx.utils.viewlist.ViewListCellFactory;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;

public class ArticlesListView implements FxmlView<ArticlesListViewModel>, Initializable {

	@FXML
	private ListView<ArticleListItemViewModel> articleView;

	@InjectViewModel
	private ArticlesListViewModel viewModel;

	private ScrollBar articleViewScrollBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		articleView.itemsProperty().setValue(viewModel.getArticles());
		viewModel.selectedArticleProperty().bind(articleView.getSelectionModel().selectedItemProperty());

		ViewListCellFactory<ArticleListItemViewModel> cellFactory = CachedViewModelCellFactory
				.create(vm -> FluentViewLoader.fxmlView(ArticleListItemView.class).viewModel(vm).load());

		articleView.setCellFactory(cellFactory);

		viewModel.getArticles().addListener((ListChangeListener<ArticleListItemViewModel>) c -> {
			initScrolls();
			int index = articleView.getSelectionModel().getSelectedIndex();
			articleView.scrollTo(index);
		});

		NotificationCenterFactory.getNotificationCenter().subscribe("PREV_ARTICLE", (a, b) -> {
			articleView.getSelectionModel().selectPrevious();
		});
		NotificationCenterFactory.getNotificationCenter().subscribe("NEXT_ARTICLE", (a, b) -> {
			articleView.getSelectionModel().selectNext();
		});
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
}
