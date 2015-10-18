package ru.penkrat.ttrssclient.ui.view;

import org.fxmisc.easybind.EasyBind;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ru.penkrat.ttrssclient.ui.viewmodel.ArticleListItemViewModel;

public class ArticleListItemView implements FxmlView<ArticleListItemViewModel> {

	private final static String CSS_UNREAD = "list-cell-unread";

	@FXML
	public Label title;
	@FXML
	public Label date;

	@InjectViewModel
	private ArticleListItemViewModel viewModel;

	public void initialize() {
		title.textProperty().bind(viewModel.titleProperty());
		date.textProperty().bind(viewModel.dateProperty());

		EasyBind.includeWhen(title.getStyleClass(), CSS_UNREAD, viewModel.unreadProperty());
	}
}
