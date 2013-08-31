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

import java.io.Serializable;

import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.slide.graphics.RenderQualities;

/**
 * Class containing the configuration of a {@link PresentationEvent}.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class PresentationEventConfiguration implements Serializable {
	/** The version id */
	private static final long serialVersionUID = 7445474805651647169L;

	/** The device id */
	protected String presentationWindowDeviceId;
	
	/** The window type */
	protected PresentationWindowType presentationWindowType;
	
	/** The render qualities to use */
	protected RenderQualities renderQualities;
	
	/** True if we should wait for any existing transition to complete */
	protected boolean waitForTransitionEnabled;
	
	/** True if we should use smart image transitions */
	protected boolean smartImageTransitionsEnabled;
	
	/** True if we should use smart video transition */
	protected boolean smartVideoTransitionsEnabled;

	/** True if read-time video conversion is enabled */
	protected boolean readTimeVideoConversionEnabled;
	
	/**
	 * Default constructor.
	 * <p>
	 * By default the configuration will be for the secondary device (if present, otherwise the main device)
	 * using a full-screen window.
	 */
	public PresentationEventConfiguration() {
		this.presentationWindowDeviceId = WindowUtilities.getSecondaryDevice().getIDstring();
		this.presentationWindowType = PresentationWindowType.FULLSCREEN;
		this.renderQualities = new RenderQualities();
		this.waitForTransitionEnabled = true;
		this.smartImageTransitionsEnabled = true;
		this.smartVideoTransitionsEnabled = true;
		this.readTimeVideoConversionEnabled = false;
	}
	
	/**
	 * Returns the window device id that the event will be presented on.
	 * @return String
	 */
	public String getPresentationWindowDeviceId() {
		return this.presentationWindowDeviceId;
	}

	/**
	 * Sets the window device id that the event will be presented on.
	 * @param deviceId the id of the device to present to
	 */
	public void setPresentationWindowDeviceId(String deviceId) {
		this.presentationWindowDeviceId = deviceId;
	}

	/**
	 * Returns the presentation window type.
	 * @return {@link PresentationWindowType}
	 */
	public PresentationWindowType getPresentationWindowType() {
		return this.presentationWindowType;
	}

	/**
	 * Sets the presentation window type.
	 * @param type the type
	 */
	public void setPresentationWindowType(PresentationWindowType type) {
		this.presentationWindowType = type;
	}

	/**
	 * Returns the render qualities to use.
	 * @return {@link RenderQualities}
	 */
	public RenderQualities getRenderQualities() {
		return this.renderQualities;
	}

	/**
	 * Sets the render qualities to use.
	 * @param qualities the render qualities
	 */
	public void setRenderQualities(RenderQualities qualities) {
		this.renderQualities = qualities;
	}

	/**
	 * Returns true if this event should wait for any currently executing
	 * events to finish before executing.
	 * @return boolean
	 */
	public boolean isWaitForTransitionEnabled() {
		return waitForTransitionEnabled;
	}

	/**
	 * Sets the wait for transitions flag.
	 * @param flag true if this event should wait for any currently executing events to finish before executing
	 */
	public void setWaitForTransitionEnabled(boolean flag) {
		this.waitForTransitionEnabled = flag;
	}

	/**
	 * Returns true if smart image transitions are enabled.
	 * @return boolean
	 */
	public boolean isSmartImageTransitionsEnabled() {
		return this.smartImageTransitionsEnabled;
	}

	/**
	 * Toggles the use of smart image transitions.
	 * @param flag true if smart image transitions should be enabled
	 */
	public void setSmartImageTransitionsEnabled(boolean flag) {
		this.smartImageTransitionsEnabled = flag;
	}

	/**
	 * Returns true if smart video transitions are enabled.
	 * @return boolean
	 */
	public boolean isSmartVideoTransitionsEnabled() {
		return this.smartVideoTransitionsEnabled;
	}

	/**
	 * Toggles the use of smart video transitions.
	 * @param flag true if smart video transitions should be enabled
	 */
	public void setSmartVideoTransitionsEnabled(boolean flag) {
		this.smartVideoTransitionsEnabled = flag;
	}

	/**
	 * Returns true if read-time video conversion is enabled.
	 * @return boolean
	 * @since 2.0.1
	 */
	public boolean isReadTimeVideoConversionEnabled() {
		return this.readTimeVideoConversionEnabled;
	}

	/**
	 * Toggles read-time video conversion.
	 * @param flag true if read-time video conversion should be enabled
	 * @since 2.0.1
	 */
	public void setReadTimeVideoConversionEnabled(boolean flag) {
		this.readTimeVideoConversionEnabled = flag;
	}
}
