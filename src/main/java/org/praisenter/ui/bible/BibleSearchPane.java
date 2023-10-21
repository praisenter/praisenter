package org.praisenter.ui.bible;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.praisenter.Constants;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.PersistableComparator;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BibleConfiguration;
import org.praisenter.data.bible.BibleSearchResult;
import org.praisenter.data.bible.BibleTextSearchCriteria;
import org.praisenter.data.bible.LocatedVerse;
import org.praisenter.data.bible.ReadOnlyBook;
import org.praisenter.data.bible.ReadOnlyVerse;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.search.SearchResult;
import org.praisenter.data.search.SearchTextMatch;
import org.praisenter.data.search.SearchType;
import org.praisenter.ui.DataFormats;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Icons;
import org.praisenter.ui.Option;
import org.praisenter.ui.controls.AutoCompleteComboBox;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.controls.FastScrollPane;
import org.praisenter.ui.controls.ProgressOverlay;
import org.praisenter.ui.controls.SimpleSplitPaneSkin;
import org.praisenter.ui.translations.Translations;

import com.fasterxml.jackson.core.JsonProcessingException;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// FEATURE (M-M) add searching to the bible editor for finding and editing easily

public final class BibleSearchPane extends VBox {
	private static final String BIBLE_SEARCH_CSS = "p-bible-search";
	private static final String BIBLE_SEARCH_FILTERS_CSS = "p-bible-search-filters";
	private static final String BIBLE_SEARCH_CARD_CSS = "p-bible-search-card";
	private static final String BIBLE_SEARCH_CARD_SELECTED_CSS = "p-bible-search-card-selected";
	
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DecimalFormat SCORE_FORMAT = new DecimalFormat(Translations.get("search.score.format"));

	private final GlobalContext context;
	
	// data
	
	private final ObservableList<Bible> bibles;
	private final ObjectProperty<Bible> bible;
	private final ObservableList<ReadOnlyBook> books;
	private final ObjectProperty<ReadOnlyBook> book;
	private final ObjectProperty<Option<SearchType>> searchType;
	private final StringProperty terms;
	private final ObjectProperty<Option<Boolean>> matchType;
	
	// value
	
	private final ObjectProperty<BibleSearchResult> value;
	private final BooleanProperty append;
	
	// nodes
	
	private final CustomTextField txtSearch;
	
	public BibleSearchPane(GlobalContext context, BibleConfiguration configuration) {
		this.getStyleClass().add(BIBLE_SEARCH_CSS);
		
		this.context = context;
		
		this.bibles = FXCollections.observableArrayList();
		this.bible = new SimpleObjectProperty<Bible>();
		this.books = FXCollections.observableArrayList();
		this.book = new SimpleObjectProperty<ReadOnlyBook>();
		this.searchType = new SimpleObjectProperty<Option<SearchType>>();
		this.terms = new SimpleStringProperty();
		this.matchType = new SimpleObjectProperty<>();
		
		this.value = new SimpleObjectProperty<BibleSearchResult>();
		this.append = new SimpleBooleanProperty(false);
		
		ObservableList<Option<SearchType>> searchTypes = FXCollections.observableArrayList();
		searchTypes.add(new Option<SearchType>(Translations.get("search.type.phrase"), SearchType.PHRASE));
		searchTypes.add(new Option<SearchType>(Translations.get("search.type.allwords"), SearchType.ALL_WORDS));
		searchTypes.add(new Option<SearchType>(Translations.get("search.type.anyword"), SearchType.ANY_WORD));
		this.searchType.setValue(searchTypes.get(0));
		
		ObservableList<Bible> bibles = context.getWorkspaceManager().getItemsUnmodifiable(Bible.class).sorted(new PersistableComparator<Bible>());
		Bindings.bindContent(this.bibles, bibles);
		
		this.bible.addListener((obs, ov, nv) -> {
			ReadOnlyBook book = this.book.get();
			if (ov != null) {
				Bindings.unbindContent(this.books, ov.getBooks());
			}
			if (nv != null) {
				Bindings.bindContent(this.books, nv.getBooks());
				ReadOnlyBook newBook = nv.getMatchingBook(book);
				if (newBook != null) {
					this.book.set(newBook);
				}
			}
		});
		
		Bible backupBible = null;
		if (bibles != null && bibles.size() > 0) {
			backupBible = bibles.get(0);
		}
		
		UUID primaryId = configuration.getPrimaryBibleId();
		
		Bible primaryBible = null;
		if (primaryId != null) {
			primaryBible = context.getWorkspaceManager().getItem(Bible.class, primaryId);
		}
		
		if (primaryBible == null) {
			primaryBible = backupBible;
		}
		
		this.bible.set(primaryBible);
		
		this.txtSearch = new CustomTextField();
		this.txtSearch.setPromptText(Translations.get("search.terms.placeholder"));
		this.txtSearch.textProperty().bindBidirectional(this.terms);
		this.txtSearch.setLeft(Icons.getIcon(Icons.SEARCH));
		
		ChoiceBox<Option<SearchType>> cbSearchType = new ChoiceBox<Option<SearchType>>(searchTypes);
		cbSearchType.setValue(searchTypes.get(0));
		cbSearchType.valueProperty().bindBidirectional(this.searchType);
		
		ComboBox<Bible> cmbBible = new ComboBox<Bible>(bibles);
		cmbBible.valueProperty().bindBidirectional(this.bible);
		
		ComboBox<ReadOnlyBook> cmbBook = new AutoCompleteComboBox<ReadOnlyBook>(this.books, (typedText, book) -> {
			Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
			if (pattern.matcher(book.getName()).matches()) {
				return true;
			}
			return false;
		});
		cmbBook.valueProperty().bindBidirectional(this.book);
		cmbBook.setPromptText(Translations.get("bible.book.placeholder"));
		
		ObservableList<Option<Boolean>> matchTypes = FXCollections.observableArrayList();
		matchTypes.add(new Option<Boolean>(Translations.get("search.match.exact"), true));
		matchTypes.add(new Option<Boolean>(Translations.get("search.match.fuzzy"), false));
		this.matchType.setValue(matchTypes.get(0));
		
		ChoiceBox<Option<Boolean>> cbMatchType = new ChoiceBox<Option<Boolean>>(matchTypes);
		cbMatchType.setValue(matchTypes.get(0));
		cbMatchType.valueProperty().bindBidirectional(this.matchType);
		
		Button btnSearch = new Button(Translations.get("search.button"));
		btnSearch.setDefaultButton(true);

		cmbBible.setMaxWidth(Double.MAX_VALUE);
		cmbBook.setMaxWidth(Double.MAX_VALUE);
		cbSearchType.setMaxWidth(Double.MAX_VALUE);
		cbMatchType.setMaxWidth(Double.MAX_VALUE);
		
		GridPane grid = new GridPane();
		grid.add(new VBox(0, new Label(Translations.get("bible")), cmbBible), 0, 0);
		grid.add(new VBox(0, new Label(Translations.get("bible.book")), cmbBook), 1, 0);
		grid.add(new VBox(0, new Label(Translations.get("search.search.type")), cbSearchType), 2, 0);
		grid.add(new VBox(0, new Label(Translations.get("search.match.type")), cbMatchType), 3, 0);
		int[] sizes = new int[] { 30, 30, 20, 20 };
		for (int i = 0; i < sizes.length; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(sizes[i]);
			grid.getColumnConstraints().add(cc);
		}
		grid.getStyleClass().add(BIBLE_SEARCH_FILTERS_CSS);

		HBox layoutSearch = new HBox(5,
				this.txtSearch, btnSearch);
		layoutSearch.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(this.txtSearch, Priority.ALWAYS);
		layoutSearch.getStyleClass().add(BIBLE_SEARCH_FILTERS_CSS);
		
		Label lblResultPlaceholder = new Label(Translations.get("bible.search.results.placeholder"));
		lblResultPlaceholder.setWrapText(true);
		lblResultPlaceholder.setTextAlignment(TextAlignment.CENTER);
		lblResultPlaceholder.setPadding(new Insets(10));
		lblResultPlaceholder.setMinWidth(0);
		lblResultPlaceholder.setMaxWidth(200);
		
		VBox right = new VBox();
		FastScrollPane scrChapter = new FastScrollPane(right, 2.0);
		scrChapter.setFitToWidth(true);
		scrChapter.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrChapter.setMinWidth(200);
		scrChapter.setPrefWidth(300);
		
		///////////////////////////
		
		TableView<BibleSearchResult> table = new TableView<BibleSearchResult>();
		
		// columns
		TableColumn<BibleSearchResult, Number> score = new TableColumn<BibleSearchResult, Number>(Translations.get("search.score"));
		TableColumn<BibleSearchResult, BibleSearchResult> reference = new TableColumn<BibleSearchResult, BibleSearchResult>(Translations.get("bible.search.results.reference"));
		TableColumn<BibleSearchResult, BibleSearchResult> verseText = new TableColumn<BibleSearchResult, BibleSearchResult>(Translations.get("bible.search.results.text"));
		
		score.setCellValueFactory(p -> new ReadOnlyFloatWrapper(p.getValue().getScore()));
		reference.setCellValueFactory(p -> new ReadOnlyObjectWrapper<BibleSearchResult>(p.getValue()));
		verseText.setCellValueFactory(p -> new ReadOnlyObjectWrapper<BibleSearchResult>(p.getValue()));
		
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
		verseText.setCellFactory(p -> new TableCell<BibleSearchResult, BibleSearchResult>() {
			@Override
			protected void updateItem(BibleSearchResult item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
				} else {
					List<SearchTextMatch> matches = item.getMatches();
					SearchTextMatch match = null;
					if (matches != null && matches.size() > 0) {
						match = matches.get(0);
					}
					
					if (match == null) {
						setGraphic(new Text(item.getVerse().getText()));
						return;
					}
					
					// get the matched text
					String highlighted = match.getMatchedText();
					HBox text = new HBox();
					text.setAlignment(Pos.CENTER_LEFT);
//					text.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1))));
					
					// format the match text from Lucene to show what we matched on
					String[] mparts = highlighted.replaceAll("\n\r?", " ").split("<B>");
					for (String mpart : mparts) {
						if (mpart.contains("</B>")) {
							String[] nparts = mpart.split("</B>");
							Text temp = new Text(nparts[0]);
							temp.getStyleClass().add("p-search-highlight");
							text.getChildren().add(temp);
							// it's possible mpart could be "blah</B>" which would only give us one part
							if (nparts.length > 1) {
								Text part = new Text(nparts[1]);
								text.getChildren().add(part);
							}
						} else {
							Text part = new Text(mpart);
							text.getChildren().add(part);
						}
					}
					
					setGraphic(text);
				}
			}
		});
		
		score.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
		reference.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
		verseText.prefWidthProperty().bind(table.widthProperty().multiply(0.60));
		
		table.getColumns().add(score);
		table.getColumns().add(reference);
		table.getColumns().add(verseText);
		table.setPlaceholder(new Label(Translations.get("bible.search.results.none")));
		
		table.setRowFactory(tv -> {
		    TableRow<BibleSearchResult> row = new TableRow<BibleSearchResult>();
		    row.setOnMouseClicked(event -> {
		    	if (!row.isEmpty()) {
		    		BibleSearchResult rowData = row.getItem();
			        if (event.getClickCount() == 2) {
			            // set the current value
			        	this.append.set(event.isShortcutDown());
			        	this.value.set(null);
			        	this.value.set(rowData);
			        }
		    	}
		    });
		    return row ;
		});
		
		table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			right.getChildren().clear();
			scrChapter.setVvalue(0);
			
			if (nv != null) {
				for (var verse : nv.getChapter().getVersesUnmodifiable()) {
					VBox card = new VBox();
					card.getStyleClass().add(BIBLE_SEARCH_CARD_CSS);
					if (verse.getNumber() == nv.getVerse().getNumber()) {
						card.getStyleClass().add(BIBLE_SEARCH_CARD_SELECTED_CSS);
					}
					
					String location = MessageFormat.format("{0} {1}:{2}", 
							nv.getBook().getName(),
							nv.getChapter().getNumber(),
							verse.getNumber());
					String text = verse.getText();
					
					Hyperlink link = new Hyperlink();
					link.setText(location);
					link.getStyleClass().add(Styles.TEXT_CAPTION);
					link.setOnMouseClicked(e -> {
						this.append.set(e.isShortcutDown());
						this.value.set(null);
			        	this.value.set(new BibleSearchResult(nv.getBible(), nv.getBook(), nv.getChapter(), verse, null, 0));
					});
					
					Button btnCopy = new Button("", Icons.getIcon(Icons.COPY));
					btnCopy.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
					btnCopy.setOnAction(e -> {
						ClipboardContent content = new ClipboardContent();
						
						try {
							List<ReadOnlyVerse> objectData = new ArrayList<>();
							objectData.add(verse);
							String data = JsonIO.write(objectData);
							content.put(DataFormats.PRAISENTER_VERSE_ARRAY, data);
						} catch (JsonProcessingException e1) {
							LOGGER.error("Failed to serialize verse", e1);
						}
						
						content.putString(String.join(Constants.NEW_LINE, verse.getText()));
						Clipboard clipboard = Clipboard.getSystemClipboard();
						clipboard.setContent(content);
					});
					
					Label lblText = new Label();
					lblText.setWrapText(true);
					lblText.setText(text);
					
					HBox header = new HBox(5, link, btnCopy);
					header.setAlignment(Pos.CENTER_LEFT);
					
					Separator sepCard = new Separator(Orientation.HORIZONTAL);
					sepCard.getStyleClass().add(Styles.SMALL);
					
					card.getChildren().addAll(header, lblText);
					right.getChildren().addAll(card, sepCard);
				}
			}
		});
		
		ProgressOverlay overlay = new ProgressOverlay();
		overlay.setVisible(false);
		
		StackPane leftStack = new StackPane(table, overlay);
		
		StackPane rightStack = new StackPane(lblResultPlaceholder, scrChapter);
		lblResultPlaceholder.visibleProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
		lblResultPlaceholder.managedProperty().bind(lblResultPlaceholder.visibleProperty());
		
		Label lblResults = new Label();

		SplitPane splt = new SplitPane(leftStack, rightStack);
		splt.setSkin(new SimpleSplitPaneSkin(splt));
		splt.setOrientation(Orientation.HORIZONTAL);
		splt.setDividerPosition(0, 0.7);
		SplitPane.setResizableWithParent(scrChapter, false);
		
		VBox.setVgrow(splt, Priority.ALWAYS);
		
		Label lblSearch = new Label(Translations.get("bible.search.title"));
		lblSearch.getStyleClass().add(Styles.TITLE_3);
		Separator sepTitle = new Separator(Orientation.HORIZONTAL);
		sepTitle.getStyleClass().add(Styles.SMALL);
		
		this.getChildren().addAll(lblSearch, sepTitle, grid, layoutSearch, splt, lblResults);
		
		EventHandler<ActionEvent> handler = e -> {
			Bible bible = this.bible.get();
			ReadOnlyBook book = this.book.get();
			String text = this.terms.get();
			Option<SearchType> searchType = this.searchType.get();
			Option<Boolean> matchType = this.matchType.get();
			
			if (text != null && text.length() != 0 && searchType != null) {
				overlay.setVisible(true);
				
				final int maxResults = 100;
				BibleTextSearchCriteria criteria = new BibleTextSearchCriteria(
						text,
						searchType.getValue(),
						matchType != null ? !matchType.getValue() : true,
						maxResults,
						bible != null ? bible.getId() : null, 
						book != null ? book.getNumber() : -1);
				
				context.getWorkspaceManager().search(criteria).thenCompose(AsyncHelper.onJavaFXThreadAndWait((result) -> {
					table.setItems(FXCollections.observableArrayList(this.getSearchResults(result.getResults())));
					lblResults.setText(MessageFormat.format(Translations.get("bible.search.results.output"), result.hasMore() ? maxResults + "+" : result.getNumberOfResults()));
					overlay.setVisible(false);
				})).exceptionally(t -> {
					LOGGER.error("Failed to search bibles using terms '" + text + "' due to: " + t.getMessage(), t);
					Platform.runLater(() -> {
						Alert alert = Dialogs.exception(this.context.getStage(), t);
						alert.show();
					});
					return null;
				});
			}
		};
		
		// update the search results when things are changed, removed, added, etc.
		context.getWorkspaceManager().getItemsUnmodifiable(Bible.class).addListener((Change<? extends Bible> c) -> {
			handler.handle(null);
		});
		
		btnSearch.setOnAction(handler);
	}
	
	private List<BibleSearchResult> getSearchResults(List<SearchResult> results) {
		List<BibleSearchResult> output = new ArrayList<BibleSearchResult>();
		for (SearchResult result : results) {
			Document document = result.getDocument();
			Bible bible = this.context.getWorkspaceManager().getItem(Bible.class, UUID.fromString(document.get(Bible.FIELD_ID)));
			if (bible == null) {
				LOGGER.warn("Unable to find bible '{}'. A re-index might fix this problem.", document.get(Bible.FIELD_ID));
				continue;
			}
			
			// get the details
			int bookNumber = document.getField(Bible.FIELD_BOOK_NUMBER).numericValue().intValue();
			int chapterNumber = document.getField(Bible.FIELD_VERSE_CHAPTER).numericValue().intValue();
			int verseNumber = document.getField(Bible.FIELD_VERSE_NUMBER).numericValue().intValue();
			
			LocatedVerse verse = null;
			if (bible != null) {
				verse = bible.getVerse(bookNumber, chapterNumber, verseNumber);
			}
			
			// just continue if its not found
			if (verse == null) {
				LOGGER.warn("Unable to find {} {}:{} in '{}'. A re-index might fix this problem.", bookNumber, chapterNumber, verseNumber, bible != null ? bible.getName() : "null");
				continue;
			}
			
			output.add(new BibleSearchResult(
					verse.getBible(),
					verse.getBook(), 
					verse.getChapter(), 
					verse.getVerse(), 
					result.getMatches(), 
					result.getScore()));
		}
		return output;
	}
	
	public void requestSearchFocus() {
		this.txtSearch.requestFocus();
	}
	
	public BibleSearchResult getValue() {
		return this.value.get();
	}
	
	public ReadOnlyObjectProperty<BibleSearchResult> valueProperty() {
		return this.value;
	}
	
	public boolean isAppendEnabled() {
		return this.append.get();
	}
	
	public ReadOnlyBooleanProperty appendProperty() {
		return this.append;
	}
	
	public String getSearchTerms() {
		return this.terms.get();
	}
	
	public void setSearchTerms(String terms) {
		this.terms.set(terms);
	}
	
	public StringProperty searchTermsProperty() {
		return this.terms;
	}
}
