package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.javafx.command.CommandFactory;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.EditTextEditCommand;
import org.praisenter.javafx.slide.editor.commands.SelectComponentAction;
import org.praisenter.javafx.slide.editor.commands.SlideEditorCommandFactory;

import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// TODO button to pop out a big text editor

class TextRibbonTab extends ComponentEditorRibbonTab {
	private final TextArea text;

	public TextRibbonTab(SlideEditorContext context) {
		super(context, "Text");
		
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
		
		this.context.selectedProperty().addListener((obs, ov, nv) -> {
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
			ObservableSlideRegion<?> component = this.context.getSelected();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				// create a command to edit the text of a text component
				EditCommand command = new EditTextEditCommand(
						tc, 
						// the value changed operation
						CommandFactory.changed(ov, nv),
						// select the component
						SlideEditorCommandFactory.select(this.context.selectedProperty(), tc),
						// set the TextArea, focus it, and set the caret position
						CommandFactory.text(this.text));
				
				this.context.applyCommand(command);
				//tc.setText(nv);
				//notifyComponentChanged(command);
			}
		});
	}
}
