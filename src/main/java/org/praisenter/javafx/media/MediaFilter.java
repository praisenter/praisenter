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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.praisenter.Tag;
import org.praisenter.javafx.Option;
import org.praisenter.media.MediaType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

/**
 * Encapsulates the searching, filtering, and sorting of media in the {@link MediaLibraryPane}.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaFilter {
	// lists
	
	/** The master list of media items */
	private final List<MediaListItem> master;
	
	/** The filtered list of media items */
	private final ObservableList<MediaListItem> filtered;
	
	// filters
	
	/** The media type filter */
	private final ObjectProperty<Option<MediaType>> typeFilterOption;
	
	/** The tag filter */
	private final ObjectProperty<Option<Tag>> tagFilterOption;
	
	/** The search */
	private final StringProperty search;
	
	// sorting
	
	/** The sort property */
	private final ObjectProperty<Option<Integer>> sort;
	
	/** The sort direction */
	private final BooleanProperty sortDescending;
	
	/** 
	 * A flag to indicate that a search has taken place so that
	 * the assigning of other properties doesn't execute an infinite
	 * number of filter operations.
	 */
	private boolean searching;
	
	/**
	 * Creates a new media filter.
	 * @param master the list containing all the media items
	 * @param filtered the filtered and sorted list of media items
	 */
	public MediaFilter(List<MediaListItem> master, ObservableList<MediaListItem> filtered) {
		this.master = master;
		this.filtered = filtered;
		
		this.typeFilterOption = new SimpleObjectProperty<>(new Option<MediaType>());
		this.tagFilterOption = new SimpleObjectProperty<>(new Option<Tag>());
		this.search = new SimpleStringProperty();
		
		this.sort = new SimpleObjectProperty<Option<Integer>>(new Option<Integer>("Name", 0));
		this.sortDescending = new SimpleBooleanProperty(false);
		
		this.searching = false;
		
		this.typeFilterOption.addListener(new ChangeListener<Option<MediaType>>() {
			@Override
			public void changed(ObservableValue<? extends Option<MediaType>> observable, Option<MediaType> oldValue, Option<MediaType> newValue) {
				if (searching) return;
				searching = true;
				search.set(null);
				filter();
				searching = false;
			}
		});
		
		this.tagFilterOption.addListener(new ChangeListener<Option<Tag>>() {
			@Override
			public void changed(ObservableValue<? extends Option<Tag>> observable, Option<Tag> oldValue, Option<Tag> newValue) {
				if (searching) return;
				searching = true;
				search.set(null);
				filter();
				searching = false;
			}
		});
		
		this.search.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (searching) return;
				searching = true;
				typeFilterOption.set(new Option<>());
				tagFilterOption.set(new Option<>());
				filter();
				searching = false;
			}
		});
		
		this.sort.addListener(new ChangeListener<Option<Integer>>() {
			@Override
			public void changed(ObservableValue<? extends Option<Integer>> observable, Option<Integer> oldValue, Option<Integer> newValue) {
				if (searching) return;
				searching = true;
				filter();
				searching = false;
			}
		});
		
		this.sortDescending.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (searching) return;
				searching = true;
				filter();
				searching = false;
			}
		});
	}
	
	/**
	 * Filter based on the current criteria.
	 */
	public final void filter() {
		// get input
		MediaType type = this.typeFilterOption.get().getValue();
		Tag tag = this.tagFilterOption.get().getValue();
		String search = this.search.get();
		int sort = this.sort.get().getValue();
		boolean desc = this.sortDescending.get();
		
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
		})
		.sorted(new Comparator<MediaListItem>() {
			@Override
			public int compare(MediaListItem o1, MediaListItem o2) {
				int value = 0;
				if (sort == 0) {
					value = o1.name.compareTo(o2.name);
				} else {
					// check for loaded vs. not loaded media
					// sort non-loaded media to the end
					if (o1.media == null && o2.media == null) return 0;
					if (o1.media == null && o2.media != null) return 1;
					if (o1.media != null && o2.media == null) return -1;
					
					if (sort == 1) {
						value = o1.media.getMetadata().getType().compareTo(o2.media.getMetadata().getType());
					} else {
						value = -1 * Long.compare(o1.media.getMetadata().getLastModified(), o2.media.getMetadata().getLastModified());
					}
				}
				return (desc ? 1 : -1) * value;
			}
		})
		.collect(Collectors.toList());
		
		this.filtered.setAll(filtered);
	}

	public void setTypeFilterOption(Option<MediaType> type) {
		this.typeFilterOption.set(type);
	}
	
	public Option<MediaType> getTypeFilterOption() {
		return typeFilterOption.get();
	}

	public ObjectProperty<Option<MediaType>> typeFilterOptionProperty() {
		return this.typeFilterOption;
	}

	public void setTagFilterOption(Option<Tag> tag) {
		this.tagFilterOption.set(tag);
	}
	
	public Option<Tag> getTagFilterOption() {
		return tagFilterOption.get();
	}

	public ObjectProperty<Option<Tag>> tagFilterOptionProperty() {
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
	
	public ObjectProperty<Option<Integer>> sortProperty() {
		return this.sort;
	}
	
	public Option<Integer> getSort() {
		return this.sort.get();
	}
	
	public void setSort(Option<Integer> sort) {
		this.sort.set(sort);
	}
	
	public BooleanProperty sortDescendingProperty() {
		return this.sortDescending;
	}
	
	public boolean getSortDescending() {
		return this.sortDescending.get();
	}
	
	public void setSortDescending(boolean flag) {
		this.sortDescending.set(flag);
	}
}
