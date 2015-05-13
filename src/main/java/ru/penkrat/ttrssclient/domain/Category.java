package ru.penkrat.ttrssclient.domain;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class Category implements CategoryFeedTreeItem {

	private int id;
	private int unread;
	private int orderId;
	private String title;

	public Category(JsonValue value) {
		// {"id":"1","title":"ЖЖ","unread":19,"order_id":3}
		JsonObject obj = (JsonObject) value;
		id = obj.get("id").getValueType() == ValueType.NUMBER ? obj.getInt("id") : Integer
				.parseInt(obj.getString("id"));
		unread = obj.getInt("unread");
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

}
