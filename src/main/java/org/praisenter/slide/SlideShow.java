package org.praisenter.slide;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SlideShow {
	
	Path path;
	
	String name;

	List<UUID> slides;

	boolean loop;
	
	public SlideShow() {
		this.slides = new ArrayList<UUID>();
		this.loop = false;
	}
	
	public List<UUID> getSlides() {
		return slides;
	}

	public void setSlides(List<UUID> slides) {
		this.slides = slides;
	}
}
