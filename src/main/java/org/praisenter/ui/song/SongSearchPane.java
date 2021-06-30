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
import org.praisenter.data.search.SearchType;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.ReadOnlyLyrics;
import org.praisenter.data.song.ReadOnlySection;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongSearchCriteria;
import org.praisenter.data.song.SongSearchResult;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.controls.ProgressOverlay;
import org.praisenter.ui.translations.Translations;

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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

// FEATURE (M-M) add searching to the bible editor for finding and editing easily

public final class SongSearchPane extends BorderPane {
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
		
		HBox top = new HBox(5, txtSearch, cmbSearchType, btnSearch);
		HBox.setHgrow(txtSearch, Priority.ALWAYS);
		top.setPadding(new Insets(0, 0, 5, 0));
		
		this.setTop(top);
		
		///////////////////////////

		this.setPadding(new Insets(5));
		
		TableView<SongSearchResult> table = new TableView<SongSearchResult>();
		
		// columns
		TableColumn<SongSearchResult, Number> score = new TableColumn<SongSearchResult, Number>(Translations.get("search.score"));
		TableColumn<SongSearchResult, SongSearchResult> reference = new TableColumn<SongSearchResult, SongSearchResult>(Translations.get("song.search.results.reference"));
		TableColumn<SongSearchResult, ReadOnlySection> sectionText = new TableColumn<SongSearchResult, ReadOnlySection>(Translations.get("song.search.results.text"));
		
		score.setCellValueFactory(p -> new ReadOnlyFloatWrapper(p.getValue().getScore()));
		reference.setCellValueFactory(p -> new ReadOnlyObjectWrapper<SongSearchResult>(p.getValue()));
		sectionText.setCellValueFactory(p -> new ReadOnlyObjectWrapper<ReadOnlySection>(p.getValue().getSection()));
		
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
		reference.setCellFactory(p -> new TableCell<SongSearchResult, SongSearchResult>() {
			@Override
			protected void updateItem(SongSearchResult item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(MessageFormat.format("{0} {1}", 
							item.getLyrics().getTitle(),
							item.getSection().getName()));
				}
			}
		});
		sectionText.setCellFactory(p -> new TableCell<SongSearchResult, ReadOnlySection>() {
			private final Tooltip tooltip;
			{
				this.tooltip = new Tooltip();
				this.tooltip.setWrapText(true);
				this.tooltip.setMaxWidth(300);
				setTooltip(null);
			}
			@Override
			protected void updateItem(ReadOnlySection item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setTooltip(null);
				} else {
					String text = item.getText();
					if (text != null) {
						text = text.replaceAll("\r?\n", " ");
					}
					setText(text);
					tooltip.setText(item.getText());
					setTooltip(tooltip);
				}
			}
		});
		
		score.setPrefWidth(75);
		reference.setPrefWidth(150);
		sectionText.setPrefWidth(600);
		
		table.getColumns().add(score);
		table.getColumns().add(reference);
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
		
		this.setCenter(stack);
		this.setBottom(lblResults);
		
		EventHandler<ActionEvent> handler = e -> {
			String text = this.terms.get();
			Option<SearchType> type = this.searchType.get();
			
			if (text != null && text.length() != 0 && type != null) {
				overlay.setVisible(true);
				
				final int maxResults = 100;
				SongSearchCriteria criteria = new SongSearchCriteria(
						text,
						type.getValue(),
						maxResults);
				
				context.getDataManager().search(criteria).thenCompose(AsyncHelper.onJavaFXThreadAndWait((result) -> {
					this.results.setAll(this.getSearchResults(result.getResults()));
					lblResults.setText(MessageFormat.format(Translations.get("song.search.results.output"), result.hasMore() ? maxResults + "+" : result.getNumberOfResults()));
					overlay.setVisible(false);
				})).exceptionally(t -> {
					LOGGER.error("Failed to search songs using terms '" + text + "' due to: " + t.getMessage(), t);
					Platform.runLater(() -> {
						Alert alert = Alerts.exception(this.context.getStage(), t);
						alert.show();
					});
					return null;
				});
			}
		};
		
		txtSearch.setOnAction(handler);
		btnSearch.setOnAction(handler);
	}
	
	private List<SongSearchResult> getSearchResults(List<SearchResult> results) {
		List<SongSearchResult> output = new ArrayList<SongSearchResult>();
		for (SearchResult result : results) {
			Document document = result.getDocument();
			Song song = this.context.getDataManager().getItem(Song.class, UUID.fromString(document.get(Song.FIELD_ID)));
			if (song == null) {
				LOGGER.warn("Unable to find song '{}'. A re-index might fix this problem.", document.get(Song.FIELD_ID));
				continue;
			}
			
			// get the details
			UUID lyricsId = UUID.fromString(document.getField(Song.FIELD_LYRIC_ID).stringValue());
			UUID sectionId = UUID.fromString(document.getField(Song.FIELD_SECTION_ID).stringValue());
			
			ReadOnlyLyrics lyrics = null;
			ReadOnlySection section = null;
			if (song != null) {
				for (Lyrics l : song.getLyrics()) {
					if (l.getId().equals(lyricsId)) {
						lyrics = l;
						break;
					}
				}
			}
			
			if (lyrics != null) {
				for (ReadOnlySection s : lyrics.getSectionsUnmodifiable()) {
					if (s.getId().equals(sectionId)) {
						section = s;
						break;
					}
				}
			}
			
			// just continue if its not found
			if (lyrics == null) {
//				LOGGER.warn("Unable to find {} {}:{} in '{}'. A re-index might fix this problem.", bookNumber, chapterNumber, verseNumber, song != null ? song.getName() : "null");
				continue;
			}
			
			output.add(new SongSearchResult(
					song,
					lyrics, 
					section, 
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
