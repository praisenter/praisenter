package org.praisenter.data.bible;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class Verse implements ReadOnlyVerse, Copyable, Comparable<Verse> {
	private final StringProperty text;
	private final IntegerProperty number;
	
	public Verse() {
		this.text = new SimpleStringProperty();
		this.number = new SimpleIntegerProperty();
	}
	
	public Verse(int number, String text) {
		this();
		this.number.set(number);
		this.text.set(text);
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.number.get()) + " " + this.text.get();
	}
	
	@Override
	public int compareTo(Verse o) {
		return this.getNumber() - o.getNumber();
	}
	
	@Override
	public Verse copy() {
		Verse v = new Verse();
		v.setNumber(this.getNumber());
		v.setText(this.getText());
		return v;
	}
	
	@Override
	@JsonProperty
	public int getNumber() {
		return this.number.get();
	}
	
	@JsonProperty
	public void setNumber(int number) {
		this.number.set(number);
	}
	
	@Override
	@Watchable(name = "number")
	public IntegerProperty numberProperty() {
		return this.number;
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
