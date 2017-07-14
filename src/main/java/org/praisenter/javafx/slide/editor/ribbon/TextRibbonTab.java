package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.javafx.PreventUndoRedoEventFilter;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.EditTextEditCommand;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// FEATURE (L) Add a button to pop out a big text area

class TextRibbonTab extends ComponentEditorRibbonTab {
	private final TextArea text;

	public TextRibbonTab(SlideEditorContext context) {
		super(context, "Text");
		
		this.text = new TextArea();
		this.text.setMaxHeight(75);
		this.text.setWrapText(true);
		this.text.setMaxWidth(175);
		this.text.addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		// layout
		
		HBox row1 = new HBox(2, this.text);
		VBox layout = new VBox(2, row1);
		this.container.setCenter(layout);
	
		// events
		this.managedProperty().bind(this.visibleProperty());
		
		this.context.selectedProperty().addListener((obs, ov, nv) -> {
			this.mutating = true;
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
			this.mutating = false;
		});
		
		this.text.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				
				EditCommand command = new EditTextEditCommand(ov, nv, tc, this.context.selectedProperty(), this.text);
				this.applyCommand(command);
			}
		});
	}
}
