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
import org.praisenter.Constants;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleReference;
import org.praisenter.bible.Book;
import org.praisenter.bible.LocatedVerse;
import org.praisenter.bible.LocatedVerseTriplet;
import org.praisenter.javafx.AutoCompleteComboBox;
import org.praisenter.javafx.AutoCompleteComparator;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.configuration.Setting;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class BibleNavigationPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private ComboBox<BibleListItem> cmbBiblePrimary;
	private ComboBox<BibleListItem> cmbBibleSecondary;
	private AutoCompleteComboBox<Book> cmbBook;
	private Spinner<Integer> spnChapter;
	private Spinner<Integer> spnVerse;
	
	private Label text;
	
	private ListProperty<BibleReference> selected = new SimpleListProperty<BibleReference>();
	
	// JAVABUG 11/03/16 MEDIUM Fixed in Java 9; Editable ComboBox and Spinner auto commit - https://bugs.openjdk.java.net/browse/JDK-8150946
	
	// TODO features: 
	// max chapter/verse number 
	// validation 
	// selection (shift? and ctrl keys)
	// next
	// previous
	// search box (brings up full search window-non-modal)
	// toggle to use secondary translation (or just allow a blank option)
	// template selection
	// add (create slide?)
	// send (not sure here...)
	
	public BibleNavigationPane(PraisenterContext context) {

		this.spnChapter = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		this.spnVerse = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		
		this.spnChapter.setEditable(true);
		this.spnVerse.setEditable(true);
		
		this.text = new Label();
		
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
		
		cmbBook = new AutoCompleteComboBox<Book>(books, new AutoCompleteComparator<Book>() {
			public boolean matches(String typedText, Book objectToCompare) {
				Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
				if (pattern.matcher(objectToCompare.getName()).matches()) {
					return true;
				}
				return false;
			}
		});
		
		cmbBiblePrimary = new ComboBox<BibleListItem>(bibles);
		cmbBiblePrimary.getSelectionModel().select(new BibleListItem(primaryBible));
		cmbBiblePrimary.valueProperty().addListener((obs, ov, nv) -> {
			try {
				if (nv != null) {
					context.getConfiguration().setUUID(Setting.BIBLE_PRIMARY, nv.getBible().getId());
					books.setAll(nv.getBible().getBooks());
					cmbBook.setValue(null);
				} else {
					books.clear();
				}
			} catch (Exception ex) {
				LOGGER.error("An unexpected error occurred when a different primary bible was selected: " + ov + " -> " + nv, ex);
			}
		});
		
		cmbBibleSecondary = new ComboBox<BibleListItem>(bibles);
		cmbBibleSecondary.getSelectionModel().select(new BibleListItem(secondaryBible));
		cmbBibleSecondary.valueProperty().addListener((obs, ov, nv) -> {
			try {
				if (nv != null) {
					context.getConfiguration().setUUID(Setting.BIBLE_SECONDARY, nv.getBible().getId());
				}
			} catch (Exception ex) {
				LOGGER.error("An unexpected error occurred when a different secondary bible was selected: " + ov + " -> " + nv, ex);
			}
		});
		
		Button btn = new Button("show value");
		btn.setOnAction((e) -> {
			Book book = cmbBook.valueProperty().get();
			if (book != null) {
				short ch = spnChapter.getValue().shortValue();
				short v = spnVerse.getValue().shortValue();
				try {
					
					LocatedVerse lv = book.getVerse(ch, v);
					if (lv != null) {
						text.setText(lv.getVerse().getText());
					} else {
						text.setText("Verse not found");
					}
				} catch (Exception ex) {
					LOGGER.warn("Failed to get verse: " + book + " " + ch + ":" + v, ex);
				}
			}
		});
		
		Button next = new Button("next");
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
		
		Button prev = new Button("prev");
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
		
		HBox row = new HBox(5, cmbBiblePrimary, cmbBook, spnChapter, spnVerse, btn, prev, next);
		HBox row2 = new HBox(cmbBibleSecondary);
		
		setTop(new VBox(5, row, row2));
		setCenter(text);
	}
}
