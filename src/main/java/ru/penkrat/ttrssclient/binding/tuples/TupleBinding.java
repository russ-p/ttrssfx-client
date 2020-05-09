package ru.penkrat.ttrssclient.binding.tuples;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class TupleBinding<T> extends ObjectBinding<T> {

	private final Observable[] dependencies;

	public TupleBinding(Observable... dependencies) {
		this.dependencies = dependencies;
		bind(dependencies);
	}

	@Override
	public void dispose() {
		unbind(dependencies);
	}

	@Override
	public ObservableList<Observable> getDependencies() {
		return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(dependencies));
	}
}
