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
package org.praisenter.javafx.controls;

import java.io.Serializable;

import org.praisenter.Tag;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * An event when tags are added or removed.
 * @author William Bittle
 * @version 3.0.0
 */
public final class TagEvent extends Event implements Serializable {
	/** The serialization id */
	private static final long serialVersionUID = 837797201591141937L;
	
	// types
	
	/** For all Tag events */
	public static final EventType<TagEvent> ALL = new EventType<TagEvent>("TAG_ALL");
	
	/** For all added tag events */
	public static final EventType<TagEvent> ADDED = new EventType<TagEvent>(ALL, "ADDED");
	
	/** For all removed tag events */
	public static final EventType<TagEvent> REMOVED = new EventType<TagEvent>(ALL, "REMOVED");
	
	// data
	
	/** The tag */
	final Tag tag;
	
	/**
	 * Full constructor.
	 * @param source the event source
	 * @param target the event target
	 * @param type the event type
	 * @param tag the tag
	 */
	public TagEvent(Object source, EventTarget target, EventType<TagEvent> type, Tag tag) {
		super(source, target, type);
		this.tag = tag;
	}
	
	/**
	 * Returns the tag involved in the event.
	 * @return {@link Tag}
	 */
	public Tag getTag() {
		return this.tag;
	}
}
