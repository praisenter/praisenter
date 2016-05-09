package org.praisenter.javafx.bible;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.praisenter.bible.Bible;

public final class BibleTableItem {
	final Bible bible;
	
	private final ReadOnlyIntegerProperty id;
	private final ReadOnlyStringProperty name;
	private final ReadOnlyStringProperty language;
	private final ReadOnlyStringProperty source;
	
	public BibleTableItem(Bible bible) {
		this.bible = bible;
		
		id = new SimpleIntegerProperty(bible.getId());
		name = new SimpleStringProperty(bible.getName());
		language = new SimpleStringProperty(bible.getLanguage());
		source = new SimpleStringProperty(bible.getSource());
	}
	
	public int getId() {
		return this.id.get();
	}
	
	public ReadOnlyIntegerProperty idProperty() {
		return this.id;
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public ReadOnlyStringProperty nameProperty() {
		return this.name;
	}
}
