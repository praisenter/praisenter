package org.praisenter.ui.bible;

import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;

import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;

final class BibleTreeCell extends TreeCell<Object> {
	private final Label graphic;
	
	public BibleTreeCell() {
		this.graphic = new Label();
		this.graphic.getStyleClass().add("bible-tree-cell-verse-number");
	}
	
	@Override
	protected void updateItem(Object data, boolean empty) {
		super.updateItem(data, empty);
		
		// unbind
		textProperty().unbind();
		this.graphic.textProperty().unbind();
		
		// clear
		setText(null);
		setGraphic(null);
		
		// then setup
		if (!empty && data != null) {
			if (data instanceof Bible) {
				Bible bible = (Bible)data;
				textProperty().bind(bible.nameProperty());
			} else if (data instanceof Book) {
				Book book = (Book)data;
				textProperty().bind(book.nameProperty());
			} else if (data instanceof Chapter) {
				Chapter chapter = (Chapter)data;
				textProperty().bind(chapter.numberProperty().asString("Chapter %d"));
			} else if (data instanceof Verse) {
				Verse verse = (Verse)data;
				textProperty().bind(verse.textProperty());
				this.graphic.textProperty().bind(verse.numberProperty().asString());
				this.setGraphic(this.graphic);
			}
		}
	}
}
