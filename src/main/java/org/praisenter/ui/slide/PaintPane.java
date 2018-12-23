package org.praisenter.ui.slide;

import java.util.Objects;
import java.util.UUID;

import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.effects.SlideColorAdjust;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.graphics.SlideLinearGradient;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideRadialGradient;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.slide.convert.EffectConverter;
import org.praisenter.ui.slide.convert.MediaConverter;
import org.praisenter.ui.slide.convert.PaintConverter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

final class PaintPane extends StackPane implements Playable {

	private final GlobalContext context;
	private final ObjectProperty<SlideMode> mode;
	private final ObjectProperty<SlidePaint> paint;
	
	private final Region backgroundView;
	private final MediaView mediaView;
	
	protected PaintPane(GlobalContext context) {
		this.context = context;
		this.mode = new SimpleObjectProperty<>();
		this.paint = new SimpleObjectProperty<>();

		this.backgroundView = new Region();
		this.mediaView = new MediaView();
		
		this.paint.addListener((obs, ov, nv) -> {
			this.onBackgroundChanged(ov, nv);
		});
		this.mode.addListener((obs, ov, nv) -> {
			this.onSlideModeChanged(ov, nv);
		});
		
		this.getChildren().addAll(this.backgroundView, this.mediaView);
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
	
	private final MediaObject getMediaObject(SlidePaint sp) {
		if (sp != null && sp instanceof MediaObject) {
			return (MediaObject) sp;
		}
		return null;
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
				return this.context.getDataManager().getItem(Media.class, id);
			}
		}
		return null;
	}
	
	private final void cleanUpBackground() {
		this.backgroundView.setBackground(null);
		this.backgroundView.setEffect(null);
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) player.dispose();
		this.mediaView.setMediaPlayer(null);
	}
	
	private final void onBackgroundChanged(SlidePaint sp0, SlidePaint sp1) {
		SlideMode mode = this.mode.get();

		MediaObject mo0 = this.getMediaObject(sp0);
		MediaObject mo1 = this.getMediaObject(sp1);
		Media m0 = this.getMedia(mo0);
		Media m1 = this.getMedia(mo1);
		
		// check the type of update that occurred
		if (m0 != null && m1 != null && Objects.equals(m0.getId(), m1.getId())) {
			// this means that the background changed, but it's stayed the same media
			// as a result we only need to update the relevant properties instead of
			// creating a new player etc.
			if (!this.isImageOnlyMode(mode)) {
				MediaPlayer mp = this.mediaView.getMediaPlayer();
				if (mp != null) {
					mp.setMute(mo1.isMuted());
					mp.setCycleCount(mo1.isLoopEnabled() ? MediaPlayer.INDEFINITE : 1);
				}
			}
			// otherwise there's nothing to update
			return;
		} else {
			this.cleanUpBackground();
		}
		
		this.updateBackground(mode, sp1);
		
		// always update the effect
		Effect effect = this.getEffect(mo1);
		this.backgroundView.setEffect(effect);
		this.mediaView.setEffect(effect);
	}
	
	private final void updateBackground(SlideMode mode, SlidePaint sp) {
		Background background = null;
		MediaPlayer player = null;
		if (sp != null) {
			if (sp instanceof SlideColor) {
				background = new Background(new BackgroundFill(PaintConverter.toJavaFX((SlideColor)sp), null, null));
			} else if (sp instanceof SlideLinearGradient) {
				background = new Background(new BackgroundFill(PaintConverter.toJavaFX((SlideLinearGradient)sp), null, null));
			} else if (sp instanceof SlideRadialGradient) {
				background = new Background(new BackgroundFill(PaintConverter.toJavaFX((SlideRadialGradient)sp), null, null));
			} else if (sp instanceof MediaObject) {
				MediaObject mo1 = this.getMediaObject(sp);
				Media m1 = this.getMedia(mo1);
				if (m1 != null) {
					MediaType type = m1.getMediaType();
					if (type == MediaType.IMAGE || type == MediaType.AUDIO || this.isImageOnlyMode(mode)) {
						Image image = this.context.getImageCache().getOrLoadImage(m1.getId(), m1.getMediaImagePath());
						if (image != null) {
							background = new Background(new BackgroundImage(
									image,
									BackgroundRepeat.NO_REPEAT, 
									BackgroundRepeat.NO_REPEAT, 
									BackgroundPosition.CENTER, 
									MediaConverter.toJavaFX(mo1.getScaleType())));							
						}
					} else if ((type == MediaType.AUDIO || type == MediaType.VIDEO) && !this.isImageOnlyMode(mode)) {
						player = MediaConverter.toJavaFXMediaPlayer(
								m1, 
								mo1.isLoopEnabled(), 
								mode == SlideMode.PREVIEW || mo1.isMuted());
					}
				}
			}
		}
		
		// update the background and player
		this.backgroundView.setBackground(background);
		this.mediaView.setMediaPlayer(player);
	}
	
	private final void onSlideModeChanged(SlideMode m0, SlideMode m1) {
		boolean io0 = this.isImageOnlyMode(m0);
		boolean io1 = this.isImageOnlyMode(m1);
		
		SlidePaint sp = this.paint.get();
		
		// NOTE: we aren't handling the state transition from PREVIEW_NO_AUDIO to PRESENT in the most
		// efficient manner, but that's because we won't do that transition - anything that goes to 
		// PRESENT will be a copy of the previewed item
		
		if (io0 != io1) {
			// then we need to update the background
			this.updateBackground(m1, sp);
		}
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
	
	public ObjectProperty<SlideMode> modeProperty() {
		return this.mode;
	}
	
	public SlideMode getMode() {
		return this.mode.get();
	}
	
	public void setMode(SlideMode mode) {
		this.mode.set(mode);
	}
	
	public ObjectProperty<SlidePaint> paintProperty() {
		return this.paint;
	}
	
	public SlidePaint getPaint() {
		return this.paint.get();
	}
	
	public void setPaint(SlidePaint paint) {
		this.paint.set(paint);
	}
}
