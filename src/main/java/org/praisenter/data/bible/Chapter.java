package org.praisenter.data.bible;

import java.util.List;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Chapter implements ReadonlyChapter, Copyable, Comparable<Chapter> {
	private final IntegerProperty number;
	private final ObservableList<Verse> verses;
	
	public Chapter() {
		this.number = new SimpleIntegerProperty();
		this.verses = FXCollections.observableArrayList();
	}
	
	public Chapter(int number) {
		this();
		this.number.set(number);
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.number.get());
	}
	
	@Override
	public int compareTo(Chapter o) {
		return this.getNumber() - o.getNumber();
	}
	
	@Override
	public Chapter copy() {
		Chapter c = new Chapter();
		c.setNumber(this.getNumber());
		for (Verse verse : this.verses) {
			c.verses.add(verse.copy());
		}
		return c;
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
	public void setVerses(List<Verse> verses) {
		this.verses.setAll(verses);
	}
	
	@JsonProperty
	public ObservableList<Verse> getVerses() {
		return this.verses;
	}
	
	public ObservableList<? extends ReadonlyVerse> getVersesUnmodifiable() {
		return FXCollections.unmodifiableObservableList(this.verses);
	}
}
