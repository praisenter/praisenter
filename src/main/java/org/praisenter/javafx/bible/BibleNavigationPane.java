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

import java.text.MessageFormat;
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
import org.praisenter.bible.Verse;
import org.praisenter.javafx.AutoCompleteComboBox;
import org.praisenter.javafx.AutoCompleteComparator;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.resources.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * A pane used to navigate bible verses.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleNavigationPane extends BorderPane {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	// actions
	
	/** The find action */
	private static final String FIND = "FIND";
	
	/** The next action */
	private static final String NEXT = "NEXT";
	
	/** The previous action */
	private static final String PREVIOUS = "PREVIOUS";
	
	// nodes
	
	/** The primary bible */
	private final ComboBox<BibleListItem> cmbBiblePrimary;
	
	/** The secondary bible */
	private final ComboBox<BibleListItem> cmbBibleSecondary;
	
	/** The book */
	private final AutoCompleteComboBox<Book> cmbBook;
	
	/** The chapter */
	private final Spinner<Integer> spnChapter;
	
	/** The verse */
	private final Spinner<Integer> spnVerse;
	
	// validation
	
	/** The chapter validation label */
	private final Label lblChapters;
	
	/** The verse validation label */
	private final Label lblVerses;
	
	/** Node used to show a red x when controls are in an invalid state */
	private final Node invalid = FONT_AWESOME.create(FontAwesome.Glyph.CLOSE).color(Color.RED);
	
	// value
	
	/** The value for the pane */
	private final ObjectProperty<BibleReferenceTextStore> value = new SimpleObjectProperty<BibleReferenceTextStore>();
	
	/** The previous verse for the value for the pane */
	private final ObjectProperty<BibleReferenceTextStore> previous = new SimpleObjectProperty<BibleReferenceTextStore>();
	
	/** The next verse for the value for the pane */
	private final ObjectProperty<BibleReferenceTextStore> next = new SimpleObjectProperty<BibleReferenceTextStore>();
	
	/**
	 * Minimal constructor.
	 * @param context the context.
	 */
	public BibleNavigationPane(PraisenterContext context) {
		this.getStyleClass().add("bible-navigation-pane");
		
		this.value.set(new BibleReferenceTextStore());
		this.previous.set(new BibleReferenceTextStore());
		this.next.set(new BibleReferenceTextStore());
		
		this.spnChapter = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		this.spnVerse = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		
		this.spnChapter.setEditable(true);
		this.spnChapter.setMaxWidth(65);
		this.spnVerse.setEditable(true);
		this.spnVerse.setMaxWidth(65);
		
		this.lblChapters = new Label();
		this.lblVerses = new Label();
		
		// filter the list of selectable bibles by whether they are loaded or not
		ObservableBibleLibrary bl = context.getBibleLibrary();
		FilteredList<BibleListItem> filtered = context.getBibleLibrary().getItems().filtered(b -> b.isLoaded());
		SortedList<BibleListItem> bibles = filtered.sorted((a, b) -> {
			if (a == b) return 0;
			if (a == null) return -1;
			if (b == null) return 1;
			return a.compareTo(b);
		});
		
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
		this.cmbBook.setPromptText(Translations.get("bible.book.placeholder"));
		
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
					context.getConfiguration()
						.setUUID(Setting.BIBLE_PRIMARY, nv.getBible().getId())
						.execute(context.getExecutorService());
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
					context.getConfiguration()
						.setUUID(Setting.BIBLE_SECONDARY, nv.getBible().getId())
						.execute(context.getExecutorService());
				}
			} catch (Exception ex) {
				LOGGER.error("An unexpected error occurred when a different secondary bible was selected: " + ov + " -> " + nv, ex);
			}
		});
		
		this.cmbBook.valueProperty().addListener((obs, ov, nv) -> {
			validate();
		});
		
		this.spnChapter.valueProperty().addListener((obs, ov, nv) -> {
			validate();
		});
		
		this.spnVerse.valueProperty().addListener((obs, ov, nv) -> {
			validate();
		});
		
		Button btn = new Button(Translations.get("bible.nav.find"));
		btn.setOnMouseClicked((e) -> {
			LocatedVerseTriplet triplet = getTripletForInput(FIND);
			updateValue(triplet, e.isShortcutDown());
		});
		
		Button next = new Button(Translations.get("bible.nav.next"));
		next.setOnMouseClicked((e) -> {
			LocatedVerseTriplet triplet = getTripletForInput(NEXT);
			updateValue(triplet, e.isShortcutDown());
		});
		
		Button prev = new Button(Translations.get("bible.nav.previous"));
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
		
		HBox bibleRow = new HBox(5, this.cmbBiblePrimary, this.cmbBibleSecondary);
		
		
		GridPane layout = new GridPane();
		layout.setVgap(5);
		layout.setHgap(5);
		
		layout.add(bibleRow, 0, 0, 4, 1);
		
		layout.add(this.cmbBook, 0, 1);
		layout.add(this.spnChapter, 1, 1);
		layout.add(this.spnVerse, 2, 1);
		layout.add(btn, 3, 1);
		
		layout.add(prev, 1, 2);
		layout.add(next, 2, 2);
		layout.add(btnSearch, 3, 2);
		
		layout.add(this.lblChapters, 1, 3);
		layout.add(this.lblVerses, 2, 3);

		this.lblChapters.setAlignment(Pos.BASELINE_CENTER);
		this.lblVerses.setAlignment(Pos.BASELINE_CENTER);
		
		this.lblChapters.setMaxWidth(Double.MAX_VALUE);
		this.lblVerses.setMaxWidth(Double.MAX_VALUE);
		btn.setMaxWidth(200);
		prev.setMaxWidth(Double.MAX_VALUE);
		next.setMaxWidth(Double.MAX_VALUE);
		btnSearch.setMaxWidth(Double.MAX_VALUE);
		
		GridPane.setFillWidth(this.lblChapters, true);
		GridPane.setFillWidth(this.lblVerses, true);
		GridPane.setFillWidth(this.spnChapter, true);
		GridPane.setFillWidth(this.spnVerse, true);
		GridPane.setFillWidth(btn, true);
		GridPane.setFillWidth(prev, true);
		GridPane.setFillWidth(next, true);
		GridPane.setFillWidth(btnSearch, true);
		
		setCenter(layout);
	}
	
	/**
	 * Validates the control values.
	 */
	private void validate() {
		Book book = this.cmbBook.getValue();
		Integer ch = this.spnChapter.getValue();
		Integer vs = this.spnVerse.getValue();
		
		this.lblChapters.setText(null);
		this.lblChapters.setGraphic(null);
		this.lblVerses.setText(null);
		this.lblVerses.setGraphic(null);
		
		if (book != null) {
			this.lblChapters.setText(MessageFormat.format(Translations.get("bible.range"), book.getMaxChapterNumber()));
			if (ch != null) {
				Chapter chapter = book.getChapter(ch.shortValue());
				if (chapter != null) {
					this.lblVerses.setText(MessageFormat.format(Translations.get("bible.range"), chapter.getMaxVerseNumber()));
					if (vs != null) {
						Verse verse = chapter.getVerse(vs.shortValue());
						if (verse == null) {
							// invalid verse
							this.lblVerses.setGraphic(invalid);
						}
					}
				} else {
					// invalid chapter
					this.lblChapters.setGraphic(invalid);
				}
			}
		}
	}
	
	/**
	 * Returns the triplet for the current input controls.
	 * @param type the action type
	 * @return {@link LocatedVerseTriplet}
	 */
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
	
	/**
	 * Updates the current value based on the given triplet.
	 * @param triplet the triplet
	 * @param append if the triplet should be appended or replace
	 */
	private void updateValue(LocatedVerseTriplet triplet, boolean append) {
		if (triplet == null) {
			this.value.get().clear();
			this.previous.get().clear();
			this.next.get().clear();
			return;
		}
		
		// make sure the fields are updated
		this.cmbBook.setValue(triplet.getCurrent().getBook());
		this.spnChapter.getValueFactory().setValue(Integer.valueOf(triplet.getCurrent().getChapter().getNumber()));
		this.spnVerse.getValueFactory().setValue(Integer.valueOf(triplet.getCurrent().getVerse().getNumber()));
		
		BibleReferenceTextStore value = null;
		BibleReferenceTextStore previous = null;
		BibleReferenceTextStore next = null;
		if (!append){
			value = new BibleReferenceTextStore();
			previous = new BibleReferenceTextStore();
			next = new BibleReferenceTextStore();
		} else {
			value = this.value.get().copy();
			previous = this.value.get().copy();
			next = this.value.get().copy();
		}
		
		// update the text stores
		value.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getCurrent()));
		if (triplet.getPrevious() != null) {
			previous.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getPrevious()));
		}
		if (triplet.getNext() != null) {
			next.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getNext()));
		}
		
		// search for the secondary
		BibleListItem secondary = this.cmbBibleSecondary.getValue();
		if (secondary != null) {
			Bible bible2 = secondary.getBible();
			// only show the secondary if a different bible is chosen
			if (bible2 != null && bible2.getId() != triplet.getCurrent().getBible().getId()) {
				LocatedVerseTriplet matchingTriplet = bible2.getMatchingTriplet(triplet);
				if (matchingTriplet != null) {
					value.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getCurrent()));
					if (matchingTriplet.getPrevious() != null) {
						previous.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getPrevious()));
					}
					if (matchingTriplet.getNext() != null) {
						next.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getNext()));
					}
				}
			}
		}
		
		this.value.set(value);
		this.previous.set(previous);
		this.next.set(next);
	}
	
	/**
	 * Converts the given located verse into a reference.
	 * @param verse the located verse
	 * @return {@link BibleReferenceVerse}
	 */
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
	
	/**
	 * Returns the current value of this pane.
	 * @return {@link BibleReferenceTextStore}
	 */
	public BibleReferenceTextStore getValue() {
		return this.value.get();
	}
	
	/**
	 * Sets the current value of this pane.
	 * @param value the new value
	 */
	public void setValue(BibleReferenceTextStore value) {
		this.value.set(value);
	}
	
	/**
	 * The value property.
	 * @return ObjectProperty&lt;{@link BibleReferenceTextStore}&gt;
	 */
	public ObjectProperty<BibleReferenceTextStore> valueProperty() {
		return this.value;
	}
	
	// previous
	
	/**
	 * Returns the previous verse for the current value of this pane.
	 * @return {@link BibleReferenceTextStore}
	 */
	public BibleReferenceTextStore getPrevious() {
		return this.previous.get();
	}
	
	/**
	 * Sets the previous verse for the current value of this pane.
	 * @param value the new value
	 */
	public void setPrevious(BibleReferenceTextStore value) {
		this.previous.set(value);
	}
	
	/**
	 * The previous verse for the value property.
	 * @return ObjectProperty&lt;{@link BibleReferenceTextStore}&gt;
	 */
	public ObjectProperty<BibleReferenceTextStore> previousProperty() {
		return this.previous;
	}
	
	// next
	
	/**
	 * Returns the next verse for the current value of this pane.
	 * @return {@link BibleReferenceTextStore}
	 */
	public BibleReferenceTextStore getNext() {
		return this.next.get();
	}
	
	/**
	 * Sets the next verse for the current value of this pane.
	 * @param value the new value
	 */
	public void setNext(BibleReferenceTextStore value) {
		this.next.set(value);
	}
	
	/**
	 * The next verse for the value property.
	 * @return ObjectProperty&lt;{@link BibleReferenceTextStore}&gt;
	 */
	public ObjectProperty<BibleReferenceTextStore> nextProperty() {
		return this.next;
	}
}
