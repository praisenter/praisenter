package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.object.MediaObject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;

public final class ObservableMediaComponent extends ObservableSlideComponent<MediaComponent> implements SlideRegion, SlideComponent {

	final ObjectProperty<MediaObject> media = new SimpleObjectProperty<MediaObject>();
	
	// nodes
	
	// audio/video (present)
	private MediaView mediaNode;
	
	// image (present) or audio/video (preview)
	private VBox imageNode;
	
	public ObservableMediaComponent(MediaComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.media.set(component.getMedia());
		
		// listen for changes
		this.media.addListener((obs, ov, nv) -> { 
			this.region.setMedia(nv); 
		});
	}
	
	private void setup() {
		SlideStroke bdr = this.region.getBorder();
		double br = bdr != null ? bdr.getRadius() : 0;
		
		int w = this.region.getWidth();
		int h = this.region.getHeight();
		
		// get the media id
		MediaObject mo = this.region.getMedia();
		Media media = this.context.getMediaLibrary().get(mo.getId());
		if (mo != null) {
			if (this.mode == SlideMode.EDIT) {
				this.imageNode = JavaFXTypeConverter.toJavaFXAsImage(context, mo, w, h, br);
			} else if (media.getMetadata().getType() == MediaType.IMAGE) {
				this.imageNode = JavaFXTypeConverter.toJavaFXAsImage(context, mo, w, h, br);
			} else {
				this.mediaNode = JavaFXTypeConverter.toJavaFXForPlayableAudioVideo(context, mo, w, h, br);
			}
		} else {
			// FIXME logging
//			LOGGER.warn("No media set on media component {}.", this.component.getId());
		}
		
		this.root.getChildren().addAll(
				this.backgroundMedia != null ? this.backgroundMedia : this.backgroundPaint,
				this.mediaNode != null ? this.mediaNode : this.imageNode,
				this.borderNode);
	}
	
	// media
	
	public MediaObject getMedia() {
		return this.media.get();
	}
	
	public void setMedia(MediaObject media) {
		this.media.set(media);
	}
	
	public ObjectProperty<MediaObject> mediaProperty() {
		return this.media;
	}

	@Override
	public ObservableMediaComponent copy() {
		throw new UnsupportedOperationException();
	}
}
