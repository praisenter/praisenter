package org.praisenter.data.bible;

import java.util.List;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Book implements ReadOnlyBook, Copyable, Comparable<Book> {
	private final IntegerProperty number;
	private final StringProperty name;
	private final ObservableList<Chapter> chapters;
	private final ObservableList<Chapter> chaptersReadOnly;
	
	public Book() {
		this.number = new SimpleIntegerProperty();
		this.name = new SimpleStringProperty();
		this.chapters = FXCollections.observableArrayList();
		this.chaptersReadOnly = FXCollections.unmodifiableObservableList(this.chapters);
	}
	
	public Book(int number, String name) {
		this();
		this.number.set(number);
		this.name.set(name);
	}

	@Override
	public Book copy() {
		Book b = new Book();
		b.setNumber(this.getNumber());
		b.setName(this.getName());
		for (Chapter chapter : this.chapters) {
			b.chapters.add(chapter.copy());
		}
		return b;
	}

	/**
	 * Returns the maximum chapter number for this book.
	 * @return short
	 */
	@Override
	public int getMaxChapterNumber() {
		int max = -Integer.MAX_VALUE;
		for (Chapter chapter : this.chapters) {
			int n = chapter.getNumber();
			if (n > max) {
				max = n;
			}
		}
		return max >= 0 ? max : 0;
	}
	
	/**
	 * Returns the specified chapter of this book or null if not present.
	 * @param chapter the chapter number
	 * @return {@link Chapter}
	 */
	@Override
	public Chapter getChapter(int chapter) {
		if (this.chapters.isEmpty()) {
			return null;
		}
		for (short i = 0; i < this.chapters.size(); i++) {
			Chapter chap = this.chapters.get(i);
			if (chap.getNumber() == chapter) {
				return chap;
			}
		}
		return null;
	}
	
	/**
	 * Returns the last chapter of this book.
	 * @return {@link Chapter}
	 */
	@Override
	public Chapter getLastChapter() {
		if (this.chapters.isEmpty()) {
			return null;
		}
		return this.chapters.get(this.chapters.size() - 1);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name.get();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Book o) {
		if (o == null) return 1;
		return this.number.get() - o.number.get();
	}
	
	public void renumber() {
		int n = 1;
		for (Chapter chapter : this.chapters) {
			chapter.setNumber(n++);
			chapter.renumber();
		}
	}
	
	public void reorder() {
		FXCollections.sort(this.chapters);
		for (Chapter chapter : this.chapters) {
			chapter.reorder();
		}
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
	
	@JsonProperty
	public void setChapters(List<Chapter> chapters) {
		this.chapters.setAll(chapters);
	}
	
	@JsonProperty
	@Watchable(name = "chapters")
	public ObservableList<Chapter> getChapters() {
		return this.chapters;
	}
	
	@Override
	public ObservableList<? extends ReadOnlyChapter> getChaptersUnmodifiable() {
		return this.chaptersReadOnly;
	}
}