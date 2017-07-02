package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.javafx.Option;
import org.praisenter.javafx.command.CommandFactory;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.converters.PaintConverter;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.ShadowEditCommand;
import org.praisenter.javafx.slide.editor.commands.SlideEditorCommandFactory;
import org.praisenter.slide.graphics.ShadowType;
import org.praisenter.slide.graphics.SlideShadow;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

class ShadowRibbonTab extends ComponentEditorRibbonTab {

	private final ComboBox<Option<ShadowOption>> cmbType;
	private final ColorPicker pkrColor;
	private final Spinner<Double> spnX;
	private final Spinner<Double> spnY;
	private final Spinner<Double> spnRadius;
	private final Spinner<Double> spnSpread;

	public ShadowRibbonTab(SlideEditorContext context) {
		super(context, "Shadow");

		ObservableList<Option<ShadowOption>> types = FXCollections.observableArrayList();
		types.add(new Option<ShadowOption>("None", ShadowOption.NONE));
		types.add(new Option<ShadowOption>("Outer", ShadowOption.OUTER));
		types.add(new Option<ShadowOption>("Inner", ShadowOption.INNER));
		
		this.cmbType = new ComboBox<Option<ShadowOption>>(types);
		this.pkrColor = new ColorPicker();
		this.spnX = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0);
		this.spnY = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0);
		this.spnRadius = new Spinner<Double>(0.0, 127.0, 10.0, 1.0);
		this.spnSpread = new Spinner<Double>(0.0, 1.0, 0.0, 0.05);
		
		// setup values
		this.cmbType.setValue(types.get(0));
		this.pkrColor.setValue(Color.BLACK);

		// setup look
		this.pkrColor.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
		this.pkrColor.setStyle("-fx-color-label-visible: false;");
		this.spnX.setPrefWidth(60);
		this.spnY.setPrefWidth(60);
		this.spnRadius.setPrefWidth(60);
		this.spnSpread.setPrefWidth(60);
		this.spnX.setEditable(true);
		this.spnY.setEditable(true);
		this.spnRadius.setEditable(true);
		this.spnSpread.setEditable(true);
		
		// tooltips
		this.pkrColor.setTooltip(new Tooltip("Shadow Color"));
		this.spnX.setTooltip(new Tooltip("The shadow offset along the x-axis"));
		this.spnY.setTooltip(new Tooltip("The shadow offset along the y-axis"));
		this.cmbType.setTooltip(new Tooltip("Whether it's an inner or outer shadow"));
		this.spnRadius.setTooltip(new Tooltip("The shadow radius"));
		this.spnSpread.setTooltip(new Tooltip("The shadow spread"));
		
		this.cmbType.valueProperty().addListener((obs, ov, nv) -> {
			boolean flag = nv.getValue() == ShadowOption.NONE;
			pkrColor.setDisable(flag);
			spnX.setDisable(flag);
			spnY.setDisable(flag);
			spnRadius.setDisable(flag);
			spnSpread.setDisable(flag);
		});
		
		this.pkrColor.setDisable(true);
		this.spnX.setDisable(true);
		this.spnY.setDisable(true);
		this.spnRadius.setDisable(true);
		this.spnSpread.setDisable(true);
		
		// layout
		
		HBox row1 = new HBox(2, this.cmbType, this.pkrColor);
		HBox row2 = new HBox(2, this.spnX, this.spnY);
		HBox row3 = new HBox(2, this.spnRadius, this.spnSpread);
		VBox layout = new VBox(2, row1, row2, row3);
		this.container.setCenter(layout);
	
		// events

		// when the control values change
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				// set the value
				ObservableSlideRegion<?> comp = context.getSelected();
				if (comp != null) {
					context.applyCommand(new ShadowEditCommand(
							comp, 
							CommandFactory.changed(comp.getShadow(), getControlValues()), 
							SlideEditorCommandFactory.select(context.selectedProperty(), comp),
							CommandFactory.func((op) -> {
								setControlValues(op.getOldValue());
							}, (op) -> {
								setControlValues(op.getNewValue());
							})));
//					comp.setShadow(getControlValues());
//					notifyComponentChanged();
				}
			}
		};
		
		this.cmbType.valueProperty().addListener(listener);
		this.pkrColor.valueProperty().addListener(listener);
		this.spnX.valueProperty().addListener(listener);
		this.spnY.valueProperty().addListener(listener);
		this.spnRadius.valueProperty().addListener(listener);
		this.spnSpread.valueProperty().addListener(listener);
		
		// when the value is changed externally
		this.context.selectedProperty().addListener((obs, ov, nv) -> {
			// update controls
			mutating = true;
			if (nv != null) {
				this.setDisable(false);
				setControlValues(nv.getShadow());
			} else {
				this.setDisable(true);
				setControlValues(null);
			}
			mutating = false;
		});
	}

	private SlideShadow getControlValues() {
		Option<ShadowOption> type = this.cmbType.getValue();
		if (type != null && type.getValue() != ShadowOption.NONE) {
			return new SlideShadow(
					type.getValue() == ShadowOption.INNER ? ShadowType.INNER : ShadowType.OUTER,
					PaintConverter.fromJavaFX(this.pkrColor.getValue()), 
					this.spnX.getValue(), 
					this.spnY.getValue(), 
					this.spnRadius.getValue(),
					this.spnSpread.getValue());
		} else {
			return null;
		}
	}

	private void setControlValues(SlideShadow shadow) {
		if (shadow != null) {
			cmbType.setValue(new Option<ShadowOption>(null, shadow.getType() == ShadowType.INNER ? ShadowOption.INNER : ShadowOption.OUTER));
			pkrColor.setValue(PaintConverter.toJavaFX(shadow.getColor()));
			spnX.getValueFactory().setValue(shadow.getOffsetX());
			spnY.getValueFactory().setValue(shadow.getOffsetY());
			spnRadius.getValueFactory().setValue(shadow.getRadius());
			spnSpread.getValueFactory().setValue(shadow.getSpread());
		} else {
			cmbType.setValue(new Option<ShadowOption>(null, ShadowOption.NONE));
			pkrColor.setValue(Color.BLACK);
			spnX.getValueFactory().setValue(0.0);
			spnY.getValueFactory().setValue(0.0);
			spnRadius.getValueFactory().setValue(10.0);
			spnSpread.getValueFactory().setValue(0.0);
		}
	}
}