package ru.penkrat.ttrssclient.binding;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;

public class PipeBindingTest {

	@Test
	public void testOf() throws Exception {
		SimpleObjectProperty<Integer> sourcePropery = new SimpleObjectProperty<>(null);
		SimpleObjectProperty<Integer> targetPropery = new SimpleObjectProperty<>(0);

		PipeBinding.of(sourcePropery).subscribe(i -> targetPropery.set(i));
		assertThat(targetPropery.get()).isEqualTo(0);

		sourcePropery.setValue(Integer.MIN_VALUE);
		assertThat(targetPropery.get()).isEqualTo(Integer.MIN_VALUE);
	}

	@Test
	public void testOfNullable() throws Exception {
		SimpleObjectProperty<Integer> sourcePropery = new SimpleObjectProperty<>(null);
		SimpleObjectProperty<Integer> targetPropery = new SimpleObjectProperty<>(0);

		PipeBinding.ofNullable(sourcePropery).subscribe(i -> targetPropery.set(i));
		assertThat(targetPropery.get()).isNull();

		sourcePropery.setValue(Integer.MIN_VALUE);
		assertThat(targetPropery.get()).isEqualTo(Integer.MIN_VALUE);
	}

	@Test
	public void testFilterNullable() throws Exception {
		SimpleObjectProperty<Integer> sourcePropery = new SimpleObjectProperty<>(null);
		SimpleObjectProperty<Integer> targetPropery = new SimpleObjectProperty<>(0);

		PipeBinding.ofNullable(sourcePropery)
				.filter(Objects::nonNull)
				.subscribe(i -> targetPropery.set(i));

		assertThat(targetPropery.get()).isEqualTo(0);

		sourcePropery.setValue(Integer.MIN_VALUE);
		assertThat(targetPropery.get()).isEqualTo(Integer.MIN_VALUE);
	}

	@Test
	public void testFilter() throws Exception {
		SimpleObjectProperty<Integer> sourcePropery = new SimpleObjectProperty<>(-1);
		SimpleObjectProperty<Integer> targetPropery = new SimpleObjectProperty<>(0);

		PipeBinding.of(sourcePropery)
				.filter(i -> i > 0)
				.subscribe(i -> targetPropery.set(i));

		assertThat(targetPropery.get()).isEqualTo(0);

		sourcePropery.setValue(Integer.MIN_VALUE);
		assertThat(targetPropery.get()).isEqualTo(0);

		sourcePropery.setValue(Integer.MAX_VALUE);
		assertThat(targetPropery.get()).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	public void testMap() throws Exception {
		Pair<String, String> src = new Pair<>("KEY", "VALUE");
		SimpleObjectProperty<Pair<String, String>> sourcePropery = new SimpleObjectProperty<>();
		SimpleStringProperty targetPropery = new SimpleStringProperty();

		PipeBinding.of(sourcePropery)
				.map(Pair::getKey)
				.map(key -> key + " = ")
				.subscribe(targetPropery);

		assertThat(targetPropery.get()).isNull();

		sourcePropery.set(src);
		assertThat(targetPropery.get()).isEqualTo("KEY = ");
	}

	@Test
	public void testOrElse() throws Exception {
		Pair<String, String> src = new Pair<>("KEY", "VALUE");
		SimpleObjectProperty<Pair<String, String>> sourcePropery = new SimpleObjectProperty<>();
		SimpleStringProperty targetPropery = new SimpleStringProperty();

		PipeBinding.of(sourcePropery)
				.map(Pair::getKey)
				.map(key -> key + " = ")
				.orElse("IS_EMPTY")
				.subscribe(targetPropery);

		assertThat(targetPropery.get()).isEqualTo("IS_EMPTY");

		sourcePropery.set(src);
		assertThat(targetPropery.get()).isEqualTo("KEY = ");
	}

	@Test
	public void testMapFilterPipe() throws Exception {
		SimpleObjectProperty<Integer> sourcePropery = new SimpleObjectProperty<>(1);
		SimpleObjectProperty<Integer> targetPropery = new SimpleObjectProperty<>(0);

		PipeBinding.of(sourcePropery)
				.map(i -> i * 10)
				.map(i -> i * 2)
				.filter(i -> i > 1)
				.subscribe(targetPropery);

		assertThat(targetPropery.get()).isEqualTo(20);
	}

	@Test
	public void testPipeCalculationCount() throws Exception {
		SimpleObjectProperty<Integer> sourcePropery = new SimpleObjectProperty<>(1);
		SimpleObjectProperty<Integer> targetPropery = new SimpleObjectProperty<>(0);
		AtomicInteger counter = new AtomicInteger(0);
		ObjectBinding<Integer> src = new ObjectBinding<Integer>() {
			{
				bind(sourcePropery);
			}

			@Override
			protected Integer computeValue() {
				counter.incrementAndGet();
				return sourcePropery.getValue();
			}
		};

		PipeBinding.of(src)
				.filter(i -> i > 1)
				.orElse(1)
				.map(i -> i * 10)
				.filter(i -> i > 1)
				.map(i -> i * 10)
				.subscribe(targetPropery);

		assertThat(targetPropery.get()).isEqualTo(100);
		assertThat(counter).hasValue(1);

		sourcePropery.setValue(2);
		assertThat(targetPropery.get()).isEqualTo(200);
		assertThat(counter).hasValue(2);
	}

	@Test
	public void testLazyFilter() throws Exception {
		SimpleObjectProperty<Integer> sourcePropery = new SimpleObjectProperty<>(1);
		SimpleObjectProperty<Integer> targetPropery = new SimpleObjectProperty<>(0);
		AtomicInteger counter = new AtomicInteger(0);

		assertThat(counter).hasValue(0);
		PipeBinding<Integer> pipeBinding = PipeBinding.of(sourcePropery)
				.filter(i -> {
					counter.incrementAndGet();
					return i > 1;
				});
		assertThat(counter).as("filter call count after 'of' must be 0 ").hasValue(0);

		pipeBinding.subscribe(targetPropery);
		assertThat(counter).hasValue(1);

		pipeBinding.dispose();
	}

}
