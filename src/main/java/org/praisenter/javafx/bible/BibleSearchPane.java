package org.praisenter.javafx.bible;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.praisenter.SearchType;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleSearchResult;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.AutoCompleteComboBox;
import org.praisenter.javafx.AutoCompleteComparator;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.configuration.Setting;

import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class BibleSearchPane extends BorderPane {
	private static final DecimalFormat SCORE_FORMAT = new DecimalFormat("#.000000");
	
	private final PraisenterContext context;
	
	private final TextField txtSearch;
	private final ComboBox<Option<SearchType>> cmbSearchType;
	private final ComboBox<BibleListItem> cmbBiblePrimary;
	private final AutoCompleteComboBox<Book> cmbBook;
	
	private final TableView<BibleSearchResult> table;
	
	// TODO translate
	// TODO ability to double click verse to set the current "value"
	
	// FEATURE add searching to the bible editor for finding and editing easily
	
	public BibleSearchPane(PraisenterContext context) {
		this.context = context;
		
		this.setPadding(new Insets(5));
		
		ObservableList<Option<SearchType>> types = FXCollections.observableArrayList();
		types.add(new Option<SearchType>("Phrase", SearchType.PHRASE));
		types.add(new Option<SearchType>("All Words", SearchType.ALL_WORDS));
		types.add(new Option<SearchType>("Any Word", SearchType.ANY_WORD));

		this.txtSearch = new TextField();
		this.txtSearch.setPromptText("Search terms");
		
		this.cmbSearchType = new ComboBox<>(types);
		this.cmbSearchType.setValue(types.get(0));
		
		// filter the list of selectable bibles by whether they are loaded or not		
		FilteredList<BibleListItem> bibles = new FilteredList<BibleListItem>(context.getBibleLibrary().getItems());
		bibles.setPredicate(b -> b.isLoaded());
		
		Bible backupBible = null;
		if (bibles.size() > 0) {
			backupBible = bibles.get(0).getBible();
		}
		
		UUID primaryId = context.getConfiguration().getUUID(Setting.BIBLE_PRIMARY, null);
		Bible primaryBible = null;
		if (primaryId != null) {
			primaryBible = context.getBibleLibrary().get(primaryId);
			if (primaryBible == null) {
				primaryBible = backupBible;
			}
		}
		
		ObservableList<Book> books = FXCollections.observableArrayList();
		List<Book> bb = primaryBible != null ? primaryBible.getBooks() : null;
		if (bb != null) {
			books.addAll(bb);
		}
		
		this.cmbBiblePrimary = new ComboBox<BibleListItem>(bibles);
		if (primaryBible != null) {
			this.cmbBiblePrimary.getSelectionModel().select(new BibleListItem(primaryBible));
		}
		
		this.cmbBiblePrimary.valueProperty().addListener((obs, ov, nv) -> {
			try {
				if (nv != null) {
					Book selectedBook = this.cmbBook.getValue();
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
					this.cmbBook.setValue(book);
				} else {
					books.clear();
				}
			} catch (Exception ex) {
				// TODO handle error LOGGER.error("An unexpected error occurred when a different primary bible was selected: " + ov + " -> " + nv, ex);
			}
		});
		
		this.cmbBook = new AutoCompleteComboBox<Book>(books, new AutoCompleteComparator<Book>() {
			public boolean matches(String typedText, Book objectToCompare) {
				Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
				if (pattern.matcher(objectToCompare.getName()).matches()) {
					return true;
				}
				return false;
			}
		});
		
		Button btnSearch = new Button("Search");
		
		HBox top = new HBox(5, txtSearch, cmbBiblePrimary, cmbBook, cmbSearchType, btnSearch);
		HBox.setHgrow(txtSearch, Priority.ALWAYS);
		top.setPadding(new Insets(0, 0, 5, 0));
		
		this.setTop(top);
		
		///////////////////////////
		
		this.table = new TableView<BibleSearchResult>();
		
		// columns
		TableColumn<BibleSearchResult, Number> score = new TableColumn<BibleSearchResult, Number>("Score");
		TableColumn<BibleSearchResult, Bible> bibleName = new TableColumn<BibleSearchResult, Bible>("Bible");
		TableColumn<BibleSearchResult, Book> bookName = new TableColumn<BibleSearchResult, Book>("Book");
		TableColumn<BibleSearchResult, Chapter> chapterNumber = new TableColumn<BibleSearchResult, Chapter>("Chap");
		TableColumn<BibleSearchResult, Verse> verseNumber = new TableColumn<BibleSearchResult, Verse>("Verse");
		TableColumn<BibleSearchResult, Verse> verseText = new TableColumn<BibleSearchResult, Verse>("Text");
		
		score.setCellValueFactory(p -> new ReadOnlyFloatWrapper(p.getValue().getScore()));
		bibleName.setCellValueFactory(p -> new ReadOnlyObjectWrapper<Bible>(p.getValue().getBible()));
		bookName.setCellValueFactory(p -> new ReadOnlyObjectWrapper<Book>(p.getValue().getBook()));
		chapterNumber.setCellValueFactory(p -> new ReadOnlyObjectWrapper<Chapter>(p.getValue().getChapter()));
		verseNumber.setCellValueFactory(p -> new ReadOnlyObjectWrapper<Verse>(p.getValue().getVerse()));
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
		bibleName.setCellFactory(p -> new TableCell<BibleSearchResult, Bible>() {
			@Override
			protected void updateItem(Bible item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});
		bookName.setCellFactory(p -> new TableCell<BibleSearchResult, Book>() {
			@Override
			protected void updateItem(Book item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});
		chapterNumber.setCellFactory(p -> new TableCell<BibleSearchResult, Chapter>() {
			@Override
			protected void updateItem(Chapter item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(String.valueOf(item.getNumber()));
				}
			}
		});
		verseNumber.setCellFactory(p -> new TableCell<BibleSearchResult, Verse>() {
			@Override
			protected void updateItem(Verse item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(String.valueOf(item.getNumber()));
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
		bibleName.setPrefWidth(175);
		bookName.setPrefWidth(150);
		chapterNumber.setPrefWidth(50);
		verseNumber.setPrefWidth(50);
		verseText.setPrefWidth(600);
		
		this.table.getColumns().add(score);
		this.table.getColumns().add(bibleName);
		this.table.getColumns().add(bookName);
		this.table.getColumns().add(chapterNumber);
		this.table.getColumns().add(verseNumber);
		this.table.getColumns().add(verseText);
		
		// loading
		ProgressIndicator progress = new ProgressIndicator();
		progress.setMaxSize(50, 50);
		progress.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), null)));
		StackPane overlay = new StackPane(progress);
		overlay.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.2), null, null)));
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
				context.getBibleLibrary().search(
						item != null ? item.getBible().getId() : null, 
						book != null ? book.getNumber() : null,
						text, 
						type.getValue(), 
						results -> {
							this.table.setItems(FXCollections.observableArrayList(results));
							lblResults.setText("Results: " + results.size());
							overlay.setVisible(false);
						}, 
						null);
			}
		};
		
		txtSearch.setOnAction(handler);
		btnSearch.setOnAction(handler);
	}
}
