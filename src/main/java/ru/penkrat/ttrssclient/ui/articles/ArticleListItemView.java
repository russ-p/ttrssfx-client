package ru.penkrat.ttrssclient.ui.articles;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.penkrat.ttrssclient.ui.feedstree.FeedIconProvider;

public class ArticleListItemView implements FxmlView<ArticleListItemViewModel> {

	private final static String CSS_UNREAD = "list-cell-unread";

	@FXML
	public Pane rootPane;

	@FXML
	public Label title;

	@FXML
	public Label feedTitle;

	@FXML
	public Label date;

	@FXML
	public Label excerptLabel;

	@FXML
	public ImageView iconImage;

	@FXML
	public ImageView flavorImageView;

	@Inject
	private FeedIconProvider feedIconProvider;

	@InjectViewModel
	private ArticleListItemViewModel viewModel;

	public void initialize() {
		title.textProperty().bind(viewModel.titleProperty());
		title.setTooltip(new Tooltip(""));
		title.getTooltip().textProperty().bind(viewModel.titleProperty());

		MonadicBinding<Integer> height = EasyBind.map(viewModel.titleProperty(),
				title -> title.length() > 30 ? 48 : 24);
		title.minHeightProperty().bind(height);
		title.maxHeightProperty().bind(height);

		date.textProperty().bind(viewModel.dateProperty());
		feedTitle.textProperty().bind(viewModel.feedTitleProperty());

		EasyBind.includeWhen(title.getStyleClass(), CSS_UNREAD, viewModel.unreadProperty());

		iconImage.imageProperty().set(feedIconProvider.getImage(viewModel.getArticle().getFeedId()));
		excerptLabel.setText(StringUtils.strip(viewModel.getArticle().getExcerpt()));

		if (StringUtils.isNotEmpty(viewModel.getArticle().getFlavorImage())) {
			Image img = new Image(viewModel.getArticle().getFlavorImage(), true);
			flavorImageView.setImage(img);
			flavorImageView.fitHeightProperty().bind(title.heightProperty().add(excerptLabel.heightProperty()));
		} else {
			flavorImageView.setVisible(false);
			flavorImageView.setFitWidth(0);
			flavorImageView.setFitHeight(0);
		}
	}
}
