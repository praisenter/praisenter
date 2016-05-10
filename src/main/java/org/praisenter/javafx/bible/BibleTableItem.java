package org.praisenter.javafx.bible;

import org.praisenter.bible.Bible;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public final class BibleTableItem {
	final Bible bible;
	final String loadingName;
	
	private final ReadOnlyIntegerProperty id;
	private final ReadOnlyStringProperty name;
	private final ReadOnlyStringProperty language;
	private final ReadOnlyStringProperty source;
	
	public BibleTableItem(Bible bible) {
		this.bible = bible;
		this.loadingName = null;
		
		id = new SimpleIntegerProperty(bible.getId());
		name = new SimpleStringProperty(bible.getName());
		language = new SimpleStringProperty(bible.getLanguage());
		source = new SimpleStringProperty(bible.getSource());
	}
	
	public BibleTableItem(String loadingName) {
		this.bible = null;
		this.loadingName = loadingName;
		
		id = new SimpleIntegerProperty();
		name = new SimpleStringProperty();
		language = new SimpleStringProperty();
		source = new SimpleStringProperty();
	}
	
	public boolean isLoading() {
		return this.loadingName != null;
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
	
	public String getLanguage() {
		return this.language.get();
	}
	
	public ReadOnlyStringProperty languageProperty() {
		return this.language;
	}

	public String getSource() {
		return this.source.get();
	}
	
	public ReadOnlyStringProperty sourceProperty() {
		return this.source;
	}
}
