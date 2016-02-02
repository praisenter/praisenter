package org.praisenter.slide.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class SlideGradient extends AbstractSlidePaint implements SlidePaint {
	/** The default stops */
	private static final SlideGradientStop[] DEFAULT_STOPS = new SlideGradientStop[] {
		new SlideGradientStop(0, 0, 0, 0, 255), 
		new SlideGradientStop(0.5f, 127, 127, 127, 255), 
		new SlideGradientStop(1.0f, 255, 255, 255, 255)
	};
	
	/** The list of stops */
	@XmlElement(name = "stop", required = false)
	@XmlElementWrapper(name = "stops", required = false)
	final List<SlideGradientStop> stops;

	private SlideGradient() {
		this(DEFAULT_STOPS);
	}
	
	public SlideGradient(SlideGradientStop... stops) {
		if (stops == null) {
			stops = DEFAULT_STOPS;
		}
		this.stops = new ArrayList<SlideGradientStop>(Arrays.asList(stops));
	}
	
	public SlideGradient(List<SlideGradientStop> stops) {
		if (stops == null) {
			stops = new ArrayList<SlideGradientStop>(Arrays.asList(DEFAULT_STOPS));
		}
		this.stops = stops;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideGradient) {
			SlideGradient g = (SlideGradient)obj;
			if (g.stops.size() != this.stops.size()) {
				return false;
			}
			for (int i = 0; i < this.stops.size(); i++) {
				if (!g.stops.get(i).equals(this.stops.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public List<SlideGradientStop> getStops() {
		return this.stops;
	}
}
