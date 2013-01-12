package org.praisenter.slide;

import java.awt.Graphics2D;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.graphics.Fill;

/**
 * Represents a {@link RenderableComponent} that has no rendering.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "EmptyRenderableComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class EmptyRenderableComponent extends AbstractRenderableComponent {
	/**
	 * Default constructor.
	 * <p>
	 * This should only be used by JAXB.
	 */
	protected EmptyRenderableComponent() {
		super();
	}

	/**
	 * Creates a new empty renderable component.
	 * @param name the component name
	 * @param width the component width
	 * @param height the component height
	 */
	public EmptyRenderableComponent(String name, int width, int height) {
		super(name, width, height);
	}
	
	/**
	 * Copy constructor.
	 * @param component the component to copy
	 */
	public EmptyRenderableComponent(EmptyRenderableComponent component) {
		super(component);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#copy()
	 */
	@Override
	public EmptyRenderableComponent copy() {
		return new EmptyRenderableComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#getBackgroundFill()
	 */
	@Override
	public Fill getBackgroundFill() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#setBackgroundFill(org.praisenter.slide.graphics.Fill)
	 */
	@Override
	public void setBackgroundFill(Fill fill) {}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#isBackgroundVisible()
	 */
	@Override
	public boolean isBackgroundVisible() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#setBackgroundVisible(boolean)
	 */
	@Override
	public void setBackgroundVisible(boolean visible) {}
}

