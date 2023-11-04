package org.praisenter.ui.bible;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Tag;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.Action;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.controls.EditorTitledPane;
import org.praisenter.ui.controls.TagListView;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentSelectionEditor;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public final class BibleSelectionEditor extends VBox implements DocumentSelectionEditor<Bible> {
	private static final String SELECTION_EDITOR_CSS = "p-selection-editor";
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final ObjectProperty<DocumentContext<Bible>> documentContext;
	private final BooleanProperty bulkEdit;
	
	private final ObjectProperty<Bible> bible;
	private final ObjectProperty<Object> selectedItem;

	private final StringProperty name;
	private final StringProperty language;
	private final StringProperty source;
	private final StringProperty copyright;
	private final StringProperty notes;
	private final ObservableSet<Tag> tags;
	
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
	
	public BibleSelectionEditor(GlobalContext context) {
		this.getStyleClass().add(SELECTION_EDITOR_CSS);
		
		this.context = context;
		this.documentContext = new SimpleObjectProperty<>();
		this.bulkEdit = new SimpleBooleanProperty();
		
		this.bible = new SimpleObjectProperty<>();
		this.selectedItem = new SimpleObjectProperty<>();
		
		this.name = new SimpleStringProperty();
		this.language = new SimpleStringProperty();
		this.source = new SimpleStringProperty();
		this.copyright = new SimpleStringProperty();
		this.notes = new SimpleStringProperty();
		this.tags = FXCollections.observableSet(new HashSet<>());
		
		this.documentContext.addListener((obs, ov, nv) -> {
			this.bible.unbind();
			this.selectedItem.unbind();
			this.bulkEdit.unbind();
			if (nv != null) {
				this.bible.bind(nv.documentProperty());
				this.selectedItem.bind(nv.selectedItemProperty());
				this.bulkEdit.bind(nv.bulkEditProperty());
			} else {
				this.bible.set(null);
				this.selectedItem.set(null);
				this.bulkEdit.set(false);
			}
		});
		
		this.bible.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.name.unbindBidirectional(ov.nameProperty());
				this.language.unbindBidirectional(ov.languageProperty());
				this.source.unbindBidirectional(ov.sourceProperty());
				this.copyright.unbindBidirectional(ov.copyrightProperty());
				this.notes.unbindBidirectional(ov.notesProperty());
				Bindings.unbindContentBidirectional(this.tags, ov.getTags());
				
				this.name.set(null);
				this.language.set(null);
				this.source.set(null);
				this.copyright.set(null);
				this.notes.set(null);
				this.tags.clear();
			}
			if (nv != null) {
				this.name.bindBidirectional(nv.nameProperty());
				this.language.bindBidirectional(nv.languageProperty());
				this.source.bindBidirectional(nv.sourceProperty());
				this.copyright.bindBidirectional(nv.copyrightProperty());
				this.notes.bindBidirectional(nv.notesProperty());
				Bindings.bindContentBidirectional(this.tags, nv.getTags());
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

			if (nv == null || nv instanceof Bible) {
				// do nothing
			} else if (nv instanceof Book) {
				this.selectedBook.set((Book)nv);
			} else if (nv instanceof Chapter) {
				this.selectedChapter.set((Chapter)nv);
			} else if (nv instanceof Verse) {
				this.selectedVerse.set((Verse)nv);
			} else {
				LOGGER.warn("The selected item was not a recognized type {}", nv.getClass());
			}
		});
		
		// UI
		
		TextField txtBibleName = new TextField();
		txtBibleName.textProperty().bindBidirectional(this.name);

		TextField txtBibleLanguage = new TextField();
		txtBibleLanguage.textProperty().bindBidirectional(this.language);
		
		TextField txtBibleSource = new TextField();
		txtBibleSource.textProperty().bindBidirectional(this.source);
		
		TextField txtBibleCopyright = new TextField();
		txtBibleCopyright.textProperty().bindBidirectional(this.copyright);

		TextArea txtBibleNotes = new TextArea();
		txtBibleNotes.textProperty().bindBidirectional(this.notes);
		txtBibleNotes.setWrapText(true);
		
		TagListView viewTags = new TagListView(this.context.getWorkspaceManager().getTagsUmodifiable());
		Bindings.bindContentBidirectional(viewTags.getTags(), this.tags);
		
		TextField txtBookName = new TextField();
		txtBookName.textProperty().bindBidirectional(this.bookName);
		
		Spinner<Integer> spnBookNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnBookNumber.setEditable(true);
		spnBookNumber.getValueFactory().valueProperty().bindBidirectional(this.bookNumber2);
		spnBookNumber.setMaxWidth(Double.MAX_VALUE);
		spnBookNumber.setPrefWidth(120);
		spnBookNumber.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		
		Spinner<Integer> spnChapterNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnChapterNumber.setEditable(true);
		spnChapterNumber.getValueFactory().valueProperty().bindBidirectional(this.chapterNumber2);
		spnChapterNumber.setMaxWidth(Double.MAX_VALUE);
		spnChapterNumber.setPrefWidth(120);
		spnChapterNumber.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		
		TextArea txtVerseText = new TextArea();
		txtVerseText.textProperty().bindBidirectional(this.verseText);
		txtVerseText.setWrapText(true);
		
		Spinner<Integer> spnVerseNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnVerseNumber.setEditable(true);
		spnVerseNumber.getValueFactory().valueProperty().bindBidirectional(this.verseNumber2);
		spnVerseNumber.setMaxWidth(Double.MAX_VALUE);
		spnVerseNumber.setPrefWidth(120);
		spnVerseNumber.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		
		Button btnBookQuickEdit = new Button(Translations.get("action.edit.bulk"));
		Button btnChapterQuickEdit = new Button(Translations.get("action.edit.bulk"));
		
		btnBookQuickEdit.setOnAction(e -> {
			this.context.executeAction(Action.BULK_EDIT_BEGIN);
		});
		
		btnChapterQuickEdit.setOnAction(e -> {
			this.context.executeAction(Action.BULK_EDIT_BEGIN);
		});
		
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(
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
		
		EditorField fldName = new EditorField(Translations.get("bible.name"), txtBibleName);
		EditorField fldLanguage = new EditorField(Translations.get("bible.language"), txtBibleLanguage);
		EditorField fldSource = new EditorField(Translations.get("bible.source"), txtBibleSource);
		EditorField fldCopyright = new EditorField(Translations.get("bible.copyright"), txtBibleCopyright);
		EditorField fldNotes = new EditorField(Translations.get("bible.notes"), txtBibleNotes, EditorField.LAYOUT_VERTICAL);
		EditorField fldTags = new EditorField(viewTags);
		EditorFieldGroup grpBible = new EditorFieldGroup(
				fldName,
				fldLanguage,
				fldSource,
				fldCopyright,
				fldNotes,
				fldTags);
		
		EditorField fldBookName = new EditorField(Translations.get("bible.book.name"), txtBookName);
		EditorField fldBookNumber = new EditorField(Translations.get("bible.book.number"), spnBookNumber);
		EditorField fldBookQuickEdit = new EditorField("", btnBookQuickEdit);
		EditorFieldGroup grpBook = new EditorFieldGroup(fldBookName, fldBookNumber, fldBookQuickEdit);
		
		EditorField fldChapterNumber = new EditorField(Translations.get("bible.chapter.number"), spnChapterNumber);
		EditorField fldCapterQuickEdit = new EditorField("", btnChapterQuickEdit);
		EditorFieldGroup grpChapter = new EditorFieldGroup(fldChapterNumber, fldCapterQuickEdit);
		
		EditorField fldVerseNumber = new EditorField(Translations.get("bible.verse.number"), spnVerseNumber);
		EditorField fldVerseText = new EditorField(Translations.get("bible.verse.text"), txtVerseText, EditorField.LAYOUT_VERTICAL);
		EditorFieldGroup grpVerse = new EditorFieldGroup(fldVerseNumber, fldVerseText);
		
		EditorTitledPane pneGeneral = new EditorTitledPane(Translations.get("bible"), grpBible);
		EditorTitledPane pneBook = new EditorTitledPane(Translations.get("bible.book"), grpBook);
		EditorTitledPane pneChapter = new EditorTitledPane(Translations.get("bible.chapter"), grpChapter);
		EditorTitledPane pneVerse = new EditorTitledPane(Translations.get("bible.verse"), grpVerse);

		this.getChildren().addAll(
				pneGeneral,
				pneBook,
				pneChapter,
				pneVerse);
		
		// hide/show
		
		BooleanBinding hasBible = this.documentContext.isNotNull();
		pneGeneral.visibleProperty().bind(hasBible);
		pneGeneral.managedProperty().bind(hasBible);
		
		this.selectedItem.addListener((obs, ov, nv) -> {
			if (nv == null || nv instanceof Bible) {
				pneGeneral.setExpanded(true);
			} else {
				pneGeneral.setExpanded(false);
			}
			
			if (nv != null && !(nv instanceof Bible)) {
				pneBook.setExpanded(true);
				pneChapter.setExpanded(true);
				pneVerse.setExpanded(true);
			}
		});
		
		ObjectBinding<Class<?>> selectedType = Bindings.createObjectBinding(() -> {
			Object item = this.selectedItem.get();
			if (item == null) return null;
			return item.getClass();
		}, this.selectedItem);
		
		pneBook.visibleProperty().bind(selectedType.isEqualTo(Book.class));
		pneBook.managedProperty().bind(pneBook.visibleProperty());
		pneBook.disableProperty().bind(this.bulkEdit);
		
		pneChapter.visibleProperty().bind(selectedType.isEqualTo(Chapter.class));
		pneChapter.managedProperty().bind(pneChapter.visibleProperty());
		pneChapter.disableProperty().bind(this.bulkEdit);
		
		pneVerse.visibleProperty().bind(selectedType.isEqualTo(Verse.class));
		pneVerse.managedProperty().bind(pneVerse.visibleProperty());
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
