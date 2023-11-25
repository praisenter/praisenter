package org.praisenter.ui.slide;

import java.time.Instant;

import org.praisenter.data.TextStore;
import org.praisenter.data.slide.Slide;

final class PreparedSlide implements Comparable<PreparedSlide> {
	private final Slide slide;
	private final TextStore data;
	private final SlideNode node;
	private final Instant time;
	
	public PreparedSlide(Slide slide, TextStore data, SlideNode node, Instant time) {
		super();
		this.slide = slide;
		this.data = data;
		this.node = node;
		this.time = time;
	}
	
	@Override
	public int compareTo(PreparedSlide o) {
		if (o == null)
			return -1;
		
		return o.time.compareTo(this.time);
	}
	
	public Slide getSlide() {
		return this.slide;
	}
	
	public TextStore getData() {
		return this.data;
	}
	
	public SlideNode getNode() {
		return this.node;
	}
	
	public Instant getTime() {
		return this.time;
	}
}
