package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.Option;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.text.FontScaleType;

public class FontScalingEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<Option<FontScaleType>>> {
	private final ObservableTextComponent<?> component;
	
	@SafeVarargs
	public FontScalingEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<Option<FontScaleType>> operation, CommandAction<ValueChangedCommandOperation<Option<FontScaleType>>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public FontScalingEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<Option<FontScaleType>> operation, List<CommandAction<ValueChangedCommandOperation<Option<FontScaleType>>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		Option<FontScaleType> option = this.operation.getNewValue();
		FontScaleType type = option != null ? option.getValue() : null;
		this.component.setFontScaleType(type != null ? type : FontScaleType.NONE);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return this.component != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		Option<FontScaleType> option = this.operation.getOldValue();
		FontScaleType type = option != null ? option.getValue() : null;
		this.component.setFontScaleType(type != null ? type : FontScaleType.NONE);
		super.undo();
	}
	
	@Override
	public void redo() {
		Option<FontScaleType> option = this.operation.getNewValue();
		FontScaleType type = option != null ? option.getValue() : null;
		this.component.setFontScaleType(type != null ? type : FontScaleType.NONE);
		super.redo();
	}
}
