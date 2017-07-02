package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.editor.SlideEditorContext;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

abstract class SlideRegionRibbonTab<T extends ObservableSlideRegion<?>> extends HBox {
	/** The context */
	protected final SlideEditorContext context;
	
	// UI
	
	/** The container for the tab content */
	protected final BorderPane container;
	
	// state
	
	/** True when the selected region is being changed */
	protected boolean mutating = false;
	
	public SlideRegionRibbonTab(SlideEditorContext context, String name) {
		this.context = context;
		
		this.container = new BorderPane();
		
		// bottom is the label
		Label lblName = new Label(name);
		lblName.setAlignment(Pos.BASELINE_CENTER);
		lblName.setMaxWidth(Double.MAX_VALUE);
		lblName.setPadding(new Insets(3, 0, 0, 0));
		lblName.setFont(Font.font("System", FontWeight.BOLD, 10));
		this.container.setBottom(lblName);
		
		// right is the separator
		this.getChildren().addAll(container, new Separator(Orientation.VERTICAL));
		this.setPadding(new Insets(3, 0, 3, 0));
		this.setSpacing(4);
		
		this.container.setMinWidth(USE_PREF_SIZE);
	}
	
	// FIXME don't think this is needed
//	protected void notifyComponentChanged(EditCommand command) {
//		fireEvent(new SlideChangedEvent(this, this, command));
//	}
}
