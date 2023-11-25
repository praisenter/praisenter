package org.praisenter.ui.slide;

import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.ui.GlobalContext;

final class MediaComponentNode extends SlideComponentNode<MediaComponent> {
	protected final PaintPane media;
	
	public MediaComponentNode(GlobalContext context, MediaComponent region) {
		super(context, region);
		
		this.media = new PaintPane(context);
		
		this.media.slideModeProperty().bind(this.mode);
		this.media.slidePaintProperty().bind(this.region.mediaProperty());
		this.media.slideBorderProperty().bind(this.region.borderProperty());
		this.media.slideWidthProperty().bind(this.region.widthProperty());
		this.media.slideHeightProperty().bind(this.region.heightProperty());
		
		// this allows the scaling to work properly
		this.media.prefWidthProperty().bind(this.region.widthProperty());
		this.media.prefHeightProperty().bind(this.region.heightProperty());
		this.media.maxWidthProperty().bind(this.region.widthProperty());
		this.media.maxHeightProperty().bind(this.region.heightProperty());
		
		// make the media be behind the border
		this.container.getChildren().removeAll(this.content, this.borderPane);
		this.container.getChildren().addAll(this.content, this.borderPane);
		
		this.content.getChildren().add(this.media);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#play()
	 */
	public void play() {
		super.play();
		this.media.play();
	}

	/* (non-Javadoc)
	 * @see org.praisenter.ui.slide.SlideRegionNode#pause()
	 */
	@Override
	public void pause() {
		super.pause();
		this.media.pause();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#stop()
	 */
	public void stop() {
		super.stop();
		this.media.stop();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#dispose()
	 */
	public void dispose() {
		super.dispose();
		this.media.dispose();
	}
	
}
