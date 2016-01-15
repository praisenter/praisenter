/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.media;

import java.util.List;
import java.util.stream.Collectors;

import org.praisenter.Tag;
import org.praisenter.javafx.FilterOption;
import org.praisenter.media.MediaType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

/**
 * Encapsulates the searching, filtering, and sorting of media.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaFilter {
	/** The master list of media items */
	private final List<MediaListItem> master;
	
	/** The filtered list of media items */
	private final ObservableList<MediaListItem> filtered;
	
	/** The media type filter */
	private final ObjectProperty<FilterOption<MediaType>> typeFilterOption;
	
	/** The tag filter */
	private final ObjectProperty<FilterOption<Tag>> tagFilterOption;
	
	/** The search */
	private final StringProperty search;
	
	/** 
	 * A flag to indicate that a search has taken place so that
	 * the assigning of other properties doesn't execute an infinite
	 * number of filter operations.
	 */
	private boolean searching;
	
	/**
	 * 
	 * @param master
	 * @param filtered
	 */
	public MediaFilter(List<MediaListItem> master, ObservableList<MediaListItem> filtered) {
		this.master = master;
		this.filtered = filtered;
		
		this.typeFilterOption = new SimpleObjectProperty<>(new FilterOption<>());
		this.tagFilterOption = new SimpleObjectProperty<>(new FilterOption<>());
		this.search = new SimpleStringProperty();
		
		this.searching = false;
		
		this.typeFilterOption.addListener(new ChangeListener<FilterOption<MediaType>>() {
			@Override
			public void changed(ObservableValue<? extends FilterOption<MediaType>> observable, FilterOption<MediaType> oldValue, FilterOption<MediaType> newValue) {
				if (searching) return;
				searching = true;
				tagFilterOption.set(new FilterOption<>());
				search.set(null);
				filter(newValue.getData(), null, null);
				searching = false;
			}
		});
		
		this.tagFilterOption.addListener(new ChangeListener<FilterOption<Tag>>() {
			@Override
			public void changed(ObservableValue<? extends FilterOption<Tag>> observable, FilterOption<Tag> oldValue, FilterOption<Tag> newValue) {
				if (searching) return;
				searching = true;
				typeFilterOption.set(new FilterOption<>());
				search.set(null);
				filter(null, newValue.getData(), null);
				searching = false;
			}
		});
		
		this.search.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (searching) return;
				searching = true;
				typeFilterOption.set(new FilterOption<>());
				tagFilterOption.set(new FilterOption<>());
				filter(null, null, newValue);
				searching = false;
			}
		});
	}
	
	/**
	 * Filter based on the current criteria.
	 */
	public final void filter() {
		filter(this.typeFilterOption.get().getData(),
			   this.tagFilterOption.get().getData(),
			   this.search.get());
	}
	
	/**
	 * Filter based on the given criteria.
	 * @param type the media type
	 * @param tag the tag
	 * @param search the search
	 */
	private final void filter(MediaType type, Tag tag, String search) {
		// filter the master list
		List<MediaListItem> filtered = this.master.stream().filter(m -> {
			// if the media is being imported
			if (!m.loaded || 
				((type == null || m.media.getMetadata().getType() == type) &&
				 (tag == null || m.media.getMetadata().getTags().contains(tag)) &&
				 (search == null || search.length() == 0 || m.media.getMetadata().getName().toLowerCase().contains(search.toLowerCase())))) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());
		
		this.filtered.setAll(filtered);
	}

	public void setTypeFilterOption(FilterOption<MediaType> type) {
		this.typeFilterOption.set(type);
	}
	
	public FilterOption<MediaType> getTypeFilterOption() {
		return typeFilterOption.get();
	}

	public ObjectProperty<FilterOption<MediaType>> typeFilterOptionProperty() {
		return this.typeFilterOption;
	}

	public void setTagFilterOption(FilterOption<Tag> tag) {
		this.tagFilterOption.set(tag);
	}
	
	public FilterOption<Tag> getTagFilterOption() {
		return tagFilterOption.get();
	}

	public ObjectProperty<FilterOption<Tag>> tagFilterOptionProperty() {
		return this.tagFilterOption;
	}
	
	public void setSearch(String search) {
		this.search.set(search);
	}
	
	public String getSearch() {
		return search.get();
	}

	public StringProperty searchProperty() {
		return this.search;
	}
}
