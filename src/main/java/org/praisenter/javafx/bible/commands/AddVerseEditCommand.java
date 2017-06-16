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

import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.bible.ChapterTreeData;
import org.praisenter.javafx.bible.TreeData;
import org.praisenter.javafx.bible.VerseTreeData;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.resources.translations.Translations;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class AddVerseEditCommand implements EditCommand {
	private final TreeView<TreeData> tree;
	private final TreeItem<TreeData> item;
	
	private final TreeItem<TreeData> newItem;
	private final int index;
	private final Chapter chapter;
	private final Verse verse;
	
	public AddVerseEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item) {
		this(tree, item, null, -1);
	}
	
	public AddVerseEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item, Verse verse) {
		this(tree, item, verse, -1);
	}
	
	public AddVerseEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item, Verse verse, int index) {
		this.tree = tree;
		
		TreeItem<TreeData> newItem = null;
		Chapter chapter = null;
		
		if (item != null) {
			TreeData td = item.getValue();
			if (td != null) {
				if (td instanceof VerseTreeData) {
					VerseTreeData vd = (VerseTreeData)item.getValue();
					short number = (short)(vd.getVerse().getNumber() + 1);
					if (verse == null) {
						verse = new Verse(number, Translations.get("bible.edit.verse.default"));
					}
					chapter = vd.getChapter();
					// was index given?
					if (index < 0) {
						// if not, then use this item's index + 1
						index = item.getParent().getChildren().indexOf(item) + 1;
					}
					newItem = new TreeItem<TreeData>(new VerseTreeData(vd.getBible(), vd.getBook(), vd.getChapter(), verse));
					item = item.getParent();
				} else if (td instanceof ChapterTreeData) {
					ChapterTreeData cd = (ChapterTreeData)item.getValue();
					short number = cd.getChapter().getMaxVerseNumber();
					if (verse == null) {
						verse = new Verse(++number, Translations.get("bible.edit.verse.default"));
					}
					chapter = cd.getChapter();
					newItem = new TreeItem<TreeData>(new VerseTreeData(cd.getBible(), cd.getBook(), cd.getChapter(), verse));
				}
			}
		}
		
		this.item = item;
		this.verse = verse;
		this.chapter = chapter;
		this.index = index;
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
		return this.tree != null && this.item != null && this.newItem != null && this.verse != null && this.chapter != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#execute()
	 */
	@Override
	public void execute() {
		if (this.index < 0) {
			this.chapter.getVerses().add(this.verse);
			this.item.getChildren().add(this.newItem);
		} else {
			this.chapter.getVerses().add(this.index, this.verse);
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
		this.chapter.getVerses().remove(this.verse);
		this.item.getChildren().remove(this.newItem);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.EditCommand#redo()
	 */
	@Override
	public void redo() {
		if (this.index < 0) {
			this.chapter.getVerses().add(this.verse);
			this.item.getChildren().add(this.newItem);
		} else {
			this.chapter.getVerses().add(this.index, this.verse);
			this.item.getChildren().add(this.index, this.newItem);
		}
	}
}
