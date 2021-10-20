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
package org.praisenter.javafx.slide;

import java.io.Serializable;

import org.praisenter.data.Tag;

import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Event fired when a slide has a tag added or removed.
 * @author William Bittle
 * @version 3.0.0
 */
final class SlideTagEvent extends SlideMetadataEvent implements Serializable {
	/** The serialization id */
	private static final long serialVersionUID = -3118652971303002560L;

	/** The slide list item */
	private final SlideListItem slideListItem;
	
	/** The new name */
	private final Tag tag;
	
	/**
	 * Full constructor.
	 * @param source the event source
	 * @param target the event target
	 * @param type the event type
	 * @param slideListItem the slide list item
	 * @param tag the tag added or removed
	 */
	public SlideTagEvent(Object source, EventTarget target, EventType<? extends SlideTagEvent> type, SlideListItem slideListItem, Tag tag) {
		super(source, target, type);
		this.slideListItem = slideListItem;
		this.tag = tag;
	}

	/**
	 * Returns the slide list item.
	 * @return {@link SlideListItem}
	 */
	public SlideListItem getSlideListItem() {
		return this.slideListItem;
	}

	/**
	 * Returns the tag.
	 * @return {@link Tag}
	 */
	public Tag getTag() {
		return this.tag;
	}
}
