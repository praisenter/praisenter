package org.praisenter.slide;

import java.awt.image.BufferedImage;

public class Template {
	protected String name;
	protected Slide slide;
	
	public Template(String name, Slide slide) {
		this.name = name;
		this.slide = slide;
		// generate a thumbnail
	}
	
	public String getName() {
		return this.name;
	}
	
	public Slide getSlide() {
		return this.slide;
	}
	
	public BufferedImage generateThumbnail() {
		// TODO generate a buffered image that is a thumnail of the template
		// use a static(translated) text value for any text components
		// and use the preview render method
		return null;
	}
}
