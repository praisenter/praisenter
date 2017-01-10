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

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.TextVariant;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleReferenceTextStore;
import org.praisenter.bible.BibleReferenceVerse;
import org.praisenter.bible.BibleSearchResult;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.LocatedVerse;
import org.praisenter.bible.LocatedVerseTriplet;
import org.praisenter.javafx.AutoCompleteComboBox;
import org.praisenter.javafx.AutoCompleteComparator;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.configuration.Setting;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public final class BibleNavigationPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private static final String FIND = "FIND";
	private static final String NEXT = "NEXT";
	private static final String PREVIOUS = "PREVIOUS";
	
	private final ComboBox<BibleListItem> cmbBiblePrimary;
	private final ComboBox<BibleListItem> cmbBibleSecondary;
	private final AutoCompleteComboBox<Book> cmbBook;
	private final Spinner<Integer> spnChapter;
	private final Spinner<Integer> spnVerse;
	
	private final Label lblChapters;
	private final Label lblVerses;
	
	private final ObjectProperty<BibleReferenceTextStore> value = new SimpleObjectProperty<BibleReferenceTextStore>();
	
	// TODO features: 
	// validation 
	// toggle to use secondary translation (or just allow a blank option)
	// add (create slide?)
	// send (not sure here...)
	
	public BibleNavigationPane(PraisenterContext context) {
		this.getStyleClass().add("bible-navigation-pane");
		
		this.value.set(new BibleReferenceTextStore());
		
		this.spnChapter = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		this.spnVerse = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		
		this.spnChapter.setEditable(true);
		this.spnChapter.setMaxWidth(75);
		this.spnVerse.setEditable(true);
		this.spnVerse.setMaxWidth(75);
		
		this.lblChapters = new Label();
		this.lblVerses = new Label();
		
		// filter the list of selectable bibles by whether they are loaded or not
		ObservableBibleLibrary bl = context.getBibleLibrary();		
		FilteredList<BibleListItem> bibles = new FilteredList<BibleListItem>(context.getBibleLibrary().getItems());
		bibles.setPredicate(b -> b.isLoaded());
		
		UUID backupBibleId = null;
		if (bibles != null && bibles.size() > 0) {
			backupBibleId = bibles.get(0).getBible().getId();
		}
		
		UUID primaryId = context.getConfiguration().getUUID(Setting.BIBLE_PRIMARY, null);
		if (primaryId == null) {
			primaryId = backupBibleId;
		}
		
		UUID secondaryId = context.getConfiguration().getUUID(Setting.BIBLE_SECONDARY, null);
		if (secondaryId == null) {
			secondaryId = backupBibleId;
		}
		
		BibleListItem primaryBible = null;
		if (primaryId != null) {
			primaryBible = bl.getListItem(primaryId);
			if (primaryBible == null) {
				primaryId = backupBibleId;
				primaryBible = bl.getListItem(backupBibleId);
			}
		}
		
		BibleListItem secondaryBible = null;
		if (secondaryId != null) {
			secondaryBible = bl.getListItem(secondaryId);
			if (secondaryBible == null) {
				secondaryId = backupBibleId;
				secondaryBible = bl.getListItem(backupBibleId);
			}
		}
		
		ObservableList<Book> books = FXCollections.observableArrayList();
		List<Book> bb = primaryBible != null ? primaryBible.getBible().getBooks() : null;
		if (bb != null) {
			books.addAll(bb);
		}
		
		this.cmbBiblePrimary = new ComboBox<BibleListItem>(bibles);
		this.cmbBibleSecondary = new ComboBox<BibleListItem>(bibles);
		this.cmbBook = new AutoCompleteComboBox<Book>(books, new AutoCompleteComparator<Book>() {
			public boolean matches(String typedText, Book objectToCompare) {
				Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
				if (pattern.matcher(objectToCompare.getName()).matches()) {
					return true;
				}
				return false;
			}
		});
		
		if (primaryBible != null) {
			this.cmbBiblePrimary.getSelectionModel().select(primaryBible);
		}
		if (secondaryBible != null) {
			this.cmbBibleSecondary.getSelectionModel().select(secondaryBible);
		}
		
		this.cmbBiblePrimary.valueProperty().addListener((obs, ov, nv) -> {
			try {
				if (nv != null) {
					Book selectedBook = cmbBook.getValue();
					context.getConfiguration().setUUID(Setting.BIBLE_PRIMARY, nv.getBible().getId());
					books.setAll(nv.getBible().getBooks());
					
					Book book = null;
					if (selectedBook != null) {
						for (Book b : nv.getBible().getBooks()) {
							if (b.getNumber() == selectedBook.getNumber()) {
								book = b;
								break;
							}
						}
					}
					cmbBook.setValue(book);
				} else {
					books.clear();
				}
			} catch (Exception ex) {
				LOGGER.error("An unexpected error occurred when a different primary bible was selected: " + ov + " -> " + nv, ex);
			}
		});
		
		this.cmbBibleSecondary.valueProperty().addListener((obs, ov, nv) -> {
			try {
				if (nv != null) {
					context.getConfiguration().setUUID(Setting.BIBLE_SECONDARY, nv.getBible().getId());
				}
			} catch (Exception ex) {
				LOGGER.error("An unexpected error occurred when a different secondary bible was selected: " + ov + " -> " + nv, ex);
			}
		});
		
		this.cmbBook.valueProperty().addListener((obs, ov, nv) -> {
			updateRanges();
		});
		
		this.spnChapter.valueProperty().addListener((obs, ov, nv) -> {
			updateRanges();
		});
		
		Button btn = new Button("Find");
		btn.setOnMouseClicked((e) -> {
			LocatedVerseTriplet triplet = getTripletForInput(FIND);
			updateValue(triplet, e.isShortcutDown());
		});
		
		Button next = new Button("", FONT_AWESOME.create(FontAwesome.Glyph.ARROW_RIGHT));
		next.setOnMouseClicked((e) -> {
			LocatedVerseTriplet triplet = getTripletForInput(NEXT);
			updateValue(triplet, e.isShortcutDown());
		});
		
		Button prev = new Button("", FONT_AWESOME.create(FontAwesome.Glyph.ARROW_LEFT));
		prev.setOnMouseClicked((e) -> {
			LocatedVerseTriplet triplet = getTripletForInput(PREVIOUS);
			updateValue(triplet, e.isShortcutDown());
		});
		
		BibleSearchButton btnSearch = new BibleSearchButton(context);
		
		btnSearch.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				BibleSearchResult result = nv.getResult();
				LocatedVerseTriplet triplet = result.getBible().getTriplet(
						result.getBook().getNumber(), 
						result.getChapter().getNumber(), 
						result.getVerse().getNumber());
				updateValue(triplet, nv.isAppend());
			}
		});
		
		// LAYOUT
		
		GridPane layout = new GridPane();
		layout.setVgap(5);
		layout.setHgap(5);
		
		layout.add(this.cmbBiblePrimary, 0, 0);
		layout.add(this.cmbBook, 1, 0);
		layout.add(this.spnChapter, 2, 0);
		layout.add(this.spnVerse, 3, 0);
		layout.add(btn, 4, 0);
		layout.add(prev, 5, 0);
		layout.add(next, 6, 0);
		layout.add(btnSearch, 7, 0);
		
		layout.add(this.cmbBibleSecondary, 0, 1);
		layout.add(this.lblChapters, 2, 1);
		layout.add(this.lblVerses, 3, 1);

		setCenter(layout);
	}
	
	private void updateRanges() {
		Book book = this.cmbBook.getValue();
		Integer ch = this.spnChapter.getValue();
		if (book != null) {
			this.lblChapters.setText("1-" + book.getMaxChapterNumber());
		} else {
			// no book selected
			this.lblChapters.setText(null);
		}
		if (book != null && ch != null) {
			Chapter chapter = book.getChapter(ch.shortValue());
			if (chapter != null) {
				this.lblVerses.setText("1-" + chapter.getMaxVerseNumber());
			} else {
				// invalid chapter
				this.lblVerses.setText("-");
			}
		} else {
			// no book or chapter selected
			this.lblVerses.setText(null);
		}
	}
	
	private LocatedVerseTriplet getTripletForInput(String type) {
		Bible bible = this.cmbBiblePrimary.getValue().getBible();
		Book book = this.cmbBook.valueProperty().get();

		short bn = book != null ? book.getNumber() : 0;
		short ch = this.spnChapter.getValue().shortValue();
		short v = this.spnVerse.getValue().shortValue();
		
		if (bible != null && book != null) {
			switch(type) {
				case FIND:
					try {
						return bible.getTriplet(bn, ch, v);
					} catch (Exception ex) {
						LOGGER.warn("Failed to get verse: " + book.getName() + " " + ch + ":" + v, ex);
					}
					break;
				case NEXT:
					try {
						return bible.getNextTriplet(bn, ch, v);
					} catch (Exception ex) {
						LOGGER.warn("Failed to get next verse for: " + book.getName() + " " + ch + ":" + v, ex);
					}
					break;
				case PREVIOUS:
					try {
						return bible.getPreviousTriplet(bn, ch, v);
					} catch (Exception ex) {
						LOGGER.warn("Failed to get previous verse for: " + book.getName() + " " + ch + ":" + v, ex);
					}
					break;
				default:
					LOGGER.warn("Unknown operation type: {}", type);
					break;
			}
		}
		
		return null;
	}
	
	private void updateValue(LocatedVerseTriplet triplet, boolean append) {
		if (triplet == null) return;
		
		// make sure the fields are updated
		this.cmbBook.setValue(triplet.getCurrent().getBook());
		this.spnChapter.getValueFactory().setValue(Integer.valueOf(triplet.getCurrent().getChapter().getNumber()));
		this.spnVerse.getValueFactory().setValue(Integer.valueOf(triplet.getCurrent().getVerse().getNumber()));
		
		BibleReferenceTextStore data = null;
		if (!append){
			data = new BibleReferenceTextStore();
		} else {
			data = this.value.get().copy();
		}
		
		// update the text store
		data.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getCurrent()));
		
		// TODO need to somehow show next/previous verse text
		
		// search for the secondary
		BibleListItem secondary = this.cmbBibleSecondary.getValue();
		if (secondary != null) {
			Bible bible2 = secondary.getBible();
			if (bible2 != null) {
				LocatedVerseTriplet matchingTriplet = bible2.getMatchingTriplet(triplet);
				if (matchingTriplet != null) {
					data.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getCurrent()));
				}
			}
		}
		
		this.value.set(data);
	}
	
	private BibleReferenceVerse toReference(LocatedVerse verse) {
		return new BibleReferenceVerse(
				verse.getBible().getId(), 
				verse.getBible().getName(), 
				verse.getBook().getName(), 
				verse.getBook().getNumber(), 
				verse.getChapter().getNumber(), 
				verse.getVerse().getNumber(), 
				verse.getVerse().getText());
	}
	
	public BibleReferenceTextStore getValue() {
		return this.value.get();
	}
	
	public void setValue(BibleReferenceTextStore value) {
		this.value.set(value);
	}
	
	public ObjectProperty<BibleReferenceTextStore> valueProperty() {
		return this.value;
	}
}
