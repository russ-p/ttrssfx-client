package ru.penkrat.ttrssclient.service.generic.delayed;

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
public class DelayedConsumerService<T> extends Service<Void> {

	protected Consumer<T> consumer;

	protected final AtomicReference<T> paramValueRef = new AtomicReference<T>();

	private String message = "";

	private int delay;

	public DelayedConsumerService(Consumer<T> consumer, int delay) {
		this.consumer = consumer;
		this.delay = delay;
	}

	public DelayedConsumerService(Consumer<T> consumer, String message, int delay) {
		this.consumer = consumer;
		this.message = message;
		this.delay = delay;
	}

	/**
	 * Устанавливает аргумент вызываемого метода
	 * 
	 * @param paramValue
	 *            аргумент метода
	 * @return
	 */
	public DelayedConsumerService<T> setParam(T paramValue) {
		paramValueRef.set(paramValue);
		return this;
	}

	@Override
	protected Task<Void> createTask() {
		final T t = paramValueRef.get();
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				Thread.sleep(delay);
				if (!isCancelled()) {
					updateMessage(message);
					consumer.accept(t);
				}
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