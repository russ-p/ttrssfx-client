package ru.penkrat.ttrssclient.ui.articleview;

import java.text.MessageFormat;

import javax.inject.Singleton;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Singleton
public class HtmlContentWrapper {

	public enum Font {
		PT_SANS("PT Sans", "https://fonts.googleapis.com/css?family=PT+Sans&subset=latin,cyrillic"),
		OPEN_SANS("Open Sans", "https://fonts.googleapis.com/css?family=Open+Sans&subset=latin,cyrillic");

		String name, link;

		Font(String name, String link) {
			this.name = name;
			this.link = link;
		}

		public String toString() {
			return name;
		}
	}

	private static final String STYLES_PATTERN = "p '{'"
			+ " display: block;"
			+ " -webkit-margin-before: 1em;"
			+ " -webkit-margin-after: 1em;"
			+ " -webkit-margin-start: 0px;"
			+ " -webkit-margin-end: 0px;   "
			+ " line-height: 1.5;"
			+ " font-size: {0};"
			+ " font-family: ''{1}'', sans-serif;"
			+ " '}'";

	private static final MessageFormat STYLES_FORMAT = new MessageFormat(STYLES_PATTERN);

	private final ObjectProperty<Font> fontFamily = new SimpleObjectProperty<>(Font.OPEN_SANS);

	private final StringProperty fontSize = new SimpleStringProperty("15px");

	public String wrap(String content) {
		StringBuffer html = html(
				head(link(getFontFamily().link)
						.append(style(STYLES_FORMAT.format(new Object[] { getFontSize(), getFontFamily().name },
								new StringBuffer(), null)))
						.append(body(pIfAbsent(content)))));

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

	@SuppressWarnings("unused")
	private static String div(String content, String clazz) {
		return "<div class=\"" + clazz + "\">" + content + "</div>";
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
