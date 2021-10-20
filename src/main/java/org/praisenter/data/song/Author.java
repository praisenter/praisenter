package org.praisenter.data.song;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class Author implements ReadOnlyAuthor, Copyable {
	public static final String TYPE_LYRICS = "lyrics";
	public static final String TYPE_MUSIC = "music";
	public static final String TYPE_TRANSLATION = "translation";
	
	private final StringProperty name;
	private final StringProperty type;
	
	public Author() {
		this.name = new SimpleStringProperty();
		this.type = new SimpleStringProperty();
	}
	
	public Author(String name, String type) {
		this();
		this.name.set(name);
		this.type.set(type);
	}
	
	@Override
	public String toString() {
		return this.name.get() + " (" + this.type.get() + ")";
	}
	
	@Override
	public Author copy() {
		Author v = new Author();
		v.name.set(this.name.get());
		v.type.set(this.type.get());
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
	public String getType() {
		return this.type.get();
	}
	
	@JsonProperty
	public void setType(String type) {
		this.type.set(type);
	}
	
	@Override
	@Watchable(name = "type")
	public StringProperty typeProperty() {
		return this.type;
	}	
}
