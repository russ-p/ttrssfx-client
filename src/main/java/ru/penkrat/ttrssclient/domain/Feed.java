package ru.penkrat.ttrssclient.domain;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Feed implements CategoryFeedTreeItem {

	private String title;
	private int id;
	private int unread;
	private int orderId;

	/*
	 * {"feed_url":"http://naviny.by/rss/index.rss?view=alls", "title":
	 * "Белорусские новости: Все материалы", "id":34,"unread":375,
	 * "has_icon":true, "cat_id":4, "last_updated":1424521489, "order_id":0}
	 */
	public Feed(JsonValue value) {
		JsonObject obj = (JsonObject) value;

		id = obj.get("id").getValueType() == ValueType.NUMBER ? obj.getInt("id")
				: Integer.parseInt(obj.getString("id"));
		unread = obj.getInt("unread");
		orderId = obj.getInt("order_id", Integer.MAX_VALUE);
		title = obj.getString("title");
	}

	@Override
	public String getTitle() {
		return title;
	}

	public int getId() {
		return id;
	}

	public int getUnread() {
		return unread;
	}

	public int getOrderId() {
		return orderId;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public ObservableList<CategoryFeedTreeItem> getChildren() {
		return FXCollections.emptyObservableList();
	}

}
