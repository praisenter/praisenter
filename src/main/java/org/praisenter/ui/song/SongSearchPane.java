package org.praisenter.ui.song;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.search.SearchResult;
import org.praisenter.data.search.SearchTextMatch;
import org.praisenter.data.search.SearchType;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongSearchResult;
import org.praisenter.data.song.SongTextSearchCriteria;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.controls.ProgressOverlay;
import org.praisenter.ui.translations.Translations;

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
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

// FEATURE (M-M) add searching to the song editor for finding and editing easily

public final class SongSearchPane extends VBox {
	private static final String SONG_SEARCH_CSS = "p-song-search";
	private static final String SONG_SEARCH_CRITERIA_CSS = "p-song-search-criteria";
	
	private static final Logger LOGGER = LogManager.getLogger();

	private static final DecimalFormat SCORE_FORMAT = new DecimalFormat(Translations.get("search.score.format"));

	private final GlobalContext context;
	
	// data
	
	private final ObjectProperty<Option<SearchType>> searchType;
	private final StringProperty terms;
	private final ObservableList<SongSearchResult> results;
	
	// value
	
	private final ObjectProperty<SongSearchResult> value;
	
	public SongSearchPane(GlobalContext context) {
		this.getStyleClass().add(SONG_SEARCH_CSS);
		
		this.context = context;
		
		this.searchType = new SimpleObjectProperty<Option<SearchType>>();
		this.terms = new SimpleStringProperty();
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
		
		ComboBox<Option<SearchType>> cmbSearchType = new ComboBox<Option<SearchType>>(types);
		cmbSearchType.setValue(types.get(0));
		cmbSearchType.valueProperty().bindBidirectional(this.searchType);
		
		Button btnSearch = new Button(Translations.get("search.button"));
		
		GridPane top = new GridPane();
		
		top.add(txtSearch, 0, 0);
		top.add(cmbSearchType, 1, 0);
		top.add(btnSearch, 2, 0);
		
		txtSearch.setMaxWidth(Double.MAX_VALUE);
		cmbSearchType.setMaxWidth(Double.MAX_VALUE);
		btnSearch.setMaxWidth(Double.MAX_VALUE);
		
		final int[] widths = new int[] { 70, 20, 10 };
		for (int i = 0; i < widths.length; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(widths[i]);
			top.getColumnConstraints().add(cc);
		}
		
		top.getStyleClass().add(SONG_SEARCH_CRITERIA_CSS);
		
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
					
					// format the match text from Lucene to show what we matched on
					String[] mparts = highlighted.replaceAll("\n\r?", " ").split("<B>");
					for (String mpart : mparts) {
						if (mpart.contains("</B>")) {
							String[] nparts = mpart.split("</B>");
							Text temp = new Text(nparts[0]);
							temp.getStyleClass().add("highlight");
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
		sectionText.setPrefWidth(600);
		
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
		
		Bindings.bindContent(table.getItems(), this.results);
		
		ProgressOverlay overlay = new ProgressOverlay();
		overlay.setVisible(false);
		
		StackPane stack = new StackPane(table, overlay);
		
		Label lblResults = new Label();
		
		VBox.setVgrow(stack, Priority.ALWAYS);
		
		this.getChildren().addAll(top, stack, lblResults);
		
		EventHandler<ActionEvent> handler = e -> {
			String text = this.terms.get();
			Option<SearchType> type = this.searchType.get();
			
			if (text != null && text.length() != 0 && type != null) {
				overlay.setVisible(true);
				
				final int maxResults = 100;
				SongTextSearchCriteria criteria = new SongTextSearchCriteria(
						text,
						type.getValue(),
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
		
		// update the search results when things are changed, removed, added, etc.
		context.getWorkspaceManager().getItemsUnmodifiable(Song.class).addListener((Change<? extends Song> c) -> {
			handler.handle(null);
		});
		
		txtSearch.setOnAction(handler);
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
