package org.praisenter.ui.display;

import org.praisenter.data.TextStore;
import org.praisenter.data.slide.Slide;

final class DisplayChange {
	static final int SLIDE = 0;
	static final int DATA = 1;
	static final int BOTH = 2;
	static final int HIDE = 3;
	
	private final Slide slide;
	private final TextStore data;
	private final int type;
	
	public static DisplayChange slide(Slide slide, TextStore data) {
		return new DisplayChange(slide, data, SLIDE);
	}
	
	public static DisplayChange data(Slide slide, TextStore data) {
		return new DisplayChange(slide, data, DATA);
	}
	
	public static DisplayChange slideAndData(Slide slide, TextStore data) {
		return new DisplayChange(slide, data, BOTH);
	}
	
	public static DisplayChange hide() {
		return new DisplayChange(null, null, HIDE);
	}
	
	private DisplayChange(Slide slide, TextStore data, int type) {
		super();
		this.slide = slide;
		this.data = data;
		this.type = type;
	}
	
	public boolean isSlideChange() {
		return this.type == SLIDE;
	}
	
	public boolean isDataChange() {
		return this.type == DATA;
	}
	
	public boolean isSlideAndDataChange() {
		return this.type == BOTH;
	}
	
	public boolean isHide() {
		return this.type == HIDE;
	}

	public Slide getSlide() {
		return slide;
	}

	public TextStore getData() {
		return data;
	}

	public int getType() {
		return type;
	}
}
