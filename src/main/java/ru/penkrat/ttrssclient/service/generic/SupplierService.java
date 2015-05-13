package ru.penkrat.ttrssclient.service.generic;

import java.util.function.Supplier;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Service that wraps {@link java.util.function.Supplier<R>}
 * 
 * @author Ruslan Penkrat
 *
 * @param <R>
 *            the type of results supplied by this supplier
 */
public class SupplierService<R> extends Service<R> {

	protected Supplier<R> supplier;
	private String message = "";

	public SupplierService(Supplier<R> supplier) {
		this.supplier = supplier;
	}

	public SupplierService(Supplier<R> supplier, String message) {
		this.supplier = supplier;
		this.message = message;
	}

	@Override
	protected Task<R> createTask() {
		return new Task<R>() {

			@Override
			protected R call() throws Exception {
				updateMessage(message);
				R result = supplier.get();
				updateMessage("");
				return result;
			}
		};
	}

}