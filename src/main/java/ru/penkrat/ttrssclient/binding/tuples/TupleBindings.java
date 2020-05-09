package ru.penkrat.ttrssclient.binding.tuples;

import javafx.beans.value.ObservableValue;

public class TupleBindings {

	public static <A, B, C, D, E> QuintetBinding<A, B, C, D, E> of(ObservableValue<A> a,
			ObservableValue<B> b,
			ObservableValue<C> c,
			ObservableValue<D> d,
			ObservableValue<E> e) {
		return new QuintetBinding<>(a, b, c, d, e);
	}

}
