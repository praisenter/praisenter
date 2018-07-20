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

/**
 * An {@link EditCommand} that wraps any {@link EditCommand} with a ordering value.
 * <p>
 * This class does not support merging.
 * @author William Bittle
 * @version 3.0.0
 */
public final class OrderedWrappedEditCommand extends AbstractEditCommand implements EditCommand, Comparable<OrderedWrappedEditCommand> {
	/** The command */
	private final EditCommand command;
	
	/** The order */
	private final int order;
	
	/**
	 * Minimal constructor.
	 * @param command the command
	 * @param order the order
	 */
	public OrderedWrappedEditCommand(EditCommand command, int order) {
		this.command = command;
		this.order = order;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(OrderedWrappedEditCommand other) {
		return this.order - other.order;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#execute()
	 */
	@Override
	public void execute() {
		this.command.execute();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#isMergeSupported(org.praisenter.javafx.commands.EditCommand)
	 */
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.command != null && this.command.isValid();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#merge(org.praisenter.javafx.commands.EditCommand)
	 */
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#redo()
	 */
	@Override
	public void redo() {
		this.command.redo();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#undo()
	 */
	@Override
	public void undo() {
		this.command.undo();
	}
}
