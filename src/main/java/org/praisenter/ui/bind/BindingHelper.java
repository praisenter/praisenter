package org.praisenter.ui.bind;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Supplier;

import org.praisenter.Reference;
import org.praisenter.ui.Option;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public final class BindingHelper {
	private BindingHelper() {}
	
	public static final <T, E> void bindBidirectional(Property<T> from, BooleanProperty to, T value) {
		BindingHelper.bindBidirectional(from, to, new ObjectConverter<T, Boolean>() {
			private T currentValue = value;
			@Override
			public Boolean convertFrom(T t) {
				this.currentValue = t;
				return Objects.equals(t, value);
			}
			
			@Override
			public T convertTo(Boolean e) {
				return e != null && e ? value : this.currentValue;
			}
		});
	}
	
	public static final <T> void bindBidirectional(ObjectProperty<Option<T>> from, ObjectProperty<T> to) {
		BindingHelper.bindBidirectional(from, to, new ObjectConverter<Option<T>, T>() {
			@Override
			public T convertFrom(Option<T> t) {
				if (t == null) return null;
				return t.getValue();
			}
			
			@Override
			public Option<T> convertTo(T e) {
				return new Option<T>(null, e);
			}
		});
	}
	
	/**
	 * Sets up a bidirectional binding between the given properties and uses the given mapper to convert between the
	 * property types.
	 * @param from
	 * @param to
	 * @param mapper
	 */
	public static final <T, E> void bindBidirectional(Property<T> from, Property<E> to, ObjectConverter<T, E> mapper) {
		final Reference<Boolean> isOperating = new Reference<>(false);
		// set the value of the to property to the current value of the from property
		to.setValue(mapper.convertFrom(from.getValue()));
		// NOTE: use weak references to the properties so we don't leak memory by never allowing these to get GC-ed
		from.addListener(new ChangeListener<T>() {
			final WeakReference<Property<E>> propRef = new WeakReference<Property<E>>(to);
			@Override
			public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
				Property<E> to = propRef.get();
				if (to == null) return;
				if (isOperating.get()) return;
				isOperating.set(true);
				to.setValue(mapper.convertFrom(newValue));
				isOperating.set(false);
			}
		});
		to.addListener(new ChangeListener<E>() {
			final WeakReference<Property<T>> propRef = new WeakReference<Property<T>>(from);
			@Override
			public void changed(ObservableValue<? extends E> observable, E oldValue, E newValue) {
				Property<T> from = propRef.get();
				if (from == null) return;
				if (isOperating.get()) return;
				isOperating.set(true);
				from.setValue(mapper.convertTo(newValue));
				isOperating.set(false);
			}
		});
	}
	
	/**
	 * Creates a binding where the given function is executed off of the UI thread.
	 * @param <T> the value type
	 * @param func the function to run asynchronously
	 * @param dependencies the dependencies
	 * @return ObservableValue
	 */
	public static final <T> ObservableValue<T> createAsyncObjectBinding(Supplier<T> func, Observable... dependencies) {
		return new AsyncBinding<T>(func, dependencies);
	}
}
