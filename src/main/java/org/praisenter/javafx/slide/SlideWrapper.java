package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.easing.EasingType;
import org.praisenter.javafx.easing.Easings;
import org.praisenter.javafx.transition.CustomTransition;
import org.praisenter.javafx.transition.TransitionType;
import org.praisenter.javafx.transition.Transitions;
import org.praisenter.javafx.utility.JavaFxNodeHelper;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideTransition;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.object.MediaObject;

import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

// TODO need to address editing and resizing.  The building of the JavaFX UI is highly dependent on a number of things so we may end up needing to update a bunch of stuff (width/height in particluar)

public class SlideWrapper extends SlideRegionWrapper<Slide> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	final IntegerProperty x;
	final IntegerProperty y;
	final IntegerProperty width;
	final IntegerProperty height;
	final StringProperty name;
	final LongProperty time;
	final ObjectProperty<SlidePaint> background;
	final ObjectProperty<SlideStroke> border;
	final ListProperty<SlideTransition> animations;
	final ListProperty<SlideComponent> components;
	
	private Node backgroundNode;
	final Pane foregroundNode;
	private final Region borderNode;
	
	final List<SlideComponentWrapper> children;
	
	// players for audio/video
	final List<MediaPlayer> players;
	
	// the slide transition
	Transition transition;
	
	// any other playable animations
	ParallelTransition animation;
	
	public SlideWrapper(PraisenterContext context, Slide component, SlideMode mode) {
		super(context, component, mode);
		
		if (mode == SlideMode.EDIT) {
			this.x = new SimpleIntegerProperty();
			this.y = new SimpleIntegerProperty();
			this.width = new SimpleIntegerProperty();
			this.height = new SimpleIntegerProperty();
			this.name = new SimpleStringProperty();
			this.time = new SimpleLongProperty();
			this.background = new SimpleObjectProperty<SlidePaint>();
			this.border = new SimpleObjectProperty<SlideStroke>();
			this.animations = new SimpleListProperty<SlideTransition>();
			this.components = new SimpleListProperty<SlideComponent>();
		} else {
			this.x = null;
			this.y = null;
			this.width = null;
			this.height = null;
			this.name = null;
			this.time = null;
			this.background = null;
			this.border = null;
			this.animations = null;
			this.components = null;
		}
		
		this.foregroundNode = new Pane();
		this.borderNode = new Region();
		this.children = new ArrayList<>();
		this.players = new ArrayList<MediaPlayer>();
		
		build();
	}

	void build() {
		
		// TODO positioning should not be reflected here, instead it should be reflected on the window (when presenting) or node containing this slide (when editing)
		
		// width/height
		int w = this.component.getWidth();
		int h = this.component.getHeight();
//		JavaFxNodeHelper.setSize(this.backgroundNode, w, h);
		JavaFxNodeHelper.setSize(this.foregroundNode, w, h);
		
		// border
		// the border will go on the background node
		SlideStroke bdr = this.component.getBorder();
		if (bdr != null) {
			this.borderNode.setBorder(new Border(getBorderStroke(bdr)));
		}

		// background
		SlidePaint bg = this.component.getBackground();
		if (bg instanceof MediaObject) {
			// get the media id
			MediaObject mo = (MediaObject)bg;
			UUID id = mo.getId();
			// make sure the id is present
			if (id != null) {
				// get the media
				Media m = this.context.getMediaLibrary().get(id);
				// check for missing media
				if (m != null) {
					// check the media type
					if (m.getMetadata().getType() == MediaType.VIDEO) {
						// check if we need to show a single frame (EDIT) or the video (PRESENT)
						if (this.mode == SlideMode.PRESENT) {
							try {
								// attempt to open the media
								javafx.scene.media.Media media = new javafx.scene.media.Media(m.getMetadata().getPath().toUri().toString());
								// create a player
								MediaPlayer player = new MediaPlayer(media);
								// set the player attributes
								player.setMute(mo.isMute());
								player.setCycleCount(mo.isLoop() ? MediaPlayer.INDEFINITE : 0);
								MediaView view = new MediaView(player);
								// set the scaling
								if (mo.getScaling() == ScaleType.NONUNIFORM) { 
									view.setFitWidth(w);
									view.setFitHeight(h);
								} else if (mo.getScaling() == ScaleType.UNIFORM) {
									// set the fit w/h based on the min
									if (w < h) {
										view.setFitWidth(w);
									} else {
										view.setFitWidth(h);
									}
								} else {
									// then center it
									view.setLayoutX((w - m.getMetadata().getWidth()) * 0.5);
									view.setLayoutY((h - m.getMetadata().getHeight()) * 0.5);
								}
								// add the player to the background node
								this.backgroundNode = view;
							} catch (Exception ex) {
								// if it blows up, then just log the error
								LOGGER.error("Failed to create media or media player.", ex);
							}
						} else {
							// if not in present mode, then just show the single frame
							try  {
								Image image = this.context.getImageCache().get(this.context.getMediaLibrary().getFramePath(m));
								VBox img = new VBox();
								JavaFxNodeHelper.setSize(img, w, h);
								Rectangle r = new Rectangle(0, 0, w, h);
								if (bdr != null) {
									r.setArcHeight(bdr.getRadius() * 2);
									r.setArcWidth(bdr.getRadius() * 2);
								}
								img.setClip(r);
								img.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, this.getBackgroundSize(mo.getScaling()))));
								this.backgroundNode = img;
							} catch (Exception ex) {
								// just log the error
								LOGGER.warn("Failed to load image " + m.getMetadata().getPath() + ".", ex);
							}
						}
					} else if (m.getMetadata().getType() == MediaType.IMAGE) {
						// image
						try  {
							Image image = this.context.getImageCache().get(m.getMetadata().getPath());
							VBox img = new VBox();
							JavaFxNodeHelper.setSize(img, w, h);
							Rectangle r = new Rectangle(0, 0, w, h);
							if (bdr != null) {
								r.setArcHeight(bdr.getRadius() * 2);
								r.setArcWidth(bdr.getRadius() * 2);
							}
							img.setClip(r);
							img.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, this.getBackgroundSize(mo.getScaling()))));
							this.backgroundNode = img;
						} catch (Exception ex) {
							// just log the error
							LOGGER.warn("Failed to load image " + m.getMetadata().getPath() + ".", ex);
						}
					} else {
						// log warning about type (audio)
						LOGGER.warn("Invalid media type for background {} with path {}.", m.getMetadata().getType(), m.getMetadata().getPath());
					}
				} else {
					// log warning about missing media
					LOGGER.warn("The referenced media {} was not found in the media library.", id);
				}
			} else {
				LOGGER.warn("The media id is null.");
			}
		} else {
			Paint paint = getPaint(bg);
			VBox bgNode = new VBox();
			JavaFxNodeHelper.setSize(bgNode, w, h);
			bgNode.setBackground(new Background(new BackgroundFill(paint, bdr != null ? new CornerRadii(bdr.getRadius()) : null, null)));
			this.backgroundNode = bgNode;
		}
		
		// this assumes the components are in the correct order
		Iterator<SlideComponent> it = this.component.getComponentIterator();
		while (it.hasNext()) {
			SlideComponent sc = it.next();
			SlideComponentWrapper wrapper = new SlideComponentWrapper(this.context, sc, this.mode);
			// add the node to the foreground
			this.foregroundNode.getChildren().add(wrapper.node);
			this.children.add(wrapper);
		}

		// set the slide transition
		// TODO the exiting slide will also need to be transitioned, maybe add a method to get a transition to exit the current slide
		SlideTransition stx = this.component.getTransition(this.component.getId());
		if (stx != null) {
			CustomTransition ctx = Transitions.getTransition(stx.getTransitionId());
			// TODO not sure if IN is correct here or not
			ctx.setInterpolator(Easings.getEasing(stx.getEasingId(), EasingType.IN));
			ctx.setAutoReverse(false);
			ctx.setCycleCount(0);
			ctx.setDelay(Duration.ZERO);
			ctx.setDuration(Duration.millis(stx.getDuration()));
			// TODO this depends on the outgoing and incoming slides
			// ctx.setNode(node);
			ctx.setType(TransitionType.IN);
			this.transition = ctx;
		}
		
		// TODO other animations
		
		// now if we are in edit mode then wire up all
		// the editable properties
		if (this.mode == SlideMode.EDIT) {
			// x/y
			// wire up
			this.x.set(this.component.getX());
			this.y.set(this.component.getY());
			this.x.addListener((obs, o, n) -> {
				int v = n.intValue();
				this.component.setX(v);
			});
			this.y.addListener((obs, o, n) -> {
				int v = n.intValue();
				this.component.setY(v);
			});

			// width/height
			// wire up
			this.width.set(w);
			this.height.set(h);
			this.width.addListener((obs, o, n) -> {
				int v = n.intValue();
				int ch = height.get();
				this.component.setWidth(v);
				//JavaFxNodeHelper.setSize(this.backgroundNode, v, ch);
				JavaFxNodeHelper.setSize(this.foregroundNode, v, ch);
			});
			this.height.addListener((obs, o, n) -> {
				int v = n.intValue();
				int cw = width.get();
				this.component.setHeight(v);
				//JavaFxNodeHelper.setSize(this.backgroundNode, cw, v);
				JavaFxNodeHelper.setSize(this.foregroundNode, cw, v);
			});

			// border
			// wire up
			this.border.set(bdr);
			this.border.addListener((obs, o, n) -> {
				this.component.setBorder(n);
				Border border = new Border(getBorderStroke(n));
				this.borderNode.setBorder(border);
			});

			// background
			// wire up
			this.background.set(bg);
			this.background.addListener((obs, o, n) -> {
				this.component.setBackground(n);
				Background background = getBackground(n);
				//this.backgroundNode.setBackground(background);
			});
			
			// name
			// wire up
			this.name.set(this.component.getName());
			this.name.addListener((obs, o, n) -> {
				this.component.setName(n);
			});
			
			// time
			// wire up
			this.time.set(this.component.getTime());
			this.time.addListener((obs, o, n) -> {
				this.component.setTime(n.intValue());
			});
			
			// components
			// wire up
			this.components.addListener((Change<? extends SlideComponent> c) -> {
				while (c.next()) {
					if (c.wasPermutated()) {
						// this means they were reordered
						// this will happen when an object is set backward or
						// forward and the list is re-sorted
						// reorder the components in the foreground
						for (int i = c.getFrom(); i < c.getTo(); ++i) {
							Collections.swap(foregroundNode.getChildren(), i, c.getPermutation(i));
							Collections.swap(children, i, c.getPermutation(i));
						}
					} else if (c.wasUpdated()) {
						// elements between from and to were updated
						// this means that items a certain indexes were replaced
						// TODO this shouldn't happen, but i suppose its possible
					} else {
						// items were either removed or added
						for (SlideComponent remitem : c.getRemoved()) {
							// remove from the children and from the foreground
							component.removeComponent(remitem);
							// remove based on id from the scene graph
							foregroundNode.getChildren().removeIf(n -> n.getUserData().equals(remitem.getId()));
							// remove it from the children list too
							children.removeIf(n -> n.component.getId().equals(remitem.getId()));
						}
						for (SlideComponent additem : c.getAddedSubList()) {
							// add to the children and foreground
							component.addComponent(additem);
							// convert it
							SlideComponentWrapper wrapper = new SlideComponentWrapper(this.context, additem, this.mode);
							// add it to the foreground and the children list
							foregroundNode.getChildren().add(wrapper.node);
							children.add(wrapper);
						}
					}
				 }
			});
			
			// TODO animations
		}
	}
	

	public Node getBackgroundNode() {
		return backgroundNode;
	}

	public Pane getForegroundNode() {
		return foregroundNode;
	}
	
	public Region getBorderNode() {
		return borderNode;
	}
	
	public List<MediaPlayer> getMediaPlayers() {
		List<MediaPlayer> players = new ArrayList<>();
		
		if (this.backgroundNode instanceof MediaView) {
			players.add(((MediaView)this.backgroundNode).getMediaPlayer());
		}
		for (SlideComponentWrapper wrapper : this.children) {
			players.addAll(wrapper.getMediaPlayers());
		}
		
		return players;
	}
}
