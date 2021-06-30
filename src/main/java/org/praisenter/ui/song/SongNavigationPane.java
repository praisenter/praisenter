package org.praisenter.ui.song;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Persistable;
import org.praisenter.data.TextVariant;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.ReadOnlyLyrics;
import org.praisenter.data.song.ReadOnlySection;
import org.praisenter.data.song.ReadOnlySong;
import org.praisenter.data.song.Section;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongReferenceTextStore;
import org.praisenter.data.song.SongReferenceVerse;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;
import org.praisenter.ui.controls.WindowHelper;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public final class SongNavigationPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ObjectProperty<ReadOnlySong> song;
	private final ObservableList<ReadOnlyLyrics> lyrics;
	private final ObservableList<ReadOnlySection> sections;
	
	// value

	private final ObjectProperty<SongReferenceTextStore> value;
	
	// nodes
	
	private final ObservableList<Node> sectionsToNodesMapping;
	
	private Stage searchDialog;
	
	public SongNavigationPane(GlobalContext context) {
		this.song = new SimpleObjectProperty<>(null);
		this.lyrics = FXCollections.observableArrayList();
		this.sections = FXCollections.observableArrayList();
		this.value = new SimpleObjectProperty<SongReferenceTextStore>(new SongReferenceTextStore());

		this.song.addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.lyrics, ov.getLyricsUnmodifiable());
			}
			if (nv != null) {
				Bindings.bindContent(this.lyrics, nv.getLyricsUnmodifiable());
			}
		});
		
		ComboBox<ReadOnlyLyrics> cmbPrimaryLyrics = new ComboBox<>();
		Bindings.bindContent(cmbPrimaryLyrics.getItems(), this.lyrics);
		
		cmbPrimaryLyrics.getItems().addListener((Change<? extends ReadOnlyLyrics> c) -> {
			if (c.next()) {
				Platform.runLater(() -> {
					cmbPrimaryLyrics.setValue(cmbPrimaryLyrics.getItems().get(0));
				});
			}
		});
		cmbPrimaryLyrics.valueProperty().addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.sections, ov.getSectionsUnmodifiable());
			}
			if (nv != null) {
				Bindings.bindContent(this.sections, nv.getSectionsUnmodifiable());
			}
		});
		
		ComboBox<ReadOnlyLyrics> cmbSecondaryLyrics = new ComboBox<>();
		Bindings.bindContent(cmbSecondaryLyrics.getItems(), this.lyrics);
		
		this.sectionsToNodesMapping = new MappedList<>(this.sections, (section) -> {
			Button btnSection = new Button(section.getName());
			Tooltip tooltip = new Tooltip(section.getText());
			tooltip.setMaxWidth(200);
			tooltip.setWrapText(true);
			btnSection.setTooltip(tooltip);
			btnSection.setOnMouseClicked((e) -> {
				SongReferenceTextStore text = new SongReferenceTextStore();
				text.setVariant(TextVariant.PRIMARY, new SongReferenceVerse(
						this.song.get().getId(), 
						cmbPrimaryLyrics.getValue().getId(), 
						cmbPrimaryLyrics.getValue().getTitle(),
						section.getName(),
						section.getText()));
				ReadOnlyLyrics secondary = cmbSecondaryLyrics.getValue();
				if (secondary != null) {
					// try to find the secondary section based on the first
					ReadOnlySection secondarySection = secondary.getSectionByName(section.getName());
					if (secondarySection != null) {
						text.setVariant(TextVariant.SECONDARY, new SongReferenceVerse(
								this.song.get().getId(), 
								secondary.getId(), 
								secondary.getTitle(),
								secondarySection.getName(),
								secondarySection.getText()));
					}
				}
				this.value.set(text);
			});
			return btnSection;
		});
		
		FlowPane sectionButtons = new FlowPane();
		Bindings.bindContent(sectionButtons.getChildren(), sectionsToNodesMapping);
		
		Button btnSearch = new Button(Translations.get("search"));
		btnSearch.setOnAction(e -> {
			if (this.searchDialog == null) {
				SongSearchPane pneSearch = new SongSearchPane(context);
				pneSearch.valueProperty().addListener((obs, ov, nv) -> {
					if (nv != null) {
						this.song.set(nv.getSong());
					}
				});
				
				Window owner = this.getScene().getWindow();
				this.searchDialog = new Stage();
				this.searchDialog.initOwner(owner);
				this.searchDialog.setTitle(Translations.get("song.search.title"));
				this.searchDialog.initModality(Modality.NONE);
				this.searchDialog.initStyle(StageStyle.UTILITY);
				this.searchDialog.setWidth(800);
				this.searchDialog.setHeight(450);
				this.searchDialog.setResizable(true);
				this.searchDialog.setScene(WindowHelper.createSceneWithOwnerCss(pneSearch, owner));
				this.searchDialog.setOnCloseRequest(evt -> pneSearch.clear());
			}
			
			this.searchDialog.show();
			WindowHelper.centerOnParent(this.getScene().getWindow(), this.searchDialog);
		});
		
		// listen for edit changes
		context.getDataManager().getItemsUnmodifiable().addListener((Change<? extends Persistable> c) -> {
			ReadOnlySong song = this.song.get();
			if (song != null) {
				while (c.next()) {
					if (c.wasAdded()) {
						for (Persistable p : c.getAddedSubList()) {
							if (p.identityEquals(song)) {
								// it was updated (so update it here)
								this.song.set(null);
								this.song.set((ReadOnlySong)p);
								return;
							}
						}
					}
					
					if (c.wasRemoved()) {
						for (Persistable p : c.getRemoved()) {
							if (p.identityEquals(song)) {
								// it was removed, so remove it here
								this.song.set(null);
								return;
							}
						}
					}
				}
			}
		});
		
		// LAYOUT
		
		HBox bibleRow = new HBox(5, cmbPrimaryLyrics, cmbSecondaryLyrics, btnSearch);
		
//		GridPane layout = new GridPane();
//		layout.setVgap(5);
//		layout.setHgap(5);
//		
//		layout.add(bibleRow, 0, 0, 4, 1);
//		
//		layout.add(cmbBook, 0, 1);
//		layout.add(spnChapter, 1, 1);
//		layout.add(spnVerse, 2, 1);
//		layout.add(btnFind, 3, 1);
//		
//		layout.add(prev, 1, 2);
//		layout.add(next, 2, 2);
//		layout.add(btnSearch, 3, 2);
//		
//		layout.add(lblChapters, 1, 3);
//		layout.add(lblVerses, 2, 3);
//
//		lblChapters.setAlignment(Pos.BASELINE_CENTER);
//		lblVerses.setAlignment(Pos.BASELINE_CENTER);
//		
//		lblChapters.setMaxWidth(Double.MAX_VALUE);
//		lblVerses.setMaxWidth(Double.MAX_VALUE);
//		btnFind.setMaxWidth(200);
//		prev.setMaxWidth(Double.MAX_VALUE);
//		next.setMaxWidth(Double.MAX_VALUE);
//		btnSearch.setMaxWidth(Double.MAX_VALUE);
//		
//		GridPane.setFillWidth(lblChapters, true);
//		GridPane.setFillWidth(lblVerses, true);
//		GridPane.setFillWidth(spnChapter, true);
//		GridPane.setFillWidth(spnVerse, true);
//		GridPane.setFillWidth(btnFind, true);
//		GridPane.setFillWidth(prev, true);
//		GridPane.setFillWidth(next, true);
//		GridPane.setFillWidth(btnSearch, true);
		
		this.setTop(bibleRow);
		this.setCenter(sectionButtons);
		
//		// set the initial value
//		LocatedVerseTriplet triplet = getTripletForInput(FIND);
//		updateValue(triplet, false);
	}
//	
//	private LocatedVerseTriplet getTripletForInput(String type) {
//		ReadOnlyBible bible = this.primary.get();
//		ReadOnlyBook book = this.book.get();
//
//		int bn = book != null ? book.getNumber() : 0;
//		int cn = this.chapter.get();
//		int vn = this.verse.get();
//		
//		if (bible != null && book != null) {
//			switch(type) {
//				case FIND:
//					try {
//						return bible.getTriplet(bn, cn, vn);
//					} catch (Exception ex) {
//						LOGGER.warn("Failed to get verse: " + book.getName() + " " + cn + ":" + vn, ex);
//					}
//					break;
//				case NEXT:
//					try {
//						return bible.getNextTriplet(bn, cn, vn);
//					} catch (Exception ex) {
//						LOGGER.warn("Failed to get next verse for: " + book.getName() + " " + cn + ":" + vn, ex);
//					}
//					break;
//				case PREVIOUS:
//					try {
//						return bible.getPreviousTriplet(bn, cn, vn);
//					} catch (Exception ex) {
//						LOGGER.warn("Failed to get previous verse for: " + book.getName() + " " + cn + ":" + vn, ex);
//					}
//					break;
//				default:
//					LOGGER.warn("Unknown operation type: {}", type);
//					break;
//			}
//		}
//		
//		return null;
//	}
//	
//	private void updateValue(LocatedVerseTriplet triplet, boolean append) {
//		if (triplet == null) {
//			this.value.get().clear();
//			this.previous.get().clear();
//			this.next.get().clear();
//			return;
//		}
//		
//		// make sure the fields are updated
//		this.primary.set(triplet.getCurrent().getBible());
//		this.book.set(triplet.getCurrent().getBook());
//		this.chapter.set(triplet.getCurrent().getChapter().getNumber());
//		this.verse.set(triplet.getCurrent().getVerse().getNumber());
//		
//		BibleReferenceTextStore value = null;
//		BibleReferenceTextStore previous = null;
//		BibleReferenceTextStore next = null;
//		if (!append){
//			value = new BibleReferenceTextStore();
//			previous = new BibleReferenceTextStore();
//			next = new BibleReferenceTextStore();
//		} else {
//			value = this.value.get().copy();
//			previous = this.value.get().copy();
//			next = this.value.get().copy();
//		}
//		
//		// update the text stores
//		value.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getCurrent()));
//		if (triplet.getPrevious() != null) {
//			previous.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getPrevious()));
//		}
//		if (triplet.getNext() != null) {
//			next.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(toReference(triplet.getNext()));
//		}
//		
//		// search for the secondary
//		ReadOnlyBible bible2 = this.secondary.getValue();
//		// only show the secondary if a different bible is chosen
//		if (bible2 != null && bible2.getId() != triplet.getCurrent().getBible().getId()) {
//			LocatedVerseTriplet matchingTriplet = bible2.getMatchingTriplet(triplet);
//			if (matchingTriplet != null) {
//				if (matchingTriplet.getCurrent() != null) {
//					value.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getCurrent()));
//				}
//				if (matchingTriplet.getPrevious() != null) {
//					previous.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getPrevious()));
//				}
//				if (matchingTriplet.getNext() != null) {
//					next.getVariant(TextVariant.SECONDARY).getReferenceVerses().add(toReference(matchingTriplet.getNext()));
//				}
//			}
//		}
//		
//		this.value.set(value);
//		this.previous.set(previous);
//		this.next.set(next);
//	}
//	
//	private BibleReferenceVerse toReference(LocatedVerse verse) {
//		return new BibleReferenceVerse(
//				verse.getBible().getId(), 
//				verse.getBible().getName(), 
//				verse.getBook().getName(), 
//				verse.getBook().getNumber(), 
//				verse.getChapter().getNumber(), 
//				verse.getVerse().getNumber(), 
//				verse.getVerse().getText());
//	}
	
	public SongReferenceTextStore getValue() {
		return this.value.get();
	}
	
	public void setValue(SongReferenceTextStore value) {
		this.value.set(value);
	}
	
	public ObjectProperty<SongReferenceTextStore> valueProperty() {
		return this.value;
	}
}
