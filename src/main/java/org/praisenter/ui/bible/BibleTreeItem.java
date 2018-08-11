package org.praisenter.ui.bible;

import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.MappedList;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

final class BibleTreeItem extends TreeItem<Object> {
	
	private final StringProperty label;
	private ObservableList<TreeItem<Object>> children = null;
	
	public BibleTreeItem() {
		this.label = new SimpleStringProperty();
		
		this.valueProperty().addListener((obs, ov, nv) -> {
			this.children = null;
			super.getChildren().clear();
			if (nv != null) {
				if (nv instanceof Bible) {
					Bible bible = (Bible)nv;
					this.label.bind(bible.nameProperty());
				} else if (nv instanceof Book) {
					Book book = (Book)nv;
					this.label.bind(book.nameProperty());
				} else if (nv instanceof Chapter) {
					Chapter chapter = (Chapter)nv;
					this.label.bind(chapter.numberProperty().asString());
				} else if (nv instanceof Verse) {
					Verse verse = (Verse)nv;
					this.label.bind(verse.textProperty());
				}
			} else {
				this.label.unbind();
				this.label.set(null);
				this.setExpanded(false);
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
		if (value instanceof Verse) {
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
			if (value instanceof Bible) {
				data = ((Bible) value).getBooks();
			} else if (value instanceof Book) {
				data = ((Book) value).getChapters();
			} else if (value instanceof Chapter) {
				data = ((Chapter) value).getVerses();
			}
			
			if (data != null) {
				this.children = new MappedList<TreeItem<Object>, Object>(data, (index, item) -> {
					BibleTreeItem bti = new BibleTreeItem();
	               	bti.setValue(item);
					return bti;
				});
				
				Bindings.bindContent(children, this.children);
			}
		}
		return children;
	}
	
}
