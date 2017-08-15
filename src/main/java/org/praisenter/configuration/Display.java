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
package org.praisenter.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.geometry.Rectangle2D;

/**
 * Represents a unique display of the host system.
 * <p>
 * The information stored here is both to aid the presentation system and to 
 * detect changes to the screen layout at start up.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Display {
	/** The id (index) of the display */
	@JsonProperty
	private final int id;
	
	@JsonProperty
	private final DisplayRole role;
	
	@JsonProperty
	private final String name;
	
	/** The x coordinate of the top left corner of this display */
	@JsonProperty
	private final int x;
	
	/** The y coordinate of the top left corner of this display */
	@JsonProperty
	private final int y;
	
	/** The width of this display */
	@JsonProperty
	private final int width;
	
	/** The height of this display */
	@JsonProperty
	private final int height;

	/**
	 * Default constructor.
	 */
	Display() {
		this.id = 0;
		this.role = null;
		this.name = null;
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}
	
	/**
	 * Optional constructor.
	 * @param id the screen id (index)
	 * @param role the display role
	 * @param name the display name
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width
	 * @param h the height
	 */
	public Display(int id, DisplayRole role, String name, int x, int y, int w, int h) {
		this.id = id;
		this.role = role;
		this.name = name;
		
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	/**
	 * Creates a display like this one with the given role.
	 * @param role the new role
	 * @return {@link Display}
	 */
	public final Display withRole(DisplayRole role) {
		return new Display(
			this.id,
			role,
			this.name,
			this.x,
			this.y,
			this.width,
			this.height);
	}
	
	/**
	 * Creates a display like this one with the given name.
	 * @param name the new name
	 * @return {@link Display}
	 */
	public final Display withName(String name) {
		return new Display(
			this.id,
			this.role,
			name,
			this.x,
			this.y,
			this.width,
			this.height);
	}
	
	/**
	 * Creates a display like this one with the given bounds.
	 * @param bounds the new screen bounds
	 * @return {@link Display}
	 */
	public final Display withBounds(Rectangle2D bounds) {
		return new Display(
			this.id,
			this.role,
			this.name,
			(int)bounds.getMinX(),
			(int)bounds.getMinY(),
			(int)bounds.getWidth(),
			(int)bounds.getHeight());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + " #" + this.id + " " + this.role + " (" + this.x + "," + this.y + ") " + this.width + "x" + this.height;
	}
	
	/**
	 * Returns the id (index) of this display.
	 * @return int
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Returns the display's role.
	 * @return {@link DisplayRole}
	 */
	public DisplayRole getRole() {
		return this.role;
	}
	
	/**
	 * Returns the name for the display.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the x coordinate of the top left corner of this display.
	 * @return int
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * Returns the y coordinate of the top left corner of this display.
	 * @return int
	 */
	public int getY() {
		return this.y;
	}
	
	/**
	 * Returns the width of this display.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height of this display.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}
}
