package org.praisenter.javafx.slide.editor;

import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.PlaceholderVariant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class PlaceholderRibbonTab extends ComponentEditorRibbonTab {

	private final ComboBox<Option<PlaceholderType>> cmbPlaceholderType;
	private final CheckComboBox<Option<PlaceholderVariant>> cmbPlaceholderVariants;
	
	public PlaceholderRibbonTab() {
		super("Placeholder");

		ObservableList<Option<PlaceholderType>> placeholderTypes = FXCollections.observableArrayList();
		placeholderTypes.add(new Option<PlaceholderType>("Text", PlaceholderType.TEXT));
		placeholderTypes.add(new Option<PlaceholderType>("Title", PlaceholderType.TITLE));
		
		// FEATURE add more variants
		ObservableList<Option<PlaceholderVariant>> placeholderVariants = FXCollections.observableArrayList();
		placeholderVariants.add(new Option<PlaceholderVariant>("Primary", PlaceholderVariant.PRIMARY));
		placeholderVariants.add(new Option<PlaceholderVariant>("Secondary", PlaceholderVariant.SECONDARY));
		
		this.cmbPlaceholderType = new ComboBox<Option<PlaceholderType>>(placeholderTypes);
		this.cmbPlaceholderType.setValue(placeholderTypes.get(0));
		this.cmbPlaceholderType.setMaxWidth(200);
		this.cmbPlaceholderType.setPrefWidth(200);
		
		this.cmbPlaceholderVariants = new CheckComboBox<Option<PlaceholderVariant>>(placeholderVariants);
		this.cmbPlaceholderVariants.setMaxWidth(200);
		this.cmbPlaceholderVariants.setPrefWidth(200);
		
		// layout
		
		HBox row1 = new HBox(2, this.cmbPlaceholderType);
		HBox row2 = new HBox(2, this.cmbPlaceholderVariants);

		VBox layout = new VBox(2, row1, row2);
		
		this.container.setCenter(layout);
		
		// events

		this.cmbPlaceholderType.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent tc = (ObservableTextPlaceholderComponent)component;
				tc.setPlaceholderType(nv.getValue());
			}
		});
		
		this.cmbPlaceholderVariants.checkModelProperty().get().getCheckedItems().addListener((javafx.collections.ListChangeListener.Change<? extends Option<PlaceholderVariant>> change) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent tc = (ObservableTextPlaceholderComponent)component;
				tc.getVariants().clear();
				tc.getVariants().addAll(change.getList().stream().map((o) -> o.getValue()).collect(Collectors.toList()));
			}
		});
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null && nv instanceof ObservableTextPlaceholderComponent) {
				this.setDisable(false);
				ObservableTextPlaceholderComponent otpc = (ObservableTextPlaceholderComponent)nv;
				this.cmbPlaceholderType.setValue(new Option<PlaceholderType>(null, otpc.getPlaceholderType()));
				this.cmbPlaceholderVariants.getCheckModel().clearChecks();
				for (PlaceholderVariant variant : otpc.getVariants()) {
					this.cmbPlaceholderVariants.getCheckModel().check(new Option<PlaceholderVariant>(null, variant));
				}
			} else {
				this.setDisable(true);
				this.cmbPlaceholderType.setValue(placeholderTypes.get(0));
				this.cmbPlaceholderVariants.getCheckModel().clearChecks();
			}
			mutating = false;
		});
	}
}
