open module ttrssclient {
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;
	
	requires transitive javafx.graphics;

	requires java.prefs;
	
	requires java.net.http;
	requires java.json;
	requires javax.inject;

	requires org.controlsfx.controls;
	requires com.samskivert.jmustache;
	requires org.slf4j;

	requires de.saxsys.mvvmfx;
	requires de.saxsys.mvvmfx.spring_boot;

	requires spring.beans;
	requires spring.core;
	requires spring.context;
	requires spring.boot;
	requires spring.boot.autoconfigure;

	requires com.dlsc.formsfx;

	requires org.kordamp.iconli.core;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.ikonli.materialdesign;

	requires com.github.russ_p.ico4jfx;
	
	requires org.apache.commons.lang3;
	requires org.apache.commons.text;
	requires lombok;
	
	exports ru.penkrat.ttrssclient;
}