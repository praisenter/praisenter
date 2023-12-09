package org.praisenter.ui.display;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.data.workspace.DisplayConfiguration;
import org.praisenter.data.workspace.DisplayType;
import org.praisenter.data.workspace.Resolution;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.MappedList;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditorDivider;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.IntegerSliderField;
import org.praisenter.ui.controls.WidthHeightPicker;
import org.praisenter.ui.translations.Translations;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

public final class NDIDisplaySettingsPane extends BorderPane {
	private static final String NDI_DISPLAY_SETTINGS_PANE_CSS = "p-ndi-display-settings-pane";
	
	private final GlobalContext context;
	
	private final ObservableList<Option<Resolution>> resolutions;
	
	private final IntegerProperty width;
	private final IntegerProperty height;
	private final IntegerProperty fps;
	private final StringProperty name;
	
	private final ObjectProperty<DisplayConfiguration> value;

	public NDIDisplaySettingsPane(GlobalContext context) {
		this.context = context;
		
		this.width = new SimpleIntegerProperty(1920);
		this.height = new SimpleIntegerProperty(1080);
		this.fps = new SimpleIntegerProperty(24);
		this.name = new SimpleStringProperty();
		
		this.value = new SimpleObjectProperty<DisplayConfiguration>();
		
		this.getStyleClass().add(NDI_DISPLAY_SETTINGS_PANE_CSS);
		
		ObservableList<Resolution> resolutions = FXCollections.observableArrayList();
		resolutions.addAll(context.getWorkspaceConfiguration().getResolutions());
		this.updateScreenResolutions(resolutions);
		
		Screen.getScreens().addListener((InvalidationListener)(obs -> this.updateScreenResolutions(resolutions)));
		
		this.resolutions = new MappedList<Option<Resolution>, Resolution>(resolutions.sorted(), r -> {
			Option<Resolution> option = new Option<>(null, r);
			option.nameProperty().bind(Bindings.createStringBinding(() -> {
				boolean isNative = this.isNativeResolution(r);
				return isNative 
						? Translations.get("resolution.native", r.getWidth(), r.getHeight())
						: Translations.get("resolution", r.getWidth(), r.getHeight());
			}, Screen.getScreens()));
			return option;
		});
		
		ComboBox<Option<Resolution>> cmbTargetSize = new ComboBox<>(this.resolutions);
		cmbTargetSize.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				Resolution r = nv.getValue();
				if (r != null) {
					this.width.set(r.getWidth());
					this.height.set(r.getHeight());
				}
			}
		});
		
		WidthHeightPicker pkrSize = new WidthHeightPicker();
		pkrSize.selectedWidthProperty().bindBidirectional(this.width);
		pkrSize.selectedHeightProperty().bindBidirectional(this.height);
		
		TextField txtName = new TextField();
		txtName.textProperty().bindBidirectional(this.name);
		
		IntegerSliderField sldFps = new IntegerSliderField(1.0, 60.0, 24.0, 1.0);
		sldFps.valueProperty().bindBidirectional(this.fps);
		
		EditorField fldName = new EditorField(
				Translations.get("ndi.display.name"), 
				Translations.get("ndi.display.name.description"), 
				txtName, 
				EditorField.LAYOUT_HORIZONTAL);
		
		EditorField fldSizeOptions = new EditorField(
				Translations.get("ndi.display.resolution"), 
				Translations.get("ndi.display.resolution.description"), 
				cmbTargetSize, 
				EditorField.LAYOUT_HORIZONTAL);
		
		EditorField fldSize = new EditorField(
				Translations.get("ndi.display.size"),
				pkrSize, 
				EditorField.LAYOUT_HORIZONTAL);
		
		EditorField fldFps = new EditorField(
				Translations.get("ndi.display.fps"), 
				Translations.get("ndi.display.fps.description"), 
				sldFps, 
				EditorField.LAYOUT_HORIZONTAL);
		
		VBox layout = new VBox(
				fldName,
				fldFps,
				new EditorDivider(Translations.get("ndi.display.size")),
				fldSize,
				fldSizeOptions);
		layout.setSpacing(10);
		
		this.setCenter(layout);
		
		BindingHelper.bindBidirectional(this.name, this.value, new ObjectConverter<String, DisplayConfiguration>() {
			@Override
			public DisplayConfiguration convertFrom(String t) {
				return NDIDisplaySettingsPane.this.getCurrentValue();
			}
			@Override
			public String convertTo(DisplayConfiguration e) {
				if (e == null) return "";
				return e.getName();
			}
		});
		
		BindingHelper.bindBidirectional(this.width, this.value, new ObjectConverter<Number, DisplayConfiguration>() {
			@Override
			public DisplayConfiguration convertFrom(Number t) {
				return NDIDisplaySettingsPane.this.getCurrentValue();
			}
			@Override
			public Number convertTo(DisplayConfiguration e) {
				if (e == null) return null;
				return e.getWidth();
			}
		});
		
		BindingHelper.bindBidirectional(this.height, this.value, new ObjectConverter<Number, DisplayConfiguration>() {
			@Override
			public DisplayConfiguration convertFrom(Number t) {
				return NDIDisplaySettingsPane.this.getCurrentValue();
			}
			@Override
			public Number convertTo(DisplayConfiguration e) {
				if (e == null) return 0;
				return e.getHeight();
			}
		});
		
		BindingHelper.bindBidirectional(this.fps, this.value, new ObjectConverter<Number, DisplayConfiguration>() {
			@Override
			public DisplayConfiguration convertFrom(Number t) {
				return NDIDisplaySettingsPane.this.getCurrentValue();
			}
			@Override
			public Number convertTo(DisplayConfiguration e) {
				if (e == null) return 0;
				return e.getFramesPerSecond();
			}
		});
	}
	
	private void updateScreenResolutions(ObservableList<Resolution> resolutions) {
		// add any screen sizes based on the current displays
		List<Resolution> nr = new ArrayList<>();
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D bounds = screen.getBounds();
			Resolution r = new Resolution((int)Math.ceil(bounds.getWidth()), (int)Math.ceil(bounds.getHeight()));
			int index = resolutions.indexOf(r);
			if (index < 0) {
				// doesn't exist
				nr.add(r);
			}
		}
		resolutions.addAll(nr);
	}
	
	private boolean isNativeResolution(Resolution r) {
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D bounds = screen.getBounds();
			if ((int)bounds.getWidth() == r.getWidth() && (int)bounds.getHeight() == r.getHeight()) {
				return true;
			}
		}
		return false;
	}
	
	private DisplayConfiguration getCurrentValue() {
		List<DisplayConfiguration> configurations = this.context.getWorkspaceConfiguration().getDisplayConfigurations();
		
		// get next unique id
		int max = 0;
		for (DisplayConfiguration configuration : configurations) {
			int id = configuration.getId();
			if (id > max) {
				max = id;
			}
		}
		
		if (max < 10000) {
			max = 10000;
		} else {
			max += 10;
		}
		
		String name = this.name.get();
		int width = this.width.get();
		int height = this.height.get();
		int fps = this.fps.get();
		
		DisplayConfiguration dc = new DisplayConfiguration();
		dc.setActive(true);
		dc.setAutoShowEnabled(false);
		dc.setFramesPerSecond(fps);
		dc.setHeight(height);
		dc.setId(max);
		dc.setName(name);
		dc.setPreviewTransitionEnabled(false);
		dc.setPrimary(false);
		dc.setType(DisplayType.NDI);
		dc.setWidth(width);
		dc.setX(0);
		dc.setY(0);
		
		return dc;
	}

	public DisplayConfiguration getValue() {
		return this.value.get();
	}
	
	public void setValue(DisplayConfiguration value) {
		this.value.set(value);
	}
	
	public ObjectProperty<DisplayConfiguration> valueProperty() {
		return this.value;
	}
}
