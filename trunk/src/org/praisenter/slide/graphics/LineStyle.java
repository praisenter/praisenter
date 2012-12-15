package org.praisenter.slide.graphics;

import java.awt.BasicStroke;
import java.awt.Stroke;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a line style.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "LineStyle")
@XmlAccessorType(XmlAccessType.NONE)
public class LineStyle {
	/** The line width */
	@XmlElement(name = "Width")
	protected float width;
	
	/** The cap type */
	@XmlElement(name = "Cap")
	protected CapType cap;
	
	/** The join type */
	@XmlElement(name = "Join")
	protected JoinType join;
	
	/** The pattern */
	@XmlElement(name = "Pattern")
	protected DashPattern pattern;

	/**
	 * Default constructor.
	 */
	public LineStyle() {
		this(5.0f, CapType.SQUARE, JoinType.BEVEL, DashPattern.SOLID);
	}
	
	/**
	 * Full constructor.
	 * @param width the line width
	 * @param cap the cap type
	 * @param join the join type
	 * @param pattern the pattern
	 */
	public LineStyle(float width, CapType cap, JoinType join, DashPattern pattern) {
		this.width = width;
		this.cap = cap;
		this.join = join;
		this.pattern = pattern;
	}
	
	/**
	 * Returns the a new stroke for this {@link LineStyle}.
	 * @return Stroke
	 */
	public Stroke getStroke() {
		return new BasicStroke(
				this.width,
				this.cap.getStrokeValue(),
				this.join.getStrokeValue(),
				this.width,
				this.pattern.getDashLengths(this.width),
				0.0f);
	}
	
	/**
	 * Returns the line width.
	 * @return float
	 */
	public float getWidth() {
		return this.width;
	}

	/**
	 * Returns the cap type.
	 * @return {@link CapType}
	 */
	public CapType getCap() {
		return this.cap;
	}

	/**
	 * Returns the join type.
	 * @return {@link JoinType}
	 */
	public JoinType getJoin() {
		return this.join;
	}

	/**
	 * Returns the pattern.
	 * @return {@link DashPattern}
	 */
	public DashPattern getPattern() {
		return this.pattern;
	}
}
