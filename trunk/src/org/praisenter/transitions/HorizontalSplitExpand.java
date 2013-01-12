package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a horizontal split expand {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class HorizontalSplitExpand extends Transition {
	/** The {@link HorizontalSplitExpand} transition id */
	public static final int ID = 42;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public HorizontalSplitExpand(Type type) {
		super(Messages.getString("transition.horizontalSplitExpand"), type);
	} 

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getTransitionId() {
		return ID;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage, double)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc) {
		Shape shape = g2d.getClip();
		// to get pixel perfect results we need to make sure we use the y and height from
		// image0 so that image1 will be clipped appropriately (this is only necessary when
		// we have both image0 and image1)
		int y0 = 0;
		int h0 = 0;
		if (image0 != null) {
			// create two rectangles and merge them into one area for the clip
			int hh = image0.getHeight() / 2;
			// h = hh - hh * pc
			int h = h0 = (int)Math.ceil((double)hh * (1.0 - pc));
			int w = image0.getWidth();
			// y = hh + hh * pc
			int y = y0 = (int)Math.ceil((double)hh * (1.0 + pc));
			Rectangle left = new Rectangle(0, 0, w, h);
			Rectangle right = new Rectangle(0, y, w, h);
			Area area = new Area();
			area.add(new Area(left));
			area.add(new Area(right));
			g2d.setClip(area);
			g2d.drawImage(image0, 0, 0, null);
		}
		if (this.type == Transition.Type.IN && image1 != null) {
			int y = 0;
			int h = 0;
			if (image0 != null) {
				h = y0 - h0;
				y = h0;
			} else {
				int hh = image1.getHeight() / 2;
				y = (int)Math.ceil((double)hh * (1.0 - pc));
				h = (int)Math.ceil(image1.getHeight() * pc);
			}
			g2d.setClip(0, y, image1.getWidth(), h);
			g2d.drawImage(image1, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
