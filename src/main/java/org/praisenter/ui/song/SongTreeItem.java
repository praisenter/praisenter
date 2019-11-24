package org.praisenter.ui.song;

import org.praisenter.data.song.Author;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.Section;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongBook;
import org.praisenter.ui.MappedList;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

final class SongTreeItem extends TreeItem<Object> {
	private final StringProperty label;
	private ObservableList<TreeItem<Object>> children = null;
	
	public SongTreeItem() {
		this.label = new SimpleStringProperty();
		
		this.valueProperty().addListener((obs, ov, nv) -> {
			this.children = null;
			super.getChildren().clear();
			this.setExpanded(false);
			if (nv != null) {
				if (nv instanceof Song) {
					Song song = (Song)nv;
					this.label.bind(song.nameProperty());
				} else if (nv instanceof Lyrics) {
					Lyrics lyrics = (Lyrics)nv;
					this.label.bind(lyrics.titleProperty());
				} else if (nv instanceof Container) {
					Container container = (Container)nv;
					this.label.bind(container.nameProperty());					
				} else if (nv instanceof Author) {
					Author author = (Author)nv;
					this.label.bind(author.nameProperty());
				} else if (nv instanceof SongBook) {
					SongBook songbook = (SongBook)nv;
					this.label.bind(songbook.nameProperty());
				} else if (nv instanceof Section) {
					Section section = (Section)nv;
					this.label.bind(section.nameProperty());
				}
			} else {
				this.label.unbind();
				this.label.set(null);
				if (this.children != null) {
					Bindings.unbindContent(super.getChildren(), this.children);
				}
				this.children = null;
				super.getChildren().clear();
			}
		});
	}
	
	@Override
	public boolean isLeaf() {
		Object value = this.getValue();
		if (value == null) return true;
		if (value instanceof Section || value instanceof Author || value instanceof SongBook) {
			if (this.children != null) {
				return this.children.size() > 0;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public ObservableList<TreeItem<Object>> getChildren() {
		Object value = this.getValue();
		ObservableList<TreeItem<Object>> children = super.getChildren();
		
		if (this.children == null && value != null) {
			ObservableList<?> data = null;
			if (value instanceof Song) {
				data = ((Song) value).getLyrics();
			} else if (value instanceof Lyrics) {
				Lyrics lyrics = ((Lyrics) value);
				data = FXCollections.observableArrayList(
					new Container(Translations.get("song.lyrics.authors"), Author.class, lyrics.getAuthors()),
					new Container(Translations.get("song.lyrics.songbooks"), SongBook.class, lyrics.getSongBooks()),
					new Container(Translations.get("song.lyrics.sections"), Section.class, lyrics.getSections()));
			} else if (value instanceof Container) {
				data = ((Container) value).getData();
			}
			
			if (data != null) {
				this.children = new MappedList<TreeItem<Object>, Object>(data, (item) -> {
					SongTreeItem bti = new SongTreeItem();
	               	bti.setValue(item);
					return bti;
				});
				
				Bindings.bindContent(children, this.children);
			}
		}
		return children;
	}
	
}
