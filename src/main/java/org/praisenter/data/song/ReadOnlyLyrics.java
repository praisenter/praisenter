package org.praisenter.data.song;

import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.Localized;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;

public interface ReadOnlyLyrics extends Copyable, Localized {
	public UUID getId();
	public boolean isOriginal();
	public String getLanguage();
	public String getTransliteration();
	public String getTitle();
	
	public ReadOnlyObjectProperty<UUID> idProperty();
	public ReadOnlyBooleanProperty originalProperty();
	public ReadOnlyStringProperty languageProperty();
	public ReadOnlyStringProperty transliterationProperty();
	public ReadOnlyStringProperty titleProperty();
	
	public ObservableList<? extends ReadOnlyAuthor> getAuthorsUnmodifiable();
	public ObservableList<? extends ReadOnlySongBook> getSongBooksUnmodifiable();
	public ObservableList<? extends ReadOnlySection> getSectionsUnmodifiable();
	
	public ReadOnlyAuthor getDefaultAuthor();
	public ReadOnlySection getSectionByName(String name);
	public ReadOnlySection getSectionById(UUID id);
}
