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

import org.praisenter.bible.Verse;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.bible.VerseTreeData;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.ValueChangedEditCommand;

import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Represents an edit to the text of a {@link Verse}.
 * @author William Bittle
 * @version 3.0.0
 */
public final class VerseTextEditCommand extends ValueChangedEditCommand<String> implements EditCommand {
	/** The tree view */
	private final TreeView<TreeData> tree;
	
	/** The tree item */
	private final TreeItem<TreeData> item;
	
	/** The editor control */
	private final TextInputControl editor;
	
	/** The tree data for the verse */
	private final VerseTreeData data;

	/**
	 * Constructor.
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @param tree the tree view
	 * @param item the tree item
	 * @param editor the text editor
	 */
	public VerseTextEditCommand(String oldValue, String newValue, TreeView<TreeData> tree, TreeItem<TreeData> item, TextInputControl editor) {
		super(oldValue, newValue);
		
		this.tree = tree;
		this.item = item;
		this.editor = editor;
		
		VerseTreeData data = null;
		if (item != null) {
			TreeData td = item.getValue();
			if (td != null && td instanceof VerseTreeData) {
				data = (VerseTreeData)td;
			}
		}
		
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.data != null && 
			   this.data.getVerse() != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isMergeSupported(org.praisenter.javafx.command.EditCommand)
	 */
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command instanceof VerseTextEditCommand) {
			VerseTextEditCommand other = (VerseTextEditCommand)command;
			return other.data.getBook() == this.data.getBook() &&
				   other.data.getChapter() == this.data.getChapter() &&
				   other.data.getVerse() == this.data.getVerse();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#merge(org.praisenter.javafx.command.EditCommand)
	 */
	@Override
	public EditCommand merge(EditCommand command) {
		if (command instanceof VerseTextEditCommand) {
			VerseTextEditCommand other = (VerseTextEditCommand)command;
			return new VerseTextEditCommand(other.oldValue, this.newValue, this.tree, this.item, this.editor);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#execute()
	 */
	@Override
	public void execute() {
		// update the data
		this.data.getVerse().setText(this.newValue);
		this.data.update();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.ActionsEditCommand#undo()
	 */
	@Override
	public void undo() {
		// update the data
		this.data.getVerse().setText(this.oldValue);
		this.data.update();
		
		// perform actions
		this.select(this.tree, this.item);
		this.text(this.editor, this.oldValue);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.ActionsEditCommand#redo()
	 */
	@Override
	public void redo() {
		// update the data
		this.data.getVerse().setText(this.newValue);
		this.data.update();

		// perform actions
		this.select(this.tree, this.item);
		this.text(this.editor, this.newValue);
	}
}
