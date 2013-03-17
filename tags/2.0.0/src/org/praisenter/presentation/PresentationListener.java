/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.presentation;

import java.util.EventListener;

/**
 * Interface to listen to presentation events.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public interface PresentationListener extends EventListener {
	/**
	 * Called when an "in" transition begins.
	 * @param event the issuing {@link PresentationEvent}
	 */
	public void inTransitionBegin(SendEvent event);
	
	/**
	 * Called when an "out" transition begins.
	 * @param event the issuing {@link PresentationEvent}
	 */
	public void outTransitionBegin(ClearEvent event);
	
	/**
	 * Called when an event gets dropped.
	 * <p>
	 * An event will be dropped when the user executes events too quickly
	 * to be processed.  The last event issued will always be the one executed
	 * and the rest will be dropped.  This will also happen if the event
	 * issued will have not effect (clear event to an already cleared surface). 
	 * @param event the issuing {@link PresentationEvent}
	 */
	public void eventDropped(PresentationEvent event);
	
	/**
	 * Called when an "in" transition has completed.
	 * @param event the issuing {@link PresentationEvent}
	 */
	public void inTransitionComplete(SendEvent event);
	
	/**
	 * Called when an "out" transition has completed.
	 * @param event the issuing {@link PresentationEvent}
	 */
	public void outTransitionComplete(ClearEvent event);
}
