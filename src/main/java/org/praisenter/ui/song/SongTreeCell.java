package org.praisenter.ui.song;

import org.controlsfx.glyphfont.Glyph;
import org.praisenter.data.song.Author;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.Section;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongBook;
import org.praisenter.ui.Glyphs;
import org.praisenter.ui.Icons;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;

final class SongTreeCell extends TreeCell<Object> {
	private final Node lyrics;
	private final Node songbook;
	private final Node author;
	private final Label graphic;
	
	public SongTreeCell() {
		this.songbook = Icons.getIcon(Icons.BOOK);// Glyphs.NEW_SONGBOOK.duplicate();
		this.lyrics = Icons.getIcon(Icons.MUSIC);//Glyphs.NEW_LYRICS.duplicate();
		this.author = Icons.getIcon(Icons.USER);//Glyphs.NEW_AUTHOR.duplicate();
		
		this.songbook.getStyleClass().addAll("p-songbook-icon");
		this.lyrics.getStyleClass().addAll("p-lyrics-icon");
		this.author.getStyleClass().addAll("p-author-icon");
		
		this.graphic = new Label();
		this.graphic.getStyleClass().addAll("p-section-name");
	}
	
	@Override
	protected void updateItem(Object data, boolean empty) {
		super.updateItem(data, empty);
		
		// unbind
		this.textProperty().unbind();
		this.graphic.textProperty().unbind();
		
		// clear
		this.setText(null);
		this.setGraphic(null);
		
		// then setup
		if (!empty && data != null) {
			if (data instanceof Song) {
				Song song = (Song)data;
				textProperty().bind(song.nameProperty());
			} else if (data instanceof Lyrics) {
				Lyrics lyrics = (Lyrics)data;
				textProperty().bind(lyrics.titleProperty());
				this.setGraphic(this.lyrics);
			} else if (data instanceof Container) {
				Container container = (Container)data;
				textProperty().bind(container.nameProperty());
			} else if (data instanceof Author) {
				Author author = (Author)data;
				textProperty().bind(author.nameProperty());
				this.setGraphic(this.author);
			} else if (data instanceof SongBook) {
				SongBook songbook = (SongBook)data;
				textProperty().bind(songbook.nameProperty());
				this.setGraphic(this.songbook);
			} else if (data instanceof Section) {
				Section section = (Section)data;
				this.textProperty().bind(Bindings.createStringBinding(() -> {
					return section.getText().replaceAll("\\r?\\n", " ");
				}, section.textProperty()));
				this.graphic.textProperty().bind(section.nameProperty());
				this.setGraphic(this.graphic);
			}
		}
	}
}
