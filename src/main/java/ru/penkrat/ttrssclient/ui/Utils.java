package ru.penkrat.ttrssclient.ui;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;

public class Utils {
	
	public static Window parentWindowFromEvent(Event event) {
		if (event.getSource() instanceof Node) {
			return ((Node) event.getSource()).getScene().getWindow();
		}

		if (event.getSource() instanceof MenuItem) {
			return ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
		}

		return null;
	}

}
