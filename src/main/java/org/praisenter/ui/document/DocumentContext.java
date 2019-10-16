package org.praisenter.ui.document;

import java.util.Objects;

import org.praisenter.async.InOrderExecutionManager;
import org.praisenter.data.Persistable;
import org.praisenter.ui.undo.UndoManager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

public final class DocumentContext<T extends Persistable> {
	protected final Class<T> clazz;
	protected final ObjectProperty<T> document;

	protected final ObservableList<Object> selectedItems;
	protected final ObjectProperty<Object> selectedItem;
	protected final ObjectProperty<Class<?>> selectedType;
	protected final BooleanProperty singleTypeSelected;
	protected final IntegerProperty selectedCount;

	protected final BooleanProperty isNew;
	protected final BooleanProperty hasUnsavedChanges;
	protected final StringProperty documentName;
	
	protected final UndoManager undoManager;
	protected final InOrderExecutionManager saveExecutionManager;
	
	@SuppressWarnings("unchecked")
	public DocumentContext(T document) {
		if (document == null) throw new NullPointerException("You cannot create a document context with a null object.");

		this.clazz = (Class<T>) document.getClass();
		this.document = new SimpleObjectProperty<>(document);
		this.selectedItem = new SimpleObjectProperty<>();
		this.selectedItems = FXCollections.observableArrayList();
		this.selectedType = new SimpleObjectProperty<>();
		this.singleTypeSelected = new SimpleBooleanProperty();
		this.selectedCount = new SimpleIntegerProperty();
		
		this.isNew = new SimpleBooleanProperty();
		this.hasUnsavedChanges = new SimpleBooleanProperty();
		this.documentName = new SimpleStringProperty();
		
		this.undoManager = new UndoManager();
		this.undoManager.setTarget(document);
		
		this.saveExecutionManager = new InOrderExecutionManager();
		
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
		
		this.hasUnsavedChanges.bind(this.undoManager.notTopMarkedProperty().or(this.isNew));
		
		this.documentName.bind((document).nameProperty());
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
		return 0;
	}
	
	@Override
	public String toString() {
		T document = this.document.get();
		return document != null ? document.toString() : "null";
	}
	
	public T getDocument() {
		return this.document.get();
	}
	
	public ReadOnlyObjectProperty<T> documentProperty() {
		return this.document;
	}
	
	public Class<T> getDocumentClass() {
		return this.clazz;
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
	
	public boolean isNew() {
		return this.isNew.get();
	}
	
	public void setNew(boolean isNew) {
		this.isNew.set(isNew);
	}
	
	public BooleanProperty isNewProperty() {
		return this.hasUnsavedChanges;
	}
	
	public boolean hasUnsavedChanges() {
		return this.hasUnsavedChanges.get();
	}
	
	public ReadOnlyBooleanProperty hasUnsavedChangesProperty() {
		return this.hasUnsavedChanges;
	}
	
	public String getDocumentName() {
		return this.documentName.get();
	}
	
	public ReadOnlyStringProperty documentNameProperty() {
		return this.documentName;
	}
	
	public UndoManager getUndoManager() {
		return this.undoManager;
	}
	
	public InOrderExecutionManager getSaveExecutionManager() {
		return this.saveExecutionManager;
	}
}
