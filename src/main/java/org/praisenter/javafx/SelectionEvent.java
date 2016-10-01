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
package org.praisenter.javafx;

import java.io.Serializable;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Event fired for selection and deselection.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SelectionEvent extends Event implements Serializable {
	/** The serialization id */
	private static final long serialVersionUID = -7638832493983231255L;
	
	// types
	
	/** The base selection event type */
	public static final EventType<SelectionEvent> ALL = new EventType<SelectionEvent>("SELECTION_ALL");
	
	/** The selection event type for single selection */
	public static final EventType<SelectionEvent> SELECT = new EventType<SelectionEvent>(ALL, "SELECT");
	
	/** The selection event type for multi-selection */
	public static final EventType<SelectionEvent> SELECT_MULTIPLE = new EventType<SelectionEvent>(ALL, "SELECT_MULTIPLE");
	
	/** The selection event type for single deselection */
	public static final EventType<SelectionEvent> DESELECT = new EventType<SelectionEvent>(ALL, "DESELECT");
	
	/** The selection event type for multi-deselection */
	public static final EventType<SelectionEvent> DESELECT_MULTIPLE = new EventType<SelectionEvent>(ALL, "DESELECT_MULTIPLE");

	/** The selection event for double clicking on an item */
	public static final EventType<SelectionEvent> DOUBLE_CLICK = new EventType<SelectionEvent>("SELECTION_DOUBLE_CLICK");
	
	/**
	 * Full constructor.
	 * @param source the event source
	 * @param target the event target
	 * @param type the event type
	 */
	public SelectionEvent(Object source, EventTarget target, EventType<SelectionEvent> type) {
		super(source, target, type);
	}
}
