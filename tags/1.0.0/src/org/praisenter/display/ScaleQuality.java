package org.praisenter.display;

import java.awt.image.AffineTransformOp;

/**
 * Enumeration of the scaling qualities.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ScaleQuality {
	/** Analogous to AffineTransformOp.TYPE_NEAREST_NEIGHBOR */
	NEAREST_NEIGHBOR(AffineTransformOp.TYPE_NEAREST_NEIGHBOR),
	
	/** Analogous to AffineTransformOp.TYPE_BILINEAR */
	BILINEAR(AffineTransformOp.TYPE_BILINEAR),
	
	/** Analogous to AffineTransformOp.TYPE_BICUBIC */
	BICUBIC(AffineTransformOp.TYPE_BICUBIC);
	
	/** The quality used by AffineTransformOp */
	protected int quality;
	
	/**
	 * Full constructor.
	 * @param quality the quality defined by AffineTransformOp
	 */
	private ScaleQuality(int quality) {
		this.quality = quality;
	}
	
	/**
	 * Returns the quality defined by AffineTransformOp.
	 * @return int
	 */
	public int getQuality() {
		return this.quality;
	}
}
