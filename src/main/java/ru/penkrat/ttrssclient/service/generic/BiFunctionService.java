package ru.penkrat.ttrssclient.service.generic;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class BiFunctionService<T, U, R> extends Service<R> {

	protected BiFunction<T, U, R> function;

	protected final AtomicReference<T> param1ValueRef = new AtomicReference<T>();
	protected final AtomicReference<U> param2ValueRef = new AtomicReference<U>();

	private String message = "";

	public BiFunctionService(BiFunction<T, U, R> function) {
		this.function = function;
	}

	public BiFunctionService(BiFunction<T, U, R> function, String message) {
		this.function = function;
		this.message = message;
	}

	public BiFunctionService<T, U, R> setParams(T param1Value, U param2Value) {
		param1ValueRef.set(param1Value);
		param2ValueRef.set(param2Value);
		return this;
	}

	@Override
	protected Task<R> createTask() {
		final T t = param1ValueRef.get();
		final U u = param2ValueRef.get();
		return new Task<R>() {

			@Override
			protected R call() throws Exception {
				updateMessage(message);
				R result = function.apply(t, u);
				updateMessage("");
				return result;
			}
		};
	}

	public void restart(T param1Value, U param2Value) {
		setParams(param1Value, param2Value);
		super.restart();
	}

}