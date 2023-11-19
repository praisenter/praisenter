package org.praisenter.ui.slide;

import java.util.Objects;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.effects.SlideColorAdjust;
import org.praisenter.data.slide.graphics.ScaleType;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.graphics.SlideGradient;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideStroke;
import org.praisenter.data.slide.graphics.SlideStrokeStyle;
import org.praisenter.data.slide.graphics.SlideStrokeType;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Playable;
import org.praisenter.ui.slide.convert.EffectConverter;
import org.praisenter.ui.slide.convert.MediaConverter;
import org.praisenter.ui.slide.convert.PaintConverter;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

final class PaintPane extends StackPane implements Playable {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final ObjectProperty<SlideMode> slideMode;
	private final ObjectProperty<SlidePaint> slidePaint;
	private final ObjectProperty<SlideStroke> slideBorder;
	private final DoubleProperty slideWidth;
	private final DoubleProperty slideHeight;
	
	private final ObjectProperty<MediaObject> mediaObject;
	private final ObjectProperty<Media> media;
	private final ObjectProperty<MediaType> mediaType;
	private final ObjectProperty<ScaleType> scaleType;
	private final DoubleProperty mediaWidth;
	private final DoubleProperty mediaHeight;
	
	private final Region backgroundView;
	private final MediaView mediaView;
	
	protected PaintPane(GlobalContext context) {
		this.context = context;
		this.slideMode = new SimpleObjectProperty<SlideMode>();
		this.slidePaint = new SimpleObjectProperty<SlidePaint>();
		this.slideBorder = new SimpleObjectProperty<SlideStroke>();
		this.slideWidth = new SimpleDoubleProperty();
		this.slideHeight = new SimpleDoubleProperty();

		this.mediaObject = new SimpleObjectProperty<>();
		this.media = new SimpleObjectProperty<>();
		this.mediaType = new SimpleObjectProperty<>();
		this.scaleType = new SimpleObjectProperty<>();
		this.mediaWidth = new SimpleDoubleProperty();
		this.mediaHeight = new SimpleDoubleProperty();
		
		this.backgroundView = new Region();
		this.mediaView = new MediaView();
		
		this.mediaObject.bind(Bindings.createObjectBinding(() -> {
			SlidePaint paint = this.slidePaint.get();
			if (paint == null || !(paint instanceof MediaObject)) return null;
			return (MediaObject)paint;
		}, this.slidePaint));
		
		this.media.bind(Bindings.createObjectBinding(() -> {
			MediaObject mo = this.mediaObject.get();
			return this.getMedia(mo);
		}, this.mediaObject));
		
		this.mediaType.bind(Bindings.createObjectBinding(() -> {
			Media m = this.media.get();
			if (m != null) return m.getMediaType();
			// as a fallback if the media is missing
			MediaObject mo = this.mediaObject.get();
			if (mo != null) return mo.getMediaType();
			return null;
		}, this.mediaObject, this.media));
		
		this.scaleType.bind(Bindings.createObjectBinding(() -> {
			MediaObject mo = this.mediaObject.get();
			if (mo != null) {
				return mo.getScaleType();
			}
			return null;
		}, this.mediaObject));
		
		this.mediaWidth.bind(Bindings.createDoubleBinding(() -> {
			Media media = this.media.get();
			if (media != null) {
				return (double)media.getWidth();
			}
			return -1.0;
		}, this.media));

		this.mediaHeight.bind(Bindings.createDoubleBinding(() -> {
			Media media = this.media.get();
			if (media != null) {
				return (double)media.getHeight();
			}
			return -1.0;
		}, this.media));
		
		// node bindings
		
		this.backgroundView.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
			SlidePaint paint = this.slidePaint.get();
			SlideMode mode = this.slideMode.get();
			SlideStroke border = this.slideBorder.get();
			if (paint == null) return null;
			if (paint instanceof SlideColor || paint instanceof SlideGradient) {
				double radius = border != null ? border.getRadius() : 0;
				return new Background(new BackgroundFill(PaintConverter.toJavaFX(paint), new CornerRadii(radius), null));
			} else if (paint instanceof MediaObject) {
				MediaObject mo1 = (MediaObject)paint;
				Media m1 = this.getMedia(mo1);
				if (m1 == null) {
					LOGGER.warn("The media " + mo1.getMediaId() + " " + mo1.getMediaName() + " was not found.");
					return null;
				}
				
				MediaType type = m1.getMediaType();
				if (type == MediaType.IMAGE || type == MediaType.AUDIO || this.isImageOnlyMode(mode)) {
					return this.createBackground(m1, mo1.getScaleType(), mo1.isRepeatEnabled());
				}
			} else {
				LOGGER.warn("Unsupported paint type '" + paint.getClass().getName() + "'.");
			}
			return null;
		}, this.slidePaint, this.slideMode, this.slideBorder));
		
		this.backgroundView.clipProperty().bind(Bindings.createObjectBinding(() -> {
			SlidePaint paint = this.slidePaint.get();
			SlideStroke border = this.slideBorder.get();
			if (paint == null) return null;
			if (paint instanceof SlideColor || paint instanceof SlideGradient) {
				return null;
			} else if (paint instanceof MediaObject) {
				double w = this.slideWidth.get();
				double h = this.slideHeight.get();
				return this.getBorderBasedClip(border, w, h);
			} else {
				LOGGER.warn("Unsupported paint type '" + paint.getClass().getName() + "'.");
			}
			return null;
		}, this.slidePaint, this.slideBorder, this.slideWidth, this.slideHeight));
		
		this.mediaView.preserveRatioProperty().bind(Bindings.createBooleanBinding(() -> {
			return this.scaleType.get() != ScaleType.NONUNIFORM;
		}, this.scaleType));
		
		this.mediaView.fitWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			double w = this.slideWidth.get();
			double h = this.slideHeight.get();
			ScaleType type = this.scaleType.get();
			if (type == null || w <= 0 || h <= 0) return 0.0;
			if (type == ScaleType.NONE) return 0.0;
			if (type == ScaleType.UNIFORM) {
				if (w < h) return w;
				return 0.0;
			}
			if (type == ScaleType.NONUNIFORM) {
				return w;
			}
			return 0.0;
		}, this.scaleType, this.slideWidth, this.slideHeight));
		
		this.mediaView.fitHeightProperty().bind(Bindings.createDoubleBinding(() -> {
			double w = this.slideWidth.get();
			double h = this.slideHeight.get();
			ScaleType type = this.scaleType.get();
			if (type == null || w <= 0 || h <= 0) return 0.0;
			if (type == ScaleType.NONE) return 0.0;
			if (type == ScaleType.UNIFORM) {
				if (w > h) return h;
				return 0.0;
			}
			if (type == ScaleType.NONUNIFORM) {
				return h;
			}
			return 0.0;
		}, this.scaleType, this.slideWidth, this.slideHeight));
		
		this.mediaView.effectProperty().bind(Bindings.createObjectBinding(() -> {
			MediaObject mo = this.mediaObject.get();
			return this.getEffect(mo);
		}, this.mediaObject));
		
		this.backgroundView.effectProperty().bind(Bindings.createObjectBinding(() -> {
			MediaObject mo = this.mediaObject.get();
			return this.getEffect(mo);
		}, this.mediaObject));
		
		// make sure the background view cannot expand past the bounds of this pane
		// this forces the non-uniform scaling to work in a background fill
		this.backgroundView.maxWidthProperty().bind(this.maxWidthProperty());
		this.backgroundView.maxHeightProperty().bind(this.maxHeightProperty());
		
		// listeners
		
		this.mediaObject.addListener((obs, ov, nv) -> {
			boolean mediaChanged = true;
			if (ov != null && nv != null) {
				UUID oid = ov.getMediaId();
				UUID nid = nv.getMediaId();
				mediaChanged = !Objects.equals(oid, nid);
			}
			SlideMode mode = this.slideMode.get();
			this.updateMediaPlayer(nv, mode, mediaChanged, false);
		});
		this.slideMode.addListener((obs, ov, nv) -> {
			boolean ioo = this.isImageOnlyMode(ov);
			boolean ion = this.isImageOnlyMode(nv);
			MediaObject mo = this.mediaObject.get();
			this.updateMediaPlayer(mo, nv, false, ioo != ion);
		});
		
		this.getChildren().addAll(this.backgroundView, this.mediaView);
	}
	
	private final Shape getBorderBasedClip(SlideStroke stroke, double width, double height) {
		if (stroke != null) {
			double r = stroke.getRadius();
			double r2 = r * 2;
			double sw = stroke.getWidth();
			SlideStrokeStyle style = stroke.getStyle();
			if (r2 > 0) {
				// try to match the behavior of the Java FX Border class when the radius is bigger
				// than the actual container
				if (width < height && r2 > width) {
					r2 = width;
				} else if (height < width && r2 > height) {
					r2 = height;
				}
				
				double xoffset = 0;
				double yoffset = 0;
				
				// for inside type, we want the background to still fill
				// at the moment we don't support INSIDE due to performance reasons
				// but I've left this in for now
				if (style != null && style.getType() == SlideStrokeType.INSIDE) {
					xoffset = yoffset = sw * 0.5;
					width -= sw;
					height -= sw;
				}
				
				Rectangle clip = new Rectangle();
				clip.setX(xoffset);
				clip.setY(yoffset);
				clip.setWidth(width);
				clip.setHeight(height);
				clip.setArcHeight(r2);
				clip.setArcWidth(r2);
				return clip;
			}
		}
		return null;
	}
	
	private final boolean isImageOnlyMode(SlideMode mode) {
		return mode != SlideMode.PRESENT &&
			mode != SlideMode.PREVIEW;
	}
	
	private final boolean isMediaPlayerReady(MediaPlayer mp) {
		MediaPlayer.Status status = mp.getStatus();
		if (status == MediaPlayer.Status.HALTED ||
			status == MediaPlayer.Status.DISPOSED) {
			return false;
		}
		return true;
	}
	
	private final Effect getEffect(MediaObject mo) {
		if (mo != null) {
			SlideColorAdjust effect = mo.getColorAdjust();
			if (effect != null) {
				return EffectConverter.toJavaFX(effect);
			}
		}	
		return null;
	}
	
	private final Media getMedia(MediaObject mo) {
		if (mo != null) {
			UUID id = mo.getMediaId();
			if (id != null) {
				return this.context.getWorkspaceManager().getItem(Media.class, id);
			}
		}
		return null;
	}
	
	private void updateMediaPlayer(MediaObject nv, SlideMode mode, boolean mediaChanged, boolean modeChanged) {
		// clean up if media changed or the slide mode changed
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (mediaChanged && player != null) {
			player.dispose();
			this.mediaView.setMediaPlayer(null);
			player = null;
		}
		
		// check if the new media is null
		if (nv == null) return;
		
		// get the new media
		Media media = this.getMedia(nv);
		
		// check if the new media wasn't found
		if (media == null) return;
		
		MediaType type = media.getMediaType();
		
		if ((type == MediaType.AUDIO || type == MediaType.VIDEO) && !this.isImageOnlyMode(mode)) {
			// create a new media player if needed
			if (player == null) {
				player = MediaConverter.toJavaFXMediaPlayer(media, nv.isLoopEnabled(), mode == SlideMode.PREVIEW || nv.isMuted());
				this.mediaView.setMediaPlayer(player);
			} else {
				player.setMute(mode == SlideMode.PREVIEW || nv.isMuted());
				player.setCycleCount(nv.isLoopEnabled() ? MediaPlayer.INDEFINITE : 0);
			}
		}
	}
	
	private final Background createBackground(Media media, ScaleType scale, boolean repeat) {
		Image image = this.context.getImageCache().getOrLoadImage(media.getId(), media.getMediaImagePath());
		if (image != null) {
			if (repeat && media.getMediaType() == MediaType.IMAGE) {
				return new Background(new BackgroundImage(
						image,
						BackgroundRepeat.REPEAT, 
						BackgroundRepeat.REPEAT, 
						BackgroundPosition.DEFAULT,
						BackgroundSize.DEFAULT));
			} else {
				return new Background(new BackgroundImage(
						image,
						BackgroundRepeat.NO_REPEAT, 
						BackgroundRepeat.NO_REPEAT, 
						BackgroundPosition.CENTER, 
						MediaConverter.toJavaFX(scale)));
			}
		}
		return null;
	}
	
	@Override
	public void play() {
		// media
		MediaPlayer mp = this.mediaView.getMediaPlayer();
		if (mp != null && this.isMediaPlayerReady(mp)) {
			mp.play();
		}
	}
	
	@Override
	public void pause() {
		// media
		MediaPlayer mp = this.mediaView.getMediaPlayer();
		if (mp != null && this.isMediaPlayerReady(mp)) {
			mp.pause();
		}
	}
	
	@Override
	public void stop() {
		// media
		MediaPlayer mp = this.mediaView.getMediaPlayer();
		if (mp != null && this.isMediaPlayerReady(mp)) {
			mp.stop();
		}
	}
	
	@Override
	public void dispose() {
		// media
		MediaPlayer mp = this.mediaView.getMediaPlayer();
		if (mp != null) {
			//mp.stop();
			mp.dispose();
		}
	}
	
	// resets the UI to the source data
	public void reset() {
		this.setTranslateX(0);
		this.setTranslateY(0);
		this.setTranslateZ(0);
		this.setScaleX(0);
		this.setScaleY(0);
		this.setScaleZ(0);
		this.setRotate(0);
		this.setOpacity(1.0);
		this.setClip(null);
		
		// media
		this.stop();
	}
	
	public ObjectProperty<SlideMode> slideModeProperty() {
		return this.slideMode;
	}
	
	public SlideMode getSlideMode() {
		return this.slideMode.get();
	}
	
	public void setSlideMode(SlideMode mode) {
		this.slideMode.set(mode);
	}
	
	public ObjectProperty<SlidePaint> slidePaintProperty() {
		return this.slidePaint;
	}
	
	public SlidePaint getSlidePaint() {
		return this.slidePaint.get();
	}
	
	public void setSlidePaint(SlidePaint paint) {
		this.slidePaint.set(paint);
	}
	
	public ObjectProperty<SlideStroke> slideBorderProperty() {
		return this.slideBorder;
	}

	public SlideStroke getSlideBorder() {
		return this.slideBorder.get();
	}
	
	public void setSlideBorder(SlideStroke stroke) {
		this.slideBorder.set(stroke);
	}
	
	public DoubleProperty slideWidthProperty() {
		return this.slideWidth;
	}
	
	public DoubleProperty slideHeightProperty() {
		return this.slideHeight;
	}
}
