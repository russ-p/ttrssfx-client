package ru.penkrat.ttrssclient.ui.articleview;

import java.net.URL;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.stereotype.Component;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.web.WebView;
import ru.penkrat.ttrssclient.binding.PipeBinding;
import ru.penkrat.ttrssclient.binding.Subscription;

@Component
public class ArticleView implements FxmlView<ArticleViewModel>, Initializable {

	@FXML
	private Hyperlink link;

	@FXML
	private WebView webView;

	@InjectViewModel
	private ArticleViewModel viewModel;

	Subscription subscription;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		subscription = PipeBinding.of(viewModel.selectedArticleContentProperty())
				.subscribe(webView.getEngine()::loadContent);
		
		link.textProperty().bind(viewModel.selectedArticleTitleProperty());
		link.setVisited(false);
		link.setGraphic(new FontIcon());
		link.setContentDisplay(ContentDisplay.RIGHT);

		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(viewModel.selectedArticleLinkProperty());
		link.setTooltip(tooltip);
	}

	@FXML
	public void onOpenLink() {
		viewModel.openInBrowser();
	}

}
