package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.object.MediaObject;

public class FxSlideMediaComponent extends FxSlideComponent<MediaComponent> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	MediaView mediaNode;
	VBox imageNode;
	
	public FxSlideMediaComponent(PraisenterContext context, MediaComponent component, SlideMode mode) {
		super(context, component, mode);
		
		SlideStroke bdr = this.component.getBorder();
		
		int w = this.component.getWidth();
		int h = this.component.getHeight();
		
		// get the media id
		MediaObject mo = ((MediaComponent)this.component).getMedia();
		if (mo != null) {
			Node node = getMediaNode(mo, w, h, bdr != null ? bdr.getRadius() : 0);
			if (node instanceof MediaView) {
				this.mediaNode = (MediaView)node;
			} else {
				this.imageNode = (VBox)node;
			}
		} else {
			LOGGER.warn("No media set on media component {}.", this.component.getId());
		}
		
		this.contentNode.getChildren().addAll(
				this.backgroundMedia != null ? this.backgroundMedia : this.backgroundPaint,
				this.mediaNode != null ? this.mediaNode : this.imageNode,
				this.borderNode);
	}
	
	public List<MediaPlayer> getMediaPlayers() {
		List<MediaPlayer> players = new ArrayList<MediaPlayer>();
		players.addAll(super.getMediaPlayers());
		if (this.mediaNode != null) {
			players.add(this.mediaNode.getMediaPlayer());
		}
		return players;
	}
}
