package ru.penkrat.ttrssclient.ui.articleview;

import java.net.URL;
import java.util.ResourceBundle;

import org.fxmisc.easybind.EasyBind;
import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.web.WebView;

@Component
public class ArticleView implements FxmlView<ArticleViewModel>, Initializable {

	@FXML
	private Hyperlink link;

	@FXML
	private WebView webView;

	@InjectViewModel
	private ArticleViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		EasyBind.subscribe(viewModel.selectedArticleContentProperty(),
				content -> webView.getEngine().loadContent(content));

		link.textProperty().bind(viewModel.selectedArticleTitleProperty());
		link.setVisited(false);

		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(viewModel.selectedArticleLinkProperty());
		link.setTooltip(tooltip);
	}

	@FXML
	public void onOpenLink() {
		viewModel.openInBrowser();
	}

}
