package ru.penkrat.ttrssclient.binding.tuples;

@FunctionalInterface
public interface QuintetConsumer<A, B, C, D, E> {
	void accept(A a, B b, C c, D d, E e);
}