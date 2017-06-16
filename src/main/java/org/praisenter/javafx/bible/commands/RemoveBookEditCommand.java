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
import org.praisenter.bible.Book;
import org.praisenter.javafx.bible.BookTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.RemoveEditCommand;

import javafx.scene.control.TreeItem;

/**
 * Represents a command to remove a single book from a bible.
 * @author William Bittle
 * @version 3.0.0
 */
public final class RemoveBookEditCommand implements RemoveEditCommand, EditCommand, Comparable<RemoveEditCommand> {
	/** The item */
	private final TreeItem<TreeData> item;
	
	/** The parent */
	private final TreeItem<TreeData> parent;
	
	/** The bible the book is being removed from */
	private final Bible bible;
	
	/** The book being removed */
	private final Book book;
	
	/** It's index in the bible */
	private final int index;
	
	/**
	 * Minimal constructor.
	 * @param item the item
	 */
	public RemoveBookEditCommand(TreeItem<TreeData> item) {
		this.item = item;
		this.parent = item.getParent();
		this.index = item.getParent().getChildren().indexOf(item);
		
		Bible bible = null;
		Book book = null;
		
		TreeData data = item.getValue();
		if (data != null) {
			if (data instanceof BookTreeData) {
				BookTreeData td = (BookTreeData)data;
				bible = td.getBible();
				book = td.getBook();
			}
		}
		
		this.bible = bible;
		this.book = book;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RemoveEditCommand o) {
		if (o == null) return -1;
		if (o instanceof RemoveBookEditCommand) {
			return ((RemoveBookEditCommand)o).index - this.index;
		} else {
			return 1;
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
	 * @see org.praisenter.javafx.command.EditCommand#merge(org.praisenter.javafx.command.EditCommand)
	 */
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.bible != null &&
			   this.book != null &&
			   this.item != null &&
			   this.parent != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#execute()
	 */
	@Override
	public void execute() {
		this.redo();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#undo()
	 */
	@Override
	public void undo() {
		this.bible.getBooks().add(this.index, this.book);
		this.parent.getChildren().add(this.index, this.item);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#redo()
	 */
	@Override
	public void redo() {
		this.bible.getBooks().remove(this.book);
		this.parent.getChildren().remove(this.item);
	}
}
