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

import java.util.function.Consumer;

/**
 * Represents an edit command that performs arbitrary actions.  This is primarily used to perform
 * UI related actions after a chain of other commands has been executed.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ActionEditCommand extends AbstractEditCommand implements EditCommand {
	/** The execute action */
	private final Consumer<ActionEditCommand> executeAction;
	
	/** The undo action */
	private final Consumer<ActionEditCommand> undoAction;
	
	/** The redo action */
	private final Consumer<ActionEditCommand> redoAction;

	/**
	 * Minimal constructor.
	 * @param action an action to perform for execute, undo, and redo; can be null
	 */
	public ActionEditCommand(Consumer<ActionEditCommand> action) {
		this(action, action, action);
	}
	
	/**
	 * Optional constructor.
	 * @param executeAndRedoAction an action to perform for execute and redo; can be null
	 * @param undoAction an action to perform for undo; can be null
	 */
	public ActionEditCommand(Consumer<ActionEditCommand> executeAndRedoAction, Consumer<ActionEditCommand> undoAction) {
		this(executeAndRedoAction, undoAction, executeAndRedoAction);
	}
	
	/**
	 * Optional constructor.
	 * @param executeAction an action to perform for execute; can be null
	 * @param undoAction an action to perform for undo; can be null
	 * @param redoAction an action to perform for redo; can be null
	 */
	public ActionEditCommand(Consumer<ActionEditCommand> executeAction, Consumer<ActionEditCommand> undoAction, Consumer<ActionEditCommand> redoAction) {
		this.executeAction = executeAction;
		this.undoAction = undoAction;
		this.redoAction = redoAction;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#execute()
	 */
	@Override
	public void execute() {
		if (this.executeAction != null) {
			this.executeAction.accept(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isMergeSupported(org.praisenter.javafx.command.EditCommand)
	 */
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isValid()
	 */
	@Override
	public boolean isValid() {
		// one of the actions must be non-null
		return this.executeAction != null || this.redoAction != null || this.undoAction != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#merge(org.praisenter.javafx.command.EditCommand)
	 */
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#redo()
	 */
	@Override
	public void redo() {
		if (this.redoAction != null) {
			this.redoAction.accept(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#undo()
	 */
	public void undo() {
		if (this.undoAction != null) {
			this.undoAction.accept(this);
		}
	}
}
