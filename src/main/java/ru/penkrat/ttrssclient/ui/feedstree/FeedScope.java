package ru.penkrat.ttrssclient.ui.feedstree;

import org.springframework.stereotype.Component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.penkrat.ttrssclient.domain.Feed;

@Component
public class FeedScope {

	private final ObjectProperty<Feed> selectedFeed = new SimpleObjectProperty<>(null);
	
	private final StringProperty loadingMessage = new SimpleStringProperty();

	public final ObjectProperty<Feed> selectedFeedProperty() {
		return this.selectedFeed;
	}

	public final ru.penkrat.ttrssclient.domain.Feed getSelectedFeed() {
		return this.selectedFeedProperty().get();
	}

	public final void setSelectedFeed(final ru.penkrat.ttrssclient.domain.Feed selectedFeed) {
		this.selectedFeedProperty().set(selectedFeed);
	}

	public final StringProperty loadingMessageProperty() {
		return this.loadingMessage;
	}
	
}
