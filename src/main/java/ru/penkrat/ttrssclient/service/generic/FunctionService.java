package ru.penkrat.ttrssclient.service.generic;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Обёртка над интерфейсом {@link java.util.function.Function<T,R>}
 * 
 * 
 * @author Ruslan Penkrat
 *
 * @param <T>
 *            the type of the input to the function
 * @param <R>
 *            the type of the result of the function
 */
public class FunctionService<T, R> extends Service<R> {

	protected Function<T, R> function;

	protected final AtomicReference<T> paramValueRef = new AtomicReference<T>();

	private String message = "";

	public FunctionService(Function<T, R> function) {
		this.function = function;
	}

	public FunctionService(Function<T, R> function, String message) {
		this.function = function;
		this.message = message;
	}

	public FunctionService<T, R> setParam(T paramValue) {
		paramValueRef.set(paramValue);
		return this;
	}

	@Override
	protected Task<R> createTask() {
		final T t = paramValueRef.get();
		return new Task<R>() {

			@Override
			protected R call() throws Exception {
				updateMessage(message);
				R result = function.apply(t);
				updateMessage("");
				return result;
			}
		};
	}

	public void restart(T entity) {
		setParam(entity);
		super.restart();
	}

}