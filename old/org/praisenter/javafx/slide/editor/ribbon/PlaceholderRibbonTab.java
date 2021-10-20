package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.PlaceholderTypeEditCommand;
import org.praisenter.javafx.slide.editor.commands.PlaceholderVariantEditCommand;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class PlaceholderRibbonTab extends ComponentEditorRibbonTab {

	private final ComboBox<Option<TextType>> cmbTextType;
	private final ComboBox<Option<TextVariant>> cmbTextVariant;
	
	public PlaceholderRibbonTab(SlideEditorContext context) {
		super(context, "Placeholder");

		ObservableList<Option<TextType>> placeholderTypes = FXCollections.observableArrayList();
		placeholderTypes.add(new Option<TextType>("Text", TextType.TEXT));
		placeholderTypes.add(new Option<TextType>("Title", TextType.TITLE));
		
		// FEATURE (L-L) Add more variant options other than Primary/Secondary
		ObservableList<Option<TextVariant>> placeholderVariants = FXCollections.observableArrayList();
		placeholderVariants.add(new Option<TextVariant>("Primary", TextVariant.PRIMARY));
		placeholderVariants.add(new Option<TextVariant>("Secondary", TextVariant.SECONDARY));
		
		this.cmbTextType = new ComboBox<Option<TextType>>(placeholderTypes);
		this.cmbTextType.setValue(placeholderTypes.get(0));
		this.cmbTextType.setMaxWidth(175);
		this.cmbTextType.setPrefWidth(175);
		
		this.cmbTextVariant = new ComboBox<Option<TextVariant>>(placeholderVariants);
		this.cmbTextVariant.setValue(placeholderVariants.get(0));
		this.cmbTextVariant.setMaxWidth(175);
		this.cmbTextVariant.setPrefWidth(175);
		
		this.cmbTextType.setTooltip(new Tooltip("The type of text that will be placed\nin this placeholder"));
		this.cmbTextVariant.setTooltip(new Tooltip("The variant of the text, primary\nor secondary language for example"));
		
		// layout
		
		HBox row1 = new HBox(2, this.cmbTextType);
		HBox row2 = new HBox(2, this.cmbTextVariant);

		VBox layout = new VBox(2, row1, row2);
		
		this.container.setCenter(layout);
		
		// events
		this.managedProperty().bind(this.visibleProperty());

		this.cmbTextType.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlide<?> slide = this.context.getSlide();
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent tc = (ObservableTextPlaceholderComponent)component;
				this.applyCommand(new PlaceholderTypeEditCommand(ov, nv, slide, tc, this.context.selectedProperty(), this.cmbTextType));
			}
		});
		
		this.cmbTextVariant.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlide<?> slide = this.context.getSlide();
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent tc = (ObservableTextPlaceholderComponent)component;
				this.applyCommand(new PlaceholderVariantEditCommand(ov, nv, slide, tc, this.context.selectedProperty(), this.cmbTextVariant));
			}
		});
		
		this.context.selectedProperty().addListener((obs, ov, nv) -> {
			this.mutating = true;
			if (nv != null && nv instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent otpc = (ObservableTextPlaceholderComponent)nv;
				this.cmbTextType.setValue(new Option<TextType>(null, otpc.getPlaceholderType()));
				this.cmbTextVariant.setValue(new Option<TextVariant>(null, otpc.getPlaceholderVariant()));
				this.setVisible(true);
			} else {
				this.cmbTextType.setValue(placeholderTypes.get(0));
				this.cmbTextVariant.setValue(placeholderVariants.get(0));
				this.setVisible(false);
			}
			this.mutating = false;
		});
	}
}
