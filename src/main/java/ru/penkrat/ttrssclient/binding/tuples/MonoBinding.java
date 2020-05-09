package ru.penkrat.ttrssclient.binding.tuples;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class MonoBinding<T> extends ObjectBinding<T> {

	private final Observable dependency;

	public MonoBinding(Observable dependency) {
		this.dependency = dependency;
		bind(dependency);
	}

	@Override
	public void dispose() {
		unbind(dependency);
	}

	@Override
	public ObservableList<Observable> getDependencies() {
		return FXCollections.singletonObservableList(dependency);
	}
}
