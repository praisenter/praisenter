package org.praisenter.slide.graphics;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.utility.Maf;

@XmlRootElement(name = "linearGradient")
@XmlAccessorType(XmlAccessType.NONE)
public class SlideLinearGradient extends SlideGradient implements SlidePaint {
	@XmlAttribute(name = "sx", required = false)
	final double startX;
	
	@XmlAttribute(name = "sy", required = false)
	final double startY;
	
	@XmlAttribute(name = "ex", required = false)
	final double endX;
	
	@XmlAttribute(name = "ey", required = false)
	final double endY;
	
	@XmlAttribute(name = "cycle", required = false)
	final SlideGradientCycleType cycleType;
	
	private SlideLinearGradient() {
		this(0, 0, 0, 0, SlideGradientCycleType.NONE, (SlideGradientStop[])null);
	}
	
	public SlideLinearGradient(double startX, double startY, double endX, double endY, SlideGradientCycleType cycleType, List<SlideGradientStop> stops) {
		super(stops);
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.cycleType = cycleType;
	}
	
	public SlideLinearGradient(double startX, double startY, double endX, double endY, SlideGradientCycleType cycleType, SlideGradientStop... stops) {
		super(stops);
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.cycleType = cycleType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideLinearGradient) {
			SlideLinearGradient g = (SlideLinearGradient)obj;
			if (!Maf.equals(this.startX, g.startX) ||
				!Maf.equals(this.startY, g.startY) ||
				!Maf.equals(this.endX, g.endX) ||
				!Maf.equals(this.endY, g.endY) ||
				this.cycleType != g.cycleType) {
				return false;
			}
			return super.equals(obj);
		}
		return false;
	}
	
	public double getStartX() {
		return this.startX;
	}

	public double getStartY() {
		return this.startY;
	}

	public double getEndX() {
		return this.endX;
	}

	public double getEndY() {
		return this.endY;
	}

	public SlideGradientCycleType getCycleType() {
		return this.cycleType;
	}
}
