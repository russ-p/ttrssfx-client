package ru.penkrat.ttrssclient.ui.articles;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

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
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.VirtualFlow;

@Component
public class ArticlesListView implements FxmlView<ArticlesListViewModel>, Initializable {

	@FXML
	private ListView<ArticleListItemViewModel> articleView;

	@InjectViewModel
	private ArticlesListViewModel viewModel;

	private ScrollBar articleViewScrollBar;

	private Optional<Integer> beforeFirstVisibleCellIndex = Optional.empty();

	private VirtualFlow<?> virtualFlow;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		articleView.itemsProperty().setValue(viewModel.getArticles());
		viewModel.selectedArticleProperty().bind(articleView.getSelectionModel().selectedItemProperty());

		ViewListCellFactory<ArticleListItemViewModel> cellFactory = CachedViewModelCellFactory
				.create(vm -> FluentViewLoader.fxmlView(ArticleListItemView.class).viewModel(vm).load());

		articleView.setCellFactory(cellFactory);

		viewModel.getArticles().addListener((ListChangeListener<ArticleListItemViewModel>) c -> {
			while (c.next() && c.wasAdded()) {
				initScrolls();

				if (c.getAddedSize() == c.getList().size()) {
					articleView.scrollTo(0);
				} else {
					beforeFirstVisibleCellIndex.ifPresent(articleView::scrollTo);
				}
			}
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
			if (value + 0.02 >= articleViewScrollBar.getMax() && !viewModel.isPreloadRunning()) {
				saveViewIndex();
				viewModel.preload();
			}
		});

		articleView.getChildrenUnmodifiable()
				.stream()
				.filter(node -> node instanceof VirtualFlow)
				.map(node -> ((VirtualFlow<?>) node))
				.findAny()
				.ifPresent(virtualFlow -> this.virtualFlow = virtualFlow);
	}

	private void saveViewIndex() {
		beforeFirstVisibleCellIndex = Optional.ofNullable(virtualFlow.getFirstVisibleCell())
				.map(IndexedCell::getIndex);
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
