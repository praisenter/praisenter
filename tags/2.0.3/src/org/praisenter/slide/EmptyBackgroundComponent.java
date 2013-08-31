/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.slide;

import java.awt.Graphics2D;
import java.io.Serializable;

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
@XmlRootElement(name = "EmptyBackgroundComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class EmptyBackgroundComponent extends AbstractRenderableComponent implements BackgroundComponent, RenderableComponent, SlideComponent, Serializable {
	/** The version id */
	private static final long serialVersionUID = 1073168247822034844L;

	/**
	 * Default constructor.
	 * <p>
	 * This should only be used by JAXB.
	 */
	protected EmptyBackgroundComponent() {
		super();
	}

	/**
	 * Creates a new empty renderable component.
	 * @param name the component name
	 * @param width the component width
	 * @param height the component height
	 */
	public EmptyBackgroundComponent(String name, int width, int height) {
		super(name, width, height);
	}
	
	/**
	 * Copy constructor.
	 * @param component the component to copy
	 */
	public EmptyBackgroundComponent(EmptyBackgroundComponent component) {
		super(component);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#copy()
	 */
	@Override
	public EmptyBackgroundComponent copy() {
		return new EmptyBackgroundComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractRenderableComponent#isTransitionRequired(org.praisenter.slide.RenderableComponent)
	 */
	@Override
	public boolean isTransitionRequired(RenderableComponent component) {
		// a transition is required here unless the given component is of this type (or null)
		if (component == null) return false;
		if (component instanceof EmptyBackgroundComponent) return false;
		return true;
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

