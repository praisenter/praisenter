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
package org.praisenter.preferences;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.easings.CubicEasing;
import org.praisenter.transitions.Swap;

/**
 * Class used to store song preferences.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "SongPreferences")
@XmlAccessorType(XmlAccessType.NONE)
public class SongPreferences {

	// template
	
	/** The template to use for bible slides */
	@XmlElement(name = "Template", required = false, nillable = true)
	protected String template;
	
	// transitions
	
	/** The send transition id */
	@XmlElement(name = "SendTransitionId", required = true, nillable = false)
	protected int sendTransitionId;
	
	/** The send transition duration */
	@XmlElement(name = "SendTransitionDuration", required = true, nillable = false)
	protected int sendTransitionDuration;
	
	/** The send transition easing id */
	@XmlElement(name = "SendTransitionEasingId", required = true, nillable = false)
	protected int sendTransitionEasingId;
	
	/** The clear transition id */
	@XmlElement(name = "ClearTransitionId", required = true, nillable = false)
	protected int clearTransitionId;
	
	/** The clear transition duration */
	@XmlElement(name = "ClearTransitionDuration", required = true, nillable = false)
	protected int clearTransitionDuration;
	
	/** The clear transition easing id */
	@XmlElement(name = "ClearTransitionEasingId", required = true, nillable = false)
	protected int clearTransitionEasingId;

	/** Default constructor. */
	protected SongPreferences() {
		this.template = null;
		this.sendTransitionId = Swap.ID;
		this.sendTransitionDuration = 400;
		this.sendTransitionEasingId = CubicEasing.ID;
		this.clearTransitionId = Swap.ID;
		this.clearTransitionDuration = 300;
		this.clearTransitionEasingId = CubicEasing.ID;
	}

	// template
	
	/**
	 * Gets the default template for bible slides.
	 * @return String
	 */
	public String getTemplate() {
		return this.template;
	}
	
	/**
	 * Sets the default template for bible slides.
	 * @param template the template file path
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
	// transitions

	/**
	 * Returns the send transition id.
	 * @return int
	 */
	public int getSendTransitionId() {
		return this.sendTransitionId;
	}

	/**
	 * Sets the send transition id.
	 * @param sendTransitionId the send transition id
	 */
	public void setSendTransitionId(int sendTransitionId) {
		this.sendTransitionId = sendTransitionId;
	}

	/**
	 * Returns the send transition duration in milliseconds.
	 * @return int
	 */
	public int getSendTransitionDuration() {
		return this.sendTransitionDuration;
	}

	/**
	 * Sets the send transition duration.
	 * @param sendTransitionDuration the send transition duration in milliseconds
	 */
	public void setSendTransitionDuration(int sendTransitionDuration) {
		this.sendTransitionDuration = sendTransitionDuration;
	}

	/**
	 * Returns the send transition easing id.
	 * @return int
	 */
	public int getSendTransitionEasingId() {
		return this.sendTransitionEasingId;
	}

	/**
	 * Sets the send transition easing id.
	 * @param sendTransitionEasingId the send transition easing id
	 */
	public void setSendTransitionEasingId(int sendTransitionEasingId) {
		this.sendTransitionEasingId = sendTransitionEasingId;
	}

	/**
	 * Returns the clear transition id.
	 * @return int
	 */
	public int getClearTransitionId() {
		return this.clearTransitionId;
	}

	/**
	 * Sets the clear transition id.
	 * @param clearTransitionId the clear transition id
	 */
	public void setClearTransitionId(int clearTransitionId) {
		this.clearTransitionId = clearTransitionId;
	}

	/**
	 * Returns the clear transition duration.
	 * @return int
	 */
	public int getClearTransitionDuration() {
		return this.clearTransitionDuration;
	}

	/**
	 * Sets the clear transition duration.
	 * @param clearTransitionDuration the clear transition duration in milliseconds
	 */
	public void setClearTransitionDuration(int clearTransitionDuration) {
		this.clearTransitionDuration = clearTransitionDuration;
	}

	/**
	 * Returns the clear transition easing id.
	 * @return int
	 */
	public int getClearTransitionEasingId() {
		return this.clearTransitionEasingId;
	}

	/**
	 * Sets the clear transition easing id.
	 * @param clearTransitionEasingId the clear transition easing id
	 */
	public void setClearTransitionEasingId(int clearTransitionEasingId) {
		this.clearTransitionEasingId = clearTransitionEasingId;
	}
}
