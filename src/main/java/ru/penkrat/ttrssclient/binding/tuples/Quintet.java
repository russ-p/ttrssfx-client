package ru.penkrat.ttrssclient.binding.tuples;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Quintet<A, B, C, D, E> {
	private final A value0;
	private final B value1;
	private final C value2;
	private final D value3;
	private final E value4;

	<R> R reduce(QuinqueFunction<A, B, C, D, E, R> func) {
		return func.apply(value0, value1, value2, value3, value4);
	}
}
