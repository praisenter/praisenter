package org.praisenter.javafx.slide.editor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.TextType;
import org.praisenter.javafx.ApplicationAction;
import org.praisenter.javafx.ApplicationEvent;
import org.praisenter.javafx.ApplicationPane;
import org.praisenter.javafx.ApplicationPaneEvent;
import org.praisenter.javafx.DataFormats;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.SlideActions;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.javafx.themes.Styles;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.media.MediaType;
import org.praisenter.slide.AbstractSlideComponent;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.ShadowType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideShadow;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.graphics.SlideStrokeStyle;
import org.praisenter.slide.graphics.SlideStrokeType;
import org.praisenter.slide.object.MediaObject;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.CountdownComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.TextPlaceholderComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.utility.Scaling;
import org.praisenter.xml.XmlIO;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

// FEATURE Allow grouping of components to easily move them together
// JAVABUG 06/30/16 LOW Text border really slows when the stroke style is INSIDE or OUTSIDE - may just want to not offer this option
// JAVABUG 02/04/17 MEDIUM DropShadow and Glow effects cannot be mouse transparent - https://bugs.openjdk.java.net/browse/JDK-8092268, https://bugs.openjdk.java.net/browse/JDK-8101376

// FIXME show the appropriate ribbon when creating a new node
// FIXME show the media selection dialog before adding a the media component

// TODO slide presentation:
//		user selects slide
//		if slide has placeholders
//			clear all placeholders
//		endif
//		if user is selecting a bible verse or song verse
//			set placeholders based on languages
//		endif
//		present thumbnail of slide

//		user presents slide
//		the transition+easing+duration is used to generate a ParallelTransition for the current slide and the incoming slide
//		the new slide is added to the scene graph
//		the transition is played
public final class SlideEditorPane extends BorderPane implements ApplicationPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("edit-selected");
	
	private final PraisenterContext context;
	
	private final ObjectProperty<ObservableSlide<Slide>> slide = new SimpleObjectProperty<ObservableSlide<Slide>>();
	private final ObjectProperty<ObservableSlideRegion<?>> selected = new SimpleObjectProperty<ObservableSlideRegion<?>>();

	private final SlideEditorRibbon ribbon;
	private final AnimationsPane animations;
	private final StackPane slidePreview;
	
	public SlideEditorPane(PraisenterContext context) {
		this.getStyleClass().add(Styles.SLIDE_EDITOR_PANE);
		
		this.context = context;
		
		// create the ribbon
		this.ribbon = new SlideEditorRibbon(context);
		VBox top = new VBox(this.ribbon);
		top.setBorder(new Border(new BorderStroke(null, null, Color.GRAY, null, null, null, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, null, new BorderWidths(0, 0, 1, 0), null)));
		this.setTop(top);
		
		// create the animation picker
		this.animations = new AnimationsPane(context);
		this.setRight(this.animations);
		
		// Node hierarchy:
		// +-------------------------------+--------------+---------------------------------------------------------+
		// | Name                          | Type         | Role                                                    |
		// +-------------------------------+--------------+---------------------------------------------------------+
		// | slidePreview                  | StackPane    | Editor background color                                 |
		// | +- slideBounds                | Pane         | Transparent background, uniform sizing, and drop shadow |
		// |    +- slideCanvas             | Pane         | Contains all the Observable Slide nodes                 |
		// |       +- rootPane             | Pane         | The root pane for the slide                             |
		// |          +- container         | Pane         | Provides scaling                                        |
		// |             +- backgroundNode | FillPane     | For the slide background                                |
		// |             +- borderNode     | Region       | The slide border                                        |
		// |          +- editBorderNode    | Region       | The edit border                                         |
		// +-------------------------------+--Components--+---------------------------------------------------------+
		// |          +- componentCanvas   | Pane         | The slide components                                    |
		// |             +- rootPane       | Pane         | Component 1                                             |
		// |             +- rootPane       | Pane         | Component 2                                             |
		// |             +- ....           | Pane         | Component N                                             |
		// +-------------------------------+--------------+---------------------------------------------------------+
		
		final double padding = 10;
		
		// create the slidePreview area
		this.slidePreview = new StackPane();
		this.slidePreview.setPrefSize(500, 400);
		this.slidePreview.setPadding(new Insets(padding));
		this.slidePreview.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
		this.slidePreview.setSnapToPixel(true);
		
		// clip by the slidePreview area
		Rectangle clipRect = new Rectangle(this.slidePreview.getWidth(), this.slidePreview.getHeight());
		clipRect.heightProperty().bind(this.slidePreview.heightProperty());
		clipRect.widthProperty().bind(this.slidePreview.widthProperty());
		this.slidePreview.setClip(clipRect);
		
		// create the slideBounds area for the
		// unscaled transparency background
		Pane slideBounds = new Pane();
		slideBounds.setBackground(new Background(new BackgroundImage(Fx.TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
		// add a drop shadow effect for better looks
		DropShadow sdw = new DropShadow();
		sdw.setRadius(5);
		sdw.setColor(Color.rgb(0, 0, 0, 0.3));
		slideBounds.setEffect(sdw);
		
		// we size the slideBounds to a uniform scaled version
		// using the current available space and the slide's 
		// target resolution
		DoubleBinding widthSizing = new DoubleBinding() {
			{
				bind(slidePreview.widthProperty(), 
					 slidePreview.heightProperty());
			}
			@Override
			protected double computeValue() {
				ObservableSlide<?> s = slide.get();
				if (s != null) {
					double w = s.getWidth();
					double h = s.getHeight();
					double tw = Math.min(slidePreview.getWidth() - padding * 2, w);
					double th = Math.min(slidePreview.getHeight() - padding * 2, h);
					return Math.floor(Scaling.getUniformScaling(w, h, tw, th).width) - 1;
				}
				return 0;
			}
		};
		DoubleBinding heightSizing = new DoubleBinding() {
			{
				bind(slidePreview.widthProperty(), 
					 slidePreview.heightProperty());
			}
			@Override
			protected double computeValue() {
				ObservableSlide<?> s = slide.get();
				if (s != null) {
					double w = s.getWidth();
					double h = s.getHeight();
					double tw = Math.min(slidePreview.getWidth() - padding * 2, w);
					double th = Math.min(slidePreview.getHeight() - padding * 2, h);
					return Math.floor(Scaling.getUniformScaling(w, h, tw, th).height) - 1;
				}
				return 0;
			}
		};
		slideBounds.maxWidthProperty().bind(widthSizing);
		slideBounds.maxHeightProperty().bind(heightSizing);
		
		// create the slideCanvas
		Pane slideCanvas = new Pane();
		slideCanvas.setMinSize(0, 0);
		slideCanvas.setSnapToPixel(true);
		
		// build the preview hierarchy
		slideBounds.getChildren().add(slideCanvas);
		this.slidePreview.getChildren().addAll(slideBounds);
		StackPane.setAlignment(slideBounds, Pos.CENTER);
		StackPane.setAlignment(slideCanvas, Pos.CENTER);
		this.setCenter(this.slidePreview);

		// events
		
		// listener for selection changes
		this.selected.addListener((obs, ov, nv) -> {
			if (ov != null) {
				ov.getEditBorderNode().pseudoClassStateChanged(SELECTED, false);
			}
			if (nv != null) {
				nv.getEditBorderNode().pseudoClassStateChanged(SELECTED, true);
				nv.getEditBorderNode().requestFocus();
			}
		});
		
		// scaling must be applied to the slide and components separately so that
		// we avoid scaling the selection border
		ObjectBinding<Scaling> scaleFactor = new ObjectBinding<Scaling>() {
			{
				bind(slidePreview.widthProperty(), slidePreview.heightProperty());
			}
			@Override
			protected Scaling computeValue() {
				double tw = slidePreview.getWidth() - padding * 2;
				double th = slidePreview.getHeight() - padding * 2;
				
				ObservableSlide<?> s = slide.get();
				if (s == null) {
					return Scaling.getNoScaling(tw, th);
				}
				
				double w = s.getWidth();
				double h = s.getHeight();
				
				return Scaling.getUniformScaling(w, h, Math.min(tw, w), Math.min(th, h));
			}
		};
		
		// setup of the editor when the slide being edited changes
		this.slide.addListener((obs, ov, nv) -> {
			slideCanvas.getChildren().clear();
			this.selected.set(null);
			
			// unbind the scaling from the old value
			if (ov != null) {
				ov.scalingProperty().unbind();
				Iterator<ObservableSlideComponent<?>> components = ov.componentIterator();
				while (components.hasNext()) {
					ObservableSlideComponent<?> osr = components.next();
					osr.scalingProperty().unbind();
				}
			}
			
			if (nv != null) {
				// add the slide node to the slideCanvas
				Node rootEditPane = nv.getDisplayPane();
				slideCanvas.getChildren().add(rootEditPane);
				// bind the scale factor
				nv.scalingProperty().bind(scaleFactor);
				// setup the mouse event handler
				SlideRegionMouseEventHandler slideMouseHandler = new SlideRegionMouseEventHandler(nv, nv);
				rootEditPane.addEventHandler(MouseEvent.ANY, slideMouseHandler);
				rootEditPane.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
					selected.set(nv);
				});
				
				Iterator<ObservableSlideComponent<?>> components = nv.componentIterator();
				while (components.hasNext()) {
					ObservableSlideComponent<?> osr = components.next();
					Node pane = osr.getDisplayPane();
					// bind the scale factor
					osr.scalingProperty().bind(scaleFactor);
					// setup the mouse event handler
					SlideRegionMouseEventHandler mouseHandler = new SlideRegionMouseEventHandler(nv, osr);
					osr.getEditBorderNode().addEventHandler(MouseEvent.ANY, mouseHandler);
					osr.getEditBorderNode().addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
						selected.set(osr);
					});
				}
				
				// invalidate the scale and sizes because the slide
				// might be a different target resolution than the last
				scaleFactor.invalidate();
				widthSizing.invalidate();
				heightSizing.invalidate();
			}
		});
		
		// reordering components
		ribbon.addEventHandler(SlideEditorEvent.ORDER, (e) -> {
			ObservableSlideComponent<?> component = e.component;
			if (e.operation == SlideComponentOrderEvent.OPERATION_BACK) {
				slide.get().moveComponentBack(component);
			} else if (e.operation == SlideComponentOrderEvent.OPERATION_FRONT) {
				slide.get().moveComponentFront(component);			
			} else if (e.operation == SlideComponentOrderEvent.OPERATION_BACKWARD) {
				slide.get().moveComponentDown(component);
			} else if (e.operation == SlideComponentOrderEvent.OPERATION_FORWARD) {
				slide.get().moveComponentUp(component);
			}
		});
		
		// setting the target resolution
		ribbon.addEventHandler(SlideEditorEvent.TARGET_RESOLUTION, (e) -> {
			scaleFactor.invalidate();
			widthSizing.invalidate();
			heightSizing.invalidate();
		});

		// adding components
		ribbon.addEventHandler(SlideEditorEvent.ADD_COMPONENT, (e) -> {
			ObservableSlideComponent<?> component = e.getComponent();
			
			if (e.isCentered()) {
				// compute the location (center it)
				double sw = slide.get().getWidth();
				double sh = slide.get().getHeight();
				double w = component.getWidth();
				double h = component.getHeight();
				component.setX((sw - w) * 0.5);
				component.setY((sh - h) * 0.5);
			}
			
			Node pane = component.getDisplayPane();
			slide.get().addComponent(component);
			
			// bind the scale factor
			component.scalingProperty().bind(scaleFactor);
			// setup the mouse event handler
			SlideRegionMouseEventHandler mouseHandler = new SlideRegionMouseEventHandler(slide.get(), component);
			component.getEditBorderNode().addEventHandler(MouseEvent.ANY, mouseHandler);
			component.getEditBorderNode().addEventHandler(MouseEvent.MOUSE_PRESSED, (ev) -> {
				selected.set(component);
			});
			
			if (e.isSelected()) {
				// set it as the selected component
				selected.set(component);
				
				// if the component type is media, then show the media dialog
				if (e instanceof MediaComponentAddEvent) {
					MediaType type = ((MediaComponentAddEvent)e).getMediaType();
					ObservableMediaComponent omc = (ObservableMediaComponent)component;
					MediaLibraryDialog dialog = new MediaLibraryDialog(
							getScene().getWindow(), 
							context,
							type);
					dialog.valueProperty().addListener((obs, ov, nv) -> {
						MediaObject mo = null;
						if (nv != null) {
							UUID id = nv.getId();
							MediaObject omo = omc.getMedia();
							if (omo != null) {
								mo = new MediaObject(id, nv.getName(), omo.getScaling(), omo.isLoop(), omo.isMute());
							} else {
								mo = new MediaObject(id, nv.getName(), ScaleType.UNIFORM, false, false);
							}
						}
						omc.setMedia(mo);
					});
					dialog.show();
				}
			}
		});
		
		// bindings

		ribbon.slideProperty().bind(this.slide);
		ribbon.componentProperty().bind(this.selected);
		animations.slideProperty().bind(this.slide);
		animations.componentProperty().bind(this.selected);
		
		// set values		
		
		this.addEventHandler(ApplicationEvent.ALL, e -> {
			handleApplicationEvent(e.getAction());
		});
	}
	
	public void setSlide(Slide slide) {
		if (slide == null) {
			this.slide.set(null);
		} else {
			this.slide.set(new ObservableSlide<Slide>(slide, context, SlideMode.EDIT));
		}
	}
	
	public Slide getSlide() {
		return this.slide.get().getRegion();
	}
	
	// METHODS
	
	private final void save(boolean saveAs) {
		ObservableSlide<Slide> os = this.slide.get();
		Slide slide = os.getRegion();
		
		AsyncTask<Slide> task = null;
		if (saveAs) {
			task = SlideActions.slidePromptSaveAs(
					this.context.getSlideLibrary(), 
					this.getScene().getWindow(), 
					slide);
		} else {
			task = SlideActions.slideSave(
					this.context.getSlideLibrary(), 
					this.getScene().getWindow(), 
					slide);
		}
		
		task.addSuccessHandler(e -> {
			Slide saved = (Slide)e.getSource().getValue();
			// make the current bible being edited act
			// as the one we saved so that any subsequent
			// saves save to this one and so that we don't
			// lose any changes made by the user in the
			// while the save as action was processing
			this.slide.get().getRegion().as(saved);
			// manually update the name field
			this.ribbon.setSlideName(saved.getName());
			// TODO track changes
//			// store the value of unsavedChanges
//			boolean moreChanges = this.unsavedChanges;
//			// did the user make changes while the save was happening?
//			if (!moreChanges) {
//				// make sure changing the name field doesn't
//				// flag it as unsaved
//				this.unsavedChanges = false;
//			}
		}).addCancelledOrFailedHandler(e -> {
			//this.unsavedChanges = true;
		}).execute(this.context.getExecutorService());
	}
	
	private final void copy(boolean cut) {
		ObservableSlide<?> slide = this.slide.get();
		ObservableSlideRegion<?> selected = this.selected.get();
		
		if (selected != null) {
			Clipboard cb = Clipboard.getSystemClipboard();
			ClipboardContent cc = new ClipboardContent();
			cc.put(DataFormat.PLAIN_TEXT, selected.getId().toString());
			try {
				cc.put(DataFormats.SLIDE_COMPONENT, XmlIO.save(selected.getRegion().copy()));
				cb.setContent(cc);
				
				if (cut) {
					// remove the component
					slide.removeComponent((ObservableSlideComponent<?>)selected);
				}
				
				// notify we changed
				this.stateChanged(ApplicationPaneEvent.REASON_DATA_COPIED);
			} catch (Exception e) {
				LOGGER.warn("Failed to serialize the slide component to the clipboard.", e);
			}
		}
	}
	
	private final void paste() {
		ObservableSlide<?> slide = this.slide.get();
		
		Clipboard cb = Clipboard.getSystemClipboard();
		Object data = cb.getContent(DataFormats.SLIDE_COMPONENT);
		if (data != null && data instanceof String) {
			SlideComponent sc;
			try {
				sc = (SlideComponent)XmlIO.read((String)data, AbstractSlideComponent.class);
				sc = sc.copy();
				sc.translate(20, 20);
				ObservableSlideComponent<?> osc = slide.observableSlideComponent(sc);
				this.ribbon.fireEvent(new SlideComponentAddEvent(this, this, osc, false, true));
			} catch (Exception e) {
				LOGGER.warn("Failed to parse the copied slide component that's in the clipboard: '" + data + "'", e);
			}
		}
	}
	
	private final void delete() {
		ObservableSlide<?> slide = this.slide.get();
		ObservableSlideRegion<?> selected = this.selected.get();
		
		if (selected != null && selected instanceof ObservableSlideComponent) {
			slide.removeComponent((ObservableSlideComponent<?>)selected);
			this.selected.set(slide);
		}
	}
	
	private void handleApplicationEvent(ApplicationAction action) {
		switch (action) {
			case SAVE:
				this.save(false);
				break;
			case SAVE_AS:
				this.save(true);
				break;
			case CUT:
				this.copy(true);
				break;
			case COPY:
				this.copy(false);
				break;
			case PASTE:
				this.paste();
				break;
			case DELETE:
				this.delete();
				break;
			default:
				return;
		}
	}
	
	@Override
	public boolean isApplicationActionEnabled(ApplicationAction action) {
		Node focused = this.getScene().getFocusOwner();
		ObservableSlideRegion<?> selected = this.selected.get();
		
		switch (action) {
			case SAVE:
			case SAVE_AS:
				return true;
			case COPY:
			case CUT:
			case DELETE:
				return selected != null && selected instanceof ObservableSlideComponent;
			case PASTE:
				Clipboard cb = Clipboard.getSystemClipboard();
				if (Fx.isNodeInFocusChain(focused, this.slidePreview)) {
					return cb.hasContent(DataFormats.SLIDE_COMPONENT);
				}
				return false;
			default:
				return false;
		}
	}

	@Override
	public void setDefaultFocus() {
		// NOTE: this is here so that the slide ribbon has focus initially so that
		// the focus is inside the editor pane, allowing the application actions
		// to work. selection of components in the editor isn't the same as standard
		// java fx selection
		this.ribbon.requestFocus();
	}
	
	@Override
	public boolean isApplicationActionVisible(ApplicationAction action) {
		return true;
	}

    /**
     * Called when the state of this pane changes.
     * @param reason the reason
     */
    private final void stateChanged(String reason) {
    	Scene scene = this.getScene();
    	// don't bother if there's no place to send the event to
    	if (scene != null) {
    		fireEvent(new ApplicationPaneEvent(this, this, ApplicationPaneEvent.STATE_CHANGED, this, reason));
    	}
    }
}
