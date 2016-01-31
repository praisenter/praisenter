package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.utility.Maf;

@XmlRootElement(name = "strokeStyle")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideStrokeStyle {
	@XmlAttribute(name = "type", required = false)
	final SlideStrokeType type;
	
	@XmlAttribute(name = "join", required = false)
	final SlideStrokeJoin join;
	
	@XmlAttribute(name = "cap", required = false)
	final SlideStrokeCap cap;
	
	@XmlElement(name = "length", required = false)
	@XmlElementWrapper(name = "dashes", required = false)
	final double[] dashes;
	
	private SlideStrokeStyle() {
		// for jaxb
		this.type = SlideStrokeType.CENTERED;
		this.join = SlideStrokeJoin.MITER;
		this.cap = SlideStrokeCap.SQUARE;
		this.dashes = new double[0];
	}
	
	public SlideStrokeStyle(SlideStrokeType type, SlideStrokeJoin join, SlideStrokeCap cap, double[] dashes) {
		this.type = type;
		this.join = join;
		this.cap = cap;
		if (dashes == null) {
			dashes = new double[0];
		}
		this.dashes = dashes;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideStrokeStyle) {
			SlideStrokeStyle s = (SlideStrokeStyle)obj;
			if (this.type != s.type ||
				this.join != s.join ||
				this.cap != s.cap) {
				return false;
			}
			if (this.dashes.length != s.dashes.length) {
				return false;
			}
			for (int i = 0; i < this.dashes.length; i++) {
				if (!Maf.equals(this.dashes[i], s.dashes[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public SlideStrokeType getType() {
		return type;
	}

	public SlideStrokeJoin getJoin() {
		return join;
	}

	public SlideStrokeCap getCap() {
		return cap;
	}

	public double[] getDashes() {
		return dashes;
	}
}
