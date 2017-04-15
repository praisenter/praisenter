package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;

import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// TODO button to pop out a big text editor

class TextRibbonTab extends ComponentEditorRibbonTab {
	private final TextArea text;

	public TextRibbonTab() {
		super("Text");
		
		this.text = new TextArea();
		this.text.setMaxHeight(75);
		this.text.setWrapText(true);
		this.text.setMaxWidth(175);
		
		// layout
		
		HBox row1 = new HBox(2, this.text);
		VBox layout = new VBox(2, row1);
		this.container.setCenter(layout);
	
		// events
		this.managedProperty().bind(this.visibleProperty());
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null && nv instanceof ObservableTextComponent && 
				!(nv instanceof ObservableCountdownComponent) &&
				!(nv instanceof ObservableDateTimeComponent) &&
				!(nv instanceof ObservableTextPlaceholderComponent)) {
				this.setVisible(true);
				ObservableTextComponent<?> otc = (ObservableTextComponent<?>)nv;
				this.text.setText(otc.getText()); 
			} else {
				this.text.setText(null); 
				this.setVisible(false);
			}
			mutating = false;
		});
		
		this.text.textProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setText(nv);
			}
		});
	}
}
