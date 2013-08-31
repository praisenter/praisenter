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

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * Class used to manage presentation events.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class PresentationManager {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(PresentationManager.class);
	
	// static interface
	
	/** The current manager instance */
	private static PresentationManager instance;
	
	/**
	 * Initializes the presentation manager to use in-process presentation windows.
	 * @param deviceIds the device ids to initialize
	 */
	public static final synchronized void initialize(final String... deviceIds) {
		// see if one already exists
		if (instance != null) {
			LOGGER.debug("Presentation manager already exists. Cleaning up old one.");
			// dispose it
			instance.dispose();
			instance = null;
		}
		// create a new one
		PresentationManager manager = new PresentationManager();
		// initialize the windows
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						// initialize the windows given
						PresentationWindows.initialize(deviceIds);
					}
				});
			} catch (Exception e) {
				LOGGER.error(e);
			}
		} else {
			PresentationWindows.initialize(deviceIds);
		}
		// assign it to the instance
		instance = manager;
	}
	
	/**
	 * Returns the current {@link PresentationManager} instance.
	 * @return {@link PresentationManager}
	 */
	public static final synchronized PresentationManager getInstance() {
		if (instance == null) {
			LOGGER.warn("Presentation manager was not initialized.");
			instance = new PresentationManager();
		}
		return instance;
	}
	
	// instance interface
	
	/**
	 * Hidden default constructor.
	 */
	private PresentationManager() {}
	
	/**
	 * Executes the event in process.
	 * @param event the event to execute
	 */
	private void executeInProcess(SendEvent event) {
		PresentationWindow window = PresentationWindows.getPresentationWindowForEvent(event);
		if (window != null) {
			window.execute(event);
		} else {
			LOGGER.warn("No window available for presentation.");
		}
	}
	
	/**
	 * Executes the event in process.
	 * @param event the event to execute
	 */
	private void executeInProcess(SendWaitClearEvent event) {
		PresentationWindow window = PresentationWindows.getPresentationWindowForEvent(event);
		if (window != null) {
			window.execute(event);
		} else {
			LOGGER.warn("No window available for presentation.");
		}
	}
	
	/**
	 * Executes the event in process.
	 * @param event the event to execute
	 */
	private void executeInProcess(ClearEvent event) {
		PresentationWindow window = PresentationWindows.getPresentationWindowForEvent(event);
		if (window != null) {
			window.execute(event);
		} else {
			LOGGER.warn("No window available for presentation.");
		}
	}
	
	/**
	 * Executes the given event.
	 * @param event the event
	 */
	public void execute(final SendEvent event) {
		if (!SwingUtilities.isEventDispatchThread()) {
			// execute on the EDT
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					executeInProcess(event);
				}
			});
		} else {
			this.executeInProcess(event);
		}
	}
	
	/**
	 * Executes the given event.
	 * @param event the event
	 */
	public void execute(final SendWaitClearEvent event) {
		if (!SwingUtilities.isEventDispatchThread()) {
			// execute on the EDT
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					executeInProcess(event);
				}
			});
		} else {
			this.executeInProcess(event);
		}
	}
	
	/**
	 * Executes the given event.
	 * @param event the event
	 */
	public void execute(final ClearEvent event) {
		if (!SwingUtilities.isEventDispatchThread()) {
			// execute on the EDT
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					executeInProcess(event);
				}
			});
		} else {
			this.executeInProcess(event);
		}
	}
	
	/**
	 * Disposes the underlying resources held by this {@link PresentationManager}.
	 */
	public void dispose() {
		PresentationWindows.dispose();
	}
}
