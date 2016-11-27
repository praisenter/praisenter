package org.praisenter.javafx.slide.editor;

import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;
import org.praisenter.TextType;
import org.praisenter.TextVariant;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class PlaceholderRibbonTab extends ComponentEditorRibbonTab {

	private final ComboBox<Option<TextType>> cmbTextType;
	private final CheckComboBox<Option<TextVariant>> cmbTextVariants;
	
	public PlaceholderRibbonTab() {
		super("Placeholder");

		ObservableList<Option<TextType>> placeholderTypes = FXCollections.observableArrayList();
		placeholderTypes.add(new Option<TextType>("Text", TextType.TEXT));
		placeholderTypes.add(new Option<TextType>("Title", TextType.TITLE));
		
		// FEATURE Add more variant options other than Primary/Secondary
		ObservableList<Option<TextVariant>> placeholderVariants = FXCollections.observableArrayList();
		placeholderVariants.add(new Option<TextVariant>("Primary", TextVariant.PRIMARY));
		placeholderVariants.add(new Option<TextVariant>("Secondary", TextVariant.SECONDARY));
		
		this.cmbTextType = new ComboBox<Option<TextType>>(placeholderTypes);
		this.cmbTextType.setValue(placeholderTypes.get(0));
		this.cmbTextType.setMaxWidth(200);
		this.cmbTextType.setPrefWidth(200);
		
		this.cmbTextVariants = new CheckComboBox<Option<TextVariant>>(placeholderVariants);
		this.cmbTextVariants.setMaxWidth(200);
		this.cmbTextVariants.setPrefWidth(200);
		
		// layout
		
		HBox row1 = new HBox(2, this.cmbTextType);
		HBox row2 = new HBox(2, this.cmbTextVariants);

		VBox layout = new VBox(2, row1, row2);
		
		this.container.setCenter(layout);
		
		// events

		this.cmbTextType.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent tc = (ObservableTextPlaceholderComponent)component;
				tc.setPlaceholderType(nv.getValue());
			}
		});
		
		this.cmbTextVariants.checkModelProperty().get().getCheckedItems().addListener((javafx.collections.ListChangeListener.Change<? extends Option<TextVariant>> change) -> {
			if (mutating) return;
			ObservableSlideRegion<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent tc = (ObservableTextPlaceholderComponent)component;
				tc.getPlaceholderVariants().clear();
				tc.getPlaceholderVariants().addAll(change.getList().stream().map((o) -> o.getValue()).collect(Collectors.toList()));
			}
		});
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null && nv instanceof ObservableTextPlaceholderComponent) {
				this.setDisable(false);
				ObservableTextPlaceholderComponent otpc = (ObservableTextPlaceholderComponent)nv;
				this.cmbTextType.setValue(new Option<TextType>(null, otpc.getPlaceholderType()));
				this.cmbTextVariants.getCheckModel().clearChecks();
				for (TextVariant variant : otpc.getPlaceholderVariants()) {
					this.cmbTextVariants.getCheckModel().check(new Option<TextVariant>(null, variant));
				}
			} else {
				this.setDisable(true);
				this.cmbTextType.setValue(placeholderTypes.get(0));
				this.cmbTextVariants.getCheckModel().clearChecks();
			}
			mutating = false;
		});
	}
}
