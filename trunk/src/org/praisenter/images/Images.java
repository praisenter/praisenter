package org.praisenter.images;

import java.awt.image.BufferedImage;

import org.praisenter.utilities.ImageUtilities;

/**
 * Class storing all the images used by the application.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Images {
	/** Transparent background */
	public static final BufferedImage TRANSPARENT_BACKGROUND = ImageUtilities.getImageFromClassPathSuppressExceptions("/org/praisenter/images/transparent.png");
	
	/** Midi audio icon */
	public static final BufferedImage MIDI_AUDIO = ImageUtilities.getImageFromClassPathSuppressExceptions("/org/praisenter/images/midi-audio.png");
	
	/** Sampled audio icon */
	public static final BufferedImage SAMPLED_AUDIO = ImageUtilities.getImageFromClassPathSuppressExceptions("/org/praisenter/images/sampled-audio.png");
}
