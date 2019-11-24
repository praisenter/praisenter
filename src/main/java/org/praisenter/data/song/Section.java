package org.praisenter.data.song;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class Section implements ReadOnlySection, Copyable {
	public static final int USE_TEMPLATE_FONT_SIZE = -1;
	
	private final StringProperty name;
	private final StringProperty text;
	
	public Section() {
		this.name = new SimpleStringProperty();
		this.text = new SimpleStringProperty();
	}
	
	public Section(String name, String text) {
		this();
		this.name.set(name);
		this.text.set(text);
	}
	
	@Override
	public String toString() {
		return this.name.get() + ": " + this.text.get();
	}
	
	@Override
	public Section copy() {
		Section v = new Section();
		v.name.set(this.name.get());
		v.text.set(this.text.get());
		return v;
	}
	
	/**
	 * Sets the name given the type, number and part.
	 * @param type the type (c, v, e, etc.)
	 * @param number the number 1-n
	 * @param part the part (a, b, c, etc.)
	 */
	public void setName(String type, int number, String part) {
		this.name.set( 
				(type == null || type.length() == 0 ? "c" : type) + 
				(number > 0 ? number : "") + 
				(part == null || part.length() == 0 ? "" : part));
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
	public String getText() {
		return this.text.get();
	}
	
	@JsonProperty
	public void setText(String text) {
		this.text.set(text);
	}
	
	@Override
	@Watchable(name = "text")
	public StringProperty textProperty() {
		return this.text;
	}
}
