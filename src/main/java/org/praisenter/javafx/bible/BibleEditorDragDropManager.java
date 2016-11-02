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
package org.praisenter.javafx.bible;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.Constants;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

// JAVABUG Dragging to the edge of a scrollable window doesn't scroll it and there's no good way to scroll it manually

/**
 * Class used to managed the drag and drop features of the {@link BibleEditorPane}.
 * @author William Bittle
 * @version 3.0.0
 */
final class BibleEditorDragDropManager {
	/** The class when dragged over a parent node */
	private static final PseudoClass DRAG_OVER_PARENT = PseudoClass.getPseudoClass("drag-over-parent");
	
	/** The class when dragged over the top half of a sibling node */
	private static final PseudoClass DRAG_OVER_SIBLING_TOP = PseudoClass.getPseudoClass("drag-over-sibling-top");
	
	/** The class when dragged over the bottom half of a sibling node */
	private static final PseudoClass DRAG_OVER_SIBLING_BOTTOM = PseudoClass.getPseudoClass("drag-over-sibling-bottom");
	
	// state
	
	/** The list of select nodes */
	private final List<TreeItem<TreeData>> selected;
	
	/** The type of the selected nodes */
	private Class<?> selectedType;
	
	/**
	 * Default constructor.
	 */
	public BibleEditorDragDropManager() {
		this.selected = new ArrayList<TreeItem<TreeData>>();
	}
	
	/**
	 * Called when a drag event is detected on a TreeCell.
	 * @param cell the cell that was dragged
	 * @param e the mouse event triggering the drag
	 */
	public void dragDetected(BibleTreeCell cell, MouseEvent e) {
		if (!cell.isEmpty()) {
			TreeView<TreeData> view = cell.getTreeView();
			
			// how much is selected?
			this.selected.addAll(view.getSelectionModel().getSelectedItems());
			int count = this.selected.size();
			
			// are all the selections the same type?
			boolean sameType = true;
			if (count > 0) {
				TreeItem<TreeData> first = this.selected.get(0);
				if (first != null) {
					this.selectedType = first.getValue().getClass();
					for (TreeItem<TreeData> item : this.selected) {
						if (!item.getValue().getClass().equals(this.selectedType)) {
							sameType = false;
							break;
						}
					}
				}
			}
			
			// must be the same type and must have at least one selected
			if (sameType && count > 0) {
				StringBuilder text = new StringBuilder();
				for (TreeItem<TreeData> item : this.selected) {
					TreeData td = item.getValue();
					if (td instanceof BookTreeData) {
						BookTreeData btd = (BookTreeData)td;
						Book book = btd.book;
						text.append(book.getName()).append(Constants.NEW_LINE);
					} else if (td instanceof ChapterTreeData) {
						ChapterTreeData ctd = (ChapterTreeData)td;
						Chapter chapter = ctd.chapter;
						text.append(chapter.getNumber()).append(Constants.NEW_LINE);
					} else if (td instanceof VerseTreeData) {
						VerseTreeData vtd = (VerseTreeData)td;
						Verse verse = vtd.verse;
						text.append(verse.getText()).append(Constants.NEW_LINE);
					}
				}
				
				Dragboard db = cell.startDragAndDrop(TransferMode.COPY_OR_MOVE);
				ClipboardContent cc = new ClipboardContent();
				// we have to put something in there to make sure the d&d works
				cc.put(DataFormat.PLAIN_TEXT, text.toString().trim());
				db.setContent(cc);
				
				Label label = new Label(cell.getText());
		        new Scene(label);
		        db.setDragView(label.snapshot(null, null));
			}
		}
	}
	
	/**
	 * Called when the mouse leaves a node during a drag.
	 * @param cell the cell that was exited
	 * @param e the drag event
	 */
	public void dragExited(BibleTreeCell cell, DragEvent e) {
		cell.pseudoClassStateChanged(DRAG_OVER_PARENT, false);
		cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, false);
		cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, false);
	}
	
	/**
	 * Called when the mouse enters a node during a drag.
	 * @param cell the cell that was entered
	 * @param e the drag event
	 */
	public void dragEntered(BibleTreeCell cell, DragEvent e) {
		// nothing to do here
	}
	
	/**
	 * Called when the is over another cell during a drag.
	 * @param cell the cell that the drag is over
	 * @param e the drag event
	 */
	public void dragOver(BibleTreeCell cell, DragEvent e) {
		if (!cell.isEmpty()) {
			TreeItem<TreeData> item = cell.getTreeItem();
			TreeData data = cell.getItem();
			boolean allowed = false;
			
			if (!this.selected.contains(item)) {
				boolean parent = false;
				if (data instanceof ChapterTreeData && VerseTreeData.class.equals(this.selectedType)) {
					allowed = true;
					parent = true;
				} else if (data instanceof VerseTreeData && VerseTreeData.class.equals(this.selectedType)) {
					allowed = true;
				} else if (data instanceof BookTreeData && ChapterTreeData.class.equals(this.selectedType)) {
					allowed = true;
					parent = true;
				} else if (data instanceof ChapterTreeData && ChapterTreeData.class.equals(this.selectedType)) {
					allowed = true;
				} else if (data instanceof BibleTreeData && BookTreeData.class.equals(this.selectedType)) { 
					allowed = true;
					parent = true;
				} else if (data instanceof BookTreeData && BookTreeData.class.equals(this.selectedType)) {
					allowed = true;
				}
				
				if (allowed) {
					e.acceptTransferModes(TransferMode.MOVE);
					
					if (parent) {
//						cell.setBackground(DRAG_BACKGROUND);
						cell.pseudoClassStateChanged(DRAG_OVER_PARENT, true);
					} else {
						if (e.getY() < cell.getHeight() * 0.75) {
//							cell.setBorder(DRAG_TOP);
							cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, true);
							cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, false);
						} else {
//							cell.setBorder(DRAG_BOTTOM);
							cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, true);
							cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, false);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Called when the drag is dropped.
	 * @param cell the cell that had the drag dropped on
	 * @param e the drag event
	 */
	public void dragDropped(BibleTreeCell cell, DragEvent e) {
		TreeItem<TreeData> toNode = cell.getTreeItem();
		
		// make sure we don't paste onto ourself
		if (this.selected.contains(toNode)) {
			return;
		}
		
		TreeData data = toNode.getValue();
		boolean after = e.getY() >= cell.getHeight() * 0.75;
		
		// remove all the nodes first so that the indexes don't get jacked
		for (TreeItem<TreeData> item : this.selected) {
			// remove it from the tree
			item.getParent().getChildren().remove(item);
			// remove it from the bible
			TreeData td = item.getValue();
			if (td instanceof VerseTreeData) {
				VerseTreeData vtd = (VerseTreeData)td;
				vtd.chapter.getVerses().remove(vtd.verse);
			} else if (td instanceof ChapterTreeData) {
				ChapterTreeData ctd = (ChapterTreeData)td;
				ctd.book.getChapters().remove(ctd.chapter);
			} else if (td instanceof BookTreeData) {
				BookTreeData btd = (BookTreeData)td;
				btd.bible.getBooks().remove(btd.book);
			}
		}
		
		// compute the index of the target node
		int index = toNode.getParent().getChildren().indexOf(toNode);
		if (after) {
			index++;
		}
		
		// if we drag it to the parent type, then always add the nodes/data to the end
		if (data instanceof ChapterTreeData && VerseTreeData.class.equals(this.selectedType) ||
			data instanceof BookTreeData && ChapterTreeData.class.equals(this.selectedType) ||
			data instanceof BibleTreeData && BookTreeData.class.equals(this.selectedType)) {
			index = -1;
		} else {
			// if we drag it to the same type, then get the parent
			toNode = toNode.getParent();
		}
		
		for (TreeItem<TreeData> item : this.selected) {
			// add the node at the target
			if (index < 0) {
				toNode.getChildren().add(item);
			} else {
				toNode.getChildren().add(index, item);
			}
			
			// add the data
			if (VerseTreeData.class.equals(this.selectedType)) {
				// moving verses onto a chapter
				ChapterTreeData ctd = (ChapterTreeData)toNode.getValue();
				Verse verse = ((VerseTreeData)item.getValue()).verse;
				if (index < 0) {
					ctd.chapter.getVerses().add(verse);
				} else {
					ctd.chapter.getVerses().add(index, verse);
				}
			} else if (ChapterTreeData.class.equals(this.selectedType)) {
				// moving chapters onto a book
				BookTreeData btd = (BookTreeData)toNode.getValue();
				Chapter chapter = ((ChapterTreeData)item.getValue()).chapter;
				if (index < 0) {
					btd.book.getChapters().add(chapter);
				} else {
					btd.book.getChapters().add(index, chapter);
				}
			} else if (BookTreeData.class.equals(this.selectedType)) {
				// moving books onto a bible
				BibleTreeData btd = (BibleTreeData)toNode.getValue();
				Book book = ((BookTreeData)item.getValue()).book;
				if (index < 0) {
					btd.bible.getBooks().add(book);
				} else {
					btd.bible.getBooks().add(index, book);
				}
			}
			
			if (index >= 0) {
				index++;
			}
		}
		
		e.setDropCompleted(true);
	}
	
	/**
	 * Called when the drag event finishes.
	 * @param cell the cell that the drag was initiated on
	 * @param e the drag event
	 */
	public void dragDone(BibleTreeCell cell, DragEvent e) {
		// regardless if the transfer was successful
		// clear the local state
		this.selected.clear();
		this.selectedType = null;
		
		// this indicates the transfer was successful
		if (e.getTransferMode() != null) {
			cell.getTreeView().getSelectionModel().clearSelection();
		}
	}
}
