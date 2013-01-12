package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a vertical split collapse {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class VerticalSplitCollapse extends Transition {
	/** The {@link VerticalSplitCollapse} transition id */
	public static final int ID = 40;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public VerticalSplitCollapse(Type type) {
		super(Messages.getString("transition.verticalSplitCollapse"), type);
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
			int hw = image0.getWidth() / 2;
			x0 = (int)Math.ceil((double)hw * pc);
			w0 = (int)Math.ceil((double)image0.getWidth() * (1.0 - pc));
			g2d.setClip(x0, 0, w0, image0.getHeight());
			g2d.drawImage(image0, 0, 0, null);
		}
		if (this.type == Transition.Type.IN && image1 != null) {
			// create two rectangles and merge them into one area for the clip
			int w = 0;
			int x = 0;
			int h = image1.getHeight();
			if (image0 != null) {
				w = x0;
				x = x0 + w0;
			} else {
				int hw = image1.getWidth() / 2;
				// w = hw * pc
				w = (int)Math.ceil((double)hw * pc);
				// x = w - hw * pc
				x = (int)Math.ceil((double)image1.getWidth() - (double)hw * pc);
			}
			Rectangle left = new Rectangle(0, 0, w, h);
			Rectangle right = new Rectangle(x, 0, w, h);
			Area area = new Area();
			area.add(new Area(left));
			area.add(new Area(right));
			g2d.setClip(area);
			g2d.drawImage(image1, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
