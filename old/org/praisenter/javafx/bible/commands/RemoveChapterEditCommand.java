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
import org.praisenter.bible.Chapter;
import org.praisenter.javafx.bible.ChapterTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.RemoveEditCommand;

import javafx.scene.control.TreeItem;

/**
 * Represents a command to remove a single chapter from a bible.
 * @author William Bittle
 * @version 3.0.0
 */
public final class RemoveChapterEditCommand implements RemoveEditCommand, EditCommand, Comparable<RemoveEditCommand> {
	/** The item */
	private final TreeItem<TreeData> item;
	
	/** The parent */
	private final TreeItem<TreeData> parent;
	
	/** The book the chapter is in */
	private final Book book;
	
	/** The chapter being removed */
	private final Chapter chapter;
	
	/** It's index in the book */
	private final int index;

	/**
	 * Minimal constructor.
	 * @param item the item
	 */
	public RemoveChapterEditCommand(TreeItem<TreeData> item) {
		this.item = item;
		this.parent = item.getParent();
		this.index = item.getParent().getChildren().indexOf(item);
		
		Book book = null;
		Chapter chapter = null;
		
		TreeData data = item.getValue();
		if (data != null) {
			if (data instanceof ChapterTreeData) {
				ChapterTreeData td = (ChapterTreeData)data;
				book = td.getBook();
				chapter = td.getChapter();
			}
		}
		
		this.chapter = chapter;
		this.book = book;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RemoveEditCommand o) {
		if (o == null) return -1;
		if (o instanceof RemoveChapterEditCommand) {
			return ((RemoveChapterEditCommand)o).index - this.index;
		} else if (o instanceof RemoveVerseEditCommand) {
			return 1;
		} else {
			return -1;
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
		return this.book != null &&
			   this.chapter != null && 
			   this.item != null &&
			   this.parent != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#execute()
	 */
	@Override
	public void execute() {
		this.book.getChapters().remove(this.chapter);
		this.parent.getChildren().remove(this.item);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#undo()
	 */
	@Override
	public void undo() {
		this.book.getChapters().add(this.index, this.chapter);
		this.parent.getChildren().add(this.index, this.item);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#redo()
	 */
	@Override
	public void redo() {
		this.book.getChapters().remove(this.chapter);
		this.parent.getChildren().remove(this.item);
	}
}
