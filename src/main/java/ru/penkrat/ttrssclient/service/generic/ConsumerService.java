package ru.penkrat.ttrssclient.service.generic;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * 
 * Обёртка над интерфейсом {@link java.util.function.Consumer<T>}
 * 
 * @author Пенкрат Руслан
 *
 * @param <T>
 *            the type of the input to the operation
 */
public class ConsumerService<T> extends Service<Void> {

	protected Consumer<T> consumer;

	protected final AtomicReference<T> paramValueRef = new AtomicReference<T>();

	private String message = "";

	public ConsumerService(Consumer<T> consumer) {
		this.consumer = consumer;
	}

	public ConsumerService(Consumer<T> consumer, String message) {
		this.consumer = consumer;
		this.message = message;
	}

	/**
	 * Устанавливает аргумент вызываемого метода
	 * 
	 * @param paramValue
	 *            аргумент метода
	 * @return
	 */
	public ConsumerService<T> setParam(T paramValue) {
		paramValueRef.set(paramValue);
		return this;
	}

	@Override
	protected Task<Void> createTask() {
		final T t = paramValueRef.get();
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				updateMessage(message);
				consumer.accept(t);
				updateMessage("");
				return null;
			}
		};
	}

	public void restart(T paramValue) {
		setParam(paramValue);
		super.restart();
	}
}