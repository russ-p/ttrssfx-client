package ru.penkrat.ttrssclient.ui.articleview;

import java.text.MessageFormat;

import javax.inject.Singleton;

import org.fxmisc.easybind.EasyBind;

import javafx.beans.binding.Binding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Singleton
public class HtmlContentWrapper {

	public enum Font {
		PT_SANS("PT Sans", "https://fonts.googleapis.com/css?family=PT+Sans&subset=latin,cyrillic"),
		OPEN_SANS("Open Sans", "https://fonts.googleapis.com/css?family=Open+Sans&subset=latin,cyrillic"),
		UBUNTU("Ubuntu", "https://fonts.googleapis.com/css?family=Ubuntu&subset=latin,cyrillic"),
		NOTO_SANS("Noto Sans", "https://fonts.googleapis.com/css?family=Noto+Sans&subset=latin,cyrillic"),
		PHILOSOPHER(
				"Philosopher",
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

	private static final String STYLES_PATTERN = "body '{'"
			+ " line-height: 1.5;"
			+ " font-size: {0};"
			+ " font-family: ''{1}'', sans-serif;"
			+ " '}'\n"
			+ "p '{'"
			+ " display: block;"
			+ " -webkit-margin-before: 1em;"
			+ " -webkit-margin-after: 1em;"
			+ " -webkit-margin-start: 0px;"
			+ " -webkit-margin-end: 0px;   "
			+ " '}'\n"
			+ " img '{' max-width:98%; height: auto; '}'\n";

	private static final MessageFormat STYLES_FORMAT = new MessageFormat(STYLES_PATTERN);

	private final ObjectProperty<Font> fontFamily = new SimpleObjectProperty<>(Font.OPEN_SANS);

	private final StringProperty fontSize = new SimpleStringProperty("15px");

	public String wrap(String content) {
		return wrap(content, getFontFamily(), getFontSize());
	}

	public Binding<String> bind(ReadOnlyObjectProperty<String> contentProperty) {
		return EasyBind.combine(contentProperty, fontFamily, fontSize,
				(cntnt, fFamily, fSize) -> wrap(cntnt, fFamily, fSize));
	}

	public static String wrap(String content, Font fFamily, String fSize) {
		if (content == null)
			return "";
		if (fFamily == null)
			fFamily = Font.OPEN_SANS;
		if (fSize == null) {
			fSize = "15px";
		}
		StringBuffer html = html(
				head(link(fFamily.link)
						.append(style(STYLES_FORMAT.format(new Object[] { fSize, fFamily.name },
								new StringBuffer(), null)))
						).append(body(div(pIfAbsent(content)))));

		return html.toString();
	}

	private static StringBuffer tag(String start, StringBuffer content, String end) {
		return content.insert(0, start).append(end);
	}

	private static StringBuffer tag(String start, String content, String end) {
		return new StringBuffer().append(start).append(content).append(end);
	}

	private static StringBuffer body(StringBuffer content) {
		return tag("<body>", content, "</body>");
	}

	private static StringBuffer div(StringBuffer content) {
		return tag("<div>", content, "</div>");
	}

	private static StringBuffer html(StringBuffer content) {
		return tag("<html>", content, "</html>");
	}

	private static StringBuffer head(StringBuffer content) {
		return tag("<head>", content, "</head>");
	}

	private static StringBuffer style(StringBuffer content) {
		return tag("<style>", content, "</style>");
	}

	@SuppressWarnings("unused")
	private static StringBuffer link(StringBuffer href) {
		return tag("<link href='", href, "' rel='stylesheet' type='text/css'>");
	}

	private static StringBuffer link(String href) {
		return tag("<link href='", href, "' rel='stylesheet' type='text/css'>");
	}

	private static StringBuffer pIfAbsent(String content) {
		if (content.startsWith("<p>"))
			return new StringBuffer(content);
		return tag("<p>", content, "</p>");
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

}
