/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
package org.praisenter.javafx.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An ordered composite {@link EditCommand} that sorts the given commands and executes them in order for
 * the execute and redo actions and in reverse order for the undo action.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the {@link EditCommand} type
 */
public final class OrderedCompositeEditCommand<T extends EditCommand & Comparable<T>> extends AbstractCompositeEditCommand<T> implements EditCommand {
	/**
	 * Minimal constructor.
	 * @param commands the commands
	 */
	public OrderedCompositeEditCommand(List<T> commands) {
		super(commands);
		Collections.sort(commands);
	}
	
	/**
	 * Optional constructor.
	 * @param commands the commands
	 */
	@SafeVarargs
	public OrderedCompositeEditCommand(T... commands) {
		this(Arrays.asList(commands));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.AbstractCompositeEditCommand#undo()
	 */
	@Override
	public void undo() {
		if (this.commands == null) return;
		// since these are ordered, we need to do them in reverse order for the undo operation
		for (int i = this.commands.size() - 1; i >= 0; i--) {
			EditCommand command = this.commands.get(i);
			command.undo();
		}
	}
}
