package ru.penkrat.ttrssclient.ui.articles;

import javax.inject.Inject;

import org.fxmisc.easybind.EasyBind;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import ru.penkrat.ttrssclient.ui.feedstree.FeedIconProvider;

public class ArticleListItemView implements FxmlView<ArticleListItemViewModel> {

	private final static String CSS_UNREAD = "list-cell-unread";

	@FXML
	public Label title;

	@FXML
	public Label feedTitle;

	@FXML
	public Label date;

	@FXML
	public ImageView iconImage;

	@Inject
	private FeedIconProvider feedIconProvider;

	@InjectViewModel
	private ArticleListItemViewModel viewModel;

	public void initialize() {
		title.textProperty().bind(viewModel.titleProperty());
		title.setTooltip(new Tooltip(""));
		title.getTooltip().textProperty().bind(viewModel.titleProperty());
		date.textProperty().bind(viewModel.dateProperty());
		feedTitle.textProperty().bind(viewModel.feedTitleProperty());

		EasyBind.includeWhen(title.getStyleClass(), CSS_UNREAD, viewModel.unreadProperty());

		iconImage.imageProperty().set(feedIconProvider.getImage(viewModel.getArticle().getFeedId()));
	}
}
