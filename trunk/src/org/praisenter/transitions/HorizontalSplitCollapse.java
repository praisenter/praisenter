package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a horizontal split collapse {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class HorizontalSplitCollapse extends Transition {
	/** The {@link HorizontalSplitCollapse} transition id */
	public static final int ID = 43;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public HorizontalSplitCollapse(Type type) {
		super(Messages.getString("transition.horizontalSplitCollapse"), type);
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
		if (image0 != null) {
			int hh = image0.getHeight() / 2;
			int y = (int)Math.ceil((double)hh * pc);
			int h = (int)Math.ceil(image0.getHeight() * (1.0 - pc));
			g2d.setClip(0, y, image0.getWidth(), h);
			g2d.drawImage(image0, 0, 0, null);
		}
		if (this.type == Transition.Type.IN && image1 != null) {
			// create two rectangles and merge them into one area for the clip
			int hh = image1.getHeight() / 2;
			// h = hh - hh * pc
			int h = (int)Math.ceil((double)hh * pc);
			int w = image1.getWidth();
			// y = hh + hh * pc
			int y = (int)Math.ceil((double)image1.getHeight() - (double)hh * pc);
			Rectangle left = new Rectangle(0, 0, w, h);
			Rectangle right = new Rectangle(0, y, w, h);
			Area area = new Area();
			area.add(new Area(left));
			area.add(new Area(right));
			g2d.setClip(area);
			g2d.drawImage(image1, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
