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
package org.praisenter.application.slide.ui;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.common.threading.AbstractTask;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.Template;

/**
 * Task to load slides from the Slide Library.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.1
 */
public class LoadSlidesTask extends AbstractTask {
	/** The slide files */
	protected List<SlideFile> files;
	
	/** The slide class; all the slides must be the same type; null if {@link BasicSlide} */
	protected Class<? extends Template> clazz;
	
	/** The loaded slide */
	protected List<Slide> slides;
	
	/**
	 * Full constructor.
	 * @param file the slide file
	 * @param clazz the slide class; null if {@link BasicSlide}
	 */
	public LoadSlidesTask(SlideFile file, Class<? extends Template> clazz) {
		this.files = new ArrayList<SlideFile>();
		this.files.add(file);
		this.clazz = clazz;
		this.slides = new ArrayList<Slide>();
	}
	
	/**
	 * Full constructor.
	 * @param files the slide files
	 * @param clazz the slide class; null if {@link BasicSlide}
	 */
	public LoadSlidesTask(List<SlideFile> files, Class<? extends Template> clazz) {
		this.files = files;
		this.clazz = clazz;
		this.slides = new ArrayList<Slide>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			for (SlideFile file : this.files) {
				Slide slide = null;
				if (this.clazz != null) {
					slide = SlideLibrary.getInstance().getTemplate(file, this.clazz);
				} else {
					slide = SlideLibrary.getInstance().getSlide(file);
				}
				this.slides.add(slide);
			}
			this.setSuccessful(true);
		} catch (Exception ex) {
			this.handleException(ex);
		}
	}
	
	/**
	 * Returns the slide files.
	 * @return List&lt;{@link SlideFile}&gt;
	 */
	public List<SlideFile> getFiles() {
		return this.files;
	}
	
	/**
	 * Returns the class type.
	 * @return Class&lt;{@link Template}&gt;
	 */
	public Class<? extends Template> getClazz() {
		return this.clazz;
	}
	
	/**
	 * Returns the (first) loaded slide.
	 * @return {@link Slide}
	 */
	public Slide getSlide() {
		return this.slides.get(0);
	}
	
	/**
	 * Returns the loaded slides.
	 * @return List&lt;{@link Slide}&gt;
	 */
	public List<Slide> getSlides() {
		return this.slides;
	}
}