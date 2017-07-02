package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.TextType;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;

public class PlaceholderTypeEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<Option<TextType>>> {
	private final ObservableSlide<?> slide;
	private final ObservableTextPlaceholderComponent component;
	
	@SafeVarargs
	public PlaceholderTypeEditCommand(ObservableSlide<?> slide, ObservableTextPlaceholderComponent component, ValueChangedCommandOperation<Option<TextType>> operation, CommandAction<ValueChangedCommandOperation<Option<TextType>>>... actions) {
		this(slide, component, operation, Arrays.asList(actions));
	}
	
	public PlaceholderTypeEditCommand(ObservableSlide<?> slide, ObservableTextPlaceholderComponent component, ValueChangedCommandOperation<Option<TextType>> operation, List<CommandAction<ValueChangedCommandOperation<Option<TextType>>>> actions) {
		super(operation, actions);
		this.slide = slide;
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setPlaceholderType(this.operation.getNewValue().getValue());
		this.slide.updatePlaceholders();
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return this.component != null &&  this.slide != null &&
				this.operation.getNewValue() != null && this.operation.getNewValue().getValue() != null &&
				this.operation.getOldValue() != null && this.operation.getOldValue().getValue() != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.component.setPlaceholderType(this.operation.getOldValue().getValue());
		this.slide.updatePlaceholders();
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setPlaceholderType(this.operation.getNewValue().getValue());
		this.slide.updatePlaceholders();
		super.redo();
	}
}
