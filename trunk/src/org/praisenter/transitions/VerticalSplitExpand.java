package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a vertical split expand {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class VerticalSplitExpand extends Transition {
	/** The {@link VerticalSplitExpand} transition id */
	public static final int ID = 41;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public VerticalSplitExpand(Type type) {
		super(Messages.getString("transition.verticalSplitExpand"), type);
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
		// to get pixel perfect results we need to make sure we use the x and width from
		// image0 so that image1 will be clipped appropriately (this is only necessary when
		// we have both image0 and image1)
		int x0 = 0;
		int w0 = 0;
		if (image0 != null) {
			// create two rectangles and merge them into one area for the clip
			int hw = image0.getWidth() / 2;
			// w = hw - hw * pc
			w0 = (int)Math.ceil((double)hw * (1.0 - pc));
			int h = image0.getHeight();
			// x = hw + hw * pc
			x0 = (int)Math.ceil((double)hw * (1.0 + pc));
			Rectangle left = new Rectangle(0, 0, w0, h);
			Rectangle right = new Rectangle(x0, 0, w0, h);
			Area area = new Area();
			area.add(new Area(left));
			area.add(new Area(right));
			g2d.setClip(area);
			g2d.drawImage(image0, 0, 0, null);
		}
		if (this.type == Transition.Type.IN && image1 != null) {
			int x = 0;
			int w = 0;
			if (image0 != null) {
				w = x0 - w0;
				x = w0;
			} else {
				int hw = image1.getWidth() / 2;
				x = (int)Math.ceil((double)hw * (1.0 - pc));
				w = (int)Math.ceil(image1.getWidth() * pc);
			}
			g2d.setClip(x, 0, w, image1.getHeight());
			g2d.drawImage(image1, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
