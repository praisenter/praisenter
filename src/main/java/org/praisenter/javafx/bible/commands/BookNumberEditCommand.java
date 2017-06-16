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
package org.praisenter.javafx.bible.commands;

import org.praisenter.bible.Book;
import org.praisenter.javafx.bible.BookTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;

import javafx.scene.control.TreeItem;

/**
 * Represents an edit to the number of a {@link Book}.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BookNumberEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<Integer>> implements EditCommand {
	/** The data */
	private final BookTreeData data;
	
	/**
	 * Minimal constructor.
	 * @param item the item
	 * @param operation the operation
	 * @param actions the actions
	 */
	@SafeVarargs
	public BookNumberEditCommand(TreeItem<TreeData> item, ValueChangedCommandOperation<Integer> operation, CommandAction<ValueChangedCommandOperation<Integer>>... actions) {
		super(operation, actions);
		
		BookTreeData data = null;
		if (item != null) {
			TreeData td = item.getValue();
			if (td != null && td instanceof BookTreeData) {
				data = (BookTreeData)td;
			}
		}
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.data != null && this.data.getBook() != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isMergeSupported(org.praisenter.javafx.command.EditCommand)
	 */
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#merge(org.praisenter.javafx.command.EditCommand)
	 */
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#execute()
	 */
	@Override
	public void execute() {
		this.data.getBook().setNumber(this.operation.getNewValue().shortValue());
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.ActionsEditCommand#undo()
	 */
	@Override
	public void undo() {
		this.data.getBook().setNumber(this.operation.getOldValue().shortValue());
		super.undo();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.ActionsEditCommand#redo()
	 */
	@Override
	public void redo() {
		this.data.getBook().setNumber(this.operation.getNewValue().shortValue());
		super.redo();
	}
}
