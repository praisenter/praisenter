package org.praisenter.ui.bible;

import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.DocumentContext;
import org.praisenter.ui.TextInputFieldFieldEventFilter;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public final class BiblePropertiesPane extends VBox {
	private final ObjectProperty<DocumentContext<Bible>> documentContext;
	
	private final ObjectProperty<Bible> bible;
	private final ObjectProperty<Object> selectedItem;

	private final StringProperty name;
	private final StringProperty language;
	private final StringProperty source;
	private final StringProperty copyright;
	private final StringProperty notes;
	
	private final ObjectProperty<Book> selectedBook;
	private final StringProperty bookName;
	private final IntegerProperty bookNumber;
	private final ObjectProperty<Integer> bookNumber2;
	
	private final ObjectProperty<Chapter> selectedChapter;
	private final IntegerProperty chapterNumber;
	private final ObjectProperty<Integer> chapterNumber2;
	
	private final ObjectProperty<Verse> selectedVerse;
	private final StringProperty verseText;
	private final IntegerProperty verseNumber;
	private final ObjectProperty<Integer> verseNumber2;
	
	public BiblePropertiesPane() {
		this.documentContext = new SimpleObjectProperty<>();
		
		this.bible = new SimpleObjectProperty<>();
		this.selectedItem = new SimpleObjectProperty<>();
		
		this.name = new SimpleStringProperty();
		this.language = new SimpleStringProperty();
		this.source = new SimpleStringProperty();
		this.copyright = new SimpleStringProperty();
		this.notes = new SimpleStringProperty();
		
		this.documentContext.addListener((obs, ov, nv) -> {
			this.bible.unbind();
			this.selectedItem.unbind();
			if (nv != null) {
				this.bible.bind(nv.documentProperty());
				this.selectedItem.bind(nv.selectedItemProperty());
			}
		});
		
		this.bible.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.name.unbindBidirectional(ov.nameProperty());
				this.language.unbindBidirectional(ov.languageProperty());
				this.source.unbindBidirectional(ov.sourceProperty());
				this.copyright.unbindBidirectional(ov.copyrightProperty());
				this.notes.unbindBidirectional(ov.notesProperty());
				
				this.name.set(null);
				this.language.set(null);
				this.source.set(null);
				this.copyright.set(null);
				this.notes.set(null);
			}
			if (nv != null) {
				this.name.bindBidirectional(nv.nameProperty());
				this.language.bindBidirectional(nv.languageProperty());
				this.source.bindBidirectional(nv.sourceProperty());
				this.copyright.bindBidirectional(nv.copyrightProperty());
				this.notes.bindBidirectional(nv.notesProperty());
			}
		});
		
		this.bookNumber = new SimpleIntegerProperty();
		this.bookName = new SimpleStringProperty();
		this.bookNumber2 = this.bookNumber.asObject();
		
		this.selectedBook = new SimpleObjectProperty<>();
		this.selectedBook.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.bookName.unbindBidirectional(ov.nameProperty());
				this.bookNumber.unbindBidirectional(ov.numberProperty());
				
				this.bookName.set(null);
				this.bookNumber.set(1);
			}
			if (nv != null) {
				this.bookName.bindBidirectional(nv.nameProperty());
				this.bookNumber.bindBidirectional(nv.numberProperty());
			}
		});
		
		this.chapterNumber = new SimpleIntegerProperty();
		this.chapterNumber2 = this.chapterNumber.asObject();
		
		this.selectedChapter = new SimpleObjectProperty<>();
		this.selectedChapter.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.chapterNumber.unbindBidirectional(ov.numberProperty());
				
				this.chapterNumber.set(0);
			}
			if (nv != null) {
				this.chapterNumber.bindBidirectional(nv.numberProperty());
			}
		});
		
		this.verseNumber = new SimpleIntegerProperty();
		this.verseText = new SimpleStringProperty();
		this.verseNumber2 = this.verseNumber.asObject();
		
		this.selectedVerse = new SimpleObjectProperty<>();
		this.selectedVerse.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.verseText.unbindBidirectional(ov.textProperty());
				this.verseNumber.unbindBidirectional(ov.numberProperty());
				
				this.verseText.set(null);
				this.verseNumber.set(0);
			}
			if (nv != null) {
				this.verseText.bindBidirectional(nv.textProperty());
				this.verseNumber.bindBidirectional(nv.numberProperty());
			}
		});
		
		this.selectedItem.addListener((obs, ov, nv) -> {
			// handle selection of a tree item
			this.selectedBook.set(null);
			this.selectedChapter.set(null);
			this.selectedVerse.set(null);

			if (nv == null) {
				// do nothing
				// TODO logging - something was null here
			} else if (nv instanceof Book) {
				this.selectedBook.set((Book)nv);
			} else if (nv instanceof Chapter) {
				this.selectedChapter.set((Chapter)nv);
			} else if (nv instanceof Verse) {
				this.selectedVerse.set((Verse)nv);
			} else {
				// TODO logging - unsupported type
			}
		});
		
		// UI
		
		Label lblBibleName = new Label("Name");
		TextField txtBibleName = new TextField();
		txtBibleName.textProperty().bindBidirectional(this.name);

		Label lblBibleLanguage = new Label("Language");
		TextField txtBibleLanguage = new TextField();
		txtBibleLanguage.textProperty().bindBidirectional(this.language);
		
		Label lblBibleSource = new Label("Source");
		TextField txtBibleSource = new TextField();
		txtBibleSource.textProperty().bindBidirectional(this.source);
		
		Label lblBibleCopyright = new Label("Copyright");
		TextField txtBibleCopyright = new TextField();
		txtBibleCopyright.textProperty().bindBidirectional(this.copyright);

		Label lblBibleNotes = new Label("Notes");
		TextArea txtBibleNotes = new TextArea();
		txtBibleNotes.textProperty().bindBidirectional(this.notes);
		txtBibleNotes.setWrapText(true);
		
		Label lblBookName = new Label("Name");
		TextField txtBookName = new TextField();
		txtBookName.textProperty().bindBidirectional(this.bookName);
		
		Label lblBookNumber = new Label("Number");
		Spinner<Integer> spnBookNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnBookNumber.setEditable(true);
		spnBookNumber.getValueFactory().valueProperty().bindBidirectional(this.bookNumber2);
		
		Label lblChapterNumber = new Label("Number");
		Spinner<Integer> spnChapterNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnChapterNumber.setEditable(true);
		spnChapterNumber.getValueFactory().valueProperty().bindBidirectional(this.chapterNumber2);
		
		Label lblVerseText = new Label("Text");
		TextArea txtVerseText = new TextArea();
		txtVerseText.textProperty().bindBidirectional(this.verseText);
		txtVerseText.setWrapText(true);
		
		Label lblVerseNumber = new Label("Number");
		Spinner<Integer> spnVerseNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnVerseNumber.setEditable(true);
		spnVerseNumber.getValueFactory().valueProperty().bindBidirectional(this.verseNumber2);
		
		TextInputFieldFieldEventFilter.applyTextInputFieldEventFilter(
				txtBibleName,
				txtBibleLanguage,
				txtBibleSource,
				txtBibleCopyright,
				txtBibleNotes,
				txtBookName,
				spnBookNumber.getEditor(),
				spnChapterNumber.getEditor(),
				txtVerseText,
				spnVerseNumber.getEditor());
		
		TitledPane ttlBible = new TitledPane("bible", new VBox(
				lblBibleName, txtBibleName,
				lblBibleLanguage, txtBibleLanguage,
				lblBibleSource, txtBibleSource,
				lblBibleCopyright, txtBibleCopyright,
				lblBibleNotes, txtBibleNotes));
		
		TitledPane ttlBook = new TitledPane("book", new VBox(
				lblBookNumber, spnBookNumber,
				lblBookName, txtBookName));

		TitledPane ttlChapter = new TitledPane("chapter", new VBox(
				lblChapterNumber, spnChapterNumber));
		
		TitledPane ttlVerse = new TitledPane("book", new VBox(
				lblVerseNumber, spnVerseNumber,
				lblVerseText, txtVerseText));
		
		this.getChildren().addAll(
				ttlBible,
				ttlBook,
				ttlChapter,
				ttlVerse);
		
		// hide/show
		
		BooleanBinding bibleSelected = this.documentContext.isNotNull();
		ttlBible.visibleProperty().bind(bibleSelected);
		ttlBible.managedProperty().bind(bibleSelected);
		
		BooleanBinding bookSelected = this.selectedBook.isNotNull();
		ttlBook.visibleProperty().bind(bookSelected);
		ttlBook.managedProperty().bind(bookSelected);
		
		BooleanBinding chapterSelected = this.selectedChapter.isNotNull();
		ttlChapter.visibleProperty().bind(chapterSelected);
		ttlChapter.managedProperty().bind(chapterSelected);
		
		BooleanBinding verseSelected = this.selectedVerse.isNotNull();
		ttlVerse.visibleProperty().bind(verseSelected);
		ttlVerse.managedProperty().bind(verseSelected);
	}
	
	public DocumentContext<Bible> getDocumentContext() {
		return this.documentContext.get();
	}
	
	public void setDocumentContext(DocumentContext<Bible> ctx) {
		this.documentContext.set(ctx);
	}
	
	public ObjectProperty<DocumentContext<Bible>> documentContextProperty() {
		return this.documentContext;
	}
}
