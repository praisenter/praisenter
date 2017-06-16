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

public final class RenumberEditCommand implements EditCommand {
	private final TreeView<TreeData> tree;
	private final TreeItem<TreeData> item;
	
	private class Renumber {
		TreeItem<TreeData> item;
		short oldNumber;
		public Renumber(TreeItem<TreeData> item, short oldNumber) {
			this.item = item;
			this.oldNumber = oldNumber;
		}
	}
	
	private final List<Renumber> oldNumbers = new ArrayList<Renumber>();
	
	public RenumberEditCommand(TreeView<TreeData> tree, TreeItem<TreeData> item) {
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
		
		this.tree.getSelectionModel().clearSelection();
		this.tree.requestFocus();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.EditCommand#redo()
	 */
	@Override
	public void redo() {
		this.oldNumbers.clear();
		this.execute();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.EditCommand#undo()
	 */
	@Override
	public void undo() {
		for (Renumber renumber : this.oldNumbers) {
			// Data
			TreeData data = renumber.item.getValue();
			if (data != null) {
				if (data instanceof BookTreeData) {
					BookTreeData btd = ((BookTreeData)data);
					btd.getBook().setNumber(renumber.oldNumber);
				} else if (data instanceof ChapterTreeData) {
					ChapterTreeData ctd = ((ChapterTreeData)data);
					ctd.getChapter().setNumber(renumber.oldNumber);
				} else if (data instanceof VerseTreeData) {
					VerseTreeData vtd = ((VerseTreeData)data);
					vtd.getVerse().setNumber(renumber.oldNumber);
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
			this.oldNumbers.add(new Renumber(item, td.getBook().getNumber()));
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
			this.oldNumbers.add(new Renumber(item, td.getChapter().getNumber()));
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
			this.oldNumbers.add(new Renumber(item, td.getVerse().getNumber()));
			// update the data
			td.getVerse().setNumber(i++);
			// update the label
			td.update();
		}
	}
}
