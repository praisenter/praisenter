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
import org.praisenter.javafx.bible.BookTreeData;
import org.praisenter.javafx.bible.ChapterTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.command.EditCommand;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class AddChapterEditCommand implements EditCommand {
	private final TreeView<TreeData> tree;
	private final TreeItem<TreeData> item;
	
	private final TreeItem<TreeData> newItem;
	private final int index;
	private final Book book;
	private final Chapter chapter;
	
	public AddChapterEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item) {
		this(tree, item, null, -1);
	}
	
	public AddChapterEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item, Chapter chapter) {
		this(tree, item, chapter, -1);
	}
	
	public AddChapterEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item, Chapter chapter, int index) {
		this.tree = tree;
		this.item = item;
		this.index = index;
		
		TreeItem<TreeData> newItem = null;
		Book book = null;
		
		if (item != null) {
			TreeData td = item.getValue();
			if (td != null) {
				if (td instanceof BookTreeData) {
					// add new chapter
					BookTreeData bd = (BookTreeData)item.getValue();
					short number = bd.getBook().getMaxChapterNumber();
					book = bd.getBook();
					if (chapter == null) {
						chapter = new Chapter(++number);
					}
					newItem = new TreeItem<TreeData>(new ChapterTreeData(bd.getBible(), bd.getBook(), chapter));
				}
			}
		}
		
		this.book = book;
		this.chapter = chapter;
		this.newItem = newItem;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#isMergeSupported(org.praisenter.javafx.commands.EditCommand)
	 */
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#merge(org.praisenter.javafx.commands.EditCommand)
	 */
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.tree != null && this.item != null && this.newItem != null && this.book != null && this.chapter != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#execute()
	 */
	@Override
	public void execute() {
		if (this.index < 0) {
			this.book.getChapters().add(this.chapter);
			this.item.getChildren().add(this.newItem);
		} else {
			this.book.getChapters().add(this.index, this.chapter);
			this.item.getChildren().add(this.index, this.newItem);
		}
		
		this.item.setExpanded(true);
		
		int index = this.tree.getRow(this.newItem);
		if (index > 0) {
			// selected it
			this.tree.getSelectionModel().clearAndSelect(index);
			final int offset = 5;
			// scroll to it (well, close to it, we don't want it at the top)
			if (index - offset > 0) {
				this.tree.scrollTo(index - offset);
			}
		}
		this.tree.requestFocus();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#undo()
	 */
	@Override
	public void undo() {
		this.book.getChapters().remove(this.chapter);
		this.item.getChildren().remove(this.newItem);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#redo()
	 */
	@Override
	public void redo() {
		if (this.index < 0) {
			this.book.getChapters().add(this.chapter);
			this.item.getChildren().add(this.newItem);
		} else {
			this.book.getChapters().add(this.index, this.chapter);
			this.item.getChildren().add(this.index, this.newItem);
		}
	}
}
