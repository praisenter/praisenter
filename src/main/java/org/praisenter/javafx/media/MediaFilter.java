package org.praisenter.javafx.media;

import java.util.List;

import org.praisenter.Tag;
import org.praisenter.javafx.FilterOption;
import org.praisenter.media.MediaType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

public final class MediaFilter {
	private final ObjectProperty<FilterOption<MediaType>> type;
	private final ObjectProperty<FilterOption<Tag>> tag;
	private final StringProperty search;
	
	public MediaFilter() {
		type = new SimpleObjectProperty<>();
		tag = new SimpleObjectProperty<>();
		search = new SimpleStringProperty();
	}
	
	public ObjectProperty<FilterOption<MediaType>> typeProperty() {
		return type;
	}

	public ObjectProperty<FilterOption<Tag>> tagProperty() {
		return tag;
	}
	
	public StringProperty searchProperty() {
		return search;
	}
}
