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
import org.praisenter.javafx.bible.BibleTreeData;
import org.praisenter.javafx.bible.BookTreeData;
import org.praisenter.javafx.bible.ChapterTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.bible.VerseTreeData;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.SelectTreeItemAddedCommandAction;
import org.praisenter.javafx.command.operation.CommandOperation;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Command for adding a new, copying an existing, or moving an existing book.
 * @author William Bittle
 * @version 3.0.0
 */
public final class AddBookEditCommand extends ActionsEditCommand<CommandOperation> implements EditCommand {
	/** The tree */
	private final TreeView<TreeData> tree;
	
	/** The target item that the book will be placed under */
	private final TreeItem<TreeData> item;
	
	/** The new item (and it's sub items) */
	private final TreeItem<TreeData> newItem;
	
	/** The index in the item's children to place the new item */
	private final int index;
	
	/** The bible to place the book in */
	private final Bible bible;
	
	/** The book to added */
	private final Book book;
	
	/**
	 * Minimal constructor (for a new book).
	 * <p>
	 * This will add a new book to the given item (bible node) at the end with a book number of 
	 * the maximum book number in the bible plus one.
	 * @param tree the tree
	 * @param item the item
	 */
	public AddBookEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item) {
		this(tree, item, null, -1);
	}
	
	/**
	 * Optional constructor (for copying).
	 * <p>
	 * This will add the given book to the given (bible) item.  The book will be added at the end
	 * of the list of books.
	 * @param tree the tree
	 * @param item the item
	 * @param book the book
	 */
	public AddBookEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item, Book book) {
		this(tree, item, book, -1);
	}
	
	/**
	 * Optional constructor (for drag-drop).
	 * <p>
	 * This will add the given book to the given (bible) item.  The book will be placed at the given index
	 * in the list of books.
	 * @param tree the tree
	 * @param item the item
	 * @param book the book
	 * @param index the index
	 */
	public AddBookEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item, Book book, int index) {
		super(null, new ArrayList<>());
		this.tree = tree;
		this.item = item;
		
		TreeItem<TreeData> newItem = null;
		Bible bible = null;
		
		if (item != null) {
			TreeData td = item.getValue();
			if (td != null) {
				if (td instanceof BibleTreeData) {
					BibleTreeData bd = (BibleTreeData)item.getValue();
					short number = bd.getBible().getMaxBookNumber();
					bible = bd.getBible();
					if (book == null) {
						book = new Book(Translations.get("bible.edit.book.default"), ++number);
					}
					newItem = createBookItem(bible, book);
				}
			}
		}
		
		this.bible = bible;
		this.book = book;
		this.newItem = newItem;
		this.index = index;
		
		this.actions.add(new SelectTreeItemAddedCommandAction<>(tree, newItem, item));
	}

	/**
	 * Creates a new tree item for the given book.
	 * @param bible the target bible
	 * @param book the source book
	 * @return TreeItem&lt;{@link TreeData}&gt;
	 */
	private TreeItem<TreeData> createBookItem(Bible bible, Book book) {
		TreeItem<TreeData> item = new TreeItem<TreeData>(new BookTreeData(bible, book));
		for (Chapter chapter : book.getChapters()) {
			TreeItem<TreeData> cItem = new TreeItem<TreeData>(new ChapterTreeData(bible, book, chapter));
			item.getChildren().add(cItem);
			for (Verse verse : chapter.getVerses()) {
				cItem.getChildren().add(new TreeItem<TreeData>(new VerseTreeData(bible, book, chapter, verse)));
			}
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
		return this.tree != null && this.item != null && this.newItem != null && this.book != null && this.bible != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#execute()
	 */
	@Override
	public void execute() {
		if (this.index < 0) {
			this.bible.getBooks().add(this.book);
			this.item.getChildren().add(this.newItem);
		} else {
			this.bible.getBooks().add(this.index, this.book);
			this.item.getChildren().add(this.index, this.newItem);
		}
		super.redo();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#undo()
	 */
	@Override
	public void undo() {
		this.bible.getBooks().remove(this.book);
		this.item.getChildren().remove(this.newItem);
		super.undo();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#redo()
	 */
	@Override
	public void redo() {
		if (this.index < 0) {
			this.bible.getBooks().add(this.book);
			this.item.getChildren().add(this.newItem);
		} else {
			this.bible.getBooks().add(this.index, this.book);
			this.item.getChildren().add(this.index, this.newItem);
		}
		super.redo();
	}
}
