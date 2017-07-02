package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.TextVariant;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;

public class PlaceholderVariantEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<Option<TextVariant>>> {
	private final ObservableSlide<?> slide;
	private final ObservableTextPlaceholderComponent component;
	
	@SafeVarargs
	public PlaceholderVariantEditCommand(ObservableSlide<?> slide, ObservableTextPlaceholderComponent component, ValueChangedCommandOperation<Option<TextVariant>> operation, CommandAction<ValueChangedCommandOperation<Option<TextVariant>>>... actions) {
		this(slide, component, operation, Arrays.asList(actions));
	}
	
	public PlaceholderVariantEditCommand(ObservableSlide<?> slide, ObservableTextPlaceholderComponent component, ValueChangedCommandOperation<Option<TextVariant>> operation, List<CommandAction<ValueChangedCommandOperation<Option<TextVariant>>>> actions) {
		super(operation, actions);
		this.slide = slide;
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setPlaceholderVariant(this.operation.getNewValue().getValue());
		this.slide.updatePlaceholders();
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return this.component != null && this.slide != null &&
				this.operation.getNewValue() != null && this.operation.getNewValue().getValue() != null &&
				this.operation.getOldValue() != null && this.operation.getOldValue().getValue() != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.component.setPlaceholderVariant(this.operation.getOldValue().getValue());
		this.slide.updatePlaceholders();
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setPlaceholderVariant(this.operation.getNewValue().getValue());
		this.slide.updatePlaceholders();
		super.redo();
	}
}
