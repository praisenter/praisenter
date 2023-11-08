package org.praisenter.ui.library;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.Persistable;
import org.praisenter.data.Tag;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.song.Song;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.controls.MediaPreview;
import org.praisenter.ui.controls.RowVisGridPane;
import org.praisenter.ui.controls.TagListView;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideView;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.Formatter;

import atlantafx.base.theme.Styles;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.HPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;

final class LibraryItemDetails extends VBox {
	private static final String LIBRARY_ITEM_DETAILS_CSS = "p-library-item-details";
	private static final String LIBRARY_ITEM_DETAILS_VALUE_LIST_CSS = "p-library-item-details-value-list";
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	
	// data
	
	private final ObjectProperty<Persistable> item;
	
	// standard data
	
	private final StringProperty name;
	private final ObjectProperty<Instant> modified;
	private final ObjectProperty<Instant> created;
	private final IntegerProperty width;
	private final IntegerProperty height;
	private final LongProperty length;
	private final LongProperty size;
	private final ObservableSet<Tag> tags;
	
	// bible data
	
	private final StringProperty bibleLanguage;
	private final StringProperty bibleSource;
	private final StringProperty bibleCopyright;
	
	// media data
	
	private final BooleanProperty mediaAudio;
	
	// song data
	
	private final StringProperty songSource;
	private final StringProperty songCopyright;
	private final StringProperty songCCLINumber;
	private final StringProperty songReleased;
	private final StringProperty songPublisher;
	private final StringProperty songKeywords;
	
	// UI
	
	/* helper for async loaded slides */
	private Slide mostRecentSlide;
	
	private final SetChangeListener<Tag> tagListener;
	
	public LibraryItemDetails(GlobalContext context) {
		this.context = context;
		this.item = new SimpleObjectProperty<>();
		
		// standard data
		// name, modified, created, tags
		this.name = new SimpleStringProperty();
		this.modified = new SimpleObjectProperty<>();
		this.created = new SimpleObjectProperty<>();
		this.width = new SimpleIntegerProperty();
		this.height = new SimpleIntegerProperty();
		this.length = new SimpleLongProperty();
		this.size = new SimpleLongProperty();
		this.tags = FXCollections.observableSet(new HashSet<>());
		this.tagListener = (change) -> {
			Persistable p = this.item.get();
			if (p != null) {
				Persistable pc = p.copy();
				if (change.wasAdded()) {
					pc.getTags().add(change.getElementAdded());
				} else if (change.wasRemoved()) {
					pc.getTags().remove(change.getElementRemoved());
				}
				this.onTagsChanged(pc);
			}
		};
		
		// bible data
		// language, source, copyright
		this.bibleLanguage = new SimpleStringProperty();
		this.bibleSource = new SimpleStringProperty();
		this.bibleCopyright = new SimpleStringProperty();

		// song data
		this.songSource = new SimpleStringProperty();
		this.songCopyright = new SimpleStringProperty();
		this.songCCLINumber = new SimpleStringProperty();
		this.songReleased = new SimpleStringProperty();
		this.songPublisher = new SimpleStringProperty();
		this.songKeywords = new SimpleStringProperty();
		
		// media data
		// hasAudio, preview
		this.mediaAudio = new SimpleBooleanProperty();
		
		this.item.addListener((obs, ov, nv) -> {
			this.name.unbind();
			this.modified.unbind();
			this.created.unbind();
			this.width.unbind();
			this.height.unbind();
			this.length.unbind();
			this.size.unbind();
			
			this.bibleLanguage.unbind();
			this.bibleSource.unbind();
			this.bibleCopyright.unbind();
			
			this.songCCLINumber.unbind();
			this.songCopyright.unbind();
			this.songKeywords.unbind();
			this.songPublisher.unbind();
			this.songReleased.unbind();
			this.songSource.unbind();
			
			this.mediaAudio.unbind();
			
			this.name.set(null);
			
			if (ov != null) {
				//Bindings.unbindContentBidirectional(this.tags, ov.getTags());
				this.tags.removeListener(this.tagListener);
				this.tags.clear();
			}
			
			if (nv != null) {
				this.name.bind(nv.nameProperty());
				this.modified.bind(nv.modifiedDateProperty());
				this.created.bind(nv.createdDateProperty());
				this.size.setValue(this.getFileSize(nv));
				//Bindings.bindContentBidirectional(this.tags, nv.getTags());
				this.tags.addAll(nv.getTags());
				this.tags.addListener(this.tagListener);
				
				if (nv instanceof Bible) {
					Bible bible = (Bible)nv;
					this.bibleCopyright.bind(bible.copyrightProperty());
					this.bibleLanguage.bind(bible.languageProperty());
					this.bibleSource.bind(bible.sourceProperty());
				} else if (nv instanceof Song) {
					Song song = (Song)nv;
					this.songCCLINumber.bind(song.ccliNumberProperty());
					this.songCopyright.bind(song.copyrightProperty());
					this.songKeywords.bind(song.keywordsProperty());
					this.songPublisher.bind(song.publisherProperty());
					this.songReleased.bind(song.releasedProperty());
					this.songSource.bind(song.sourceProperty());
				} else if (nv instanceof Media) {
					Media media = (Media)nv;
					this.mediaAudio.bind(media.audioAvailableProperty());
					this.height.bind(media.heightProperty());
					this.length.bind(media.lengthProperty());
					this.size.bind(media.sizeProperty());
					this.width.bind(media.widthProperty());
				} else if (nv instanceof Slide) {
					Slide slide = (Slide)nv;
					this.width.bind(slide.widthProperty());
					this.height.bind(slide.heightProperty());
					this.length.bind(slide.timeProperty());
				}
			}
		});
		
		// label section
		Label lblNameValue = new Label();
		Label lblModifiedValue = new Label();
		Label lblCreatedValue = new Label();
		Label lblWidthValue = new Label();
		Label lblHeightValue = new Label();
		Label lblSizeValue = new Label();
		Label lblLengthValue = new Label();
		
		TagListView viewTags = new TagListView(this.context.getWorkspaceManager().getTagsUmodifiable());
		Bindings.bindContentBidirectional(viewTags.getTags(), this.tags);
		
		Label lblBibleLanguageValue = new Label();
		Label lblBibleSourceValue = new Label();
		Label lblBibleCopyrightValue = new Label();
		
		Label lblMediaAudioValue = new Label();
		
		Label lblSongSourceValue = new Label();
		Label lblSongCopyrightValue = new Label();
		Label lblSongCCLINumberValue = new Label();
		Label lblSongReleasedValue = new Label();
		Label lblSongPublisherValue = new Label();
		Label lblSongKeywordsValue = new Label();
		
		lblNameValue.textProperty().bind(this.name);
		lblNameValue.getStyleClass().add(Styles.TITLE_4);
		
		lblModifiedValue.textProperty().bind(Bindings.createStringBinding(() -> {
			Instant dt = this.modified.get();
			if (dt == null) return null;
			return dt.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
		}, this.modified));
		lblCreatedValue.textProperty().bind(Bindings.createStringBinding(() -> {
			Instant dt = this.created.get();
			if (dt == null) return null;
			return dt.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
		}, this.created));
		lblWidthValue.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf((int)this.width.get()), this.width));
		lblHeightValue.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf((int)this.height.get()), this.height));
		lblSizeValue.textProperty().bind(Bindings.createStringBinding(() -> Formatter.getSizeFormattedString(this.size.get()), this.size));
		lblLengthValue.textProperty().bind(Bindings.createStringBinding(() -> {
			Persistable item = this.item.get();
			if (item == null) return null;
			if (item instanceof Slide) {
				return Formatter.getMillisecondsFormattedString(this.length.get());
			}
			return Formatter.getSecondsFormattedString(this.length.get());	
		}, this.length));
		
		lblBibleLanguageValue.textProperty().bind(this.bibleLanguage);
		lblBibleSourceValue.textProperty().bind(this.bibleSource);
		lblBibleCopyrightValue.textProperty().bind(this.bibleCopyright);
		
		lblMediaAudioValue.textProperty().bind(Bindings.createStringBinding(() -> this.mediaAudio.get() ? Translations.get("yes") : Translations.get("no"), this.mediaAudio));
		
		lblSongCCLINumberValue.textProperty().bind(this.songCCLINumber);
		lblSongCopyrightValue.textProperty().bind(this.songCopyright);
		lblSongKeywordsValue.textProperty().bind(this.songKeywords);
		lblSongPublisherValue.textProperty().bind(this.songPublisher);
		lblSongReleasedValue.textProperty().bind(this.songReleased);
		lblSongSourceValue.textProperty().bind(this.songSource);
		
		// preview section

		// slide preview
		SlideView slide = new SlideView(context);
		slide.setClipEnabled(true);
		slide.setViewMode(SlideMode.VIEW);
		slide.managedProperty().bind(slide.visibleProperty());
		slide.setFitToWidthEnabled(true);
		slide.setViewScaleAlignCenter(false);
		slide.setVisible(false);
		slide.prefWidthProperty().bind(this.widthProperty());
		
		// preview for image
		ImageView image = new ImageView();
		image.imageProperty().bind(BindingHelper.createAsyncObjectBinding(() -> {
			Persistable item = this.item.get();
			if (item == null) return null;
			if (item instanceof Media) {
				Media media = (Media)item;
				MediaType type = media.getMediaType();
				if (type == MediaType.IMAGE) {
					return this.context.getImageCache().getOrLoadImage(media.getId(), media.getMediaImagePath());
				} else if (type == MediaType.AUDIO) {
					return this.context.getImageCache().getOrLoadClasspathImage("/org/praisenter/images/audio-default-thumbnail.png");
				}
			} else if (item instanceof Bible) {
				return this.context.getImageCache().getOrLoadClasspathImage("/org/praisenter/images/bible-icon.png");
			} else if (item instanceof Song) {
				return this.context.getImageCache().getOrLoadClasspathImage("/org/praisenter/images/song-lyrics-icon.png");
			}
			return null;
		}, this.item));
		image.setPreserveRatio(true);
		image.fitWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			double tw = this.widthProperty().get();
			Image img = image.imageProperty().get();
			if (img != null) {
				double iw = img.getWidth();
				tw = Math.min(tw, iw);
			}
			return tw;
		}, this.widthProperty(), image.imageProperty()));
		image.managedProperty().bind(image.visibleProperty());
		image.setVisible(false);

		// media player for audio/video
		MediaPreview player = new MediaPreview();
		player.setMinWidth(0);
		player.prefWidthProperty().bind(this.widthProperty());
		player.managedProperty().bind(player.visibleProperty());
		player.setVisible(false);
		player.mediaPlayerProperty().bind(Bindings.createObjectBinding(() -> {
			Persistable item = this.item.get();
			if (item instanceof Media) {
				Media media = (Media)item;
				MediaType type = media.getMediaType();
				if (type != MediaType.IMAGE) {
					try {
	        			javafx.scene.media.Media m = new javafx.scene.media.Media(media.getMediaPath().toUri().toString());
	        			Exception ex = m.getError();
	        			if (ex != null) {
	        				LOGGER.error("Error loading media " + media.getMediaPath().toAbsolutePath(), ex);
	        			} else {
	        				MediaPlayer mp = new MediaPlayer(m);
	        				ex = mp.getError();
	        				if (ex != null) {
	        					LOGGER.error("Error creating media player for " + media.getMediaPath().toAbsolutePath(), ex);
	        				}
	        				return mp;
	        			}
        			} catch (Exception ex) {
        				LOGGER.error("Error loading media " + media.getMediaPath().toAbsolutePath(), ex);
        			}
				}
			}
			return null;
		}, this.item));
		
		// make sure we dispose of the old mediaplayer when it gets replaced
		player.mediaPlayerProperty().addListener((obs, ov, nv) -> {
			if (ov != null) {
				ov.dispose();
			}
		});
		
		Label lblCreated = new Label(Translations.get("item.created"));
		Label lblModified = new Label(Translations.get("item.modified"));
		Label lblWidth = new Label(Translations.get("item.width"));
		Label lblHeight = new Label(Translations.get("item.height"));
		Label lblLength = new Label(Translations.get("item.length"));
		Label lblSize = new Label(Translations.get("item.size"));
		
		Label lblBibleLanguage = new Label(Translations.get("item.language"));
		Label lblBibleSource = new Label(Translations.get("item.source"));
		Label lblBibleCopyright = new Label(Translations.get("item.copyright"));
		
		Label lblSongSource = new Label(Translations.get("item.source"));
		Label lblSongCopyright = new Label(Translations.get("item.copyright"));
		Label lblSongCCLINumber = new Label(Translations.get("song.ccli"));
		Label lblSongKeywords = new Label(Translations.get("song.keywords"));
		Label lblSongPublisher = new Label(Translations.get("song.publisher"));
		Label lblSongReleased = new Label(Translations.get("song.released"));
		
		Label lblMediaAudio = new Label(Translations.get("media.hasAudio"));
		
		int r = 0;
		RowVisGridPane labels = new RowVisGridPane();
		labels.getStyleClass().add(LIBRARY_ITEM_DETAILS_VALUE_LIST_CSS);
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setPercentWidth(50);
		ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setPercentWidth(50);
		cc2.setHalignment(HPos.RIGHT);
		labels.getColumnConstraints().addAll(cc1, cc2);
		labels.setMaxWidth(Double.MAX_VALUE);
		
		labels.add(lblCreated, 0, r); labels.add(lblCreatedValue, 1, r++);
		labels.add(lblModified, 0, r); labels.add(lblModifiedValue, 1, r++);
		labels.add(lblWidth, 0, r); labels.add(lblWidthValue, 1, r++);
		labels.add(lblHeight, 0, r); labels.add(lblHeightValue, 1, r++);
		labels.add(lblLength, 0, r); labels.add(lblLengthValue, 1, r++);
		labels.add(lblSize, 0, r); labels.add(lblSizeValue, 1, r++);
		
		labels.add(lblBibleLanguage, 0, r); labels.add(lblBibleLanguageValue, 1, r++);
		labels.add(lblBibleSource, 0, r); labels.add(lblBibleSourceValue, 1, r++);
		labels.add(lblBibleCopyright, 0, r); labels.add(lblBibleCopyrightValue, 1, r++);
		
		labels.add(lblMediaAudio, 0, r); labels.add(lblMediaAudioValue, 1, r++);

		labels.add(lblSongSource, 0, r); labels.add(lblSongSourceValue, 1, r++);
		labels.add(lblSongCopyright, 0, r); labels.add(lblSongCopyrightValue, 1, r++);
		labels.add(lblSongPublisher, 0, r); labels.add(lblSongPublisherValue, 1, r++);
		labels.add(lblSongCCLINumber, 0, r); labels.add(lblSongCCLINumberValue, 1, r++);
		labels.add(lblSongReleased, 0, r); labels.add(lblSongReleasedValue, 1, r++);
		labels.add(lblSongKeywords, 0, r); labels.add(lblSongKeywordsValue, 1, r++);
		
		labels.add(viewTags, 0, r++, 2);
		
		labels.hideRows(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16);
		
		this.item.addListener((obs, ov, nv) -> {
			this.mostRecentSlide = null;
			slide.setSlide(null);
			slide.setVisible(false);
			image.setVisible(false);
			player.setVisible(false);
			if (nv == null) {
				labels.showRowsOnly();
			} else if (nv instanceof Slide) {
				this.mostRecentSlide = (Slide)nv;
				// load the slide async
				slide.loadSlideAsync((Slide)nv).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
					// by the time this finishes it could have been changed, so make sure
					// its the same and if so, set it up
					if (nv == this.mostRecentSlide) {
						slide.setSlide((Slide)nv);
						slide.setVisible(true);
					}
				}));
				
				labels.showRowsOnly(0,1,2,3,4,5,16);
			} else if (nv instanceof Media) {
				Media media = (Media)nv;
				if (media.getMediaType() == MediaType.IMAGE) {
					image.setVisible(true);
					labels.showRowsOnly(0,1,2,3,5,16);
				} else if (media.getMediaType() == MediaType.AUDIO) {
					image.setVisible(true);
					player.setVisible(true);
					labels.showRowsOnly(0,1,4,5,9,16);
				} else if (media.getMediaType() == MediaType.VIDEO) {
					player.setVisible(true);
					labels.showRowsOnly(0,1,2,3,4,5,9,16);
				}
			} else if (nv instanceof Bible) {
				image.setVisible(true);
				labels.showRowsOnly(0,1,5,6,7,8,16);
			} else if (nv instanceof Song) {
				image.setVisible(true);
				labels.showRowsOnly(0,1,5,10,11,12,13,14,15,16);
			} else {
				labels.showRowsOnly();
			}
		});
		
		this.getStyleClass().add(LIBRARY_ITEM_DETAILS_CSS);
		this.getChildren().addAll(
				lblNameValue
				,image
				,slide
				,player
				,labels);
	}
	
	private long getFileSize(Persistable item) {
		try {
			return Files.size(context.getWorkspaceManager().getFilePath(item));
		} catch (IOException e) {
			LOGGER.warn("Failed to retrieve file size for item '" + item.getName() + "'");
		}
		return 0;
	}
	
	private void onTagsChanged(Persistable p) {
		this.context.saveTags(p).exceptionally((t) -> {
			// present them to the user
			Platform.runLater(() -> {
				Alert errorAlert = Dialogs.exception(
						this.context.getStage(), 
						t);
				errorAlert.show();
			});
			return null;
		});
	}
	
	public Persistable getItem() {
		return this.item.get();
	}
	
	public void setItem(Persistable item) {
		this.item.set(item);
	}
	
	public ObjectProperty<Persistable> itemProperty() {
		return this.item;
	}
}
