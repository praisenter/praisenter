package org.praisenter.ui;

import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionModel;

public final class SelectionInfo<T> {
	private final ObjectProperty<Class<?>> selectedType;
	private final BooleanProperty singleTypeSelected;
	private final IntegerProperty selectedCount;
	
	private SelectionInfo() {
		this.selectedType = new SimpleObjectProperty<>();
		this.singleTypeSelected = new SimpleBooleanProperty();
		this.selectedCount = new SimpleIntegerProperty();
		
		this.singleTypeSelected.bind(Bindings.createBooleanBinding(() -> {
			return this.selectedType.get() != null;
		}, this.selectedType));
	}
	
	public SelectionInfo(SelectionModel<T> model, Function<T, Class<?>> extractor) {
		this();
		this.selectedType.bind(Bindings.createObjectBinding(() -> {
			T item = model.getSelectedItem();
			if (item != null) {
				return extractor.apply(item);
			} else {
				return null;
			}
		}, model.selectedItemProperty()));
		
		this.selectedCount.bind(Bindings.createIntegerBinding(() -> {
			return model.getSelectedItem() != null ? 1 : 0;
		}, model.selectedItemProperty()));
	}
	
	public SelectionInfo(MultipleSelectionModel<T> model, Function<T, Class<?>> extractor) {
		this();
		this.selectedType.bind(Bindings.createObjectBinding(() -> {
			Class<?> clazz = null;
			for (T item : model.getSelectedItems()) {
				if (clazz == null) {
					clazz = extractor.apply(item);
				} else if (!extractor.apply(item).equals(clazz)) {
					clazz = null;
					break;
				}
			}
			return clazz;
		}, model.getSelectedItems()));
		
		this.selectedCount.bind(Bindings.createIntegerBinding(() -> {
			return model.getSelectedItems().size();
		}, model.getSelectedItems()));
	}
	
	public Class<?> getSelectedType() {
		return this.selectedType.get();
	}
	
	public ReadOnlyObjectProperty<Class<?>> selectedTypeProperty() {
		return this.selectedType;
	}
	
	public int getSelectedCount() {
		return this.selectedCount.get();
	}
	
	public ReadOnlyIntegerProperty selectedCountProperty() {
		return this.selectedCount;
	}
	
	public boolean isSingleTypeSelected() {
		return this.singleTypeSelected.get();
	}
	
	public ReadOnlyBooleanProperty singleTypeSelectedProperty() {
		return this.singleTypeSelected;
	}
}
