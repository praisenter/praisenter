package org.praisenter.javafx.slide;

import javafx.scene.layout.StackPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;

public abstract class FxSlideComponent<E extends SlideComponent> extends FxSlideRegion<E> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	final StackPane contentNode;
	
	public FxSlideComponent(PraisenterContext context, E component, SlideMode mode) {
		super(context, component, mode);
		
		this.contentNode = new StackPane();
	}
	
	// StackPane
	//		VBox				background (color, gradient, or image)
	//			While the background could technically be a video since the
	//			the data structure allows for it, we won't allow it for components.
	//			Instead, the user should just create a video object.
	// 		Text/VBox/MediaView	content (text, image, video/audio respectively)
	//			This represents the components content, so text, audio, video, etc.
	//			This can be a number of different node types so we'll need to do
	//			logic on the type using instanceof.
	//		VBox				border
	//			The border should be overlaid onto the background and contents
	//			so we need to add another layer for just the border
	
}
