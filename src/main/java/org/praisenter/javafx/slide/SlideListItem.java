package org.praisenter.javafx.slide;

import org.praisenter.Tag;
import org.praisenter.slide.Slide;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

final class SlideListItem implements Comparable<SlideListItem> {
	/** The slide name */
	private final StringProperty name = new SimpleStringProperty();
	
	/** The slide; can be null */
	private final ObjectProperty<Slide> slide = new SimpleObjectProperty<Slide>(null);
	
	/** True if the slide is present (or loaded) */
	private final BooleanProperty loaded = new SimpleBooleanProperty(false);

	/** An observable list of tags to maintain */
	private final ObservableSet<Tag> tags = FXCollections.observableSet();
	
	/**
	 * Optional constructor for pending items.
	 * @param name the name
	 */
	public SlideListItem(String name) {
		this.name.set(name);
		this.slide.set(null);
		this.loaded.set(false);
	}
	
	/**
	 * Optional constructor for existing items.
	 * @param slide the slide
	 */
	public SlideListItem(Slide slide) {
		this.name.set(slide.getName());
		this.slide.set(slide);
		this.loaded.set(true);
		this.tags.addAll(slide.getTags());
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
	public int compareTo(SlideListItem o) {
		// sort loaded last
		// then sort by media (name)
		if (this.loaded.get() && o.loaded.get()) {
			return this.slide.get().compareTo(o.slide.get());
		} else if (this.loaded.get()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	/**
	 * Returns the name of this item.
	 * @return String
	 */
	public String getName() {
		return this.name.get();
	}
	
	/**
	 * Sets the name of this item.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name.set(name);
	}
	
	/**
	 * Returns the name property.
	 * @return StringProperty
	 */
	public StringProperty nameProperty() {
		return this.name;
	}
	
	/**
	 * Returns the slide or null.
	 * @return {@link Slide}
	 */
	public Slide getSlide() {
		return this.slide.get();
	}
	
	/**
	 * Sets the slide.
	 * @param slide the slide
	 */
	public void setSlide(Slide slide) {
		this.slide.set(slide);
	}
	
	/**
	 * Returns the slide property.
	 * @return ObjectProperty&lt;{@link Slide}&gt;
	 */
	public ObjectProperty<Slide> slideProperty() {
		return this.slide;
	}
	
	/**
	 * Returns true if this slide is loaded.
	 * @return boolean
	 */
	public boolean isLoaded() {
		return this.loaded.get();
	}
	
	/**
	 * Sets if this slide has been loaded.
	 * @param loaded true if loaded
	 */
	public void setLoaded(boolean loaded) {
		this.loaded.set(loaded);
	}
	
	/**
	 * Returns the loaded property.
	 * @return BooleanProperty
	 */
	public BooleanProperty loadedProperty() {
		return this.loaded;
	}
}
