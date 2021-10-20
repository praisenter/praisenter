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
package org.praisenter.slide;

import java.awt.image.BufferedImage;
import java.text.Collator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.praisenter.Constants;
import org.praisenter.ReadonlyIterator;
import org.praisenter.bible.BibleReferenceTextStore;
import org.praisenter.data.Tag;
import org.praisenter.data.TextItem;
import org.praisenter.data.TextStore;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.data.json.BufferedImagePngJsonDeserializer;
import org.praisenter.data.json.BufferedImagePngJsonSerializer;
import org.praisenter.data.json.InstantJsonDeserializer;
import org.praisenter.data.json.InstantJsonSerializer;
import org.praisenter.slide.animation.Animation;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.text.TextPlaceholderComponent;
import org.praisenter.song.SongReferenceTextStore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Implementation of the {@link Slide} interface.
 * @author William Bittle
 * @version 3.0.0
 */
public class BasicSlide extends AbstractSlideRegion implements Slide, SlideRegion, Comparable<Slide> {
	/** The collator for locale dependent sorting */
	private static final Collator COLLATOR = Collator.getInstance();
	
	/** The format (for format identification only) */
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	final String format;
	
	/** The slide format version */
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	final String version;

	/** The slide name */
	@JsonProperty
	String name;

	/** The date the slide was created */
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	Instant createdDate;

	/** The date the slide was last changed */
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	Instant lastModifiedDate;

	/** The time the slide will show in milliseconds */
	@JsonProperty
	long time;
	
	/** The slide components */
	@JsonProperty
	final List<SlideComponent> components;

	/** The slide animations */
	@JsonProperty
	final List<SlideAnimation> animations;

	/** Any placeholder data */
	@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY)
	@JsonSubTypes({
		@Type(value = BibleReferenceTextStore.class, name = "bibleTextStore"),
		@Type(value = SongReferenceTextStore.class, name = "songTextStore")
	})
	TextStore placeholderData;
	
	/** The tags */
	@JsonProperty
	final Set<Tag> tags;
	
	/** The thumbnail for this slide */
	@JsonProperty
	@JsonSerialize(using = BufferedImagePngJsonSerializer.class)
	@JsonDeserialize(using = BufferedImagePngJsonDeserializer.class)
	BufferedImage thumbnail;
	
	/**
	 * Default constructor.
	 */
	public BasicSlide() {
		this(Constants.FORMAT_NAME, Slide.CURRENT_VERSION);
	}
	
	@JsonCreator
	private BasicSlide(
			@JsonProperty("format") String format, 
			@JsonProperty("version") String version) {
		this(format, version, UUID.randomUUID());
	}

	/**
	 * Constructor for setting an explicit id.
	 * @param id the id
	 */
	public BasicSlide(UUID id) {
		this(Constants.FORMAT_NAME, Slide.CURRENT_VERSION, UUID.randomUUID());
	}
	
	/**
	 * Constructor for setting an explicit id.
	 * @param id the id
	 */
	private BasicSlide(
			String format, 
			String version, 
			UUID id) {
		super(id);
		this.format = format;
		this.version = version;
		this.components = new ArrayList<SlideComponent>();
		this.animations = new ArrayList<SlideAnimation>();
		this.placeholderData = null;
		this.time = Slide.TIME_FOREVER;
		this.tags = new TreeSet<Tag>();
		this.createdDate = Instant.now();
		this.lastModifiedDate = this.createdDate;
	}
	
	/**
	 * Copy constructor.
	 * @param other the component to copy
	 * @param exact whether to copy the component exactly
	 */
	public BasicSlide(BasicSlide other, boolean exact) {
		// copy over the super class stuff
		super(other, exact);
		
		this.format = other.format;
		this.version = other.version;
		
		this.components = new ArrayList<SlideComponent>();
		this.animations = new ArrayList<SlideAnimation>();
		this.placeholderData = other.placeholderData != null ? other.placeholderData.copy() : null;
		this.tags = new TreeSet<Tag>();
		
		if (exact) {
			this.createdDate = other.createdDate;
			this.lastModifiedDate = other.lastModifiedDate;
		} else {
			this.createdDate = Instant.now();
			this.lastModifiedDate = this.createdDate;
		}
		
		this.time = other.time;
		this.name = other.name;
		
		// copy the components
		for (int i = 0; i < other.components.size(); i++) {
			SlideComponent sc = other.components.get(i);
			SlideComponent copy = sc.copy(exact);
			this.components.add(copy);
			
			// component animations
			List<SlideAnimation> animations = other.getAnimations(sc.getId());
			for(SlideAnimation animation : animations) {
				this.animations.add(animation.copy(copy.getId()));
			}
		}
		
		// slide animations
		List<SlideAnimation> animations = other.getAnimations(other.getId());
		for(SlideAnimation animation : animations) {
			this.animations.add(animation.copy(this.id));
		}
		
		// tags
		this.tags.addAll(other.tags);
		
		// thumbnail
		this.thumbnail = other.thumbnail;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy()
	 */
	@Override
	public BasicSlide copy() {
		return this.copy(false);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy(boolean)
	 */
	@Override
	public BasicSlide copy(boolean exact) {
		return new BasicSlide(this, exact);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#as(org.praisenter.slide.Slide)
	 */
	public void as(Slide slide) {
		this.id = slide.getId();
		this.name = slide.getName();
		this.createdDate = slide.getCreatedDate();
		this.lastModifiedDate = slide.getLastModifiedDate();
		this.thumbnail = slide.getThumbnail();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Slide other) {
		if (other == null) {
			return -1;
		} else if (other.getName() == null) {
			return -1;
		} else if (this.name == null) {
			return 1;
		} else {
			return COLLATOR.compare(this.name, other.getName());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getCreatedDate()
	 */
	@Override
	public Instant getCreatedDate() {
		return this.createdDate;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getLastModifiedDate()
	 */
	@Override
	public Instant getLastModifiedDate() {
		return this.lastModifiedDate;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#setLastModifiedDate(java.time.Instant)
	 */
	@Override
	public void setLastModifiedDate(Instant date) {
		this.lastModifiedDate = date;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getTime()
	 */
	@Override
	public long getTime() {
		return time;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#setTime(long)
	 */
	@Override
	public void setTime(long time) {
		this.time = time;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getTotalTime()
	 */
	@Override
	public long getTotalTime() {
		long time = this.time;
		if (time == Slide.TIME_FOREVER) {
			return time;
		}
		long max = 0;
		List<SlideAnimation> animations = this.getAnimations(this.id);
		if (animations != null && !animations.isEmpty()) {
			for (SlideAnimation animation : animations) {
				Animation ani = animation.getAnimation();
				if (ani.getRepeatCount() == Animation.INFINITE) {
					return Slide.TIME_FOREVER;
				}
				if (ani.getType() == AnimationType.IN) {
					long tx = Math.max(0, ani.getDuration()) * Math.max(1, ani.getRepeatCount()) * (ani.isAutoReverse() ? 2 : 1);
					tx += Math.max(0, ani.getDelay());
					if (tx > max) {
						max = tx;
					}
				}
			}
		}
		return time + max;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#addComponent(org.praisenter.slide.SlideComponent)
	 */
	@Override
	public void addComponent(SlideComponent component) {
		this.components.add(component);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#removeComponent(org.praisenter.slide.SlideComponent)
	 */
	@Override
	public boolean removeComponent(SlideComponent component) {
		// no re-sort required here
		if (this.components.remove(component)) {
			this.animations.removeIf(st -> st.getId().equals(component.getId()));
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getComponentIterator()
	 */
	@Override
	public Iterator<SlideComponent> getComponentIterator() {
		return new ReadonlyIterator<SlideComponent>(this.components.iterator());
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getComponents(java.lang.Class)
	 */
	@Override
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz) {
		List<E> components = new ArrayList<E>();
		for (SlideComponent component : this.components) {
			if (clazz.isInstance(component)) {
				components.add(clazz.cast(component));
			}
		}
		return components;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getComponent(java.util.UUID)
	 */
	@Override
	public SlideComponent getComponent(UUID id) {
		for (SlideComponent component : this.components) {
			if (component.getId().equals(id)) {
				return component;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#moveComponentUp(org.praisenter.slide.SlideComponent)
	 */
	@Override
	public void moveComponentUp(SlideComponent component) {
		// move the given component up in the order
		
		// get all the components
		List<SlideComponent> components = this.components;
		// verify the component exists on this slide
		if (components.contains(component) && components.size() > 0) {
			int size = components.size();
			int index = components.indexOf(component);
			// see if the component is already in the last position
			if (components.get(size - 1).equals(component)) {
				// if it is, then just return
				return;
			} else {
				Collections.swap(components, index, index + 1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#moveComponentDown(org.praisenter.slide.SlideComponent)
	 */
	@Override
	public void moveComponentDown(SlideComponent component) {
		// move the given component down in the order
		
		// get all the components
		List<SlideComponent> components = this.components;
		// verify the component exists on this slide
		if (components.contains(component) && components.size() > 0) {
			int index = components.indexOf(component);
			// see if the component is already in the first position
			if (components.get(0).equals(component)) {
				// if it is, then just return its order
				return;
			} else {
				Collections.swap(components, index, index - 1);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#moveComponentFront(org.praisenter.slide.SlideComponent)
	 */
	@Override
	public void moveComponentFront(SlideComponent component) {
		// move the given component up in the order
		
		// get all the components
		List<SlideComponent> components = this.components;
		// verify the component exists on this slide
		if (components.contains(component) && components.size() > 0) {
			int size = components.size();
			// see if the component is already in the last position
			if (components.get(size - 1).equals(component)) {
				// if it is, then just return its order
				return;
			} else {
				components.remove(component);
				components.add(component);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#moveComponentBack(org.praisenter.slide.SlideComponent)
	 */
	@Override
	public void moveComponentBack(SlideComponent component) {
		// move the given component up in the order
		
		// get all the components
		List<SlideComponent> components = this.components;
		// verify the component exists on this slide
		if (components.contains(component) && components.size() > 0) {
			// see if the component is already in the last position
			if (components.get(0).equals(component)) {
				// if it is, then just return its order
				return;
			} else {
				components.remove(component);
				components.add(0, component);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#moveComponent(org.praisenter.slide.SlideComponent, int)
	 */
	@Override
	public int moveComponent(SlideComponent component, int index) {
		// get all the components
		List<SlideComponent> components = this.components;
		int size = components.size();
		
		// validate the index
		if (index < 0) index = 0;
		if (index >= size) index = size -1; 
		
		// verify the component exists on this slide
		if (components.contains(component) && components.size() > 0) {
			this.components.remove(component);
			this.components.add(index, component);
			return index;
		}
		
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#hasPlaceholders()
	 */
	@Override
	public boolean hasPlaceholders() {
		for (SlideComponent component : this.components) {
			if (TextPlaceholderComponent.class.isInstance(component)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#fit(int, int)
	 */
	@Override
	public void fit(double width, double height) {
		// compute the resize percentages
		double pw = width / this.width;
		double ph = height / this.height;
		// set the slide size
		this.width = width;
		this.height = height;
		// set the sizes for the components
		for (SlideComponent component : this.components) {
			component.adjust(pw, ph);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getAnimations()
	 */
	@Override
	public List<SlideAnimation> getAnimations() {
		return this.animations;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getAnimations(java.util.UUID)
	 */
	@Override
	public List<SlideAnimation> getAnimations(UUID id) {
		List<SlideAnimation> animations = new ArrayList<SlideAnimation>();
		for (int i = 0; i < this.animations.size(); i++) {
			SlideAnimation st = this.animations.get(i);
			if (st.getId().equals(id)) {
				animations.add(st);
			}
		}
		return animations;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getTags()
	 */
	@Override
	public Set<Tag> getTags() {
		return this.tags;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getThumbnail()
	 */
	@Override
	public BufferedImage getThumbnail() {
		return this.thumbnail;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#setThumbnail(java.awt.image.BufferedImage)
	 */
	@Override
	public void setThumbnail(BufferedImage thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getPlaceholderData(org.praisenter.TextVariant)
	 */
	@Override
	public TextStore getPlaceholderData() {
		return this.placeholderData;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#setPlaceholderData(org.praisenter.TextVariant, org.praisenter.TextTypeSet)
	 */
	@Override
	public void setPlaceholderData(TextStore data) {
		this.placeholderData = data;
		this.updatePlaceholders();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#updatePlaceholders()
	 */
	@Override
	public void updatePlaceholders() {
		// iterate all the placeholders
		for (TextPlaceholderComponent tpc : this.getComponents(TextPlaceholderComponent.class)) {
			TextItem data = this.getPlaceholderText(tpc.getPlaceholderType(), tpc.getPlaceholderVariant());
			if (data != null) {
				// override the text
				tpc.setText(data.getText());
				// override the font size if necessary
				if (data.getFontSize() > 0) {
					tpc.setFont(tpc.getFont().size(data.getFontSize()));
				}
			} else {
				tpc.setText(null);
			}
		}
	}
	
	/**
	 * Returns the placeholder text for the given type and variant.
	 * @param type the type
	 * @param variant the variant
	 * @return String
	 */
	protected TextItem getPlaceholderText(TextType type, TextVariant variant) {
		if (this.placeholderData == null) {
			return null;
		}
		
		return this.placeholderData.get(variant, type);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractSlideRegion#getReferencedMedia()
	 */
	@Override
	public Set<UUID> getReferencedMedia() {
		Set<UUID> media = super.getReferencedMedia();
		for (SlideComponent component : this.components) {
			media.addAll(component.getReferencedMedia());
		}
		return media;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#isBackgroundTransitionRequired(org.praisenter.slide.Slide)
	 */
	@Override
	public boolean isBackgroundTransitionRequired(Slide slide) {
		if (slide == null) return true;
		if (slide == this) return false;
		
		// we need a transition if the position, size, background
		// or border are different
		if (this.x != slide.getX() ||
			this.y != slide.getY() ||
			this.width != slide.getWidth() || 
			this.height != slide.getHeight() ||
			!Objects.equals(this.background, slide.getBackground()) ||
			!Objects.equals(this.border, slide.getBorder()) ||
			this.opacity != slide.getOpacity()) {
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getFormat()
	 */
	@Override
	public String getFormat() {
		return this.format;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getVersion()
	 */
	@Override
	public String getVersion() {
		return this.version;
	}
}
