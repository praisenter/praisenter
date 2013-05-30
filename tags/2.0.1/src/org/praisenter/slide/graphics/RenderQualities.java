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
package org.praisenter.slide.graphics;

import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the qualities used for rendering.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "RenderQualities")
@XmlAccessorType(XmlAccessType.NONE)
public class RenderQualities implements Serializable {
	/** The version id */
	private static final long serialVersionUID = -5160640563804912115L;

	/** The overall render quality */
	@XmlElement(name = "RenderQuality", required = true, nillable = false)
	protected RenderQuality renderQuality;
	
	/** The interpolation quality (scaling) */
	@XmlElement(name = "InterpolationQuality", required = true, nillable = false)
	protected RenderQuality interpolationQuality;
	
	/** The color quality (conversion) */
	@XmlElement(name = "ColorQuality", required = true, nillable = false)
	protected RenderQuality colorQuality;
	
	/** The alpha interpolation quality (alpha blending) */
	@XmlElement(name = "AlphaInterpolationQuality", required = true, nillable = false)
	protected RenderQuality alphaInterpolationQuality;
	
	/** The anti-aliasing quality */
	@XmlElement(name = "AntialiasingQuality", required = true, nillable = false)
	protected RenderQuality antialiasingQuality;
	
	/** The text anti-aliasing quality */
	@XmlElement(name = "TextAntialiasingQuality", required = true, nillable = false)
	protected RenderQuality textAntialiasingQuality;
	
	/** The fractional metrics quality (for text) */
	@XmlElement(name = "FractionalMetricsQuality", required = true, nillable = false)
	protected RenderQuality fractionalMetricsQuality;
	
	/** The stroke control quality (sub pixel accuracy) */
	@XmlElement(name = "StrokeControlQuality", required = true, nillable = false)
	protected RenderQuality strokeControlQuality;
	
	/**
	 * Default constructor.
	 */
	public RenderQualities() {
		// default render qualities
		this.renderQuality = RenderQuality.MEDIUM;
		this.interpolationQuality = RenderQuality.MEDIUM;
		this.colorQuality = RenderQuality.MEDIUM;
		this.alphaInterpolationQuality = RenderQuality.MEDIUM;
		this.antialiasingQuality = RenderQuality.HIGH;
		this.textAntialiasingQuality = RenderQuality.HIGH;
		this.fractionalMetricsQuality = RenderQuality.HIGH;
		this.strokeControlQuality = RenderQuality.HIGH;
	}

	/**
	 * Returns the overall render quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getRenderQuality() {
		return this.renderQuality;
	}

	/**
	 * Sets the overall render quality.
	 * @param renderQuality the render quality
	 */
	public void setRenderQuality(RenderQuality renderQuality) {
		this.renderQuality = renderQuality;
	}
	
	/**
	 * Returns the interpolation quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getInterpolationQuality() {
		return this.interpolationQuality;
	}

	/**
	 * Sets the interpolation quality.
	 * <p>
	 * This effects the quality of scaled graphics.
	 * @param quality the quality
	 */
	public void setInterpolationQuality(RenderQuality quality) {
		this.interpolationQuality = quality;
	}

	/**
	 * Returns the color quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getColorQuality() {
		return this.colorQuality;
	}

	/**
	 * Sets the color quality.
	 * <p>
	 * This effects the quality of color conversions.
	 * @param quality the quality
	 */
	public void setColorQuality(RenderQuality quality) {
		this.colorQuality = quality;
	}

	/**
	 * Returns the alpha interpolation quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getAlphaInterpolationQuality() {
		return this.alphaInterpolationQuality;
	}

	/**
	 * Sets the alpha interpolation quality.
	 * <p>
	 * This effects the quality of alpha blending.
	 * @param quality the quality
	 */
	public void setAlphaInterpolationQuality(RenderQuality quality) {
		this.alphaInterpolationQuality = quality;
	}

	/**
	 * Returns the anti-aliasing quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getAntialiasingQuality() {
		return this.antialiasingQuality;
	}

	/**
	 * Sets the anti-aliasing quality.
	 * @param quality the quality
	 */
	public void setAntialiasingQuality(RenderQuality quality) {
		this.antialiasingQuality = quality;
	}

	/**
	 * Returns the text anti-aliasing quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getTextAntialiasingQuality() {
		return this.textAntialiasingQuality;
	}

	/**
	 * Sets the text anti-aliasing quality.
	 * @param quality the quality
	 */
	public void setTextAntialiasingQuality(RenderQuality quality) {
		this.textAntialiasingQuality = quality;
	}

	/**
	 * Returns the fractional metrics quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getFractionalMetricsQuality() {
		return this.fractionalMetricsQuality;
	}

	/**
	 * Sets the fractional metrics quality.
	 * <p>
	 * This effects the sub-pixel accuracy of text.
	 * @param quality the quality
	 */
	public void setFractionalMetricsQuality(RenderQuality quality) {
		this.fractionalMetricsQuality = quality;
	}

	/**
	 * Returns the stroke control quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getStrokeControlQuality() {
		return this.strokeControlQuality;
	}

	/**
	 * Sets the stroke control quality.
	 * <p>
	 * This effects the stroke sub-pixel accuracy.
	 * @param quality the quality
	 */
	public void setStrokeControlQuality(RenderQuality quality) {
		this.strokeControlQuality = quality;
	}

	/**
	 * Returns the rendering hints for the set render quality preferences.
	 * @return RenderingHints
	 */
	public RenderingHints getRenderingHints() {
		Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
		// setup the rendering hints based on the set qualities
		map.put(RenderingHints.KEY_RENDERING, this.renderQuality.getRenderingHintValue(RenderingHints.KEY_RENDERING));
		map.put(RenderingHints.KEY_INTERPOLATION, this.interpolationQuality.getRenderingHintValue(RenderingHints.KEY_INTERPOLATION));
		map.put(RenderingHints.KEY_COLOR_RENDERING, this.colorQuality.getRenderingHintValue(RenderingHints.KEY_COLOR_RENDERING));
		map.put(RenderingHints.KEY_ALPHA_INTERPOLATION, this.alphaInterpolationQuality.getRenderingHintValue(RenderingHints.KEY_ALPHA_INTERPOLATION));
		map.put(RenderingHints.KEY_ANTIALIASING, this.antialiasingQuality.getRenderingHintValue(RenderingHints.KEY_ANTIALIASING));
		map.put(RenderingHints.KEY_TEXT_ANTIALIASING, this.textAntialiasingQuality.getRenderingHintValue(RenderingHints.KEY_TEXT_ANTIALIASING));
		map.put(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalMetricsQuality.getRenderingHintValue(RenderingHints.KEY_FRACTIONALMETRICS));
		map.put(RenderingHints.KEY_STROKE_CONTROL, this.strokeControlQuality.getRenderingHintValue(RenderingHints.KEY_STROKE_CONTROL));
		return new RenderingHints(map);
	}
}
