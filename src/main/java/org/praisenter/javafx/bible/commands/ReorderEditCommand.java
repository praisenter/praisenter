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

public final class ReorderEditCommand implements EditCommand {
	private final TreeView<TreeData> tree;
	private final TreeItem<TreeData> item;
	
	private class Reorder {
		TreeItem<TreeData> item;
		int oldIndex;
		public Reorder(TreeItem<TreeData> item, int oldIndex) {
			this.item = item;
			this.oldIndex = oldIndex;
		}
	}
	
	private final List<Reorder> oldOrder = new ArrayList<>();
	
	public ReorderEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item) {
		this.tree = tree;
		this.item = item;
	}
	

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.commands.AbstractSingleValueTextEditCommand#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.item != null && this.tree != null;
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.EditCommand#execute()
	 */
	@Override
	public void execute() {
		// remove the data
		TreeData td = item.getValue();
		if (td instanceof BibleTreeData) {
			reorderBooks(item);
		} else if (td instanceof BookTreeData) {
			reorderChapters(item);
		} else if (td instanceof ChapterTreeData) {
			reorderVerses(item);
		} else if (td instanceof VerseTreeData) {
			reorderVerses(item.getParent());
		}
		
		this.tree.getSelectionModel().clearSelection();
		this.tree.requestFocus();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.EditCommand#redo()
	 */
	@Override
	public void redo() {
		this.oldOrder.clear();
		this.execute();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.EditCommand#undo()
	 */
	@Override
	public void undo() {
		for (Reorder reorder : this.oldOrder) {
			// UI
			TreeItem<TreeData> node = reorder.item.getParent();
			if (node != null) {
				node.getChildren().remove(reorder.item);
				node.getChildren().add(reorder.oldIndex, reorder.item);
			}
			
			// Data
			TreeData data = reorder.item.getValue();
			if (data != null) {
				if (data instanceof BookTreeData) {
					// reorder the book
					BookTreeData btd = ((BookTreeData)data);
					Bible bible = btd.getBible();
					Book book = btd.getBook();
					bible.getBooks().remove(book);
					bible.getBooks().add(reorder.oldIndex, book);
				} else if (data instanceof ChapterTreeData) {
					// reorder the chapter
					ChapterTreeData ctd = ((ChapterTreeData)data);
					Book book = ctd.getBook();
					Chapter chapter = ctd.getChapter();
					book.getChapters().remove(chapter);
					book.getChapters().add(reorder.oldIndex, chapter);
				} else if (data instanceof VerseTreeData) {
					// reorder the verse
					VerseTreeData vtd = ((VerseTreeData)data);
					Chapter chapter = vtd.getChapter();
					Verse verse = vtd.getVerse();
					chapter.getVerses().remove(verse);
					chapter.getVerses().add(reorder.oldIndex, verse);
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
			this.oldOrder.add(new Reorder(item, i));
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
			this.oldOrder.add(new Reorder(item, i));
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
			this.oldOrder.add(new Reorder(item, i));
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
