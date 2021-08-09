package org.praisenter.ui.slide;

import org.praisenter.data.slide.SlideRegion;
import org.praisenter.data.slide.graphics.SlideStroke;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Playable;
import org.praisenter.ui.slide.convert.BorderConverter;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

/**
 * This class represents any node within the scene of a slide
 * @author WBittle
 *
 */
abstract class SlideRegionNode<T extends SlideRegion> extends StackPane implements Playable {
	// need to support transformations (scale, translate, rotate)
	// need to support borders, padding, backgrounds, etc
	// need to support selection UI border with resize and move handles
		// NOTE: this needs to be outside of the transformations and visible based on SlideMode
	// need to support reset (play/stop/pause/etc)
	// need to support edit/undo
	
	protected final GlobalContext context;
	protected final T region;
	protected final ObjectProperty<SlideMode> mode;
	
	// scaling/clipping is performed at the parent node to the slide
	// this					X, Y, Width, Height, Animation (TranslateX, TranslateY, ScaleX, ScaleY, Fade, Rotate, etc.)
	//  +- container		Rotation, Opacity, Effects (Glow/Shadow)
	//		+- background		the background (color, gradient, image)
	//		+- mediaView		the media view (video)
	//		+- *content*		supplied from the subclass
	//  	+- border			the border
	
	// a node bound to the transformed coordinates outside the scaling of the viewport
	// would align with each SlideNode to provide the UI for editing.
	// SlideNode				EditNode
	//  +- SlideComponentNode	EditNode	
	//  +- SlideComponentNode	EditNode
	//  +- ...					...
	
	// SlideRegionNode									StackPane, w/ MediaView & Pane (border), Playable
	//	+- SlideNode
	//	+- SlideComponentNode
	//		+- SlideMediaComponentNode
	//		+- SlideTextComponentNode
	//			+- SlideCountdownComponentNode
	//			+- SlideDateTimeComponentNode
	//			+- SlideTextPlaceholderComponentNode
	
	protected final StackPane container;
	protected final PaintPane background;
	protected final StackPane content;
	protected final Region borderPane;
	
	protected SlideRegionNode(GlobalContext context, T region) {
		this.context = context;
		this.region = region;
		this.mode = new SimpleObjectProperty<>(SlideMode.VIEW);
		
		this.container = new StackPane();
		this.background = new PaintPane(context);
		this.borderPane = new Region();
		this.content = new StackPane();
		
		// for testing
//		this.setBorder(new Border(new BorderStroke(Color.ORANGE, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(2.0))));
//		this.container.setBorder(new Border(new BorderStroke(Color.GREEN, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(2.0))));
//		this.background.setBorder(new Border(new BorderStroke(Color.RED, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(2.0))));
//		this.content.setBorder(new Border(new BorderStroke(Color.YELLOW, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(2.0))));
//		this.borderPane.setBorder(new Border(new BorderStroke(Color.MAGENTA, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(2.0))));
		
		this.prefWidthProperty().bind(this.region.widthProperty());
		this.prefHeightProperty().bind(this.region.heightProperty());
		this.minWidthProperty().bind(this.region.widthProperty());
		this.minHeightProperty().bind(this.region.heightProperty());
		this.maxWidthProperty().bind(this.region.widthProperty());
		this.maxHeightProperty().bind(this.region.heightProperty());
		
		this.background.prefWidthProperty().bind(this.region.widthProperty());
		this.background.prefHeightProperty().bind(this.region.heightProperty());
		this.background.maxWidthProperty().bind(this.region.widthProperty());
		this.background.maxHeightProperty().bind(this.region.heightProperty());
//		this.background.minWidthProperty().bind(this.region.widthProperty());
//		this.background.minHeightProperty().bind(this.region.heightProperty());
		
		this.content.prefWidthProperty().bind(this.region.widthProperty());
		this.content.prefHeightProperty().bind(this.region.heightProperty());
		this.content.maxWidthProperty().bind(this.region.widthProperty());
		this.content.maxHeightProperty().bind(this.region.heightProperty());
		
		this.borderPane.prefWidthProperty().bind(this.region.widthProperty());
		this.borderPane.prefHeightProperty().bind(this.region.heightProperty());
		this.borderPane.maxWidthProperty().bind(this.region.widthProperty());
		this.borderPane.maxHeightProperty().bind(this.region.heightProperty());
//		this.borderPane.minWidthProperty().bind(this.region.widthProperty());
//		this.borderPane.minHeightProperty().bind(this.region.heightProperty());
		
		this.container.opacityProperty().bind(this.region.opacityProperty());
		
		this.background.slideModeProperty().bind(this.mode);
		this.background.slidePaintProperty().bind(this.region.backgroundProperty());
		this.background.slideBorderProperty().bind(this.region.borderProperty());
		this.background.slideWidthProperty().bind(this.region.widthProperty());
		this.background.slideHeightProperty().bind(this.region.heightProperty());
		
		this.borderPane.borderProperty().bind(Bindings.createObjectBinding(() -> {
			SlideStroke border = this.region.getBorder();
			return new Border(BorderConverter.toJavaFX(border));
		}, this.region.borderProperty()));
		
		this.container.getChildren().addAll(this.background, this.borderPane, this.content);
		this.getChildren().add(this.container);
	}

	@Override
	public void play() {
		// media
		this.background.play();
	}
	
	@Override
	public void pause() {
		// media
		this.background.pause();
	}
	
	@Override
	public void stop() {
		// media
		this.background.stop();
	}
	
	@Override
	public void dispose() {
		// media
		this.background.dispose();
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
	
	public T getRegion() {
		return this.region;
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
}
