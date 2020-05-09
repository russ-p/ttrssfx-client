package ru.penkrat.ttrssclient.binding.tuples;

@FunctionalInterface
public interface QuinqueFunction<A, B, C, D, E, R> {

	R apply(A a, B b, C c, D d, E e);

}
