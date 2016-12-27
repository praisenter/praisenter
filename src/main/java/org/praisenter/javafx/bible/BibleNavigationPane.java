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
import org.praisenter.Constants;
import org.praisenter.SearchType;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleReferenceSet;
import org.praisenter.bible.BibleReferenceTextStore;
import org.praisenter.bible.BibleSearchResult;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.LocatedVerse;
import org.praisenter.bible.LocatedVerseTriplet;
import org.praisenter.javafx.AutoCompleteComboBox;
import org.praisenter.javafx.AutoCompleteComparator;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.configuration.Setting;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public final class BibleNavigationPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private ComboBox<BibleListItem> cmbBiblePrimary;
	private ComboBox<BibleListItem> cmbBibleSecondary;
	private AutoCompleteComboBox<Book> cmbBook;
	private Spinner<Integer> spnChapter;
	private Spinner<Integer> spnVerse;
	private TextField txtSearch;
	
	private Label text;
	
	private Label lblChapters;
	private Label lblVerses;
	
	private BibleReferenceTextStore selected = new BibleReferenceTextStore();
	
	// TODO features: 
	// validation 
	// selection (shift? and ctrl keys)
	// search box (brings up full search window-non-modal)
	// toggle to use secondary translation (or just allow a blank option)
	// template selection
	// add (create slide?)
	// send (not sure here...)
	
	public BibleNavigationPane(PraisenterContext context) {
		this.getStyleClass().add("bible-navigation-pane");
		
		this.spnChapter = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		this.spnVerse = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		
		this.spnChapter.setEditable(true);
		this.spnChapter.setMaxWidth(75);
		this.spnVerse.setEditable(true);
		this.spnVerse.setMaxWidth(75);
		
		this.text = new Label();
		this.txtSearch = new TextField();
		this.txtSearch.setPromptText("search");
		
		lblChapters = new Label();
		lblVerses = new Label();
		
		// filter the list of selectable bibles by whether they are loaded or not
		ObservableBibleLibrary bl = context.getBibleLibrary();		
		FilteredList<BibleListItem> bibles = new FilteredList<BibleListItem>(context.getBibleLibrary().getItems());
		bibles.setPredicate(b -> b.isLoaded());
		
		UUID backupBible = null;
		if (bibles != null && bibles.size() > 0) {
			backupBible = bibles.get(0).getBible().getId();
		}
		
		UUID primaryId = context.getConfiguration().getUUID(Setting.BIBLE_PRIMARY, null);
		if (primaryId == null) {
			primaryId = backupBible;
		}
		
		UUID secondaryId = context.getConfiguration().getUUID(Setting.BIBLE_SECONDARY, null);
		if (secondaryId == null) {
			secondaryId = backupBible;
		}
		
		Bible primaryBible = null;
		if (primaryId != null) {
			primaryBible = bl.get(primaryId);
			if (primaryBible == null) {
				primaryId = backupBible;
				primaryBible = bl.get(backupBible);
			}
		}
		
		Bible secondaryBible = null;
		if (secondaryId != null) {
			secondaryBible = bl.get(secondaryId);
			if (secondaryBible == null) {
				secondaryId = backupBible;
				secondaryBible = bl.get(backupBible);
			}
		}
		
		ObservableList<Book> books = FXCollections.observableArrayList();
		List<Book> bb = primaryBible != null ? primaryBible.getBooks() : null;
		if (bb != null) {
			books.addAll(bb);
		}
		
		cmbBiblePrimary = new ComboBox<BibleListItem>(bibles);
		if (primaryBible != null) {
			cmbBiblePrimary.getSelectionModel().select(new BibleListItem(primaryBible));
		}
		cmbBiblePrimary.valueProperty().addListener((obs, ov, nv) -> {
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
		
		cmbBibleSecondary = new ComboBox<BibleListItem>(bibles);
		if (secondaryBible != null) {
			cmbBibleSecondary.getSelectionModel().select(new BibleListItem(secondaryBible));
		}
		cmbBibleSecondary.valueProperty().addListener((obs, ov, nv) -> {
			try {
				if (nv != null) {
					context.getConfiguration().setUUID(Setting.BIBLE_SECONDARY, nv.getBible().getId());
				}
			} catch (Exception ex) {
				LOGGER.error("An unexpected error occurred when a different secondary bible was selected: " + ov + " -> " + nv, ex);
			}
		});
		
		cmbBook = new AutoCompleteComboBox<Book>(books, new AutoCompleteComparator<Book>() {
			public boolean matches(String typedText, Book objectToCompare) {
				Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
				if (pattern.matcher(objectToCompare.getName()).matches()) {
					return true;
				}
				return false;
			}
		});
		cmbBook.valueProperty().addListener((obs, ov, nv) -> {
			updateRanges();
		});
		
		spnChapter.valueProperty().addListener((obs, ov, nv) -> {
			updateRanges();
		});
		
		Button btn = new Button("Find");
		btn.setOnAction((e) -> {
			Bible bible = cmbBiblePrimary.getValue().getBible();
			Bible b2 = cmbBibleSecondary.getValue().getBible();
			Book book = cmbBook.valueProperty().get();
			if (book != null) {
				short bn = book.getNumber();
				short ch = spnChapter.getValue().shortValue();
				short v = spnVerse.getValue().shortValue();
				StringBuilder sb = new StringBuilder();
				try {
					LocatedVerse lv = bible.getVerse(bn, ch, v);
					if (lv != null) {
						sb.append(lv.getVerse().getText());
					} else {
						sb.append("Verse not found in " + bible.getName());
					}
					
					if (b2 != null) {
						LocatedVerse tv = b2.getVerse(bn, ch, v);
						if (tv != null) {
							sb.append(tv.getVerse().getText());
						}
					}
					
					text.setText(sb.toString());
				} catch (Exception ex) {
					LOGGER.warn("Failed to get verse: " + book + " " + ch + ":" + v, ex);
				}
			}
		});
		
		Button next = new Button("", FONT_AWESOME.create(FontAwesome.Glyph.ARROW_RIGHT));
		next.setOnAction((e) -> {
			Bible bbl = cmbBiblePrimary.valueProperty().get().getBible();
			Book book = cmbBook.valueProperty().get();
			if (book != null) {
				short ch = spnChapter.getValue().shortValue();
				short v = spnVerse.getValue().shortValue();
				try {
					LocatedVerseTriplet lv = bbl.getNextTriplet(book.getNumber(), ch, v);
					if (lv != null) {
						cmbBook.setValue(lv.getCurrent().getBook());
						spnChapter.getValueFactory().setValue(Integer.valueOf(lv.getCurrent().getChapter().getNumber()));
						spnVerse.getValueFactory().setValue(Integer.valueOf(lv.getCurrent().getVerse().getNumber()));
						text.setText(
								lv.getPrevious().getVerse().getText() + Constants.NEW_LINE +
								lv.getCurrent().getVerse().getText() + Constants.NEW_LINE +
								(lv.getNext() != null ? lv.getNext().getVerse().getText() : ""));
					}
				} catch (Exception ex) {
					LOGGER.warn("Failed to get next verse for: " + book + " " + ch + ":" + v, ex);
				}
			}
		});
		
		Button prev = new Button("", FONT_AWESOME.create(FontAwesome.Glyph.ARROW_LEFT));
		prev.setOnAction((e) -> {
			
			Bible bbl = cmbBiblePrimary.valueProperty().get().getBible();
			Book book = cmbBook.valueProperty().get();
			if (book != null) {
				short ch = spnChapter.getValue().shortValue();
				short v = spnVerse.getValue().shortValue();
				try {
					LocatedVerseTriplet lv = bbl.getPreviousTriplet(book.getNumber(), ch, v);
					if (lv != null) {
						cmbBook.setValue(lv.getCurrent().getBook());
						spnChapter.getValueFactory().setValue(Integer.valueOf(lv.getCurrent().getChapter().getNumber()));
						spnVerse.getValueFactory().setValue(Integer.valueOf(lv.getCurrent().getVerse().getNumber()));
						text.setText(
								(lv.getPrevious() != null ? lv.getPrevious().getVerse().getText() + Constants.NEW_LINE : "") +
								lv.getCurrent().getVerse().getText() + Constants.NEW_LINE +
								lv.getNext().getVerse().getText());
					}
				} catch (Exception ex) {
					LOGGER.warn("Failed to get previous verse for: " + book + " " + ch + ":" + v, ex);
				}
			}
		});
		
		txtSearch.setOnAction(e -> {
			// FIXME this should instead send the current text to a new non-modal window with the results in a grid
			try {
				bl.search("In the beginning", SearchType.PHRASE, results -> {
					StringBuilder sb = new StringBuilder();
					for (BibleSearchResult result : results) {
						sb.append(result.getBible().getName())
						  .append(" ")
						  .append(result.getBook().getName())
						  .append(" ")
						  .append(result.getChapter().getNumber())
						  .append(":")
						  .append(result.getVerse().getNumber())
						  .append(" ")
						  .append(result.getVerse().getText())
						  .append(Constants.NEW_LINE);
					}
					text.setText(sb.toString());
				}, null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		// LAYOUT
		
		GridPane layout = new GridPane();
		layout.setVgap(5);
		layout.setHgap(5);
		
		layout.add(cmbBiblePrimary, 0, 0);
		layout.add(cmbBook, 1, 0);
		layout.add(spnChapter, 2, 0);
		layout.add(spnVerse, 3, 0);
		layout.add(btn, 4, 0);
		layout.add(prev, 5, 0);
		layout.add(next, 6, 0);
		
		layout.add(cmbBibleSecondary, 0, 1);
		layout.add(lblChapters, 2, 1);
		layout.add(lblVerses, 3, 1);

		setCenter(layout);
		
		setBottom(text);
	}
	
	private void updateRanges() {
		Book book = this.cmbBook.getValue();
		Integer ch = this.spnChapter.getValue();
		if (book != null) {
			lblChapters.setText("1-" + book.getMaxChapterNumber());
		} else {
			// no book selected
			lblChapters.setText(null);
		}
		if (book != null && ch != null) {
			Chapter chapter = book.getChapter(ch.shortValue());
			if (chapter != null) {
				lblVerses.setText("1-" + chapter.getMaxVerseNumber());
			} else {
				// invalid chapter
				lblVerses.setText("N/A");
			}
		} else {
			// no book or chapter selected
			lblVerses.setText(null);
		}
	}
}
