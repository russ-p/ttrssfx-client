open module ttrssclient {
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;
	requires javafx.swing;
	requires transitive javafx.graphics;
	
	requires java.prefs;
	requires java.desktop;

	requires org.controlsfx.controls;
	requires com.samskivert.jmustache;
	requires org.slf4j;
	
	requires de.saxsys.mvvmfx;
	requires de.saxsys.mvvmfx.spring_boot;
	requires org.apache.httpcomponents.httpclient;
	requires org.apache.httpcomponents.httpcore;
	
	requires spring.beans;
	requires spring.core;
	requires spring.context;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	
	requires com.dlsc.formsfx;
	requires javax.inject;
	
	requires easybind;
	requires image4j;
	requires javax.json;
	requires commons.lang;

	exports ru.penkrat.ttrssclient;
}