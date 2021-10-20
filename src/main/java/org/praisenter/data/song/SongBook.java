package org.praisenter.data.song;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class SongBook implements ReadOnlySongBook, Copyable {
	private final StringProperty name;
	private final StringProperty entry;
	
	public SongBook() {
		this.name = new SimpleStringProperty();
		this.entry = new SimpleStringProperty();
	}
	
	public SongBook(String name, String type) {
		this();
		this.name.set(name);
		this.entry.set(type);
	}
	
	@Override
	public String toString() {
		return this.name.get() + " (" + this.entry.get() + ")";
	}
	
	@Override
	public SongBook copy() {
		SongBook v = new SongBook();
		v.name.set(this.name.get());
		v.entry.set(this.entry.get());
		return v;
	}
	
	@Override
	@JsonProperty
	public String getName() {
		return this.name.get();
	}
	
	@JsonProperty
	public void setName(String name) {
		this.name.set(name);
	}
	
	@Override
	@Watchable(name = "name")
	public StringProperty nameProperty() {
		return this.name;
	}
	
	@Override
	@JsonProperty
	public String getEntry() {
		return this.entry.get();
	}
	
	@JsonProperty
	public void setEntry(String entry) {
		this.entry.set(entry);
	}
	
	@Override
	@Watchable(name = "entry")
	public StringProperty entryProperty() {
		return this.entry;
	}	
}
