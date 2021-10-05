package org.praisenter.ui.song;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Persistable;
import org.praisenter.data.TextVariant;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.ReadOnlyLyrics;
import org.praisenter.data.song.ReadOnlySection;
import org.praisenter.data.song.ReadOnlySong;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongReferenceTextStore;
import org.praisenter.data.song.SongReferenceVerse;
import org.praisenter.ui.EmptyItemList;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public final class SongNavigationPane extends VBox {
	private static final String SONG_NAVIGATION_CSS = "p-song-nav";
	private static final String SONG_NAVIGATION_LYRIC_SELECTION_CSS = "p-song-nav-lyric-selection";
	private static final String SONG_NAVIGATION_SECTIONS_CSS = "p-song-nav-sections";
	
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Lyrics EMPTY_LYRICS = new Lyrics();
	
	private final ObjectProperty<ReadOnlySong> song;
	private final ObservableList<ReadOnlyLyrics> lyrics;
	private final ObservableList<ReadOnlyLyrics> lyricsWithEmptyOption;
	private final ObservableList<ReadOnlySection> sections;
	
	// value

	private final ObjectProperty<SongReferenceTextStore> value;
	
	private final ObservableList<Node> sectionsToNodesMapping;
	
	private Stage searchDialog;
	
	private boolean mutating = false;
	
	public SongNavigationPane(GlobalContext context) {
		this.getStyleClass().add(SONG_NAVIGATION_CSS);
		
		this.song = new SimpleObjectProperty<>(null);
		this.lyrics = FXCollections.observableArrayList();
		this.sections = FXCollections.observableArrayList();
		this.value = new SimpleObjectProperty<SongReferenceTextStore>(new SongReferenceTextStore());

		this.lyricsWithEmptyOption = new EmptyItemList<ReadOnlyLyrics>(this.lyrics, EMPTY_LYRICS);
		
		this.song.addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.lyrics, ov.getLyricsUnmodifiable());
				this.lyrics.clear();
			}
			if (nv != null) {
				Bindings.bindContent(this.lyrics, nv.getLyricsUnmodifiable());
			}
		});
		
		ComboBox<ReadOnlyLyrics> cmbPrimaryLyrics = new ComboBox<>();
		cmbPrimaryLyrics.setPromptText(Translations.get("song.nav.primary"));
		Bindings.bindContent(cmbPrimaryLyrics.getItems(), this.lyrics);
		
		this.lyrics.addListener((Change<? extends ReadOnlyLyrics> c) -> {
			Platform.runLater(() -> {
				if (cmbPrimaryLyrics.getItems().size() > 0) {
					cmbPrimaryLyrics.setValue(cmbPrimaryLyrics.getItems().get(0));
				}
			});
		});
		cmbPrimaryLyrics.valueProperty().addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.sections, ov.getSectionsUnmodifiable());
				this.sections.clear();
			}
			if (nv != null) {
				Bindings.bindContent(this.sections, nv.getSectionsUnmodifiable());
			}
		});
		
		ComboBox<ReadOnlyLyrics> cmbSecondaryLyrics = new ComboBox<>();
		cmbSecondaryLyrics.setPromptText(Translations.get("song.nav.secondary"));
		Bindings.bindContent(cmbSecondaryLyrics.getItems(), this.lyricsWithEmptyOption);
		
		this.sectionsToNodesMapping = new MappedList<>(this.sections, (section) -> {
			Button btnSection = new Button(section.getName());
			btnSection.setMaxWidth(Double.MAX_VALUE);
//			btnSection.setMaxHeight(Double.MAX_VALUE);
//			btnSection.minHeightProperty().bind(btnSection.widthProperty().multiply(0.8));
			
			Tooltip tooltip = new Tooltip(section.getText());
			tooltip.setMaxWidth(200);
			tooltip.setWrapText(true);
			btnSection.setTooltip(tooltip);
			btnSection.setOnAction((e) -> {
				this.mutating = true;
				SongReferenceTextStore text = new SongReferenceTextStore();
				text.setVariant(TextVariant.PRIMARY, new SongReferenceVerse(
						this.song.get().getId(), 
						cmbPrimaryLyrics.getValue().getId(),
						section.getId(),
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
								secondarySection.getId(),
								secondary.getTitle(),
								secondarySection.getName(),
								secondarySection.getText()));
					}
				}
				this.value.set(text);
				this.mutating = false;
			});
			return btnSection;
		});

		Label lblSongTitle = new Label();
		lblSongTitle.textProperty().bind(Bindings.createStringBinding(() -> {
			ReadOnlySong song = this.song.get();
			if (song == null) {
				return null;
			}
			
			return Translations.get("song.nav.selected", song.getDefaultTitle());
		}, this.song));
		
		TextArea txtDescription = new TextArea();
		txtDescription.setEditable(false);
		txtDescription.setMinHeight(0);
		txtDescription.textProperty().bind(Bindings.createStringBinding(() -> {
			ReadOnlySong song = this.song.get();
			if (song == null) {
				return null;
			}
			
			return song.getNotes();
		}, this.song));
		
		final int columns = 6;
		GridPane sectionButtons = new GridPane();
		sectionButtons.getStyleClass().add(SONG_NAVIGATION_SECTIONS_CSS);
		
		// set column constraints
		for (int i = 0; i < columns; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(100.0 / columns);
			sectionButtons.getColumnConstraints().add(cc);
		}
		
		// listen for lyrics changes and reflow
		this.sectionsToNodesMapping.addListener((Change<? extends Node> c) -> {
			sectionButtons.getChildren().clear();
			
			int row = 0;
			int col = 0;
			for (Node node : c.getList()) {
				sectionButtons.add(node, col, row);
				
				col++;
				if (col == columns) {
					col = 0;
					row++;
				}
			}
		});
		
		Button btnSearch = new Button(Translations.get("search"));
		btnSearch.setOnAction(e -> {
			if (this.searchDialog == null) {
				SongSearchPane pneSearch = new SongSearchPane(context);
				pneSearch.valueProperty().addListener((obs, ov, nv) -> {
					if (nv != null) {
						this.song.set(nv.getSong());
						this.searchDialog.hide();
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
			}
			
			this.searchDialog.show();
			WindowHelper.centerOnParent(this.getScene().getWindow(), this.searchDialog);
		});
		
		// listen for edit changes
		context.getWorkspaceManager().getItemsUnmodifiable().addListener((Change<? extends Persistable> c) -> {
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
		
		this.value.addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			
			if (nv != null) {
				SongReferenceVerse srv = nv.getVariant(TextVariant.PRIMARY);
				if (srv != null) {
					Song song = context.getWorkspaceManager().getItem(Song.class, srv.getSongId());
					if (song != null) {
						this.song.set(song);
						
						ReadOnlyLyrics pLyrics = song.getLyricsById(srv.getLyricsId());
						if (pLyrics != null) {
							cmbPrimaryLyrics.setValue(pLyrics);
						}
						
						srv = nv.getVariant(TextVariant.SECONDARY);
						if (srv != null) {
							ReadOnlyLyrics sLyrics = song.getLyricsById(srv.getLyricsId());
							if (sLyrics != null) {
								cmbSecondaryLyrics.setValue(sLyrics);
							}
						}
					}
				}
			}
		});
		
		// LAYOUT
		
		GridPane pneLyrics = new GridPane();
		pneLyrics.getStyleClass().add(SONG_NAVIGATION_LYRIC_SELECTION_CSS);
		
		int row = 0;
		pneLyrics.add(lblSongTitle, 1, row, 3, 1);
		pneLyrics.add(btnSearch, 0, row, 1, 1);
		
		row++;
		pneLyrics.add(cmbPrimaryLyrics, 0, row, 2, 1);
		pneLyrics.add(cmbSecondaryLyrics, 2, row, 2, 1);
		
		for (int i = 0 ; i < 4; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(25);
			pneLyrics.getColumnConstraints().add(cc);
		}
		
		lblSongTitle.setMaxWidth(Double.MAX_VALUE);
		cmbPrimaryLyrics.setMaxWidth(Double.MAX_VALUE);
		cmbSecondaryLyrics.setMaxWidth(Double.MAX_VALUE);
		
		this.getChildren().addAll(pneLyrics, sectionButtons, txtDescription);
		
		VBox.setVgrow(sectionButtons, Priority.ALWAYS);
	}
	
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
