package ru.penkrat.ttrssclient.domain;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Category implements CategoryFeedTreeItem {

	private int id;
	private int unread;
	private int orderId;
	private String title;

	private ObservableList<CategoryFeedTreeItem> children = FXCollections.observableArrayList();

	public Category(JsonValue value) {
		// {"id":"1","title":"ЖЖ","unread":19,"order_id":3}
		JsonObject obj = (JsonObject) value;
		id = obj.get("id").getValueType() == ValueType.NUMBER ? obj.getInt("id")
				: Integer.parseInt(obj.getString("id"));
		unread = Integer.parseInt( obj.getString("unread", "0") );
		orderId = obj.getInt("order_id", Integer.MAX_VALUE);
		title = obj.getString("title");
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

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return "Category [title=" + title + "]";
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public ObservableList<CategoryFeedTreeItem> getChildren() {
		return children;
	}

}
