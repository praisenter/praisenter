package org.praisenter.ui.slide;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.utility.Scaling;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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

// FEATURE add quick UI for common changes (font alignment, color, etc)

// TODO right click
// TODO selection

final class EditNode extends StackPane {
	private static final double THUMB_SIZE = 10;
	private static final double BORDER_OFFSET = 6;
	private static final double BORDER_SIZE = 1;
	private static final double DASH_SIZE = 4;
	
	// this			StackPane
	//	+- sp		StackPane		Border + padding
	//  +- thumb1	Rectangle		
	//  +- thumb2	Rectangle
	//	+- ...
	
	private final DocumentContext<Slide> document;
	private final ObjectProperty<SlideComponent> component;
	private final DoubleProperty scale;
	private final BooleanProperty selected;
	
	// for selection, move, and resize

	private Cursor cursor;
	
	/** The start x value of the mouse gesture */
	private double sx;
	
	/** The start y value of the mouse gesture */
	private double sy;
	
	/** The current x coordinate of the region */
	private double x;
	
	/** The current y coordinate of the region */
	private double y;
	
	/** The current width of the region */
	private double w;
	
	/** The current height of the region */
	private double h;
	
//	private final ContextMenu contextMenu;
	
	private final ObservableList<SlideComponent> components;
	
	public EditNode(
			DocumentContext<Slide> document,
			SlideComponent component) {
		this.document = document;
		this.component = new SimpleObjectProperty<>(component);
		this.scale = new SimpleDoubleProperty(1);
		this.selected = new SimpleBooleanProperty(false);
		
//		this.contextMenu = new ContextMenu();
//		this.contextMenu.getItems().addAll(
////				this.createMenuItem(Action.NEW_BOOK),
////				this.createMenuItem(Action.NEW_CHAPTER),
////				this.createMenuItem(Action.NEW_VERSE),
////				new SeparatorMenuItem(),
//				this.createMenuItem(Action.COPY),
//				this.createMenuItem(Action.CUT),
//				this.createMenuItem(Action.PASTE),
////				new SeparatorMenuItem(),
////				this.createMenuItem(Action.REORDER),
////				this.createMenuItem(Action.RENUMBER),
//				new SeparatorMenuItem(),
//				this.createMenuItem(Action.DELETE)
//			);
//		this.contextMenu.setAutoHide(true);
//		this.setOnContextMenuRequested(e -> this.contextMenu.show(this, e.getScreenX(), e.getScreenY()));
		
		List<Double> dashes = new ArrayList<Double>();
		dashes.add(DASH_SIZE);
		dashes.add(DASH_SIZE);
		
		Region border = new Region();
		border.setBorder(new Border(
				new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, BORDER_SIZE * 2, 0.0, dashes), null, new BorderWidths(BORDER_SIZE))
				,new BorderStroke(Color.WHITE, new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, BORDER_SIZE * 2, DASH_SIZE, dashes), null, new BorderWidths(BORDER_SIZE))
				));
		border.setSnapToPixel(true);
//		border.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

		StackPane sp = new StackPane(border);
		sp.setPadding(new Insets(BORDER_OFFSET));
		sp.setSnapToPixel(true);
		
//		Rectangle bc = new Rectangle();
//		bc.widthProperty().bind(sp.widthProperty());
//		bc.heightProperty().bind(sp.heightProperty());
//		sp.clipProperty().bind(Bindings.createObjectBinding(() -> {
//			Rectangle n = new Rectangle(BORDER_SIZE + BORDER_OFFSET, BORDER_SIZE + BORDER_OFFSET, bc.getWidth() - BORDER_SIZE * 2 - BORDER_OFFSET * 2, bc.getHeight() - BORDER_SIZE * 2 - BORDER_OFFSET * 2);
////			return n;
//			return Shape.subtract(bc, n);
//		}, bc.widthProperty(), bc.heightProperty()));
		
		// TODO this could get really slow in the future, will need to keep an eye on this (the process of computing a clip for the border)
		this.components = FXCollections.observableArrayList(c -> {
			return new Observable[] {
				c.xProperty(),
				c.yProperty(),
				c.widthProperty(),
				c.heightProperty()
			};
		});
		
		Bindings.bindContent(this.components, this.document.getDocument().getComponents());
		
		// the border needs to be clipped by any components that are above this component
		border.clipProperty().bind(Bindings.createObjectBinding(() -> {
			SlideComponent me = this.component.get();
			double scale = this.scale.get();
			boolean isSelected = this.selected.get();
			
			if (isSelected) return null;
			
			Shape clip = null;

			double offsetX = me.getX();
			double offsetY = me.getY();
			
			boolean found = false;
			for (SlideComponent sc : this.components) {
				if (found && me.isOverlapping(sc)) {
					// check if we need to initialize the clip
					if (clip == null) {
						// set it to the bounds of the border node
						clip = new Rectangle(-BORDER_SIZE, -BORDER_SIZE, me.getWidth() * scale + BORDER_SIZE * 4, me.getHeight() * scale + BORDER_SIZE * 4);
					}
					// compute the offset bounds of this node
					Rectangle temp = new Rectangle((sc.getX() - offsetX) * scale, (sc.getY() - offsetY) * scale, sc.getWidth() * scale, sc.getHeight() * scale);
					// subtract this other bounds from the bounds of this node
					clip = Shape.subtract(clip, temp);
				}
				if (sc == me) {
					found = true;
				}
			}
			
			return clip;
		}, this.components, this.scale, this.selected));
		
		Rectangle tlThumb = new Rectangle(THUMB_SIZE, THUMB_SIZE);
		Rectangle tThumb = new Rectangle(THUMB_SIZE, THUMB_SIZE);
		Rectangle trThumb = new Rectangle(THUMB_SIZE, THUMB_SIZE);
		Rectangle rThumb = new Rectangle(THUMB_SIZE, THUMB_SIZE);
		Rectangle brThumb = new Rectangle(THUMB_SIZE, THUMB_SIZE);
		Rectangle bThumb = new Rectangle(THUMB_SIZE, THUMB_SIZE);
		Rectangle blThumb = new Rectangle(THUMB_SIZE, THUMB_SIZE);
		Rectangle lThumb = new Rectangle(THUMB_SIZE, THUMB_SIZE);
		
		StackPane.setAlignment(tlThumb, Pos.TOP_LEFT);
		StackPane.setAlignment(tThumb, Pos.TOP_CENTER);
		StackPane.setAlignment(trThumb, Pos.TOP_RIGHT);
		StackPane.setAlignment(rThumb, Pos.CENTER_RIGHT);
		StackPane.setAlignment(brThumb, Pos.BOTTOM_RIGHT);
		StackPane.setAlignment(bThumb, Pos.BOTTOM_CENTER);
		StackPane.setAlignment(blThumb, Pos.BOTTOM_LEFT);
		StackPane.setAlignment(lThumb, Pos.CENTER_LEFT);
		
		tlThumb.setCursor(Cursor.NW_RESIZE);
		tThumb.setCursor(Cursor.N_RESIZE);
		trThumb.setCursor(Cursor.NE_RESIZE);
		rThumb.setCursor(Cursor.E_RESIZE);
		brThumb.setCursor(Cursor.SE_RESIZE);
		bThumb.setCursor(Cursor.S_RESIZE);
		blThumb.setCursor(Cursor.SW_RESIZE);
		lThumb.setCursor(Cursor.W_RESIZE);
		
		Rectangle[] thumbs = new Rectangle[] {
			tlThumb,
			tThumb,
			trThumb,
			rThumb,
			brThumb,
			bThumb,
			blThumb,
			lThumb
		};
		
		for (Rectangle r : thumbs) {
			r.setFill(Color.WHITE);
			r.setStroke(Color.BLACK);
			r.setStrokeWidth(BORDER_SIZE);
			r.setStrokeType(StrokeType.CENTERED);
			r.setStrokeLineCap(StrokeLineCap.BUTT);
			r.setStrokeLineJoin(StrokeLineJoin.MITER);
			r.setStrokeMiterLimit(BORDER_SIZE);
			r.addEventHandler(MouseEvent.ANY, e -> {
				if (e.getEventType() == MouseEvent.MOUSE_PRESSED ||
					e.getEventType() == MouseEvent.MOUSE_CLICKED) {
					pressed(e, r.getCursor());
				} else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
					dragged(e);
				} else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
					apply(e);
				}
				e.consume();
			});
		}
		
		this.getChildren().addAll(sp, tlThumb, tThumb, trThumb, rThumb, brThumb, bThumb, blThumb, lThumb);
		
		this.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			return Math.ceil(component.getWidth() * this.scale.get()) + BORDER_OFFSET * 2;
		}, component.widthProperty(), this.scale));
		
		this.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
			return Math.ceil(component.getHeight() * this.scale.get()) + BORDER_OFFSET * 2;
		}, component.heightProperty(), this.scale));
		
		this.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
			return Math.floor(component.getX() * this.scale.get()) - BORDER_OFFSET;
		}, component.xProperty(), this.scale));
		
		this.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
			return Math.floor(component.getY() * this.scale.get()) - BORDER_OFFSET;
		}, component.yProperty(), this.scale));
		
		for (Rectangle r : thumbs) {
			r.visibleProperty().bind(this.selected);
		}
		
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> this.selected.set(true));
		this.addEventHandler(MouseEvent.ANY, e -> {
			if (e.getEventType() == MouseEvent.MOUSE_PRESSED ||
				e.getEventType() == MouseEvent.MOUSE_CLICKED) {
				pressed(e, Cursor.MOVE);
			} else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				dragged(e);
			} else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
				apply(e);
			}
			e.consume();
		});
		
		this.setCursor(Cursor.MOVE);
		this.setSnapToPixel(true);
	}

//	private MenuItem createMenuItem(Action action) {
//		MenuItem mnu = new MenuItem(Translations.get(action.getMessageKey()));
//		if (action.getGraphicSupplier() != null) {
//			//mnu.setGraphic(action.getGraphicSupplier().get());
//		}
//		// NOTE: due to bug in JavaFX, we don't apply the accelerator here
//		//mnu.setAccelerator(value);
//		// TODO need to send action to parent or define the context menu on the parent? OR don't show a context menu, but show a toolbar above the component?
//		//mnu.setOnAction(e -> this.executeAction(action));
//		mnu.setUserData(action);
//		return mnu;
//	}
	
	/**
	 * Called when a mouse button has been pressed on the component.
	 * @param event the event
	 */
	private void pressed(MouseEvent event, Cursor cursor) {
		this.cursor = cursor;
		
		// record the scene coordinates of the start
		sx = event.getSceneX();
		sy = event.getSceneY();
		
		// record the original x,y coordinates of the slide component
		x = component.get().getX();
		y = component.get().getY();
		w = component.get().getWidth();
		h = component.get().getHeight();
	}
	
	/**
	 * Called when a mouse drag gesture involves the component.
	 * @param event the event
	 */
	private void dragged(MouseEvent event) {
		// only components can be moved or resized
		
		// compute the integer difference in position
		// of the mouse from the start and scale it
		// by the scale factor
		double nx = event.getSceneX();
		double ny = event.getSceneY();
		double dx = nx - sx;
		double dy = ny - sy;
		
		// get the scaled (transformed translation)
		double sf = 1.0 / this.scale.get();
		double dxi = dx * sf;
		double dyi = dy * sf;
		
		// are we moving the node?
		if (cursor == Cursor.MOVE) {				
			// we SET the x/y for accuracy
			this.positionChanged(x + dxi, y + dyi);
		}
		
		if (cursor == Cursor.E_RESIZE) {
			dxi = clamp(w, dxi);
			this.sizeChanged(x, y, w + dxi, h);
		} else if (cursor == Cursor.S_RESIZE) {
			dyi = clamp(h, dyi);
			this.sizeChanged(x, y, w, h + dyi);
		} else if (cursor == Cursor.N_RESIZE) {
			dyi = -clamp(h, -dyi);
			this.sizeChanged(x, y + dyi, w, h - dyi);
		} else if (cursor == Cursor.W_RESIZE) {
			dxi = -clamp(w, -dxi);
			this.sizeChanged(x + dxi, y, w - dxi, h);
		} else if (cursor == Cursor.SE_RESIZE) {
			dxi = clamp(w, dxi);
			dyi = clamp(h, dyi);
			this.sizeChanged(x, y, w + dxi, h + dyi);
		} else if (cursor == Cursor.SW_RESIZE) {
			dxi = -clamp(w, -dxi);
			dyi = clamp(h, dyi);
			this.sizeChanged(x + dxi, y, w - dxi, h + dyi);
		} else if (cursor == Cursor.NE_RESIZE) {
			dxi = clamp(w, dxi);
			dyi = -clamp(h, -dyi);
			this.sizeChanged(x, y + dyi, w + dxi, h - dyi);
		} else if (cursor == Cursor.NW_RESIZE) {
			dxi = -clamp(w, -dxi);
			dyi = -clamp(h, -dyi);
			this.sizeChanged(x + dxi, y + dyi, w - dxi, h - dyi);
		}
	}
	
	/**
	 * Clamps the given dimension + value to no less than MIN_DIMENSION.
	 * @param dimension the dimension
	 * @param value the value
	 * @return double
	 */
	private static double clamp(double dimension, double value) {
		if (value < 0) {
			if (dimension + value < 20) {
				return -Math.floor(dimension - 20);
			}
		}
		return Math.floor(value);
	}
	
	/**
	 * Calls the position changed handler.
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 */
	private void positionChanged(double x, double y) {
		this.component.get().setX(x);
		this.component.get().setY(y);
	}
	
	/**
	 * Calls the size changed handler.
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 * @param w the new width
	 * @param h the new height
	 */
	private void sizeChanged(double x, double y, double w, double h) {
		SlideComponent component = this.component.get();
		component.setX(x);
		component.setY(y);
		component.setWidth(w);
		component.setHeight(h);
	}
	
	/**
	 * Records the action performed to the undo manager for easy undo/redo.
	 * @param e the mouse event
	 */
	private void apply(MouseEvent e) {
		SlideComponent component = this.component.get();
		
		double sx = this.x;
		double sy = this.y;
		double sw = this.w;
		double sh = this.h;
		
		double nx = component.getX();
		double ny = component.getY();
		double nw = component.getWidth();
		double nh = component.getHeight();
		
		if (sx != nx || 
			sy != ny ||
			sw != nw ||
			sh != nh) {
			SlideComponentBoundsEdit be = new SlideComponentBoundsEdit(
					component.xProperty(), 
					component.yProperty(),
					component.widthProperty(),
					component.heightProperty(),
					sx, sy, sw, sh, 
					nx, ny, nw, nh);
			
			// apply the undo
			this.document.getUndoManager().addEdit(be);
		}
	}
	
	public ReadOnlyObjectProperty<SlideComponent> componentProperty() {
		return this.component;
	}
	
	public SlideComponent getComponent() {
		return this.component.get();
	}
	
	public DoubleProperty scaleProperty() {
		return this.scale;
	}
	
	public double getScale() {
		return this.scale.get();
	}
	
	public void setScale(double scale) {
		this.scale.set(scale);
	}
	
	public BooleanProperty selectedProperty() {
		return this.selected;
	}
	
	public boolean isSelected() {
		return this.selected.get();
	}
	
	public void setSelected(boolean flag) {
		this.selected.set(flag);
	}
}
