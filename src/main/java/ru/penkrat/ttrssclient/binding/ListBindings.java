package ru.penkrat.ttrssclient.binding;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ListBindings {

	public static <T, U> ObservableList<U> map(ObservableList<? extends T> sourceList,
			Function<? super T, ? extends U> f) {
		return new MappedList<>(sourceList, f);
	}

	/**
	 * Sync the content of the {@code target} list with the {@code source} list.
	 * 
	 * @return a subscription that can be used to stop syncing the lists.
	 */
	public static <T> Subscription listBind(
			List<? super T> target,
			ObservableList<? extends T> source) {
		target.clear();
		target.addAll(source);
		ListChangeListener<? super T> listener = change -> {
			while (change.next()) {
				int from = change.getFrom();
				int to = change.getTo();
				if (change.wasPermutated()) {
					target.subList(from, to).clear();
					target.addAll(from, source.subList(from, to));
				} else {
					target.subList(from, from + change.getRemovedSize()).clear();
					target.addAll(from, source.subList(from, from + change.getAddedSize()));
				}
			}
		};
		source.addListener(listener);
		return () -> source.removeListener(listener);
	}

	public static <T> Subscription includeWhen(Collection<T> collection, T element,
			ObservableValue<Boolean> condition) {
		return PipeBinding.of(condition).subscribe(new Consumer<Boolean>() {
			private boolean included = false;

			@Override
			public void accept(Boolean value) {
				if (value && !included) {
					included = collection.add(element);
				} else if (!value && included) {
					collection.remove(element);
					included = false;
				}
			}
		});
	}
}
