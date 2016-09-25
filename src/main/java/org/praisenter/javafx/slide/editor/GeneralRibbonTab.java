package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GeneralRibbonTab extends EditorRibbonTab {
	
	private final Slider sldOpacity;
	private final Button btnMoveUp;
	private final Button btnMoveDown;
	private final Button btnMoveFront;
	private final Button btnMoveBack;
	
	public GeneralRibbonTab() {
		super("General");
		
		// controls
		
		Label lblOpacity = new Label("\u03B1");
		this.sldOpacity = new Slider(0.0, 1.0, 1.0);
		this.sldOpacity.setPrefWidth(60);
		this.sldOpacity.setMaxWidth(60);
		lblOpacity.setGraphic(sldOpacity);
		lblOpacity.setGraphicTextGap(5);
		
		Rectangle upr1 = new Rectangle(1, 1, 10, 10);
		Rectangle upr2 = new Rectangle(5, 5, 10, 10);
		upr1.setFill(Color.GRAY);
		upr2.setFill(Color.BLUE);
		upr1.setSmooth(false);
		Pane upPane = new Pane(upr1, upr2);
		
		Rectangle downr1 = new Rectangle(1, 1, 10, 10);
		Rectangle downr2 = new Rectangle(5, 5, 10, 10);
		downr1.setFill(Color.BLUE);
		downr2.setFill(Color.GRAY);
		downr2.setSmooth(false);
		Pane downPane = new Pane(downr1, downr2);

		this.btnMoveUp = new Button("", upPane);
		this.btnMoveDown = new Button("", downPane);
		
		Rectangle front1 = new Rectangle(1, 1, 10, 10);
		Rectangle front2 = new Rectangle(3, 3, 10, 10);
		Rectangle front3 = new Rectangle(5, 5, 10, 10);
		front1.setFill(Color.DARKGRAY);
		front2.setFill(Color.GRAY);
		front3.setFill(Color.BLUE);
		front1.setSmooth(false);
		front2.setSmooth(false);
		front3.setSmooth(false);
		Pane frontPane = new Pane(front1, front2, front3);
		
		Rectangle back1 = new Rectangle(5, 5, 10, 10);
		Rectangle back2 = new Rectangle(3, 3, 10, 10);
		Rectangle back3 = new Rectangle(1, 1, 10, 10);
		back1.setFill(Color.DARKGRAY);
		back2.setFill(Color.GRAY);
		back3.setFill(Color.BLUE);
		back1.setSmooth(false);
		back2.setSmooth(false);
		back3.setSmooth(false);
		Pane backPane = new Pane(back3, back2, back1);
		
		this.btnMoveFront = new Button("", frontPane);
		this.btnMoveBack = new Button("", backPane);
		
		// layout
		
		HBox row1 = new HBox(2, this.btnMoveUp, this.btnMoveDown);
		HBox row2 = new HBox(2, this.btnMoveFront, this.btnMoveBack);
		HBox row3 = new HBox(2, lblOpacity);
		VBox layout = new VBox(2, row1, row2, row3);
		this.container.setCenter(layout);
	
		// events
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			this.sldOpacity.setValue(nv.getOpacity());
			mutating = false;
		});

		this.sldOpacity.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null) {
				component.setOpacity(nv.doubleValue());
			}
		});
		
		this.btnMoveUp.setOnAction((e) -> {
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableSlideComponent) {
				ObservableSlideComponent<?> osc = (ObservableSlideComponent<?>)component;
				fireEvent(new SlideComponentOrderEvent(this.btnMoveUp, GeneralRibbonTab.this, osc, SlideComponentOrderEvent.OPERATION_FORWARD));
			}
		});
		this.btnMoveDown.setOnAction((e) -> {
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableSlideComponent) {
				ObservableSlideComponent<?> osc = (ObservableSlideComponent<?>)component;
				fireEvent(new SlideComponentOrderEvent(this.btnMoveDown, GeneralRibbonTab.this, osc, SlideComponentOrderEvent.OPERATION_BACKWARD));
			}
		});
		this.btnMoveFront.setOnAction((e) -> {
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableSlideComponent) {
				ObservableSlideComponent<?> osc = (ObservableSlideComponent<?>)component;
				fireEvent(new SlideComponentOrderEvent(this.btnMoveFront, GeneralRibbonTab.this, osc, SlideComponentOrderEvent.OPERATION_FRONT));
			}
		});
		this.btnMoveBack.setOnAction((e) -> {
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableSlideComponent) {
				ObservableSlideComponent<?> osc = (ObservableSlideComponent<?>)component;
				fireEvent(new SlideComponentOrderEvent(this.btnMoveBack, GeneralRibbonTab.this, osc, SlideComponentOrderEvent.OPERATION_BACK));
			}
		});
	}
}
