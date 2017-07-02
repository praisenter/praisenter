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

import java.util.ArrayList;

import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.bible.BookTreeData;
import org.praisenter.javafx.bible.ChapterTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.bible.VerseTreeData;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.SelectTreeItemAddedCommandAction;
import org.praisenter.javafx.command.operation.CommandOperation;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Command for adding a new, copying an existing, or moving an existing chapter.
 * @author William Bittle
 * @version 3.0.0
 */
public final class AddChapterEditCommand extends ActionsEditCommand<CommandOperation> implements EditCommand {
	/** The tree */
	private final TreeView<TreeData> tree;
	
	/** The target item that the new chapter will be placed under */
	private final TreeItem<TreeData> item;
	
	/** The new item (and it's sub items) */
	private final TreeItem<TreeData> newItem;
	
	/** The index in the item's children to place the new item */
	private final int index;
	
	/** The target book for the chapter */
	private final Book book;
	
	/** The chapter to add */
	private final Chapter chapter;
	
	/**
	 * Minimal constructor (for a new chapter).
	 * <p>
	 * This will add a new chapter to the given item (book node) at the end with a chapter number of 
	 * the maximum chapter number in the book plus one.
	 * @param tree the tree
	 * @param item the item
	 */
	public AddChapterEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item) {
		this(tree, item, null, -1);
	}
	
	/**
	 * Optional constructor (for copying).
	 * <p>
	 * This will add the given chapter to the given (book) item.  The chapter will be added at the end
	 * of the list of chapters.
	 * @param tree the tree
	 * @param item the item
	 * @param chapter the chapter
	 */
	public AddChapterEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item, Chapter chapter) {
		this(tree, item, chapter, -1);
	}
	
	/**
	 * Optional constructor (for drag-drop).
	 * <p>
	 * This will add the given chapter to the given (book) item.  The chapter will be placed at the given index
	 * in the list of chapters.
	 * @param tree the tree
	 * @param item the item
	 * @param chapter the chapter
	 * @param index the index
	 */
	public AddChapterEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item, Chapter chapter, int index) {
		super(null, new ArrayList<>());
		
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
					book = bd.getBook();
					short number = book.getMaxChapterNumber();
					if (chapter == null) {
						chapter = new Chapter(++number);
					}
					newItem = createChapterItem(bd.getBible(), book, chapter);
				}
			}
		}
		
		this.book = book;
		this.chapter = chapter;
		this.newItem = newItem;
		
		this.actions.add(new SelectTreeItemAddedCommandAction<>(tree, newItem, item));
	}
	
	/**
	 * Creates a new tree item for the given chapter.
	 * @param bible the target bible
	 * @param book the target book
	 * @param chapter the source chapter
	 * @return TreeItem&lt;{@link TreeData}&gt;
	 */
	private TreeItem<TreeData> createChapterItem(Bible bible, Book book, Chapter chapter) {
		TreeItem<TreeData> item = new TreeItem<TreeData>(new ChapterTreeData(bible, book, chapter));
		for (Verse verse : chapter.getVerses()) {
			item.getChildren().add(new TreeItem<TreeData>(new VerseTreeData(bible, book, chapter, verse)));
		}
		return item;
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
		
		super.redo();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#undo()
	 */
	@Override
	public void undo() {
		this.book.getChapters().remove(this.chapter);
		this.item.getChildren().remove(this.newItem);
		super.undo();
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
		super.redo();
	}
}
