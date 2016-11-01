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
package org.praisenter.javafx.screen;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * Class used to store the configuration of displays.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "screens")
@XmlAccessorType(XmlAccessType.NONE)
public final class ScreenConfiguration {
	/** The primary screen (not configured, but stored for detection) */
	private final ObjectProperty<Display> primaryScreen = new SimpleObjectProperty<Display>(null);
	
	/** The operator screen */
	private final ObjectProperty<Display> operatorScreen = new SimpleObjectProperty<Display>(null);
	
	/** The main screen */
	private final ObjectProperty<Display> mainScreen = new SimpleObjectProperty<Display>(null);
	
	/** The musician screen */
	private final ObjectProperty<Display> musicianScreen = new SimpleObjectProperty<Display>(null);
	
	/** The list of resolutions */
	private final ObservableSet<Resolution> resolutions = FXCollections.observableSet(new TreeSet<Resolution>());
	
	// mutators
	
	/**
	 * Returns the primary screen.
	 * @return {@link Display}
	 */
	@XmlElement(name = "primaryScreen", required = false)
	public Display getPrimaryScreen() {
		return this.primaryScreen.get();
	}

	/**
	 * Sets the primary screen.
	 * @param display the display
	 */
	public void setPrimaryScreen(Display display) {
		this.primaryScreen.set(display);
	}

	/**
	 * Returns the primary screen property.
	 * @return ObjectProperty&lt;{@link Display}&gt;
	 */
	public ObjectProperty<Display> primaryScreenProperty() {
		return this.primaryScreen;
	}
	
	/**
	 * Returns the operator screen.
	 * @return {@link Display}
	 */
	@XmlElement(name = "operatorScreen", required = false)
	public Display getOperatorScreen() {
		return this.operatorScreen.get();
	}
	
	/**
	 * Sets the operator screen.
	 * @param display the display
	 */
	public void setOperatorScreen(Display display) {
		this.operatorScreen.set(display);
	}
	
	/**
	 * Returns the operator screen property.
	 * @return ObjectProperty&lt;{@link Display}&gt;
	 */
	public ObjectProperty<Display> operatorScreenProperty() {
		return this.operatorScreen;
	}
	
	/**
	 * Returns the main screen.
	 * @return {@link Display}
	 */
	@XmlElement(name = "mainScreen", required = false)
	public Display getMainScreen() {
		return this.mainScreen.get();
	}

	/**
	 * Sets the main screen.
	 * @param display the display
	 */
	public void setMainScreen(Display display) {
		this.mainScreen.set(display);
	}

	/**
	 * Returns the main screen property.
	 * @return ObjectProperty&lt;{@link Display}&gt;
	 */
	public ObjectProperty<Display> mainScreenProperty() {
		return this.mainScreen;
	}
	
	/**
	 * Returns the musician screen.
	 * @return {@link Display}
	 */
	@XmlElement(name = "musicianScreen", required = false)
	public Display getMusicianScreen() {
		return this.musicianScreen.get();
	}

	/**
	 * Sets the musician screen.
	 * @param display the display
	 */
	public void setMusicianScreen(Display display) {
		this.musicianScreen.set(display);
	}

	/**
	 * Returns the musician screen property.
	 * @return ObjectProperty&lt;{@link Display}&gt;
	 */
	public ObjectProperty<Display> musicianScreenProperty() {
		return this.musicianScreen;
	}
	
	/**
	 * Returns the resolutions.
	 * @return ObservableSet&lt;{@link Resolution}&gt;
	 */
	@XmlElement(name = "resolution", required = false)
	@XmlElementWrapper(name = "resolutions", required = false)
	public ObservableSet<Resolution> getResolutions() {
		return this.resolutions;
	}
}
