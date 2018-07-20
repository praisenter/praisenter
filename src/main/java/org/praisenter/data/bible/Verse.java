package org.praisenter.data.bible;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class Verse implements ReadonlyVerse, Copyable, Comparable<Verse> {
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
	
	@JsonProperty
	public int getNumber() {
		return this.number.get();
	}
	
	@JsonProperty
	public void setNumber(int number) {
		this.number.set(number);
	}
	
	public IntegerProperty numberProperty() {
		return this.number;
	}
	
	@JsonProperty
	public String getText() {
		return this.text.get();
	}
	
	@JsonProperty
	public void setText(String text) {
		this.text.set(text);
	}
	
	public StringProperty textProperty() {
		return this.text;
	}
}
