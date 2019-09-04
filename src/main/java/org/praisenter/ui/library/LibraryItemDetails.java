package org.praisenter.ui.library;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.bible.Bible;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.Persistable;
import org.praisenter.data.Tag;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideShow;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.controls.RowVisGridPane;
import org.praisenter.ui.controls.MediaPreview;
import org.praisenter.ui.controls.TagListView;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideView;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.Formatter;
import org.praisenter.utility.StringManipulator;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

final class LibraryItemDetails extends BorderPane {
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
	private final StringProperty tags;
	
	// bible data
	
	private final StringProperty bibleLanguage;
	private final StringProperty bibleSource;
	private final StringProperty bibleCopyright;
	
	// media data
	
	private final BooleanProperty mediaAudio;
	
	// slide data
	
	// slide show data
	
	private final IntegerProperty showSlideCount;
	
	// song data
	
	// UI
	
	/* helper for async loaded slides */
	private Slide mostRecentSlide;
	
	
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
		this.tags = new SimpleStringProperty();
		
		// bible data
		// language, source, copyright
		this.bibleLanguage = new SimpleStringProperty();
		this.bibleSource = new SimpleStringProperty();
		this.bibleCopyright = new SimpleStringProperty();
		
		// media data
		// hasAudio, preview
		this.mediaAudio = new SimpleBooleanProperty();
		
		// slide data
		
		// slide show data
		// slide count, preview
		this.showSlideCount = new SimpleIntegerProperty();
		
		this.item.addListener((obs, ov, nv) -> {
			this.name.unbind();
			this.modified.unbind();
			this.created.unbind();
			this.width.unbind();
			this.height.unbind();
			this.length.unbind();
			this.size.unbind();
			this.tags.unbind();
			
			this.bibleLanguage.unbind();
			this.bibleSource.unbind();
			this.bibleCopyright.unbind();
			
			this.mediaAudio.unbind();
			
			this.showSlideCount.unbind();
			
			this.name.set(null);
			
			if (nv != null) {
				this.name.bind(nv.nameProperty());
				this.modified.bind(nv.modifiedDateProperty());
				this.created.bind(nv.createdDateProperty());
				this.size.setValue(this.getFileSize(nv));
				this.tags.bind(this.getTagsStringBinding(nv));
				
				if (nv instanceof Bible) {
					Bible bible = (Bible)nv;
					this.bibleCopyright.bind(bible.copyrightProperty());
					this.bibleLanguage.bind(bible.languageProperty());
					this.bibleSource.bind(bible.sourceProperty());
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
				} else if (nv instanceof SlideShow) {
					SlideShow show = (SlideShow)nv;
				}
			}
		});
		
		VBox content = new VBox(5);
		
		ScrollPane scrLayout = new ScrollPane(content);
		scrLayout.setFitToWidth(true);
		scrLayout.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		// song data
		// TODO song info
		
		// label section
		Label lblNameValue = new Label();
		Label lblModifiedValue = new Label();
		Label lblCreatedValue = new Label();
		Label lblWidthValue = new Label();
		Label lblHeightValue = new Label();
		Label lblSizeValue = new Label();
		Label lblLengthValue = new Label();
		Label lblTagsValue = new Label();
		
		Label lblBibleLanguageValue = new Label();
		Label lblBibleSourceValue = new Label();
		Label lblBibleCopyrightValue = new Label();
		
		Label lblMediaAudioValue = new Label();
		
		Label lblShowSlideCountValue = new Label();
		
		lblNameValue.textProperty().bind(this.name);
		lblNameValue.setFont(Font.font(lblNameValue.getFont().getFamily(), lblNameValue.getFont().getSize() + 5));
		
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
		lblTagsValue.textProperty().bind(this.tags);
		
		lblBibleLanguageValue.textProperty().bind(this.bibleLanguage);
		lblBibleSourceValue.textProperty().bind(this.bibleSource);
		lblBibleCopyrightValue.textProperty().bind(this.bibleCopyright);
		
		lblMediaAudioValue.textProperty().bind(Bindings.createStringBinding(() -> this.mediaAudio.get() ? Translations.get("yes") : Translations.get("no"), this.mediaAudio));
		
		// preview section

		// slide preview
		// TODO allow playing the slide transition, videos, animations, -- no sound though
		SlideView slide = new SlideView(context);
		slide.setClipEnabled(true);
		slide.setViewMode(SlideMode.VIEW);
		slide.prefWidthProperty().bind(content.widthProperty());
		slide.managedProperty().bind(slide.visibleProperty());
		slide.setFitToWidthEnabled(true);
		slide.setViewScaleAlignCenter(false);
		slide.setVisible(false);		
		
		// TODO slide show preview
		
		// preview for image
		// TODO create an extended image view for showing a loading while the image is being loaded
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
			}
			return null;
		}, this.item));
		image.setPreserveRatio(true);
		image.fitWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			double tw = this.widthProperty().get() - 35;
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
		player.maxWidthProperty().bind(this.widthProperty().subtract(35));
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
	        				LOGGER.error("Error loading media " + media.getName(), ex);
	        			} else {
	        				MediaPlayer mp = new MediaPlayer(m);
	        				ex = mp.getError();
	        				if (ex != null) {
	        					LOGGER.error("Error creating media player for " + media.getName(), ex);
	        				}
	        				return mp;
	        			}
        			} catch (Exception ex) {
        				LOGGER.error("Error loading media " + media.getName(), ex);
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
		
//		slide.setBorder(new Border(new BorderStroke(Color.ORANGE, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(4.0))));
		

		Label lblCreated = new Label(Translations.get("item.created"));
		Label lblModified = new Label(Translations.get("item.modified"));
		Label lblWidth = new Label(Translations.get("item.width"));
		Label lblHeight = new Label(Translations.get("item.height"));
		Label lblLength = new Label(Translations.get("item.length"));
		Label lblSize = new Label(Translations.get("item.size"));
		Label lblTags = new Label(Translations.get("tags"));
		
		Label lblBibleLanguage = new Label(Translations.get("bible.language"));
		Label lblBibleSource = new Label(Translations.get("bible.source"));
		Label lblBibleCopyright = new Label(Translations.get("bible.copyright"));
		
		Label lblMediaAudio = new Label(Translations.get("media.hasAudio"));
		
		int r = 0;
		RowVisGridPane labels = new RowVisGridPane();
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setPercentWidth(50);
		ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setPercentWidth(50);
//		cc2.setHalignment(HPos.RIGHT);
		labels.getColumnConstraints().addAll(cc1, cc2);
		labels.setMaxWidth(Double.MAX_VALUE);
		labels.setVgap(2);
		labels.setHgap(2);
		
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
		
		labels.add(lblTags, 0, r); labels.add(lblTagsValue, 1, r++);
		
		labels.hideRows(0,1,2,3,4,5,6,7,8,9,10);
		
//		labels.setBorder(new Border(new BorderStroke(Color.BLUEVIOLET, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(4.0))));
		
		this.item.addListener((obs, ov, nv) -> {
			this.mostRecentSlide = null;
			slide.setSlide(null);
			slide.setVisible(false);
			image.setVisible(false);
			player.setVisible(false);
//			labels.hideRows(0,1,2,3,4,5,6,7,8,9,10);
			if (nv == null) {
				labels.showRowsOnly();
			} else if (nv instanceof Slide) {
				this.mostRecentSlide = (Slide)nv;
				// load the slide async
				slide.loadSlideAsync((Slide)nv).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
					// by the time this finishes it could have been changed, so make sure
					// its the same and if so, set it up
					if (nv == this.mostRecentSlide) {
						slide.setSlide(this.mostRecentSlide);
						slide.setVisible(true);
					}
				}));
				
				labels.showRowsOnly(0,1,2,3,4,5,10);
			} else if (nv instanceof Media) {
				Media media = (Media)nv;
				if (media.getMediaType() == MediaType.IMAGE) {
					image.setVisible(true);
					labels.showRowsOnly(0,1,2,3,5,10);
				} else if (media.getMediaType() == MediaType.AUDIO) {
					image.setVisible(true);
					player.setVisible(true);
					labels.showRowsOnly(0,1,4,5,9,10);
				} else if (media.getMediaType() == MediaType.VIDEO) {
					player.setVisible(true);
					labels.showRowsOnly(0,1,2,3,4,5,9,10);
				}
			} else if (nv instanceof Bible) {
				image.setVisible(true);
				labels.showRowsOnly(0,1,5,6,7,8,10);
			} else {
				// TODO properties for songs and other stuff
				labels.showRowsOnly();
			}
		});
		
		content.setPadding(new Insets(10));
		content.getChildren().addAll(lblNameValue
				,image
				,slide
				,player
				,labels);
		
		this.setCenter(scrLayout);
		
		this.setMaxWidth(250);
		this.setPrefWidth(250);
	}
	
	private StringBinding getTagsStringBinding(Persistable item) {
		return Bindings.createStringBinding(() -> {
			return String.join(Translations.get("list.delimiter"), item.getTagsUnmodifiable().stream().map(t -> t.getName()).collect(Collectors.toList()));
		}, item.getTagsUnmodifiable());
	}
	
	private long getFileSize(Persistable item) {
		try {
			return Files.size(context.getDataManager().getFilePath(item));
		} catch (IOException e) {
			LOGGER.warn("Failed to retrieve file size for item '" + item.getName() + "'");
		}
		return 0;
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
