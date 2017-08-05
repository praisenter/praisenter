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

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.SearchType;
import org.praisenter.bible.BibleSearchCriteria;
import org.praisenter.bible.BibleSearchResult;
import org.praisenter.bible.Book;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.javafx.controls.AutoCompleteComboBox;
import org.praisenter.javafx.controls.AutoCompleteComparator;
import org.praisenter.javafx.controls.ProgressOverlay;
import org.praisenter.resources.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

// FEATURE (M) add searching to the bible editor for finding and editing easily

/**
 * A pane for searching for bible verses.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleSearchPane extends BorderPane {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The format for the seach score */
	private static final DecimalFormat SCORE_FORMAT = new DecimalFormat(Translations.get("search.score.format"));

	// nodes
	
	/** The search box */
	private final TextField txtSearch;
	
	/** The bible to search */
	private final ComboBox<BibleListItem> cmbBiblePrimary;

	/** The book */
	private final AutoCompleteComboBox<Book> cmbBook;
	
	/** The search type */
	private final ComboBox<Option<SearchType>> cmbSearchType;
	
	/** The results table */
	private final TableView<BibleSearchResult> table;
	
	// value
	
	/** The selected row */
	private final ObjectProperty<SelectedBibleSearchResult> value = new SimpleObjectProperty<SelectedBibleSearchResult>();
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public BibleSearchPane(PraisenterContext context) {
		this.getStyleClass().add("bible-search-pane");
		
		this.setPadding(new Insets(5));
		
		ObservableList<Option<SearchType>> types = FXCollections.observableArrayList();
		types.add(new Option<SearchType>(Translations.get("search.type.phrase"), SearchType.PHRASE));
		types.add(new Option<SearchType>(Translations.get("search.type.allwords"), SearchType.ALL_WORDS));
		types.add(new Option<SearchType>(Translations.get("search.type.anyword"), SearchType.ANY_WORD));

		this.txtSearch = new TextField();
		this.txtSearch.setPromptText(Translations.get("search.terms.placeholder"));
		
		this.cmbSearchType = new ComboBox<Option<SearchType>>(types);
		this.cmbSearchType.setValue(types.get(0));
		
		ObservableBibleLibrary library = context.getBibleLibrary();
		
		// filter the list of selectable bibles by whether they are loaded or not		
		FilteredList<BibleListItem> filtered = context.getBibleLibrary().getItems().filtered(b -> b.isLoaded());
		SortedList<BibleListItem> bibles = filtered.sorted((a, b) -> {
			if (a == b) return 0;
			if (a == null) return -1;
			if (b == null) return 1;
			return a.compareTo(b);
		});
		
		BibleListItem backupBible = null;
		if (bibles.size() > 0) {
			backupBible = bibles.get(0);
		}
		
		UUID primaryId = context.getConfiguration().getUUID(Setting.BIBLE_PRIMARY, null);
		BibleListItem primaryBible = null;
		if (primaryId != null) {
			primaryBible = library.getListItem(primaryId);
			if (primaryBible == null) {
				primaryBible = backupBible;
			}
		}
		
		ObservableList<Book> books = FXCollections.observableArrayList();
		List<Book> bb = primaryBible != null ? primaryBible.getBible().getBooks() : null;
		if (bb != null) {
			books.addAll(bb);
		}
		
		this.cmbBiblePrimary = new ComboBox<BibleListItem>(bibles);
		if (primaryBible != null) {
			this.cmbBiblePrimary.getSelectionModel().select(primaryBible);
		}
		
		this.cmbBook = new AutoCompleteComboBox<Book>(books, new AutoCompleteComparator<Book>() {
			public boolean matches(String typedText, Book objectToCompare) {
				Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
				if (pattern.matcher(objectToCompare.getName()).matches()) {
					return true;
				}
				return false;
			}
		});
		
		this.cmbBiblePrimary.valueProperty().addListener((obs, ov, nv) -> {
			try {
				if (nv != null) {
					Book selectedBook = this.cmbBook.getValue();
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
					this.cmbBook.setValue(book);
				} else {
					books.clear();
				}
			} catch (Exception ex) {
				LOGGER.error("An error occurred when a different primary bible was selected: " + ov + " -> " + nv, ex);
			}
		});
		
		this.cmbBook.setPromptText(Translations.get("bible.book.placeholder"));
		
		Button btnSearch = new Button(Translations.get("search.button"));
		
		HBox top = new HBox(5, txtSearch, cmbBiblePrimary, cmbBook, cmbSearchType, btnSearch);
		HBox.setHgrow(txtSearch, Priority.ALWAYS);
		top.setPadding(new Insets(0, 0, 5, 0));
		
		this.setTop(top);
		
		///////////////////////////
		
		this.table = new TableView<BibleSearchResult>();
		
		// columns
		TableColumn<BibleSearchResult, Number> score = new TableColumn<BibleSearchResult, Number>(Translations.get("search.score"));
		TableColumn<BibleSearchResult, BibleSearchResult> reference = new TableColumn<BibleSearchResult, BibleSearchResult>(Translations.get("bible.search.results.reference"));
		TableColumn<BibleSearchResult, Verse> verseText = new TableColumn<BibleSearchResult, Verse>(Translations.get("bible.search.results.text"));
		
		score.setCellValueFactory(p -> new ReadOnlyFloatWrapper(p.getValue().getScore()));
		reference.setCellValueFactory(p -> new ReadOnlyObjectWrapper<BibleSearchResult>(p.getValue()));
		verseText.setCellValueFactory(p -> new ReadOnlyObjectWrapper<Verse>(p.getValue().getVerse()));
		
		score.setCellFactory(p -> new TableCell<BibleSearchResult, Number>() {
			{
				setAlignment(Pos.CENTER_RIGHT);
			}
			@Override
			protected void updateItem(Number item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(SCORE_FORMAT.format(item));
				}
			}
		});
		reference.setCellFactory(p -> new TableCell<BibleSearchResult, BibleSearchResult>() {
			@Override
			protected void updateItem(BibleSearchResult item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(MessageFormat.format("{0} {1}:{2}", 
							item.getBook().getName(),
							item.getChapter().getNumber(),
							item.getVerse().getNumber()));
				}
			}
		});
		verseText.setCellFactory(p -> new TableCell<BibleSearchResult, Verse>() {
			private final Tooltip tooltip;
			{
				this.tooltip = new Tooltip();
				this.tooltip.setWrapText(true);
				this.tooltip.setMaxWidth(300);
				setTooltip(null);
			}
			@Override
			protected void updateItem(Verse item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setTooltip(null);
				} else {
					setText(item.getText());
					tooltip.setText(item.getText());
					setTooltip(tooltip);
				}
			}
		});
		
		score.setPrefWidth(75);
		reference.setPrefWidth(150);
		verseText.setPrefWidth(600);
		
		this.table.getColumns().add(score);
		this.table.getColumns().add(reference);
		this.table.getColumns().add(verseText);
		
		this.table.setRowFactory( tv -> {
		    TableRow<BibleSearchResult> row = new TableRow<BibleSearchResult>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (!row.isEmpty())) {
		        	BibleSearchResult rowData = row.getItem();
		            // set the current value
		        	value.set(new SelectedBibleSearchResult(rowData, event.isShortcutDown()));
		        }
		    });
		    return row ;
		});
		
		// loading
		ProgressOverlay overlay = new ProgressOverlay();
		StackPane stack = new StackPane(this.table, overlay);
		
		this.setCenter(stack);

		Label lblResults = new Label();
		
		this.setBottom(lblResults);
		
		overlay.setVisible(false);
		
		EventHandler<ActionEvent> handler = e -> {
			BibleListItem item = this.cmbBiblePrimary.getValue();
			Book book = this.cmbBook.getValue();
			String text = this.txtSearch.getText();
			Option<SearchType> type = this.cmbSearchType.getValue();
			
			if (text != null && text.length() != 0 && type != null) {
				overlay.setVisible(true);
				
				BibleSearchCriteria criteria = new BibleSearchCriteria(
						item != null ? item.getBible().getId() : null, 
						book != null ? book.getNumber() : null,
						text, 
						type.getValue());
				
				AsyncTask<List<BibleSearchResult>> task = library.search(criteria);
				task.addSuccessHandler(evt -> {
					List<BibleSearchResult> results = task.getValue();
					this.table.setItems(FXCollections.observableArrayList(results));
					int size = results.size();
					lblResults.setText(MessageFormat.format(Translations.get("bible.search.results.output"), size > BibleSearchCriteria.MAXIMUM_RESULTS ? BibleSearchCriteria.MAXIMUM_RESULTS + "+" : size));
				}).addCompletedHandler(evt -> {
					overlay.setVisible(false);
				}).execute(context.getExecutorService());
			}
		};
		
		txtSearch.setOnAction(handler);
		btnSearch.setOnAction(handler);
	}
	
	/**
	 * The selected result.
	 * @return {@link SelectedBibleSearchResult}
	 */
	public SelectedBibleSearchResult getValue() {
		return this.value.get();
	}
	
	/**
	 * The selected result property.
	 * @return ReadOnlyObjectProperty&lt;{@link SelectedBibleSearchResult}&gt;
	 */
	public ReadOnlyObjectProperty<SelectedBibleSearchResult> valueProperty() {
		return this.value;
	}
	
	/**
	 * Returns the search text.
	 * @return String
	 */
	public String getText() {
		return this.txtSearch.getText();
	}
	
	/**
	 * Sets the search text.
	 * @param text the text
	 */
	public void setText(String text) {
		this.txtSearch.setText(text);
		this.txtSearch.commitValue();
	}
}
