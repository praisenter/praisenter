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
package org.praisenter.javafx.slide.editor.events;

import java.io.Serializable;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Represents a generic event for a change to a slide component.
 * @author William Bittle
 * @version 3.0.0
 */
public class SlideEditorEvent extends Event implements Serializable {
	/** The serialization id */
	private static final long serialVersionUID = 7525223765039656381L;
	
	/** Event type to catch any event */
	public static final EventType<SlideEditorEvent> ANY = new EventType<SlideEditorEvent>("SLIDE");

	/** Event type to catch changes to a slide */
	public static final EventType<SlideEditorEvent> CHANGED = new EventType<SlideEditorEvent>("SLIDE_CHANGED");
	
	/** Event type to catch only target resolution change events */
	public static final EventType<SlideEditorEvent> TARGET_RESOLUTION = new EventType<SlideEditorEvent>("SLIDE_TARGET_RESOLUTION");

	/** Event type to catch only new component change events */
	public static final EventType<SlideComponentAddEvent> ADD_COMPONENT = new EventType<SlideComponentAddEvent>("SLIDE_ADD_COMPONENT");
	
	/** Event type to catch only remove component events */
	public static final EventType<SlideComponentRemoveEvent> REMOVE_COMPONENT = new EventType<SlideComponentRemoveEvent>("SLIDE_REMOVE_COMPONENT");
	
	/**
	 * Full constructor.
	 * @param source the event source
	 * @param target the event target
	 * @param type the event type
	 */
	public SlideEditorEvent(Object source, EventTarget target, EventType<? extends SlideEditorEvent> type) {
		super(source, target, type);
	}
}
