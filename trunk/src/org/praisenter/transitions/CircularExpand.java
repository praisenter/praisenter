package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a circular expand {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class CircularExpand extends Transition {
	/** The {@link CircularExpand} transition id */
	public static final int ID = 50;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public CircularExpand(Type type) {
		super(Messages.getString("transition.circularExpand"), type);
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
		
		// compute the circular area
		int hw = image0.getWidth() / 2;
		int hh = image0.getHeight() / 2;
		double r = Math.sqrt(hw * hw + hh * hh) * pc;
		double s = 2.0 * r;
		double x = hw - r;
		double y = hh - r;
		Ellipse2D.Double circle = new Ellipse2D.Double(x, y, s, s);
		
		if (image0 != null) {
			Area area = new Area(new Rectangle(0, 0, image0.getWidth(), image0.getHeight()));
			area.exclusiveOr(new Area(circle));
			g2d.setClip(area);
			g2d.drawImage(image0, 0, 0, null);
		}
		if (this.type == Transition.Type.IN && image1 != null) {
			// unfortunately we need to do the EXACT opposite clipping operation
			// for pixel perfect results (we can't just clip by the circle)
			Area area = new Area(new Rectangle(0, 0, image1.getWidth(), image1.getHeight()));
			area.intersect(new Area(circle));
			g2d.setClip(area);
			g2d.drawImage(image1, 0, 0, null);
		}
		
		g2d.setClip(shape);
	}
}
