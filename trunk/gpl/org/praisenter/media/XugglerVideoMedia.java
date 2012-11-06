package org.praisenter.media;

import java.awt.Dimension;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.Media;
import org.praisenter.media.PlayableMedia;
import org.praisenter.media.Thumbnail;
import org.praisenter.utilities.ImageUtilities;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class XugglerVideoMedia extends AbstractVideoMedia implements Media, PlayableMedia {
	protected BufferedImage firstFrame;
	protected BufferedImage currentFrame;
	
	// video play properties
	
	protected List<MediaPlayerListener> listeners;
	protected IContainer container;
	protected IStreamCoder videoCoder;
	protected IStreamCoder audioCoder;
	
	// TODO audio
//	protected IStreamCoder audioCoder;
	
	public XugglerVideoMedia(FileProperties fileProperties, BufferedImage firstFrame, IContainer container, IStreamCoder videoCoder, IStreamCoder audioCoder) {
		super(fileProperties);
		this.firstFrame = firstFrame;
		this.currentFrame = firstFrame;
		this.listeners = new ArrayList<MediaPlayerListener>();
		this.container = container;
		this.videoCoder = videoCoder;
		this.audioCoder = audioCoder;
	}

	@Override
	public BufferedImage getCurrentFrame() {
		return this.currentFrame;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.AbstractVideoMedia#getFirstFrame()
	 */
	@Override
	public BufferedImage getFirstFrame() {
		return this.firstFrame;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public Thumbnail getThumbnail(Dimension size) {
		// resize the image to a thumbnail size
		BufferedImage image = ImageUtilities.getUniformScaledImage(this.firstFrame, size.width, size.height, AffineTransformOp.TYPE_BILINEAR);
		// return the thumbnail
		return new Thumbnail(this.fileProperties, this.type, image);
	}
}
