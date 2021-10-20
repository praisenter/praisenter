package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.StackOrderEditCommand;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

class StackingRibbonTab extends ComponentEditorRibbonTab {
	
	private final Button btnMoveUp;
	private final Button btnMoveDown;
	private final Button btnMoveFront;
	private final Button btnMoveBack;
	
	public StackingRibbonTab(SlideEditorContext context) {
		super(context, "Stacking");
		
		// controls
		
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
		
		// tooltips
		
		this.btnMoveBack.setTooltip(new Tooltip("Moves the selected component behind all other components."));
		this.btnMoveFront.setTooltip(new Tooltip("Moves the selected component in front of all other components."));
		this.btnMoveDown.setTooltip(new Tooltip("Moves the selected component down in the component stack."));
		this.btnMoveUp.setTooltip(new Tooltip("Moves the selected component up in the component stack."));
		
		// layout
		
		HBox row1 = new HBox(2, this.btnMoveUp, this.btnMoveDown);
		HBox row2 = new HBox(2, this.btnMoveFront, this.btnMoveBack);
		VBox layout = new VBox(2, row1, row2);
		this.container.setCenter(layout);
	
		this.managedProperty().bind(this.visibleProperty());
		this.setVisible(false);
		
		// events
		
		this.context.selectedProperty().addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null && nv instanceof ObservableSlideComponent) {
				this.setVisible(true);
				this.setDisable(false);
			} else {
				this.setVisible(false);
				this.setDisable(true);
			}
			mutating = false;
		});

		this.btnMoveUp.setOnAction((e) -> {
			ObservableSlideRegion<?> component = this.context.getSelected();
			ObservableSlide<?> slide = this.context.getSlide();
			if (component != null && component instanceof ObservableSlideComponent) {
				ObservableSlideComponent<?> osc = (ObservableSlideComponent<?>)component;
				
				int oldValue = slide.getComponents().indexOf(component);
				int newValue = oldValue + 1;
				this.applyCommand(new StackOrderEditCommand(oldValue, newValue, slide, osc, this.context.selectedProperty(), this.btnMoveUp));
			}
		});
		this.btnMoveDown.setOnAction((e) -> {
			ObservableSlideRegion<?> component = this.context.getSelected();
			ObservableSlide<?> slide = this.context.getSlide();
			if (component != null && component instanceof ObservableSlideComponent) {
				ObservableSlideComponent<?> osc = (ObservableSlideComponent<?>)component;
				
				int oldValue = slide.getComponents().indexOf(component);
				int newValue = oldValue - 1;
				this.applyCommand(new StackOrderEditCommand(oldValue, newValue, slide, osc, this.context.selectedProperty(), this.btnMoveDown));
			}
		});
		this.btnMoveFront.setOnAction((e) -> {
			ObservableSlideRegion<?> component = this.context.getSelected();
			ObservableSlide<?> slide = this.context.getSlide();
			if (component != null && component instanceof ObservableSlideComponent) {
				ObservableSlideComponent<?> osc = (ObservableSlideComponent<?>)component;
				
				int oldValue = slide.getComponents().indexOf(component);
				int newValue = Integer.MAX_VALUE;
				this.applyCommand(new StackOrderEditCommand(oldValue, newValue, slide, osc, this.context.selectedProperty(), this.btnMoveFront));
			}
		});
		this.btnMoveBack.setOnAction((e) -> {
			ObservableSlideRegion<?> component = this.context.getSelected();
			ObservableSlide<?> slide = this.context.getSlide();
			if (component != null && component instanceof ObservableSlideComponent) {
				ObservableSlideComponent<?> osc = (ObservableSlideComponent<?>)component;
				
				int oldValue = slide.getComponents().indexOf(component);
				int newValue = 0;
				this.applyCommand(new StackOrderEditCommand(oldValue, newValue, slide, osc, this.context.selectedProperty(), this.btnMoveBack));
			}
		});
	}
}
