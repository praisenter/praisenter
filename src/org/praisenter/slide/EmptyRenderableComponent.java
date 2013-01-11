package org.praisenter.slide;

import java.awt.Graphics2D;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.resources.Messages;
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
	/** The component name */
	@XmlElement(name = "Name")
	protected String name;

	/** The z-ordering of this component */
	@XmlAttribute(name = "Order")
	protected int order;
	
	/** The width of this component */
	@XmlAttribute(name = "Width", required = true)
	protected int width;
	
	/** The height of this component */
	@XmlAttribute(name = "Height", required = true)
	protected int height;
	
	/**
	 * Default constructor.
	 * <p>
	 * This should only be used by JAXB.
	 */
	protected EmptyRenderableComponent() {
		this(Messages.getString("slide.component.unnamed"), 200, 200);
	}

	/**
	 * Creates a new empty renderable component.
	 * @param name the component name
	 * @param width the component width
	 * @param height the component height
	 */
	public EmptyRenderableComponent(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.order = 0;
	}
	
	/**
	 * Copy constructor.
	 * @param component the component to copy
	 */
	public EmptyRenderableComponent(EmptyRenderableComponent component) {
		this.name = component.name;
		this.width = component.width;
		this.height = component.height;
		this.order = component.order;
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

