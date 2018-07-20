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
import java.util.Collections;
import java.util.List;

import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.bible.BibleTreeData;
import org.praisenter.javafx.bible.BookTreeData;
import org.praisenter.javafx.bible.ChapterTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.bible.VerseTreeData;
import org.praisenter.javafx.command.EditCommand;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Command to re-order books, chapters, and/or verses based on their numbers.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ReorderEditCommand implements EditCommand {
	/** The tree */
	private final TreeView<TreeData> tree;
	
	/** The node to begin the reordering */
	private final TreeItem<TreeData> item;
	
	/** The original ordering for undo */
	private final List<NumberedItem<TreeData>> originalOrdering = new ArrayList<NumberedItem<TreeData>>();
	
	/**
	 * Minimal constructor.
	 * @param tree the tree
	 * @param item the node to begin the reordering
	 */
	public ReorderEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item) {
		this.tree = tree;
		this.item = item;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.item != null && this.tree != null;
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
		// remove the data
		TreeData td = this.item.getValue();
		if (td instanceof BibleTreeData) {
			reorderBooks(this.item);
		} else if (td instanceof BookTreeData) {
			reorderChapters(this.item);
		} else if (td instanceof ChapterTreeData) {
			reorderVerses(this.item);
		} else if (td instanceof VerseTreeData) {
			reorderVerses(this.item.getParent());
		}
		
		this.tree.getSelectionModel().clearSelection();
		this.tree.requestFocus();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#redo()
	 */
	@Override
	public void redo() {
		this.originalOrdering.clear();
		this.execute();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#undo()
	 */
	@Override
	public void undo() {
		for (NumberedItem<TreeData> reorder : this.originalOrdering) {
			TreeItem<TreeData> item = reorder.getItem();
			int index = reorder.getNumber();
			
			// UI
			TreeItem<TreeData> node = item.getParent();
			if (node != null) {
				node.getChildren().remove(item);
				node.getChildren().add(index, item);
			}
			
			// Data
			TreeData data = item.getValue();
			if (data != null) {
				if (data instanceof BookTreeData) {
					// reorder the book
					BookTreeData btd = ((BookTreeData)data);
					Bible bible = btd.getBible();
					Book book = btd.getBook();
					bible.getBooks().remove(book);
					bible.getBooks().add(index, book);
				} else if (data instanceof ChapterTreeData) {
					// reorder the chapter
					ChapterTreeData ctd = ((ChapterTreeData)data);
					Book book = ctd.getBook();
					Chapter chapter = ctd.getChapter();
					book.getChapters().remove(chapter);
					book.getChapters().add(index, chapter);
				} else if (data instanceof VerseTreeData) {
					// reorder the verse
					VerseTreeData vtd = ((VerseTreeData)data);
					Chapter chapter = vtd.getChapter();
					Verse verse = vtd.getVerse();
					chapter.getVerses().remove(verse);
					chapter.getVerses().add(index, verse);
				}
			}
		}
	}
	
	/**
	 * Reorders the books in the given bible.
	 * @param node the bible node
	 */
	private void reorderBooks(TreeItem<TreeData> node) {
		// sort the chapters in each book
		int i = 0;
		for (TreeItem<TreeData> item : node.getChildren()) {
			// record the original order
			this.originalOrdering.add(new NumberedItem<TreeData>(item, i));
			reorderChapters(item);
			i++;
		}
		// sort the books
		Bible bible = ((BibleTreeData)node.getValue()).getBible();
		Collections.sort(bible.getBooks());
		// sort the nodes
		node.getChildren().sort((TreeItem<TreeData> b1, TreeItem<TreeData> b2) -> {
			if (b1 == null) return 1;
			if (b2 == null) return -1;
			return b1.getValue().compareTo(b2.getValue());
		});
	}
	
	/**
	 * Reorders the chapters in the given book.
	 * @param node the book node.
	 */
	private void reorderChapters(TreeItem<TreeData> node) {
		int i = 0;
		for (TreeItem<TreeData> item : node.getChildren()) {
			// record the original order
			this.originalOrdering.add(new NumberedItem<TreeData>(item, i));
			reorderVerses(item);
			i++;
		}
		// make sure the data is sorted the same way
		Collections.sort(((BookTreeData)node.getValue()).getBook().getChapters());
		// sort the nodes
		node.getChildren().sort((TreeItem<TreeData> b1, TreeItem<TreeData> b2) -> {
			if (b1 == null) return 1;
			if (b2 == null) return -1;
			return b1.getValue().compareTo(b2.getValue());
		});
	}
	
	/**
	 * Reorders the verses in the given chapter.
	 * @param node the chapter node
	 */
	private void reorderVerses(TreeItem<TreeData> node) {
		int i = 0;
		for (TreeItem<TreeData> item : node.getChildren()) {
			// record the original order
			this.originalOrdering.add(new NumberedItem<TreeData>(item, i));
			i++;
		}
		// make sure the data is sorted the same way
		Collections.sort(((ChapterTreeData)node.getValue()).getChapter().getVerses());
		// sort the nodes
		node.getChildren().sort((TreeItem<TreeData> b1, TreeItem<TreeData> b2) -> {
			if (b1 == null) return 1;
			if (b2 == null) return -1;
			return b1.getValue().compareTo(b2.getValue());
		});
	}
}
