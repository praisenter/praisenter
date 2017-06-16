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

import org.praisenter.bible.Bible;
import org.praisenter.javafx.bible.BibleTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;

import javafx.scene.control.TreeItem;

/**
 * Represents an edit to the source of a {@link Bible}.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleSourceEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<String>> implements EditCommand {
	/** The data */
	private final BibleTreeData data;
	
	/**
	 * Minimal constructor.
	 * @param item the item being changed
	 * @param operation the operation being performed
	 * @param actions the actions
	 */
	@SafeVarargs
	public BibleSourceEditCommand(TreeItem<TreeData> item, ValueChangedCommandOperation<String> operation, CommandAction<ValueChangedCommandOperation<String>>... actions) {
		super(operation, actions);
		
		BibleTreeData data = null;
		if (item != null) {
			TreeData td = item.getValue();
			if (td != null && td instanceof BibleTreeData) {
				data = (BibleTreeData)td;
			}
		}
		
		this.data = data;
	}

	/**
	 * Merge constructor.
	 * @param c1 the previous command
	 * @param c2 the new command
	 */
	private BibleSourceEditCommand(BibleSourceEditCommand c1, BibleSourceEditCommand c2) {
		super(new ValueChangedCommandOperation<>(c1.operation.getOldValue(), c2.operation.getNewValue()), c2.actions);
		this.data = c2.data;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.data != null && this.data.getBible() != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isMergeSupported(org.praisenter.javafx.command.EditCommand)
	 */
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#merge(org.praisenter.javafx.command.EditCommand)
	 */
	@Override
	public BibleSourceEditCommand merge(EditCommand command) {
		if (command instanceof BibleSourceEditCommand) {
			BibleSourceEditCommand other = (BibleSourceEditCommand)command;
			return new BibleSourceEditCommand(other, this);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#execute()
	 */
	@Override
	public void execute() {
		this.data.getBible().setSource(this.operation.getNewValue());
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.ActionsEditCommand#undo()
	 */
	@Override
	public void undo() {
		this.data.getBible().setSource(this.operation.getOldValue());
		super.undo();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.ActionsEditCommand#redo()
	 */
	@Override
	public void redo() {
		this.data.getBible().setSource(this.operation.getNewValue());
		super.redo();
	}
}
