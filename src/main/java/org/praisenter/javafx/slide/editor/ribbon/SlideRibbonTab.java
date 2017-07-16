package org.praisenter.javafx.slide.editor.ribbon;

import org.controlsfx.control.PopOver;
import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.PreventUndoRedoEventFilter;
import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.command.CommandFactory;
import org.praisenter.javafx.configuration.Resolution;
import org.praisenter.javafx.configuration.ResolutionSet;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.SlideNameEditCommand;
import org.praisenter.javafx.slide.editor.commands.SlideResolutionEditCommand;
import org.praisenter.javafx.slide.editor.events.SlideEditorEvent;

import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

final class SlideRibbonTab extends SlideRegionRibbonTab<ObservableSlide<?>> {
	private final TextField name;
	private final TextField time;
	private final ComboBox<Resolution> cmbResolutions;
	
	private final PopOver popAddResolution;
	private final Spinner<Integer> spnWidth;
	private final Spinner<Integer> spnHeight;
	
	public SlideRibbonTab(SlideEditorContext context) {
		super(context, "Slide");
		
		name = new TextField();
		name.setPromptText("Name");
		name.addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		time = new TextField();
		time.setPromptText("00:00");
		time.addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		// target resolution
		SortedList<Resolution> sorted = context.getContext().getConfiguration().getResolutions().sorted((a, b) -> {
			return a.compareTo(b);
		});
		cmbResolutions = new ComboBox<Resolution>(sorted);
		
		Button btnNewResolution = new Button("", ApplicationGlyphs.ADD.duplicate());
		Button btnRemoveResolution = new Button("", ApplicationGlyphs.REMOVE.duplicate());
		
		btnNewResolution.setTooltip(new Tooltip("Add a new resolution target."));
		btnRemoveResolution.setTooltip(new Tooltip("Remove the selected resolution target."));
		
		this.spnWidth = new Spinner<Integer>(100, Integer.MAX_VALUE, 1280, 100);
		this.spnHeight = new Spinner<Integer>(100, Integer.MAX_VALUE, 1024, 100);
		this.spnWidth.setEditable(true);
		this.spnHeight.setEditable(true);
		
		Button btnAdd = new Button("Add");
		Button btnCancel = new Button("Cancel");
		GridPane pneResolution = new GridPane();
		pneResolution.setVgap(5);
		pneResolution.setHgap(5);
		pneResolution.setPadding(new Insets(10));
		pneResolution.add(new Label("Width"), 0, 0);
		pneResolution.add(this.spnWidth, 1, 0);
		pneResolution.add(new Label("Height"), 0, 1);
		pneResolution.add(this.spnHeight, 1, 1);
		pneResolution.add(btnAdd, 0, 2);
		pneResolution.add(btnCancel, 1, 2);
		
		popAddResolution = new PopOver(pneResolution);
		
		// layout
		
		HBox row1 = new HBox(2, this.name);
		HBox row2 = new HBox(2, this.time);
		HBox row3 = new HBox(2, this.cmbResolutions, btnNewResolution, btnRemoveResolution);
		VBox layout = new VBox(2, row1, row2, row3);
		this.container.setCenter(layout);
	
		// events
		
		this.context.slideProperty().addListener((obs, ov, nv) -> {
			this.mutating = true;
			if (nv != null) {
				this.name.setText(nv.getName());
				this.cmbResolutions.setValue(new Resolution((int)nv.getWidth(), (int)nv.getHeight()));
			}
			this.mutating = false;
		});
		
		this.name.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlide<?> slide = this.context.getSlide();
			this.applyCommand(new SlideNameEditCommand(ov, nv, slide, this.context.selectedProperty(), this.name));
		});
		
		// TODO time editing
		
		this.cmbResolutions.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlide<?> slide = this.context.getSlide();
			this.applyCommand(CommandFactory.chain(
					new SlideResolutionEditCommand(ov, nv, slide, this.context.selectedProperty(), this.cmbResolutions),
					new ActionEditCommand(self -> {
						fireEvent(new SlideEditorEvent(this, this, SlideEditorEvent.TARGET_RESOLUTION));
					})));
		});
		
		btnNewResolution.setOnAction((e) -> {
			Resolution r = this.cmbResolutions.getValue();
			if (r != null) {
				this.spnWidth.getValueFactory().setValue(r.getWidth());
				this.spnHeight.getValueFactory().setValue(r.getHeight());
			}
			this.popAddResolution.show(btnNewResolution);
		});
		
		btnAdd.setOnAction(e -> {
			int w = this.spnWidth.getValue();
			int h = this.spnHeight.getValue();
			Resolution r = new Resolution(w, h);
			ResolutionSet resolutions = context.getContext().getConfiguration().getObject(Setting.DISPLAY_RESOLUTIONS, ResolutionSet.class, new ResolutionSet());
			resolutions.add(r);
			context.getContext().getConfiguration()
				.setObject(Setting.DISPLAY_RESOLUTIONS, resolutions)
				.execute(context.getContext().getExecutorService());
			
			this.cmbResolutions.setValue(r);
			this.popAddResolution.hide();
		});
		
		btnCancel.setOnAction(e -> {
			this.popAddResolution.hide();
		});
		
		btnRemoveResolution.setOnAction(e -> {
			Resolution r = this.cmbResolutions.getValue();
			if (r != null) {
				ResolutionSet resolutions = context.getContext().getConfiguration().getObject(Setting.DISPLAY_RESOLUTIONS, ResolutionSet.class, new ResolutionSet());
				Resolution other = resolutions.getClosestResolution(r);
				if (resolutions.remove(r)) {
					context.getContext().getConfiguration()
						.setObject(Setting.DISPLAY_RESOLUTIONS, resolutions)
						.execute(context.getContext().getExecutorService());
				
					this.cmbResolutions.setValue(other);
				}
			}
		});
	}
	
	public void setName(String name) {
		this.mutating = true;
		this.name.setText(name);
		this.mutating = false;
	}
}
