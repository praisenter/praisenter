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
package org.praisenter.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents general file properties of an video file.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "VideoMediaFile")
@XmlAccessorType(XmlAccessType.NONE)
public class VideoMediaFile extends MediaFile {
	/** The image width */
	@XmlAttribute(name = "Width")
	protected int width;
	
	/** The image height */
	@XmlAttribute(name = "Height")
	protected int height;
	
	/** The video length in seconds */
	@XmlAttribute(name = "Length")
	protected long length;
	
	/** True if audio is present in the video */
	@XmlAttribute(name = "AudioPresent")
	protected boolean audioPresent;
	
	/**
	 * Default constructor.
	 * <p>
	 * Should only be used by JAXB.
	 */
	protected VideoMediaFile() {}
	
	/**
	 * Full constructor.
	 * @param fullPath the full file name and path
	 * @param format the file format
	 * @param width the image width
	 * @param height the image height
	 * @param length the video length in seconds
	 * @param audioPresent true if the video contains audio
	 */
	public VideoMediaFile(
			String fullPath, String format, 
			int width, int height,
			long length, boolean audioPresent) {
		super(fullPath, format);
		this.width = width;
		this.height = height;
		this.length = length;
		this.audioPresent = audioPresent;
	}

	/**
	 * Returns the width of the image.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height of the image.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the length of this video in seconds
	 * @return long
	 */
	public long getLength() {
		return this.length;
	}

	/**
	 * Returns true if this video contains audio.
	 * @return boolean
	 */
	public boolean isAudioPresent() {
		return this.audioPresent;
	}
}
