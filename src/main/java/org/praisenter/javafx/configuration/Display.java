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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * Represents a unique display of the host system.
 * <p>
 * The information stored here is both to aid the presentation system and to 
 * detect changes to the screen layout at start up.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "display")
@XmlAccessorType(XmlAccessType.NONE)
public final class Display {
	/** The id (index) of the display */
	@XmlAttribute(name = "id")
	private final int id;
	
	/** The x coordinate of the top left corner of this display */
	@XmlAttribute(name = "x")
	private final int x;
	
	/** The y coordinate of the top left corner of this display */
	@XmlAttribute(name = "y")
	private final int y;
	
	/** The width of this display */
	@XmlAttribute(name = "w")
	private final int width;
	
	/** The height of this display */
	@XmlAttribute(name = "h")
	private final int height;

	/**
	 * For JAXB.
	 */
	Display() {
		this.id = 0;
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}
	
	/**
	 * Full constructor.
	 * @param id the screen id (index)
	 * @param screen the screen
	 */
	public Display(int id, Screen screen) {
		this.id = id;
		
		Rectangle2D bounds = screen.getBounds();
		this.x = (int)bounds.getMinX();
		this.y = (int)bounds.getMinY();
		this.width = (int)bounds.getWidth();
		this.height = (int)bounds.getHeight();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "#" + this.id + " (" + this.x + "," + this.y + ") " + this.width + "x" + this.height;
	}
	
	/**
	 * Returns the id (index) of this display.
	 * @return int
	 */
	public int getId() {
		return this.id;
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
