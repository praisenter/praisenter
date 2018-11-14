package org.praisenter.data.bible;

import java.util.List;

import org.praisenter.Editable;
import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Chapter implements ReadOnlyChapter, Copyable, Comparable<Chapter> {
	private final IntegerProperty number;
	private final ObservableList<Verse> verses;
	private final ObservableList<Verse> versesReadOnly;
	
	public Chapter() {
		this.number = new SimpleIntegerProperty();
		this.verses = FXCollections.observableArrayList();
		this.versesReadOnly = FXCollections.unmodifiableObservableList(this.verses);
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

	public void renumber() {
		int n = 1;
		for (Verse verse : this.verses) {
			verse.setNumber(n++);
		}
	}
	
	public void reorder() {
		FXCollections.sort(this.verses);
	}
	
	public int getMaxVerseNumber() {
		int max = -Integer.MAX_VALUE;
		for (Verse verse : this.verses) {
			int n = verse.getNumber();
			if (n > max) {
				max = n;
			}
		}
		return max >= 0 ? max : 0;
	}
	
	@JsonProperty
	public int getNumber() {
		return this.number.get();
	}
	
	@JsonProperty
	public void setNumber(int number) {
		this.number.set(number);
	}
	
	@Override
	@Editable("number")
	public IntegerProperty numberProperty() {
		return this.number;
	}
	
	@JsonProperty
	public void setVerses(List<Verse> verses) {
		this.verses.setAll(verses);
	}
	
	@JsonProperty
	@Editable("verses")
	public ObservableList<Verse> getVerses() {
		return this.verses;
	}
	
	@Override
	public ObservableList<? extends ReadOnlyVerse> getVersesUnmodifiable() {
		return this.versesReadOnly;
	}
}
