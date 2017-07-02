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
import java.util.List;

import org.praisenter.javafx.bible.BibleTreeData;
import org.praisenter.javafx.bible.BookTreeData;
import org.praisenter.javafx.bible.ChapterTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.bible.VerseTreeData;
import org.praisenter.javafx.command.EditCommand;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Command to re-number books, chapters, and/or verses based on their current index.
 * @author William Bittle
 * @version 3.0.0
 */
public final class RenumberEditCommand implements EditCommand {
	/** The tree */
	private final TreeView<TreeData> tree;
	
	/** The node to start renumbering from */
	private final TreeItem<TreeData> item;
	
	/** The original numbering */
	private final List<NumberedItem<TreeData>> originalNumbering = new ArrayList<NumberedItem<TreeData>>();
	
	/**
	 * Minimal constructor.
	 * @param tree the tree
	 * @param item the item to start renumbering from
	 */
	public RenumberEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item) {
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
		TreeData td = this.item.getValue();
		if (td instanceof BibleTreeData) {
			renumberBible(this.item);
		} else if (td instanceof BookTreeData) {
			renumberBook(this.item);
		} else if (td instanceof ChapterTreeData) {
			renumberChapter(this.item);
		} else if (td instanceof VerseTreeData) {
			renumberChapter(this.item.getParent());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#redo()
	 */
	@Override
	public void redo() {
		this.originalNumbering.clear();
		this.execute();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.EditCommand#undo()
	 */
	@Override
	public void undo() {
		for (NumberedItem<TreeData> renumber : this.originalNumbering) {
			TreeItem<TreeData> item = renumber.getItem();
			short number = (short)renumber.getNumber();
			
			// Data
			TreeData data = item.getValue();
			if (data != null) {
				if (data instanceof BookTreeData) {
					BookTreeData btd = ((BookTreeData)data);
					btd.getBook().setNumber(number);
				} else if (data instanceof ChapterTreeData) {
					ChapterTreeData ctd = ((ChapterTreeData)data);
					ctd.getChapter().setNumber(number);
				} else if (data instanceof VerseTreeData) {
					VerseTreeData vtd = ((VerseTreeData)data);
					vtd.getVerse().setNumber(number);
				}
				data.update();
			}
		}
	}

	/**
	 * Renumbers the books in the given bible.
	 * @param node the bible node
	 */
	private void renumberBible(TreeItem<TreeData> node) {
		short i = 1;
		for (TreeItem<TreeData> item : node.getChildren()) {
			BookTreeData td = (BookTreeData)item.getValue();
			this.originalNumbering.add(new NumberedItem<TreeData>(item, td.getBook().getNumber()));
			// update the data
			td.getBook().setNumber(i++);
			// update the label
			td.update();
			// update children
			renumberBook(item);
		}
	}
	
	/**
	 * Renumbers the chapters in the given book.
	 * @param node the book node.
	 */
	private void renumberBook(TreeItem<TreeData> node) {
		short i = 1;
		for (TreeItem<TreeData> item : node.getChildren()) {
			ChapterTreeData td = (ChapterTreeData)item.getValue();
			this.originalNumbering.add(new NumberedItem<TreeData>(item, td.getChapter().getNumber()));
			// update the data
			td.getChapter().setNumber(i++);
			// update the label
			td.update();
			// update children
			renumberChapter(item);
		}
	}
	
	/**
	 * Renumbers the verses in the given chapter.
	 * @param node the chapter node
	 */
	private void renumberChapter(TreeItem<TreeData> node) {
		short i = 1;
		for (TreeItem<TreeData> item : node.getChildren()) {
			VerseTreeData td = (VerseTreeData)item.getValue();
			this.originalNumbering.add(new NumberedItem<TreeData>(item, td.getVerse().getNumber()));
			// update the data
			td.getVerse().setNumber(i++);
			// update the label
			td.update();
		}
	}
}
