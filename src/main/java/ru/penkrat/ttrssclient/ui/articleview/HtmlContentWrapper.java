package ru.penkrat.ttrssclient.ui.articleview;

import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import ru.penkrat.ttrssclient.binding.tuples.TupleBindings;
import ru.penkrat.ttrssclient.domain.Article;
import ru.penkrat.ttrssclient.ui.settings.SettingsService;
import ru.penkrat.ttrssclient.ui.settings.SettingsService.Font;

@Component
public class HtmlContentWrapper {

	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	private final Template template;

	private final SettingsService settings;

	@Inject
	public HtmlContentWrapper(SettingsService settings) {
		template = Mustache.compiler()
				.compile(new InputStreamReader(HtmlContentWrapper.class.getResourceAsStream("view.html")));
		this.settings = settings;
	}

	public ObservableValue<String> bind(ReadOnlyObjectProperty<String> contentProperty,
			ObjectProperty<Article> articleProperty) {
		return TupleBindings.of(contentProperty,
				settings.fontFamilyProperty(),
				settings.fontSizeProperty(),
				articleProperty,
				settings.themeProperty())
				.reduce(this::generate);
	}

	private String generate(String content, Font fFamily, String fSize, Article article, String theme) {
		Map<String, Object> context = new HashMap<>();
		context.put("content", content);
		if (article != null) {
			context.put("article", article);
			context.put("articleUpdated", article.getUpdated().format(DTF));
		}
		context.put("fontFamily", fFamily);
		context.put("fontSize", fSize);
		context.put("theme", theme.toLowerCase().replaceAll("\\s", "-"));
		return template.execute(context);
	}

}
