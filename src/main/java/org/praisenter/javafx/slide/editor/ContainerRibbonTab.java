package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class ContainerRibbonTab extends ComponentEditorRibbonTab {
	
	private final Slider sldOpacity;
	
	public ContainerRibbonTab() {
		super("Container");
		
		// controls
		
		Label lblOpacity = new Label("\u03B1");
		this.sldOpacity = new Slider(0.0, 1.0, 1.0);
		this.sldOpacity.setPrefWidth(60);
		this.sldOpacity.setMaxWidth(60);
		lblOpacity.setGraphic(sldOpacity);
		lblOpacity.setGraphicTextGap(5);
		
		// tooltips
		
		this.sldOpacity.setTooltip(new Tooltip("The opacity of the entire component"));
		
		// layout
		
		HBox row1 = new HBox(2, lblOpacity);
		VBox layout = new VBox(2, row1);
		this.container.setCenter(layout);
	
		// events
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null) {
				this.setDisable(false);
				this.sldOpacity.setValue(nv.getOpacity());
			} else {
				this.setDisable(true);
				this.sldOpacity.setValue(1);
			}
			mutating = false;
		});

		this.sldOpacity.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null) {
				component.setOpacity(nv.doubleValue());
			}
		});
	}
}
