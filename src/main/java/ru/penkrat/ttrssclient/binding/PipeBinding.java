package ru.penkrat.ttrssclient.binding;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PipeBinding<T> extends ObjectBinding<T> {

	private final ObservableValue<T> source;
	private boolean allowNull; // трактовать null как валидное значение

	public static <T> PipeBinding<T> of(ObservableValue<T> observable) {
		return new PipeBinding<>(observable, false);
	}

	public static <T> PipeBinding<T> ofNullable(ObservableValue<T> observable) {
		return new PipeBinding<>(observable, true);
	}

	// private constructor

	@SuppressWarnings("unchecked")
	private PipeBinding(Observable observable, boolean allowNull) {
		this.source = (ObservableValue<T>) observable;
		this.allowNull = allowNull;
		bind(observable);
	}

	// public API

	public Subscription subscribe(Consumer<? super T> subscriber) {
		T value = this.computeValue();
		if (testNull(value))
			subscriber.accept(value);

		ChangeListener<? super T> listener = (obs, oldValue, newValue) -> {
			if (testNull(newValue))
				subscriber.accept(newValue);
		};
		this.addListener(listener);

		return () -> this.removeListener(listener);
	}

	public Subscription subscribe(WritableValue<T> property) {
		return this.subscribe(property::setValue);
	}

	// transformations

	public PipeBinding<T> filter(Predicate<? super T> predicate) {
		PipeBinding<T> src = this;
		SimpleObjectProperty<T> property = new SimpleObjectProperty<T>();

		return new PipeBinding<>(property, this.allowNull) {
			private boolean hasValue = false;
			private ChangeListener<? super T> listener;

			@Override
			protected void onInvalidating() {
				hasValue = true;
			};

			@Override
			protected boolean testNull(Object value) {
				return hasValue && super.testNull(value);
			}

			@Override
			protected T computeValue() {
				if (listener == null) {
					listener = (obs, oldValue, newValue) -> {
						if (super.testNull(newValue) && predicate.test(newValue)) {
							hasValue = true;
							property.setValue(newValue);
						}
					};
					final var computeValue = src.computeValue();
					listener.changed(null, null, computeValue);
					src.addListener(listener);
				}
				return property.getValue();
			}

			@Override
			public void dispose() {
				super.dispose();
				if (listener != null) {
					src.removeListener(listener);
				}
			}

		};
	}

	public <U> PipeBinding<U> map(Function<? super T, U> mapping) {
		PipeBinding<T> src = this;
		return new PipeBinding<U>(src, src.allowNull) {
			@Override
			protected U computeValue() {
				T value = src.computeValue();
				if (testNull(value)) {
					return mapping.apply(value);
				} else {
					return null;
				}
			}

		};
	}

	public PipeBinding<T> orElse(T defaultvalue) {
		PipeBinding<T> src = this;
		return new PipeBinding<T>(src, false) {
			@Override
			protected T computeValue() {
				T value = src.computeValue();
				if (value != null) {
					return value;
				} else {
					return defaultvalue;
				}
			}
		};
	}

	// private methods

	protected boolean testNull(Object value) {
		return allowNull || value != null;
	}

	// inherited
	
	@Override
	protected void onInvalidating() {
		source.getValue(); //XXX FIX
		super.onInvalidating();
	};


	@Override
	protected T computeValue() {
		return source.getValue();
	}

	@Override
	public void dispose() {
		unbind(source);
	}

	@Override
	public ObservableList<Observable> getDependencies() {
		return FXCollections.singletonObservableList(source);
	}
}
