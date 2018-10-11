package org.praisenter.ui;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;

public final class DocumentContext<T> {
	private final ObjectProperty<T> document;
	private final ObservableList<Object> selectedItems;
	private final ObjectProperty<Object> selectedItem;
	private final ObjectProperty<Class<?>> selectedType;
	private final BooleanProperty singleTypeSelected;
	private final IntegerProperty selectedCount;
	
	public DocumentContext(T document) {
		this.document = new SimpleObjectProperty<>(document);
		this.selectedItem = new SimpleObjectProperty<>();
		this.selectedItems = FXCollections.observableArrayList();
		this.selectedType = new SimpleObjectProperty<>();
		this.singleTypeSelected = new SimpleBooleanProperty();
		this.selectedCount = new SimpleIntegerProperty();
		
		this.selectedItems.addListener((Change<? extends Object> c) -> {
			Class<?> clazz = null;
			for (Object item : this.selectedItems) {
				if (item != null) {
					if (clazz == null) {
						clazz = item.getClass();
					} else if (!item.getClass().equals(clazz)) {
						clazz = null;
						break;
					}
				}
			}
			
			this.selectedType.set(clazz);
			this.singleTypeSelected.set(clazz != null);
			
			int size = this.selectedItems.size();
			
			this.selectedCount.set(size);
			this.selectedItem.set(size == 1 ? this.selectedItems.get(0) : null);
		});
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof DocumentContext) {
			return Objects.equals(((DocumentContext<?>) obj).document.get(), this.document.get());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		T document = this.document.get();
		if (document != null) {
			return document.hashCode();
		}
		// TODO what's the hashcode of null?
		return 0;
	}
	
	public T getDocument() {
		return this.document.get();
	}
	
	public ReadOnlyObjectProperty<T> documentProperty() {
		return this.document;
	}
	
	// TODO i don't like this being publically writable, but we need it to be writable in the document editors
	public ObservableList<Object> getSelectedItems() {
		return this.selectedItems;
	}
	
	public Object getSelectedItem() {
		return this.selectedItem.get();
	}
	
	public ReadOnlyObjectProperty<Object> selectedItemProperty() {
		return this.selectedItem;
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
