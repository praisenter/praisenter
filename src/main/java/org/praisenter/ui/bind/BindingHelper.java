package org.praisenter.ui.bind;

import java.util.Objects;

import org.praisenter.Reference;
import org.praisenter.ui.Option;

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
		from.addListener(new ChangeListener<T>() {
			@Override
			public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
				if (isOperating.get()) return;
				isOperating.set(true);
				to.setValue(mapper.convertFrom(newValue));
				isOperating.set(false);
			}
		});
		to.addListener(new ChangeListener<E>() {
			@Override
			public void changed(ObservableValue<? extends E> observable, E oldValue, E newValue) {
				if (isOperating.get()) return;
				isOperating.set(true);
				from.setValue(mapper.convertTo(newValue));
				isOperating.set(false);
			}
		});
	}
}
