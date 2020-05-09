package ru.penkrat.ttrssclient.binding.tuples;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

public class QuintetBinding<A, B, C, D, E> extends TupleBinding<Quintet<A, B, C, D, E>> {

	private final ObservableValue<A> value0;
	private final ObservableValue<B> value1;
	private final ObservableValue<C> value2;
	private final ObservableValue<D> value3;
	private final ObservableValue<E> value4;

	QuintetBinding(ObservableValue<A> a,
			ObservableValue<B> b,
			ObservableValue<C> c,
			ObservableValue<D> d,
			ObservableValue<E> e) {
		super(a, b, c, d, e);
		this.value0 = a;
		this.value1 = b;
		this.value2 = c;
		this.value3 = d;
		this.value4 = e;
	}

	@Override
	protected Quintet<A, B, C, D, E> computeValue() {
		return new Quintet<>(value0.getValue(),
				value1.getValue(),
				value2.getValue(),
				value3.getValue(),
				value4.getValue());
	}

	public <R> ObjectBinding<R> reduce(QuinqueFunction<A, B, C, D, E, R> mapper) {
		QuintetBinding<A, B, C, D, E> src = this;
		return new MonoBinding<R>(src) {

			@Override
			protected R computeValue() {
				return src.getValue().reduce(mapper);
			}
		};
	}

}
