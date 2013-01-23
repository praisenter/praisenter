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
package org.praisenter.slide.ui.present;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.praisenter.preferences.Preferences;
import org.praisenter.slide.RenderableComponent;

/**
 * Represents a rendered item that uses the quality preferences.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class QualityRenderItem extends DefaultRenderItem implements RenderGroup {
	/**
	 * Full constructor.
	 * @param component the component
	 */
	public QualityRenderItem(RenderableComponent component) {
		super(component);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.display.SlideComponentCache#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		if (this.component != null) {
			RenderingHints oHints = g.getRenderingHints();
			// use the quality set in the preferences
			Preferences preferences = Preferences.getInstance();
			g.setRenderingHints(preferences.getRenderQuality().getRenderingHints());
			this.component.render(g);
			g.setRenderingHints(oHints);
		}
	}
}