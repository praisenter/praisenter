package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a gradient {@link Fill}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractGradientFill extends AbstractFill implements Fill {
	/** The offset to apply for gradients so that the middle stop can be all the way to either end */
	protected static final int OFFSET = 5;
	
	/** The default stops */
	protected static final Stop[] DEFAULT_STOPS = new Stop[] {
		new Stop(0, 0, 0, 0, 1.0f), 
		new Stop(0.5f, 0.5f, 0.5f, 0.5f, 1.0f), 
		new Stop(1.0f, 1.0f, 1.0f, 1.0f, 1.0f)
	};
	
	/** The list of stops */
	@XmlElement(name = "Stops")
	protected Stop[] stops;

	/**
	 * Default constructor.
	 */
	public AbstractGradientFill() {
		this(DEFAULT_STOPS);
	}
	
	/**
	 * Full constructor.
	 * @param stops the stops
	 */
	public AbstractGradientFill(Stop... stops) {
		if (stops == null) {
			stops = DEFAULT_STOPS;
		}
		this.stops = stops;
	}
	
	/**
	 * Returns the stops for this gradient.
	 * @return {@link Stop}[]
	 */
	public Stop[] getStops() {
		return this.stops;
	}
}
