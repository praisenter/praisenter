package org.praisenter.ui.bible;

import org.controlsfx.glyphfont.Glyph;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.Glyphs;
import org.praisenter.ui.Icons;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;

final class BibleTreeCell extends TreeCell<Object> {
	private final Node book;
	private final Node chapter;
	private final Label graphic;
	
	public BibleTreeCell() {
		this.book = Icons.getIcon(Icons.BOOK);
		this.chapter = Icons.getIcon(Icons.BOOKMARK);
//		this.book = Glyphs.NEW_BOOK.duplicate();
//		this.chapter = Glyphs.NEW_CHAPTER.duplicate();
		this.graphic = new Label();
		
//		this.book.getStyleClass().addAll("p-book-icon");
//		this.chapter.getStyleClass().addAll("p-chapter-icon");
		this.graphic.getStyleClass().addAll("p-verse-number");
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
				this.setGraphic(this.book);
			} else if (data instanceof Chapter) {
				Chapter chapter = (Chapter)data;
				textProperty().bind(Bindings.createStringBinding(() -> {
					return Translations.get("bible.chapter.name", chapter.getNumber());
				}, chapter.numberProperty()));
				this.setGraphic(this.chapter);
			} else if (data instanceof Verse) {
				Verse verse = (Verse)data;
				textProperty().bind(verse.textProperty());
				this.graphic.textProperty().bind(verse.numberProperty().asString());
				this.setGraphic(this.graphic);
			}
		}
	}
}
