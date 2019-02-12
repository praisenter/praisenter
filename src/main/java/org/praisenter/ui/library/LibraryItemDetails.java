package org.praisenter.ui.library;

import java.time.Instant;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Persistable;
import org.praisenter.data.Tag;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.controls.MediaPreview;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideView;

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
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

final class LibraryItemDetails extends VBox {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	
	// data
	
	private final ObjectProperty<Persistable> item;
	
	// standard data
	
	private final StringProperty name;
	private final ObjectProperty<Instant> modified;
	private final ObjectProperty<Instant> created;
	private final ObservableSet<Tag> tags;
	
	// bible data
	
	private final StringProperty bibleLanguage;
	private final StringProperty bibleSource;
	private final StringProperty bibleCopyright;
	
	// media data
	
	private final IntegerProperty mediaWidth;
	private final IntegerProperty mediaHeight;
	private final LongProperty mediaLength;
	private final BooleanProperty mediaAudio;
	
	// slide data
	
	private final LongProperty slideTime;
	
	// slide show data
	
	private final LongProperty showTime;
	private final IntegerProperty showSlideCount;
	
	// song data
	
	// UI
	
	
	
	public LibraryItemDetails(GlobalContext context) {
		this.context = context;
		this.item = new SimpleObjectProperty<>();
		
		// standard data
		// name, modified, created, tags
		this.name = new SimpleStringProperty();
		this.modified = new SimpleObjectProperty<>();
		this.created = new SimpleObjectProperty<>();
		this.tags = FXCollections.observableSet(new HashSet<>());
		
		// bible data
		// language, source, copyright
		this.bibleLanguage = new SimpleStringProperty();
		this.bibleSource = new SimpleStringProperty();
		this.bibleCopyright = new SimpleStringProperty();
		
		// media data
		// width, height, length, hasAudio, preview
		this.mediaWidth = new SimpleIntegerProperty();
		this.mediaHeight = new SimpleIntegerProperty();
		this.mediaLength = new SimpleLongProperty();
		this.mediaAudio = new SimpleBooleanProperty();
		
		// slide data
		// total time (if not infinite), preview
		this.slideTime = new SimpleLongProperty();
		
		// slide show data
		// total time (if not infinite), slide count, preview
		this.showTime = new SimpleLongProperty();
		this.showSlideCount = new SimpleIntegerProperty();
		
		// song data
		// TODO song info
		
		// preview section

		// slide preview
		// TODO allow playing the slide transition, videos, animations, -- no sound though
		SlideView slide = new SlideView(context);
		slide.setClipEnabled(true);
		slide.setViewMode(SlideMode.VIEW);
		slide.setViewScalingEnabled(true);
		slide.slideProperty().bind(Bindings.createObjectBinding(() -> {
			Persistable item = this.item.get();
			if (item instanceof Slide) {
				return (Slide)item;
			}
			return null;
		}, this.item));
		
		// TODO slide show preview
		
		// preview for image
		ImageView image = new ImageView();
		image.imageProperty().bind(Bindings.createObjectBinding(() -> {
			Persistable item = this.item.get();
			if (item instanceof Media) {
				Media media = (Media)item;
				MediaType type = media.getMediaType();
				if (type == MediaType.IMAGE) {
					return this.context.getImageCache().getOrLoadImage(media.getId(), media.getMediaImagePath());
				}
			}
			return null;
		}, this.item));
		image.setPreserveRatio(true);
		image.fitWidthProperty().bind(this.widthProperty());

		// media player for audio/video
		MediaPreview player = new MediaPreview();
		player.setMaxWidth(400);
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
		
		VBox.setVgrow(slide, Priority.ALWAYS);
		
		this.getChildren().addAll(
				player,
				image,
				slide);
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
