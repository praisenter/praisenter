package org.praisenter.ui.song;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Tag;
import org.praisenter.data.song.Author;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.Section;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongBook;
import org.praisenter.ui.Action;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.controls.EditGridPane;
import org.praisenter.ui.controls.TagListView;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentSelectionEditor;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public final class SongSelectionEditor extends VBox implements DocumentSelectionEditor<Song> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final ObjectProperty<DocumentContext<Song>> documentContext;
	
	private final ObjectProperty<Song> song;
	private final ObjectProperty<Object> selectedItem;

	private final StringProperty name;
	private final StringProperty source;
	private final StringProperty copyright;
	private final StringProperty ccliNumber;
	private final StringProperty released;
	private final StringProperty transposition;
	private final StringProperty tempo;
	private final StringProperty key;
	private final StringProperty variant;
	private final StringProperty publisher;
	private final StringProperty notes;
	private final StringProperty keywords;
	private final ObservableSet<Tag> tags;

	private final ObjectProperty<Author> selectedAuthor;
	private final StringProperty authorName;
	private final StringProperty authorType;
	
	private final ObjectProperty<SongBook> selectedSongBook;
	private final StringProperty songBookName;
	private final StringProperty songBookEntry;
	
	private final ObjectProperty<Lyrics> selectedLyrics;
	private final BooleanProperty lyricsOriginal;
	private final StringProperty lyricsLanguage;
	private final StringProperty lyricsTransliteration;
	private final StringProperty lyricsTitle;
	
	private final ObjectProperty<Section> selectedSection;
	private final StringProperty sectionName;
	private final StringProperty sectionText;
	
	public SongSelectionEditor(GlobalContext context) {
		this.context = context;
		this.documentContext = new SimpleObjectProperty<>();
		
		this.song = new SimpleObjectProperty<>();
		this.selectedItem = new SimpleObjectProperty<>();
		
		this.name = new SimpleStringProperty();
		this.source = new SimpleStringProperty();
		this.copyright = new SimpleStringProperty();
		this.ccliNumber = new SimpleStringProperty();
		this.released = new SimpleStringProperty();
		this.transposition = new SimpleStringProperty();
		this.tempo = new SimpleStringProperty();
		this.key = new SimpleStringProperty();
		this.variant = new SimpleStringProperty();
		this.publisher = new SimpleStringProperty();
		this.notes = new SimpleStringProperty();
		this.keywords = new SimpleStringProperty();
		this.tags = FXCollections.observableSet(new HashSet<>());

		this.authorName = new SimpleStringProperty();
		this.authorType = new SimpleStringProperty();
		
		this.songBookName = new SimpleStringProperty();
		this.songBookEntry = new SimpleStringProperty();
		
		this.lyricsOriginal = new SimpleBooleanProperty();
		this.lyricsLanguage = new SimpleStringProperty();
		this.lyricsTransliteration = new SimpleStringProperty();
		this.lyricsTitle = new SimpleStringProperty();
		
		this.sectionName = new SimpleStringProperty();
		this.sectionText = new SimpleStringProperty();
		
		this.documentContext.addListener((obs, ov, nv) -> {
			this.song.unbind();
			this.selectedItem.unbind();
			if (nv != null) {
				this.song.bind(nv.documentProperty());
				this.selectedItem.bind(nv.selectedItemProperty());
			} else {
				this.song.set(null);
				this.selectedItem.set(null);
			}
		});
		
		this.song.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.name.unbindBidirectional(ov.nameProperty());
				this.source.unbindBidirectional(ov.sourceProperty());
				this.copyright.unbindBidirectional(ov.copyrightProperty());
				this.ccliNumber.unbindBidirectional(ov.ccliNumberProperty());
				this.released.unbindBidirectional(ov.releasedProperty());
				this.transposition.unbindBidirectional(ov.transpositionProperty());
				this.tempo.unbindBidirectional(ov.tempoProperty());
				this.key.unbindBidirectional(ov.keyProperty());
				this.variant.unbindBidirectional(ov.variantProperty());
				this.publisher.unbindBidirectional(ov.publisherProperty());
				this.notes.unbindBidirectional(ov.notesProperty());
				this.keywords.unbindBidirectional(ov.keywordsProperty());
				Bindings.unbindContentBidirectional(this.tags, ov.getTags());
				
				this.name.set(null);
				this.source.set(null);
				this.copyright.set(null);
				this.ccliNumber.set(null);
				this.released.set(null);
				this.transposition.set(null);
				this.tempo.set(null);
				this.key.set(null);
				this.variant.set(null);
				this.publisher.set(null);
				this.notes.set(null);
				this.keywords.set(null);
				this.tags.clear();
			}
			
			if (nv != null) {
				this.name.bindBidirectional(nv.nameProperty());
				this.source.bindBidirectional(nv.sourceProperty());
				this.copyright.bindBidirectional(nv.copyrightProperty());
				this.ccliNumber.bindBidirectional(nv.ccliNumberProperty());
				this.released.bindBidirectional(nv.releasedProperty());
				this.transposition.bindBidirectional(nv.transpositionProperty());
				this.tempo.bindBidirectional(nv.tempoProperty());
				this.key.bindBidirectional(nv.keyProperty());
				this.variant.bindBidirectional(nv.variantProperty());
				this.publisher.bindBidirectional(nv.publisherProperty());
				this.notes.bindBidirectional(nv.notesProperty());
				this.keywords.bindBidirectional(nv.keywordsProperty());
				Bindings.bindContentBidirectional(this.tags, nv.getTags());
			}
		});
		
		this.selectedAuthor = new SimpleObjectProperty<>();
		this.selectedAuthor.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.authorName.unbindBidirectional(ov.nameProperty());
				this.authorType.unbindBidirectional(ov.typeProperty());
				
				this.authorName.set(null);
				this.authorType.set(null);
			}
			
			if (nv != null) {
				this.authorName.bindBidirectional(nv.nameProperty());
				this.authorType.bindBidirectional(nv.typeProperty());
			}
		});
		
		this.selectedSongBook = new SimpleObjectProperty<>();
		this.selectedSongBook.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.songBookName.unbindBidirectional(ov.nameProperty());
				this.songBookEntry.unbindBidirectional(ov.entryProperty());
				
				this.songBookName.set(null);
				this.songBookEntry.set(null);
			}
			if (nv != null) {
				this.songBookName.bindBidirectional(nv.nameProperty());
				this.songBookEntry.bindBidirectional(nv.entryProperty());
			}
		});
		
		this.selectedLyrics = new SimpleObjectProperty<>();
		this.selectedLyrics.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.lyricsOriginal.unbindBidirectional(ov.originalProperty());
				this.lyricsLanguage.unbindBidirectional(ov.languageProperty());
				this.lyricsTransliteration.unbindBidirectional(ov.transliterationProperty());
				this.lyricsTitle.unbindBidirectional(ov.titleProperty());

				this.lyricsOriginal.set(false);
				this.lyricsLanguage.set(null);
				this.lyricsTitle.set(null);
				this.lyricsTransliteration.set(null);
			}
			if (nv != null) {
				this.lyricsOriginal.bindBidirectional(nv.originalProperty());
				this.lyricsLanguage.bindBidirectional(nv.languageProperty());
				this.lyricsTransliteration.bindBidirectional(nv.transliterationProperty());
				this.lyricsTitle.bindBidirectional(nv.titleProperty());
			}
		});
		
		this.selectedSection = new SimpleObjectProperty<>();
		this.selectedSection.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.sectionName.unbindBidirectional(ov.nameProperty());
				this.sectionText.unbindBidirectional(ov.textProperty());
				
				this.sectionName.set(null);
				this.sectionText.set(null);
			}
			if (nv != null) {
				this.sectionName.bindBidirectional(nv.nameProperty());
				this.sectionText.bindBidirectional(nv.textProperty());
			}
		});
		
		this.selectedItem.addListener((obs, ov, nv) -> {
			// handle selection of a tree item
			this.selectedAuthor.set(null);
			this.selectedSongBook.set(null);
			this.selectedLyrics.set(null);
			this.selectedSection.set(null);

			if (nv == null || nv instanceof Song) {
				// do nothing
			} else if (nv instanceof Author) {
				this.selectedAuthor.set((Author)nv);
			} else if (nv instanceof SongBook) {
				this.selectedSongBook.set((SongBook)nv);
			} else if (nv instanceof Lyrics) {
				this.selectedLyrics.set((Lyrics)nv);
			} else if (nv instanceof Section) {
				this.selectedSection.set((Section)nv);
			} else if (nv instanceof Container) {
				// ignore
			} else {
				LOGGER.warn("The selected item was not a recognized type {}", nv.getClass());
			}
		});
		
		// UI
		
		Label lblSongName = new Label(Translations.get("item.name"));
		TextField txtSongName = new TextField();
		txtSongName.textProperty().bindBidirectional(this.name);

		Label lblSongSource = new Label(Translations.get("item.source"));
		TextField txtSongSource = new TextField();
		txtSongSource.textProperty().bindBidirectional(this.source);

		Label lblSongCopyright = new Label(Translations.get("item.copyright"));
		TextField txtSongCopyright = new TextField();
		txtSongCopyright.textProperty().bindBidirectional(this.copyright);

		Label lblSongCCLINumber = new Label(Translations.get("song.ccli"));
		TextField txtSongCCLINumber = new TextField();
		txtSongCCLINumber.textProperty().bindBidirectional(this.ccliNumber);
		
		Label lblSongReleased = new Label(Translations.get("song.released"));
		TextField txtSongReleased = new TextField();
		txtSongReleased.textProperty().bindBidirectional(this.released);
		
		Label lblSongTransposition = new Label(Translations.get("song.transposition"));
		TextField txtSongTransposition = new TextField();
		txtSongTransposition.textProperty().bindBidirectional(this.transposition);
		
		Label lblSongTempo = new Label(Translations.get("song.tempo"));
		TextField txtSongTempo = new TextField();
		txtSongTempo.textProperty().bindBidirectional(this.tempo);
		
		Label lblSongKey = new Label(Translations.get("song.key"));
		TextField txtSongKey = new TextField();
		txtSongKey.textProperty().bindBidirectional(this.key);
		
		Label lblSongVariant = new Label(Translations.get("song.variant"));
		TextField txtSongVariant = new TextField();
		txtSongVariant.textProperty().bindBidirectional(this.variant);
		
		Label lblSongPublisher = new Label(Translations.get("song.publisher"));
		TextField txtSongPublisher = new TextField();
		txtSongPublisher.textProperty().bindBidirectional(this.publisher);
		
		Label lblSongKeywords = new Label(Translations.get("song.keywords"));
		TextField txtSongKeywords = new TextField();
		txtSongKeywords.textProperty().bindBidirectional(this.keywords);
		
		Label lblSongNotes = new Label(Translations.get("item.notes"));
		TextArea txtSongNotes = new TextArea();
		txtSongNotes.textProperty().bindBidirectional(this.notes);
		txtSongNotes.setWrapText(true);

		TagListView viewTags = new TagListView(this.context.getDataManager().getTagsUmodifiable());
		Bindings.bindContentBidirectional(viewTags.getTags(), this.tags);
		
		// author
		
		Label lblAuthorName = new Label(Translations.get("song.lyrics.author.name"));
		TextField txtAuthorName = new TextField();
		txtAuthorName.textProperty().bindBidirectional(this.authorName);
		
		Label lblAuthorType = new Label(Translations.get("song.lyrics.author.type"));
		TextField txtAuthorType = new TextField();
		txtAuthorType.textProperty().bindBidirectional(this.authorType);
		
		// songbook
		
		Label lblSongBookName = new Label(Translations.get("song.lyrics.songbook.name"));
		TextField txtSongBookName = new TextField();
		txtSongBookName.textProperty().bindBidirectional(this.songBookName);
		
		Label lblSongBookEntry = new Label(Translations.get("song.lyrics.songbook.entry"));
		TextField txtSongBookEntry = new TextField();
		txtSongBookEntry.textProperty().bindBidirectional(this.songBookEntry);
		
		// lyrics
		
		Label lblLyricsOrginal = new Label(Translations.get("song.lyrics.original"));
		CheckBox chkLyricsOriginal = new CheckBox();
		chkLyricsOriginal.selectedProperty().bindBidirectional(this.lyricsOriginal);
		
		Label lblLyricsLanguage = new Label(Translations.get("item.language"));
		TextField txtLyricsLanguage = new TextField();
		txtLyricsLanguage.textProperty().bindBidirectional(this.lyricsLanguage);
		
		Label lblLyricsTransliteration = new Label(Translations.get("song.lyrics.transliteration"));
		TextField txtLyricsTransliteration = new TextField();
		txtLyricsTransliteration.textProperty().bindBidirectional(this.lyricsTransliteration);
		
		Label lblLyricsTitle = new Label(Translations.get("song.lyrics.title"));
		TextField txtLyricsTitle = new TextField();
		txtLyricsTitle.textProperty().bindBidirectional(this.lyricsTitle);
		
		// section
		
		Label lblSectionName = new Label(Translations.get("song.lyrics.section.name"));
		TextField txtSectionName = new TextField();
		txtSectionName.textProperty().bindBidirectional(this.sectionName);
		
		Label lblSectionText = new Label(Translations.get("song.lyrics.section.text"));
		TextArea txtSectionText = new TextArea();
		txtSectionText.textProperty().bindBidirectional(this.sectionText);
		txtSectionText.setWrapText(true);
				
		Button btnLyricsQuickEdit = new Button(Translations.get("action.edit.bulk"));
		
		btnLyricsQuickEdit.setOnAction(e -> {
			this.context.executeAction(Action.BULK_EDIT);
		});
		
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(
				txtAuthorName,
				txtAuthorType,
				txtLyricsLanguage,
				txtLyricsTitle,
				txtLyricsTransliteration,
				txtSectionName,
				txtSectionText,
				txtSongBookEntry,
				txtSongBookName,
				txtSongCCLINumber,
				txtSongCopyright,
				txtSongKey,
				txtSongKeywords,
				txtSongName,
				txtSongNotes,
				txtSongPublisher,
				txtSongReleased,
				txtSongSource,
				txtSongTempo,
				txtSongTransposition,
				txtSongVariant);
		
		int row = 0;
		EditGridPane songGrid = new EditGridPane();
		songGrid.add(lblSongName, 0, row); songGrid.add(txtSongName, 1, row++);
		songGrid.add(lblSongSource, 0, row); songGrid.add(txtSongSource, 1, row++);
		songGrid.add(lblSongCopyright, 0, row); songGrid.add(txtSongCopyright, 1, row++);
		songGrid.add(lblSongCCLINumber, 0, row); songGrid.add(txtSongCCLINumber, 1, row++);
		songGrid.add(lblSongReleased, 0, row); songGrid.add(txtSongReleased, 1, row++);
		songGrid.add(lblSongTransposition, 0, row); songGrid.add(txtSongTransposition, 1, row++);
		songGrid.add(lblSongTempo, 0, row); songGrid.add(txtSongTempo, 1, row++);
		songGrid.add(lblSongKey, 0, row); songGrid.add(txtSongKey, 1, row++);
		songGrid.add(lblSongVariant, 0, row); songGrid.add(txtSongVariant, 1, row++);
		songGrid.add(lblSongPublisher, 0, row); songGrid.add(txtSongPublisher, 1, row++);
		songGrid.add(lblSongKeywords, 0, row); songGrid.add(txtSongKeywords, 1, row++);
		songGrid.add(lblSongNotes, 0, row++, 2);
		songGrid.add(txtSongNotes, 0, row++, 2);
		songGrid.add(viewTags, 0, row++, 2);
		songGrid.setPadding(new Insets(5));
		TitledPane ttlSong = new TitledPane(Translations.get("song"), songGrid);
		ttlSong.setAnimated(false);
//		ttlSong.setCollapsible(false);
		
		row = 0;
		EditGridPane selectionGrid = new EditGridPane();
		selectionGrid.add(lblAuthorName, 0, row); selectionGrid.add(txtAuthorName, 1, row++);
		selectionGrid.add(lblAuthorType, 0, row); selectionGrid.add(txtAuthorType, 1, row++);
		
		selectionGrid.add(lblSongBookName, 0, row); selectionGrid.add(txtSongBookName, 1, row++);
		selectionGrid.add(lblSongBookEntry, 0, row); selectionGrid.add(txtSongBookEntry, 1, row++);

		selectionGrid.add(lblLyricsTitle, 0, row); selectionGrid.add(txtLyricsTitle, 1, row++);
		selectionGrid.add(lblLyricsLanguage, 0, row); selectionGrid.add(txtLyricsLanguage, 1, row++);
		selectionGrid.add(lblLyricsTransliteration, 0, row); selectionGrid.add(txtLyricsTransliteration, 1, row++);
		selectionGrid.add(lblLyricsOrginal, 0, row); selectionGrid.add(chkLyricsOriginal, 1, row++);
		selectionGrid.add(btnLyricsQuickEdit, 0, row++, 2);
		
		selectionGrid.add(lblSectionName, 0, row); selectionGrid.add(txtSectionName, 1, row++);
		selectionGrid.add(lblSectionText, 0, row++, 2);
		selectionGrid.add(txtSectionText, 0, row++, 2);
		selectionGrid.setPadding(new Insets(5));
		
		TitledPane ttlSelection = new TitledPane("", selectionGrid);
		ttlSelection.setAnimated(false);
//		ttlSelection.setCollapsible(false);
		ttlSelection.textProperty().bind(Bindings.createStringBinding(() -> {
			Object item = this.selectedItem.get();
			if (item == null || item instanceof Song) {
				return "";
			} else if (item instanceof SongBook) {
				return Translations.get("song.lyrics.songbook");
			} else if (item instanceof Author) {
				return Translations.get("song.lyrics.author");
			} else if (item instanceof Lyrics) {
				return Translations.get("song.lyrics");
			} else if (item instanceof Section) {
				return Translations.get("song.lyrics.section");
			}
			return "";
		}, this.selectedItem));
		
		this.selectedItem.addListener((obs, ov, nv) -> {
//			selectionGrid.hideRows(0,1,2,3,4,5);
			if (nv == null || nv instanceof Song) {
				
			} else if (nv instanceof Author) {
				selectionGrid.showRowsOnly(0,1);
			} else if (nv instanceof SongBook) {
				selectionGrid.showRowsOnly(2,3);
			} else if (nv instanceof Lyrics) {
				selectionGrid.showRowsOnly(4,5,6,7,8);
			} else if (nv instanceof Section) {
				selectionGrid.showRowsOnly(9,10,11);
			}
		});
		
//		TitledPane ttlBook = new TitledPane(Translations.get("bible.book"), new VBox(
//				lblBookNumber, spnBookNumber,
//				lblBookName, txtBookName));
//
//		TitledPane ttlChapter = new TitledPane(Translations.get("bible.chapter"), new VBox(
//				lblChapterNumber, spnChapterNumber));
//		
//		TitledPane ttlVerse = new TitledPane(Translations.get("bible.verse"), new VBox(
//				lblVerseNumber, spnVerseNumber,
//				lblVerseText, txtVerseText));
		
		ScrollPane scroller = new ScrollPane(new VBox(
				ttlSong,
				ttlSelection));
		scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroller.setFitToWidth(true);
		
		this.getChildren().addAll(scroller);
		
		// hide/show
		
		BooleanBinding hasSong = this.documentContext.isNotNull();
		ttlSong.visibleProperty().bind(hasSong);
		ttlSong.managedProperty().bind(hasSong);
		
		BooleanBinding selectionEditingEnabled = Bindings.createBooleanBinding(() -> {
			Object item = this.selectedItem.get();
			if (item != null && (item instanceof Author ||
					item instanceof SongBook ||
					item instanceof Lyrics ||
					item instanceof Section)) return true;
			return false;
		}, this.selectedItem);
		ttlSelection.visibleProperty().bind(selectionEditingEnabled);
		ttlSelection.managedProperty().bind(selectionEditingEnabled);
	}
	
	public DocumentContext<Song> getDocumentContext() {
		return this.documentContext.get();
	}
	
	public void setDocumentContext(DocumentContext<Song> ctx) {
		this.documentContext.set(ctx);
	}
	
	public ObjectProperty<DocumentContext<Song>> documentContextProperty() {
		return this.documentContext;
	}
}
