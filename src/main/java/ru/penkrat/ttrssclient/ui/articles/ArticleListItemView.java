package ru.penkrat.ttrssclient.ui.articles;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.penkrat.ttrssclient.binding.ListBindings;
import ru.penkrat.ttrssclient.ui.feedstree.FeedIconProvider;

@Component
@Scope("prototype")
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

	private BooleanBinding doubledLine;

	public void initialize() {
		title.textProperty().bind(viewModel.titleProperty());
		title.setTooltip(new Tooltip(""));
		title.getTooltip().textProperty().bind(viewModel.titleProperty());

		doubledLine = viewModel.titleLengthProperty().greaterThan(title.widthProperty().divide(9));
		ListBindings.includeWhen(title.getStyleClass(), "one", doubledLine.not());
		ListBindings.includeWhen(title.getStyleClass(), "double", doubledLine);

		date.textProperty().bind(viewModel.dateProperty());
		feedTitle.textProperty().bind(viewModel.feedTitleProperty());

		ListBindings.includeWhen(title.getStyleClass(), CSS_UNREAD, viewModel.unreadProperty());

		iconImage.imageProperty().set(feedIconProvider.getImage(viewModel.getArticle().getFeedId()));
		excerptLabel.setText(StringUtils.strip(viewModel.getArticle().getExcerpt()));

		if (StringUtils.isNotEmpty(viewModel.getArticle().getFlavorImage()))

		{
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
