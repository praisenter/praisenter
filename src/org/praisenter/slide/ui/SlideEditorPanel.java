package org.praisenter.slide.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JPanel;

import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.Slide;

/**
 * Panel used to edit a slide.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class SlideEditorPanel extends JPanel implements MouseMotionListener, MouseListener, PropertyChangeListener {
	/** The version id */
	private static final long serialVersionUID = -927595042247907332L;

	/** The resize gap (the additional gap area to allow resizing) */
	private static final int RESIZE_GAP = 50;
	
	/** Property used to notify of a slide component change */
	protected static final String PROPERTY_SLIDE_CHANGED = "PropertySlideChanged";
	
	// input
	
	/** The size of the display */
	protected Dimension displaySize;

	/** The slide being edited */
	protected Slide slide;

	// components
	
	/** The panel used to preview the changes */
	protected SlideEditorPreviewPanel pnlSlidePreview;
	
	// mouse commands
	
	/** Mouse command for moving components */
	private MoveCommand moveCommand = new MoveCommand();
	
	/** Mouse command for resizing components */
	private ResizeCommand resizeCommand = new ResizeCommand();
	
	/** Mouse command for resizing the width of components */
	private ResizeWidthCommand resizeWidthCommand = new ResizeWidthCommand();
	
	/** Mouse command for resizing the height of components */
	private ResizeHeightCommand resizeHeightCommand = new ResizeHeightCommand();
	
	/** Mouse command mutually exclusive group */
	private MutexCommandGroup<BoundsCommand> mouseCommandGroup = new MutexCommandGroup<BoundsCommand>(new BoundsCommand[] {
			this.moveCommand,
			this.resizeCommand,
			this.resizeWidthCommand,
			this.resizeHeightCommand
	});
	
	/**
	 * Minimal constructor.
	 * @param slide the slide to edit
	 * @param displaySize the display target size
	 */
	// TODO we may want to allow them to choose the target display during the slide edit process
	public SlideEditorPanel(Slide slide, Dimension displaySize) {
		this.slide = slide;
		this.displaySize = displaySize;
		
		Dimension previewSize = new Dimension(400, 200);
		this.pnlSlidePreview = new SlideEditorPreviewPanel();
		this.pnlSlidePreview.setSlide(this.slide);
		this.pnlSlidePreview.setMinimumSize(previewSize);
		this.pnlSlidePreview.setPreferredSize(previewSize);
		this.pnlSlidePreview.addMouseListener(this);
		this.pnlSlidePreview.addMouseMotionListener(this);
	}
	
	/**
	 * Returns the top level component that is at the given point.
	 * @param point the Slide space point
	 * @return {@link PositionedSlideComponent}
	 */
	protected PositionedSlideComponent getComponentAtPoint(Point point) {
		if (this.slide != null) {
			List<PositionedSlideComponent> components = this.slide.getComponents(PositionedSlideComponent.class);
			// TODO depending on the ordering we may need to reverse this loop
			for (PositionedSlideComponent component : components) {
				if (this.isInside(point, component)) {
					return component;
				}
			}
		}
		return null;
	}
	

	/**
	 * Returns true if the given point is inside any component on the slide.
	 * @param point the Slide space point
	 * @return boolean
	 */
	protected boolean isInside(Point point) {
		if (this.slide != null) {
			List<PositionedSlideComponent> components = this.slide.getComponents(PositionedSlideComponent.class);
			for (PositionedSlideComponent component : components) {
				if (this.isInside(point, component)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the given point is inside the given component.
	 * @param point the Slide space point
	 * @param component the component
	 * @return boolean
	 */
	protected boolean isInside(Point point, PositionedSlideComponent component) {
		// get the bounds
		Shape bounds = component.getBounds();
		// see if the bounds contains the point
		return bounds.contains(point);
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String p = event.getPropertyName();
		// check the property name
		if (PROPERTY_SLIDE_CHANGED.equals(p)) {
			// repaint the preview
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		if (!this.mouseCommandGroup.isCommandActive()) {
			this.setCursor(Cursor.getDefaultCursor());
			this.pnlSlidePreview.setMouseOverComponent(null);
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// check for mouse button 1
		if (e.getButton() == MouseEvent.BUTTON1) {
			// make sure no other command is active
			if (!this.mouseCommandGroup.isCommandActive()) {
				// convert the point to display space
				Point point = this.pnlSlidePreview.getSlideSpacePoint(e.getPoint());
				PositionedSlideComponent current = this.pnlSlidePreview.getMouseOverComponent();
				PositionedSlideComponent component = null;
				// see if we are still over the same component
				if (current != null && this.isInside(point, current)) {
					// then set the component as the one we are still over
					component = current;
				} else {
					// otherwise see if we are over a different one
					component = this.getComponentAtPoint(point);
				}
				// make sure we found one
				if (component != null) {
					// get the bounds
					Rectangle bounds = component.getRectangleBounds();
					int rx = bounds.x + bounds.width - RESIZE_GAP;
					int ry = bounds.y + bounds.height - RESIZE_GAP;
					if (point.x >= rx && point.y >= ry) {
						// resize both dimensions
						this.resizeCommand.begin(point, component);
					} else if (point.x >= rx) {
						// resize width
						this.resizeWidthCommand.begin(point, component);
					} else if (point.y >= ry) {
						// resize height
						this.resizeHeightCommand.begin(point, component);
					} else {
						// move
						this.moveCommand.begin(point, component);
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// check for mouse button 1
		if (e.getButton() == MouseEvent.BUTTON1) {
			// end all the mouse button 1 commands
			this.moveCommand.end();
			this.resizeCommand.end();
			this.resizeWidthCommand.end();
			this.resizeHeightCommand.end();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// see if any mouse command is active
		if (this.mouseCommandGroup.isCommandActive()) {
			// convert the point to display space
			Point end = this.pnlSlidePreview.getSlideSpacePoint(e.getPoint());
			// check if we are still over a component
			if (this.moveCommand.isActive()) {
				// move the component
				this.moveCommand.update(end);
			} else if (this.resizeCommand.isActive()) {
				this.resizeCommand.update(end);
			} else if (this.resizeWidthCommand.isActive()) {
				this.resizeWidthCommand.update(end);
			} else if (this.resizeHeightCommand.isActive()) {
				this.resizeHeightCommand.update(end);
			}
			// if we modify a component then we need to redraw it
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// make sure no other command is active in this group
		if (!this.mouseCommandGroup.isCommandActive()) {
			// convert the point to display space
			Point point = this.pnlSlidePreview.getSlideSpacePoint(e.getPoint());
			PositionedSlideComponent current = this.pnlSlidePreview.getMouseOverComponent();
			PositionedSlideComponent component = null;
			// see if we are still over the same component
			if (current != null && this.isInside(point, current)) {
				// then set the component as the one we are still over
				component = current;
			} else {
				// otherwise see if we are over a different one
				component = this.getComponentAtPoint(point);
			}
			// make sure we found one
			if (component != null) {
				// set the hover component
				if (component != current) {
					this.pnlSlidePreview.setMouseOverComponent(component);
					this.repaint();
				}
				// get the bounds
				Rectangle bounds = component.getRectangleBounds();
				int rx = bounds.x + bounds.width - RESIZE_GAP;
				int ry = bounds.y + bounds.height - RESIZE_GAP;
				if (point.x >= rx && point.y >= ry) {
					// resize both dimensions
					this.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				} else if (point.x >= rx) {
					// resize width
					this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				} else if (point.y >= ry) {
					// resize height
					this.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				} else {
					// then just show the move command
					this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			} else {
				this.repaint();
				this.pnlSlidePreview.setMouseOverComponent(null);
				this.setCursor(Cursor.getDefaultCursor());
			}
		}
	}
}
