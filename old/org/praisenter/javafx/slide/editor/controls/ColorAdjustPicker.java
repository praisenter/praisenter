package org.praisenter.javafx.slide.editor.controls;

import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.effects.SlideColorAdjust;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class ColorAdjustPicker extends Button {
	private Stage dialog;
	private final ColorAdjustPickerPane pane;
	
	private final ObjectProperty<SlideColorAdjust> value = new SimpleObjectProperty<SlideColorAdjust>();
	
	public ColorAdjustPicker() {
		super("Color Adjustment...", ApplicationGlyphs.MEDIA_COLOR_ADJUST.duplicate());
		
		this.pane = new ColorAdjustPickerPane();
		this.pane.setPadding(new Insets(5, 5, 20, 5));

		this.value.bindBidirectional(this.pane.valueProperty());
		
		this.setOnAction(e -> {
			if (dialog == null) {
				dialog = new Stage(StageStyle.UTILITY);
				dialog.setTitle("Color Adjustment");
				dialog.initOwner(this.getScene().getWindow());
				dialog.initModality(Modality.WINDOW_MODAL);
				dialog.setScene(Fx.newSceneInheritCss(this.pane, this.getScene().getWindow()));
			}
			Bounds bounds = this.localToScreen(this.getBoundsInLocal());
			dialog.setX(bounds.getMinX());
			dialog.setY(bounds.getMaxY() + 4);
			dialog.show();
		});
	}
	
	public void setValue(SlideColorAdjust value) {
		this.value.set(value);
	}
	
	public SlideColorAdjust getValue() {
		return this.value.get();
	}
	
	public ObjectProperty<SlideColorAdjust> valueProperty() {
		return this.value;
	}
}
