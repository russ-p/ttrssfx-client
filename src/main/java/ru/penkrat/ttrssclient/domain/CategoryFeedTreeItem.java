package ru.penkrat.ttrssclient.domain;

import javafx.collections.ObservableList;

public interface CategoryFeedTreeItem {

	String getTitle();

	int getUnread();

	boolean isLeaf();

	ObservableList<CategoryFeedTreeItem> getChildren();
}
