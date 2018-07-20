package org.praisenter.javafx.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class PropertyList<T> extends VBox {
	private final ObjectProperty<T> data = new SimpleObjectProperty<T>();
	
	private final ObservableList<PropertyListProperty<T>> properties;
	private final ObservableList<PropertyListItem> items;
	private final TableView<PropertyListItem> table;
	
	public PropertyList() {
		this.properties = FXCollections.observableArrayList();
		this.items = FXCollections.observableArrayList();
		this.table = new TableView<PropertyListItem>(this.items);
		
		TableColumn<PropertyListItem, String> name = new TableColumn<PropertyListItem, String>("name");
		TableColumn<PropertyListItem, String> value = new TableColumn<PropertyListItem, String>("value");
		
		name.setCellValueFactory((f) -> {
			return f.getValue().nameProperty();
		});
		value.setCellValueFactory((f) -> {
			return f.getValue().valueProperty();
		});
		
		this.table.getColumns().add(name);
		this.table.getColumns().add(value);
		
		this.data.addListener((obs, ov, nv) -> {
			this.buildPropertySet(nv);
		});
	}
	
	private void buildPropertySet(T data) {
		List<PropertyListItem> items = new ArrayList<>();
		if (data == null) {
			this.items.clear();
			return;
		}
		for (PropertyListProperty<T> prop : this.properties) {
			items.add(new PropertyListItem(prop.getName(), prop.getValue().apply(data)));
		}
		this.items.setAll(items);
	}
	
	public void addProperty(String name, Function<T, StringProperty> value) {
		this.properties.add(new PropertyListProperty<T>(name, value));
	}
	
	public void setData(T data) {
		this.data.set(data);
	}
	
	public T getData() {
		return this.data.get();
	}
	
	public ObjectProperty<T> dataProperty() {
		return this.data;
	}
}
