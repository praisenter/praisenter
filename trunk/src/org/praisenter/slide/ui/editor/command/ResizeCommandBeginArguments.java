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
package org.praisenter.slide.ui.editor.command;

import java.awt.Dimension;
import java.awt.Point;

import org.praisenter.command.Command;

/**
 * Decorator class used as input for the {@link Command#begin(Object)} method for {@link ResizeCommand}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ResizeCommandBeginArguments implements BoundsCommandBeginArguments {
	/** The resize prong location */
	protected ResizeProngLocation prongLocation;

	/** The decorated being arguments */
	protected BoundsCommandBeginArguments arguments;
	
	/**
	 * Full constructor.
	 * @param arguments the bounds arguments
	 * @param prongLocation the prong location
	 */
	public ResizeCommandBeginArguments(BoundsCommandBeginArguments arguments, ResizeProngLocation prongLocation) {
		this.arguments = arguments;
		this.prongLocation = prongLocation;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#translate(int, int)
	 */
	@Override
	public void translate(int dx, int dy) {
		this.arguments.translate(dx, dy);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#resize(int, int)
	 */
	@Override
	public Dimension resize(int dw, int dh) {
		return this.arguments.resize(dw, dh);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#getStart()
	 */
	@Override
	public Point getStart() {
		return this.arguments.getStart();
	}
	
	/**
	 * Returns the prong location.
	 * @return {@link ResizeProngLocation}
	 */
	public ResizeProngLocation getProngLocation() {
		return this.prongLocation;
	}
}
