package org.praisenter.ui.bible;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.PersistableComparator;
import org.praisenter.data.TextVariant;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BibleReferenceSet;
import org.praisenter.data.bible.BibleReferenceTextStore;
import org.praisenter.data.bible.BibleReferenceVerse;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.LocatedVerse;
import org.praisenter.data.bible.LocatedVerseTriplet;
import org.praisenter.data.bible.ReadOnlyBible;
import org.praisenter.data.bible.ReadOnlyBook;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Glyphs;
import org.praisenter.ui.controls.AutoCompleteComboBox;
import org.praisenter.ui.controls.WindowHelper;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public final class BibleNavigationPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final Node INVALID_CHAPTER = Glyphs.BIBLE_NAV_INVALID.duplicate();
	private static final Node INVALID_VERSE = Glyphs.BIBLE_NAV_INVALID.duplicate();
	
	// actions
	
	private static final String FIND = "FIND";
	private static final String NEXT = "NEXT";
	private static final String PREVIOUS = "PREVIOUS";
	
	// data
	
	private final ObservableList<ReadOnlyBible> bibles;
	private final ObjectProperty<ReadOnlyBible> primary;
	private final ObjectProperty<ReadOnlyBible> secondary;
	private final ObservableList<ReadOnlyBook> books;
	private final ObjectProperty<ReadOnlyBook> book;
	private final ObjectProperty<Integer> chapter;
	private final ObjectProperty<Integer> verse;
	
	// value
	
	private final ObjectProperty<BibleReferenceTextStore> value;
	private final ObjectProperty<BibleReferenceTextStore> previous;
	private final ObjectProperty<BibleReferenceTextStore> next;

	// other
	
	private final BooleanProperty valid;
	
	private Stage searchDialog;
	private boolean searchDialogFirstItemSelected = false;
	
	private boolean mutating = false;
	
	public BibleNavigationPane(GlobalContext context) {
		this.bibles = FXCollections.observableArrayList();
		this.primary = new SimpleObjectProperty<ReadOnlyBible>();
		this.secondary = new SimpleObjectProperty<ReadOnlyBible>();
		this.books = FXCollections.observableArrayList();
		this.book = new SimpleObjectProperty<ReadOnlyBook>();
		this.chapter = new SimpleObjectProperty<Integer>(1);
		this.verse = new SimpleObjectProperty<Integer>(1);
		
		this.value = new SimpleObjectProperty<BibleReferenceTextStore>();
		this.previous = new SimpleObjectProperty<BibleReferenceTextStore>();
		this.next = new SimpleObjectProperty<BibleReferenceTextStore>();
		
		this.valid = new SimpleBooleanProperty(true);
		
		ObservableList<Bible> bl = context.getWorkspaceManager().getItemsUnmodifiable(Bible.class);
		SortedList<Bible> bibles = bl.sorted(new PersistableComparator<Bible>());
		Bindings.bindContent(this.bibles, bibles);
		
		this.primary.addListener((obs, ov, nv) -> {
			ReadOnlyBook book = this.book.get();
			if (ov != null) {
				Bindings.unbindContent(this.books, ov.getBooksUnmodifiable());
			}
			if (nv != null) {
				Bindings.bindContent(this.books, nv.getBooksUnmodifiable());
				ReadOnlyBook newBook = nv.getMatchingBook(book);
				if (newBook == null && nv.getBooksUnmodifiable().size() > 0) {
					newBook = nv.getBooksUnmodifiable().get(0);
				}
				if (newBook != null) {
					this.book.set(newBook);
				}
				context.getConfiguration().setPrimaryBibleId(nv.getId());
			}
		});
		
		this.secondary.addListener((obs, ov, nv) -> {
			if (nv != null) {
				context.getConfiguration().setSecondaryBibleId(nv.getId());
			}
		});
		
		this.valid.bind(Bindings.createBooleanBinding(() -> {
			ReadOnlyBook book = this.book.get();
			if (book == null) return true;
			int cn = this.chapter.get();
			int vn = this.verse.get();
			Chapter chapter = book.getChapter(cn);
			if (chapter == null) return false;
			Verse verse = chapter.getVerse(vn);
			return verse != null;
		}, this.book, this.chapter, this.verse));
		
		Bible backupBible = null;
		if (bibles != null && bibles.size() > 0) {
			backupBible = bibles.get(0);
		}
		
		UUID primaryId = context.getConfiguration().getPrimaryBibleId();
		UUID secondaryId = context.getConfiguration().getSecondaryBibleId();
		
		Bible primaryBible = null;
		if (primaryId != null) {
			primaryBible = context.getWorkspaceManager().getItem(Bible.class, primaryId);
		}
		
		if (primaryBible == null) {
			primaryBible = backupBible;
		}
		
		Bible secondaryBible = null;
		if (secondaryId != null) {
			secondaryBible = context.getWorkspaceManager().getItem(Bible.class, secondaryId);
		}
		
		if (secondaryBible == null) {
			secondaryBible = backupBible;
		}
		
		this.primary.set(primaryBible);
		this.secondary.set(secondaryBible);
		this.value.set(new BibleReferenceTextStore());
		this.previous.set(new BibleReferenceTextStore());
		this.next.set(new BibleReferenceTextStore());
		
		ComboBox<ReadOnlyBible> cmbPrimary = new ComboBox<ReadOnlyBible>(this.bibles);
		cmbPrimary.valueProperty().bindBidirectional(this.primary);
		
		ComboBox<ReadOnlyBible> cmbSecondary = new ComboBox<ReadOnlyBible>(this.bibles);
		cmbSecondary.valueProperty().bindBidirectional(this.secondary);
		
		ComboBox<ReadOnlyBook> cmbBook = new AutoCompleteComboBox<ReadOnlyBook>(this.books, (typedText, book) -> {
			Pattern pattern = Pattern.compile("^" + Pattern.quote(typedText) + ".*", Pattern.CASE_INSENSITIVE);
			if (pattern.matcher(book.getName()).matches()) {
				return true;
			}
			return false;
		});
		cmbBook.valueProperty().bindBidirectional(this.book);
		cmbBook.setPromptText(Translations.get("bible.book.placeholder"));
		cmbBook.focusedProperty().addListener((obs) -> {
			Platform.runLater(cmbBook.getEditor()::selectAll);
		});
		
		Spinner<Integer> spnChapter = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		spnChapter.setEditable(true);
		spnChapter.setMaxWidth(65);
		spnChapter.getValueFactory().valueProperty().bindBidirectional(this.chapter);
		spnChapter.focusedProperty().addListener((obs) -> {
			Platform.runLater(spnChapter.getEditor()::selectAll);
		});
		
		Spinner<Integer> spnVerse = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		spnVerse.setEditable(true);
		spnVerse.setMaxWidth(65);
		spnVerse.getValueFactory().valueProperty().bindBidirectional(this.verse);
		spnVerse.focusedProperty().addListener((obs) -> {
			Platform.runLater(spnVerse.getEditor()::selectAll);
		});
		
		Label lblChapters = new Label();
		lblChapters.textProperty().bind(Bindings.createStringBinding(() -> {
			ReadOnlyBook book = this.book.get();
			if (book == null) return "";
			return String.valueOf(book.getChaptersUnmodifiable().size());
		}, this.book));
		lblChapters.graphicProperty().bind(Bindings.createObjectBinding(() -> {
			ReadOnlyBook book = this.book.get();
			if (book == null) return null;
			int cn = this.chapter.get();
			Chapter chapter = book.getChapter(cn);
			if (chapter == null) return INVALID_CHAPTER;
			return null;
		}, this.book, this.chapter));
		
		Label lblVerses = new Label();
		lblVerses.textProperty().bind(Bindings.createStringBinding(() -> {
			ReadOnlyBook book = this.book.get();
			if (book == null) return "";
			int cn = this.chapter.get();
			Chapter chapter = book.getChapter(cn);
			if (chapter == null) return "";
			return String.valueOf(chapter.getVerses().size());
		}, this.book, this.chapter));
		lblVerses.graphicProperty().bind(Bindings.createObjectBinding(() -> {
			ReadOnlyBook book = this.book.get();
			if (book == null) return INVALID_VERSE;
			int cn = this.chapter.get();
			Chapter chapter = book.getChapter(cn);
			if (chapter == null) return INVALID_VERSE;
			int vn = this.verse.get();
			Verse verse = chapter.getVerse(vn);
			if (verse == null) return INVALID_VERSE;
			return null;
		}, this.book, this.chapter, this.verse));
		
		Button btnFind = new Button(Translations.get("bible.nav.find"));
		btnFind.setOnMouseClicked((e) -> {
			LocatedVerseTriplet triplet = getTripletForInput(FIND);
			updateValue(triplet, e.isShortcutDown());
		});
		
		Button next = new Button(Translations.get("bible.nav.next"));
		next.setOnMouseClicked((e) -> {
			LocatedVerseTriplet triplet = getTripletForInput(NEXT);
			updateValue(triplet, e.isShortcutDown());
		});
		
		Button prev = new Button(Translations.get("bible.nav.previous"));
		prev.setOnMouseClicked((e) -> {
			LocatedVerseTriplet triplet = getTripletForInput(PREVIOUS);
			updateValue(triplet, e.isShortcutDown());
		});
		
		Button btnSearch = new Button(Translations.get("search"));
		btnSearch.setOnAction(e -> {
			if (this.searchDialog == null) {
				BibleSearchPane pneSearch = new BibleSearchPane(context);
				pneSearch.valueProperty().addListener((obs, ov, nv) -> {
					if (nv != null) {
						LocatedVerseTriplet triplet = nv.getBible().getTriplet(
								nv.getBook().getNumber(), 
								nv.getChapter().getNumber(), 
								nv.getVerse().getNumber());
						boolean isAppendEnabled = pneSearch.isAppendEnabled();
						
						// if we aren't appending, then we know they are done - they found
						// what they were looking for
						if (!isAppendEnabled) {
							this.searchDialog.hide();
						}
						
						// if we just opened the dialog and the user is in append mode
						// we want to set the current value instead of append
						if (!this.searchDialogFirstItemSelected) {
							isAppendEnabled = false;
						}
						
						// no matter what, after one value is selected set the
						// first item selected flag
						this.searchDialogFirstItemSelected = true;
						
						updateValue(triplet, isAppendEnabled);
					}
				});
				
				Window owner = this.getScene().getWindow();
				this.searchDialog = new Stage();
				this.searchDialog.initOwner(owner);
				this.searchDialog.setTitle(Translations.get("bible.search.title"));
				this.searchDialog.initModality(Modality.NONE);
				this.searchDialog.initStyle(StageStyle.UTILITY);
				this.searchDialog.setWidth(800);
				this.searchDialog.setHeight(450);
				this.searchDialog.setResizable(true);
				this.searchDialog.setScene(WindowHelper.createSceneWithOwnerCss(pneSearch, owner));
			}
			
			this.searchDialogFirstItemSelected = false;
			this.searchDialog.show();
			WindowHelper.centerOnParent(this.getScene().getWindow(), this.searchDialog);
		});
		
		this.value.addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			
			if (nv != null) {
				UUID primaryBibleId = null;
				UUID secondaryBibleId = null;
				
				int bookNumber = -1;
				int chapterNumber = -1;
				int verseNumber = -1;
				
				BibleReferenceSet brs = nv.getVariant(TextVariant.PRIMARY);
				if (brs != null) {
					Optional<BibleReferenceVerse> obrv = brs.getReferenceVerses().stream().findFirst();
					if (obrv.isPresent()) {
						BibleReferenceVerse brv = obrv.get();
						primaryBibleId = brv.getBibleId();
						bookNumber = brv.getBookNumber();
						chapterNumber = brv.getChapterNumber();
						verseNumber = brv.getVerseNumber();
					}
				}
				
				brs = nv.getVariant(TextVariant.SECONDARY);
				if (brs != null) {
					Optional<BibleReferenceVerse> obrv = brs.getReferenceVerses().stream().findFirst();
					if (obrv.isPresent()) {
						BibleReferenceVerse brv = obrv.get();
						secondaryBibleId = brv.getBibleId();
					}
				}
				
				Bible pBible = null;
				for (Bible b : bibles) {
					if (b.getId().equals(primaryBibleId)) {
						pBible = b;
						break;
					}
				}
				
				if (pBible != null) {
					this.primary.set(pBible);
					
					LocatedVerse lv = pBible.getVerse(bookNumber, chapterNumber, verseNumber);
					this.book.set(lv.getBook());
					this.chapter.set(chapterNumber);
					this.verse.set(verseNumber);
				}
				
				Bible sBible = null;
				for (Bible b : bibles) {
					if (b.getId().equals(secondaryBibleId)) {
						sBible = b;
						break;
					}
				}
				
				if (sBible != null) {
					this.secondary.set(sBible);
				}
			}
		});
		
		// LAYOUT
		
		HBox bibleRow = new HBox(5, cmbPrimary, cmbSecondary);
		
		GridPane layout = new GridPane();
		layout.setVgap(5);
		layout.setHgap(5);
		
		layout.add(bibleRow, 0, 0, 4, 1);
		
		layout.add(cmbBook, 0, 1);
		layout.add(spnChapter, 1, 1);
		layout.add(spnVerse, 2, 1);
		layout.add(btnFind, 3, 1);
		
		layout.add(prev, 1, 2);
		layout.add(next, 2, 2);
		layout.add(btnSearch, 3, 2);
		
		layout.add(lblChapters, 1, 3);
		layout.add(lblVerses, 2, 3);

		lblChapters.setAlignment(Pos.BASELINE_CENTER);
		lblVerses.setAlignment(Pos.BASELINE_CENTER);
		
		lblChapters.setMaxWidth(Double.MAX_VALUE);
		lblVerses.setMaxWidth(Double.MAX_VALUE);
		btnFind.setMaxWidth(200);
		prev.setMaxWidth(Double.MAX_VALUE);
		next.setMaxWidth(Double.MAX_VALUE);
		btnSearch.setMaxWidth(Double.MAX_VALUE);
		
		GridPane.setFillWidth(lblChapters, true);
		GridPane.setFillWidth(lblVerses, true);
		GridPane.setFillWidth(spnChapter, true);
		GridPane.setFillWidth(spnVerse, true);
		GridPane.setFillWidth(btnFind, true);
		GridPane.setFillWidth(prev, true);
		GridPane.setFillWidth(next, true);
		GridPane.setFillWidth(btnSearch, true);
		
		setCenter(layout);
		
		// set the initial value
		LocatedVerseTriplet triplet = getTripletForInput(FIND);
		updateValue(triplet, false);
	}
	
	private LocatedVerseTriplet getTripletForInput(String type) {
		ReadOnlyBible bible = this.primary.get();
		ReadOnlyBook book = this.book.get();

		int bn = book != null ? book.getNumber() : 0;
		int cn = this.chapter.get();
		int vn = this.verse.get();
		
		if (bible != null && book != null) {
			switch(type) {
				case FIND:
					try {
						return bible.getTriplet(bn, cn, vn);
					} catch (Exception ex) {
						LOGGER.warn("Failed to get verse: " + book.getName() + " " + cn + ":" + vn, ex);
					}
					break;
				case NEXT:
					try {
						return bible.getNextTriplet(bn, cn, vn);
					} catch (Exception ex) {
						LOGGER.warn("Failed to get next verse for: " + book.getName() + " " + cn + ":" + vn, ex);
					}
					break;
				case PREVIOUS:
					try {
						return bible.getPreviousTriplet(bn, cn, vn);
					} catch (Exception ex) {
						LOGGER.warn("Failed to get previous verse for: " + book.getName() + " " + cn + ":" + vn, ex);
					}
					break;
				default:
					LOGGER.warn("Unknown operation type: {}", type);
					break;
			}
		}
		
		return null;
	}
	
	private void updateValue(LocatedVerseTriplet triplet, boolean append) {
		this.mutating = true;
		
		if (triplet == null) {
			this.value.get().clear();
			this.previous.get().clear();
			this.next.get().clear();
			return;
		}
		
		// make sure the fields are updated
		this.primary.set(triplet.getCurrent().getBible());
		this.book.set(triplet.getCurrent().getBook());
		this.chapter.set(triplet.getCurrent().getChapter().getNumber());
		this.verse.set(triplet.getCurrent().getVerse().getNumber());
		
		BibleReferenceTextStore value = null;
		BibleReferenceTextStore previous = null;
		BibleReferenceTextStore next = null;
		if (!append){
			value = new BibleReferenceTextStore();
			previous = new BibleReferenceTextStore();
			next = new BibleReferenceTextStore();
		} else {
			value = this.value.get().copy();
			previous = this.value.get().copy();
			next = this.value.get().copy();
		}
		
		// update the text stores
		value.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getCurrent()));
		if (triplet.getPrevious() != null) {
			previous.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getPrevious()));
		}
		if (triplet.getNext() != null) {
			next.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getNext()));
		}
		
		// search for the secondary
		ReadOnlyBible bible2 = this.secondary.getValue();
		// only show the secondary if a different bible is chosen
		if (bible2 != null && bible2.getId() != triplet.getCurrent().getBible().getId()) {
			LocatedVerseTriplet matchingTriplet = bible2.getMatchingTriplet(triplet);
			if (matchingTriplet != null) {
				if (matchingTriplet.getCurrent() != null) {
					value.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getCurrent()));
				}
				if (matchingTriplet.getPrevious() != null) {
					previous.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getPrevious()));
				}
				if (matchingTriplet.getNext() != null) {
					next.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getNext()));
				}
			}
		}
		
		this.value.set(value);
		this.previous.set(previous);
		this.next.set(next);
		
		this.mutating = false;
	}
	
	private BibleReferenceVerse toReference(LocatedVerse verse) {
		return new BibleReferenceVerse(
				verse.getBible().getId(), 
				verse.getBible().getName(), 
				verse.getBook().getName(), 
				verse.getBook().getNumber(), 
				verse.getChapter().getNumber(), 
				verse.getVerse().getNumber(), 
				verse.getVerse().getText());
	}
	
	public BibleReferenceTextStore getValue() {
		return this.value.get();
	}
	
	public void setValue(BibleReferenceTextStore value) {
		this.value.set(value);
	}
	
	public ObjectProperty<BibleReferenceTextStore> valueProperty() {
		return this.value;
	}
	
	public BibleReferenceTextStore getPrevious() {
		return this.previous.get();
	}
	
	public void setPrevious(BibleReferenceTextStore value) {
		this.previous.set(value);
	}
	
	public ObjectProperty<BibleReferenceTextStore> previousProperty() {
		return this.previous;
	}
	
	public BibleReferenceTextStore getNext() {
		return this.next.get();
	}
	
	public void setNext(BibleReferenceTextStore value) {
		this.next.set(value);
	}
	
	public ObjectProperty<BibleReferenceTextStore> nextProperty() {
		return this.next;
	}
}
