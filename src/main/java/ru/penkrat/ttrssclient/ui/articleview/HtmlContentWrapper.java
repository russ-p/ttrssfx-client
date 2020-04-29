package ru.penkrat.ttrssclient.ui.articleview;

import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.fxmisc.easybind.EasyBind;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import javafx.beans.binding.Binding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.domain.Article;

@Singleton
public class HtmlContentWrapper {

	public enum Font {
		PT_SANS("PT Sans", "https://fonts.googleapis.com/css?family=PT+Sans&subset=latin,cyrillic"),
		OPEN_SANS("Open Sans", "https://fonts.googleapis.com/css?family=Open+Sans&subset=latin,cyrillic"),
		UBUNTU("Ubuntu", "https://fonts.googleapis.com/css?family=Ubuntu&subset=latin,cyrillic"),
		NOTO_SANS("Noto Sans", "https://fonts.googleapis.com/css?family=Noto+Sans&subset=latin,cyrillic"),
		PHILOSOPHER("Philosopher",
				"https://fonts.googleapis.com/css?family=Philosopher:400,400italic,700,700italic&subset=latin,cyrillic"),;

		String name, link;

		Font(String name, String link) {
			this.name = name;
			this.link = link;
		}

		public String toString() {
			return name;
		}
	}

	public static final String[] FONT_SIZES = new String[] { "13px", "15px", "18px" };
	
	public static final String[] THEMES = new String[] { "Default", "Light", "Dark", "Solarized", "Solarized Dark", "Dracula" };

	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	private final ObjectProperty<Font> fontFamily = new SimpleObjectProperty<>(Font.OPEN_SANS);

	private final StringProperty fontSize = new SimpleStringProperty("15px");
	
	private final StringProperty theme = new SimpleStringProperty("Default");

	private final Template template;

	public HtmlContentWrapper() {
		template = Mustache.compiler()
				.compile(new InputStreamReader(HtmlContentWrapper.class.getResourceAsStream("view.html")));
	}

	public Binding<String> bind(ReadOnlyObjectProperty<String> contentProperty,
			ObjectProperty<Article> articleProperty) {
		return EasyBind.combine(contentProperty, fontFamily, fontSize, articleProperty, theme, this::generate);
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

	public final ObjectProperty<Font> fontFamilyProperty() {
		return this.fontFamily;
	}

	public final ru.penkrat.ttrssclient.ui.articleview.HtmlContentWrapper.Font getFontFamily() {
		return this.fontFamilyProperty().get();
	}

	public final void setFontFamily(final ru.penkrat.ttrssclient.ui.articleview.HtmlContentWrapper.Font fontFamily) {
		this.fontFamilyProperty().set(fontFamily);
	}

	public final StringProperty fontSizeProperty() {
		return this.fontSize;
	}

	public final java.lang.String getFontSize() {
		return this.fontSizeProperty().get();
	}

	public final void setFontSize(final java.lang.String fontSize) {
		this.fontSizeProperty().set(fontSize);
	}
	
	public final StringProperty themeProperty() {
		return this.theme;
	}


}
