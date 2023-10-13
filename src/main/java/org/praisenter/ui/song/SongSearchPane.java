package org.praisenter.ui.song;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.praisenter.Constants;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.search.SearchResult;
import org.praisenter.data.search.SearchTextMatch;
import org.praisenter.data.search.SearchType;
import org.praisenter.data.song.ReadOnlySection;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongSearchResult;
import org.praisenter.data.song.SongTextSearchCriteria;
import org.praisenter.ui.DataFormats;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Icons;
import org.praisenter.ui.Option;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.controls.FastScrollPane;
import org.praisenter.ui.controls.ProgressOverlay;
import org.praisenter.ui.translations.Translations;

import com.fasterxml.jackson.core.JsonProcessingException;

import atlantafx.base.theme.Styles;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

// FEATURE (M-M) add searching to the song editor for finding and editing easily

public final class SongSearchPane extends VBox {
	private static final String SONG_SEARCH_CSS = "p-song-search";
	private static final String SONG_SEARCH_CRITERIA_CSS = "p-song-search-criteria";
	private static final String SONG_SEARCH_CARD_CSS = "p-song-search-card";
	
	private static final Logger LOGGER = LogManager.getLogger();

	private static final DecimalFormat SCORE_FORMAT = new DecimalFormat(Translations.get("search.score.format"));

	private final GlobalContext context;
	
	// data
	
	private final ObjectProperty<Option<SearchType>> searchType;
	private final StringProperty terms;
	private final ObjectProperty<Option<Boolean>> matchType;
	private final ObservableList<SongSearchResult> results;
	
	private final Runnable search;
	
	// value
	
	private final ObjectProperty<SongSearchResult> value;
	
	public SongSearchPane(GlobalContext context) {
		this.getStyleClass().add(SONG_SEARCH_CSS);
		
		this.context = context;
		
		this.searchType = new SimpleObjectProperty<Option<SearchType>>();
		this.terms = new SimpleStringProperty();
		this.matchType = new SimpleObjectProperty<>();
		this.results = FXCollections.observableArrayList();
		
		this.value = new SimpleObjectProperty<SongSearchResult>();
		
		ObservableList<Option<SearchType>> types = FXCollections.observableArrayList();
		types.add(new Option<SearchType>(Translations.get("search.type.phrase"), SearchType.PHRASE));
		types.add(new Option<SearchType>(Translations.get("search.type.allwords"), SearchType.ALL_WORDS));
		types.add(new Option<SearchType>(Translations.get("search.type.anyword"), SearchType.ANY_WORD));
		this.searchType.setValue(types.get(0));
		
		TextField txtSearch = new TextField();
		txtSearch.setPromptText(Translations.get("search.terms.placeholder"));
		txtSearch.textProperty().bindBidirectional(this.terms);
		
		ChoiceBox<Option<SearchType>> cbSearchType = new ChoiceBox<Option<SearchType>>(types);
		cbSearchType.setValue(types.get(0));
		cbSearchType.valueProperty().bindBidirectional(this.searchType);
		
		ObservableList<Option<Boolean>> matchTypes = FXCollections.observableArrayList();
		matchTypes.add(new Option<Boolean>(Translations.get("search.match.exact"), true));
		matchTypes.add(new Option<Boolean>(Translations.get("search.match.fuzzy"), false));
		this.matchType.setValue(matchTypes.get(0));
		
		ChoiceBox<Option<Boolean>> cbMatchType = new ChoiceBox<Option<Boolean>>(matchTypes);
		cbMatchType.setValue(matchTypes.get(0));
		cbMatchType.valueProperty().bindBidirectional(this.matchType);
		
		Button btnSearch = new Button(Translations.get("search.button"));
		btnSearch.setDefaultButton(true);
		
		HBox top = new HBox(2,
				txtSearch,
				cbSearchType,
				cbMatchType,
				btnSearch);
		top.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(txtSearch, Priority.ALWAYS);
		
		cbSearchType.setPrefWidth(125);
		cbSearchType.setMaxWidth(125);
		cbSearchType.setMinWidth(125);
		cbMatchType.setPrefWidth(125);
		cbMatchType.setMaxWidth(125);
		cbMatchType.setMinWidth(125);
		
		top.getStyleClass().add(SONG_SEARCH_CRITERIA_CSS);

		Label lblResultPlaceholder = new Label(Translations.get("song.search.results.placeholder"));
		
		VBox right = new VBox();
		FastScrollPane scrSong = new FastScrollPane(right, 2.0);
		scrSong.setFitToWidth(true);
		scrSong.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrSong.setMinWidth(300);

		///////////////////////////

		TableView<SongSearchResult> table = new TableView<SongSearchResult>();
		
		// columns
		TableColumn<SongSearchResult, Number> score = new TableColumn<SongSearchResult, Number>(Translations.get("search.score"));
		TableColumn<SongSearchResult, String> song = new TableColumn<SongSearchResult, String>(Translations.get("song.search.results.song"));
		TableColumn<SongSearchResult, SongSearchResult> sectionText = new TableColumn<SongSearchResult, SongSearchResult>(Translations.get("song.search.results.text"));
		
		score.setCellValueFactory(p -> new ReadOnlyFloatWrapper(p.getValue().getScore()));
		song.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getSong().getName()));
		sectionText.setCellValueFactory(p -> new ReadOnlyObjectWrapper<SongSearchResult>(p.getValue()));
		
		score.setCellFactory(p -> new TableCell<SongSearchResult, Number>() {
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
		song.setCellFactory(p -> new TableCell<SongSearchResult, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(item);
				}
			}
		});
		sectionText.setCellFactory(p -> new TableCell<SongSearchResult, SongSearchResult>() {
			@Override
			protected void updateItem(SongSearchResult item, boolean empty) {
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
						setGraphic(new Text(item.getSong().getName()));
						return;
					}
					
					// get the matched text
					String highlighted = match.getMatchedText();
					HBox text = new HBox();
					text.setAlignment(Pos.CENTER_LEFT);
					
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
								text.getChildren().add(new Text(nparts[1]));
							}
						} else {
							text.getChildren().add(new Text(mpart));
						}
					}
					
					setGraphic(text);
				}
			}
		});
		
		score.setPrefWidth(75);
		song.setPrefWidth(150);
		sectionText.setPrefWidth(680);
		
		table.getColumns().add(score);
		table.getColumns().add(song);
		table.getColumns().add(sectionText);
		table.setPlaceholder(new Label(Translations.get("song.search.results.none")));
		
		table.setRowFactory(tv -> {
		    TableRow<SongSearchResult> row = new TableRow<SongSearchResult>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (!row.isEmpty())) {
		        	SongSearchResult rowData = row.getItem();
		            // set the current value
		        	this.value.set(rowData);
		        }
		    });
		    return row ;
		});

		table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			right.getChildren().clear();
			scrSong.setVvalue(0);
			
			if (nv != null) {
				var lyrics = nv.getSong().getDefaultLyrics();
				for (var verse : lyrics.getSectionsUnmodifiable()) {
					VBox card = new VBox();
					card.getStyleClass().add(SONG_SEARCH_CARD_CSS);

					String location = verse.getName();
					String text = verse.getText();
					
					Hyperlink link = new Hyperlink();
					link.setText(location);
					link.getStyleClass().add(Styles.TEXT_CAPTION);
					link.setOnAction(e -> {
						this.value.set(null);
			        	this.value.set(nv);
					});
					
					Button btnCopy = new Button("", Icons.getIcon(Icons.COPY));
					btnCopy.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
					btnCopy.setOnAction(e -> {
						ClipboardContent content = new ClipboardContent();
						
						try {
							List<ReadOnlySection> objectData = new ArrayList<>();
							objectData.add(verse);
							String data = JsonIO.write(objectData);
							content.put(DataFormats.PRAISENTER_SECTION_ARRAY, data);
						} catch (JsonProcessingException e1) {
							LOGGER.error("Failed to serialize section", e1);
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
		
		Bindings.bindContent(table.getItems(), this.results);
		
		ProgressOverlay overlay = new ProgressOverlay();
		overlay.setVisible(false);
		
		StackPane leftStack = new StackPane(table, overlay);
		
		StackPane rightStack = new StackPane(lblResultPlaceholder, scrSong);
		lblResultPlaceholder.visibleProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
		lblResultPlaceholder.managedProperty().bind(lblResultPlaceholder.visibleProperty());
		
		Label lblResults = new Label();
		
		SplitPane splt = new SplitPane(leftStack, rightStack);
		splt.setOrientation(Orientation.HORIZONTAL);
		splt.setDividerPosition(0, 0.7);
		SplitPane.setResizableWithParent(scrSong, false);
		
		VBox.setVgrow(splt, Priority.ALWAYS);
		
		this.getChildren().addAll(top, splt, lblResults);
		
		this.search = () -> {
			String text = this.terms.get();
			Option<SearchType> searchType = this.searchType.get();
			Option<Boolean> matchType = this.matchType.getValue();
			
			if (text != null && text.length() != 0 && searchType != null) {
				overlay.setVisible(true);
				
				final int maxResults = 100;
				SongTextSearchCriteria criteria = new SongTextSearchCriteria(
						text,
						searchType.getValue(),
						matchType != null ? !matchType.getValue() : true,
						maxResults);
				
				context.getWorkspaceManager().search(criteria).thenCompose(AsyncHelper.onJavaFXThreadAndWait((result) -> {
					this.results.setAll(this.getSearchResults(result.getResults()));
					lblResults.setText(MessageFormat.format(Translations.get("song.search.results.output"), result.hasMore() ? maxResults + "+" : result.getNumberOfResults()));
					overlay.setVisible(false);
				})).exceptionally(t -> {
					LOGGER.error("Failed to search songs using terms '" + text + "' due to: " + t.getMessage(), t);
					Platform.runLater(() -> {
						Alert alert = Dialogs.exception(this.context.getStage(), t);
						alert.show();
					});
					return null;
				});
			}
		};
		
		EventHandler<ActionEvent> handler = e -> {
			this.search.run();
		};
		
		// update the search results when things are changed, removed, added, etc.
		context.getWorkspaceManager().getItemsUnmodifiable(Song.class).addListener((Change<? extends Song> c) -> {
			handler.handle(null);
		});
		
		btnSearch.setOnAction(handler);
	}
	
	private List<SongSearchResult> getSearchResults(List<SearchResult> results) {
		List<SongSearchResult> output = new ArrayList<SongSearchResult>();
		for (SearchResult result : results) {
			Document document = result.getDocument();
			
			Song song = this.context.getWorkspaceManager().getItem(Song.class, UUID.fromString(document.get(Song.FIELD_ID)));
			if (song == null) {
				LOGGER.warn("Unable to find song '{}'. A re-index might fix this problem.", document.get(Song.FIELD_ID));
				continue;
			}
			
			output.add(new SongSearchResult(
					song,
					result.getMatches(), 
					result.getScore()));
		}
		
		return output;
	}
	
	public void clear() {
		this.terms.set(null);
		this.results.clear();
	}
	
	public void search() {
		this.search.run();
	}
	
	public SongSearchResult getValue() {
		return this.value.get();
	}
	
	public ReadOnlyObjectProperty<SongSearchResult> valueProperty() {
		return this.value;
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
