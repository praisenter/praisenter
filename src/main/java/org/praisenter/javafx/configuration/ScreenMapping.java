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
package org.praisenter.javafx.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a mapping of screen to role.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
@XmlRootElement(name = "screen")
@XmlAccessorType(XmlAccessType.NONE)
public final class ScreenMapping {
	/** The screen id */
	@XmlElement(name = "id", required = false)
	private final String id;
	
	/** The screen role */
	@XmlElement(name = "role", required = false)
	private final ScreenRole role;
	
	/**
	 * For JAXB only.
	 */
	@SuppressWarnings("unused")
	private ScreenMapping() {
		// for jaxb
		this.id = null;
		this.role = null;
	}
	
	/**
	 * Full constructor.
	 * @param id the screen id
	 * @param role the screen role
	 */
	public ScreenMapping(String id, ScreenRole role) {
		this.id = id;
		this.role = role;
	}

	/**
	 * Returns the screen id.
	 * @return String
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Returns the screen role.
	 * @return {@link ScreenRole}
	 */
	public ScreenRole getRole() {
		return this.role;
	}
}
