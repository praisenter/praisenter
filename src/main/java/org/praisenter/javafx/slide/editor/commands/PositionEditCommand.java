package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.graphics.Rectangle;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class PositionEditCommand extends SlideRegionValueChangedEditCommand<Rectangle, ObservableSlideRegion<?>> implements EditCommand {
	private final Node focusNode;
	
	public PositionEditCommand(Rectangle oldValue, Rectangle newValue, ObservableSlideRegion<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, component, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setX(this.newValue.getX());
		this.region.setY(this.newValue.getY());
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof PositionEditCommand) {
			PositionEditCommand pec = (PositionEditCommand)command;
			return pec.region == this.region;
		}
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof PositionEditCommand) {
			PositionEditCommand pec = (PositionEditCommand)command;
			return new PositionEditCommand(
					pec.oldValue,
					this.newValue,
					this.region,
					this.selection,
					this.focusNode);
		}
		return null;
	}
	
	@Override
	public void undo() {
		this.region.setX(this.oldValue.getX());
		this.region.setY(this.oldValue.getY());
		
	}
	
	@Override
	public void redo() {
		this.region.setX(this.newValue.getX());
		this.region.setY(this.newValue.getY());
		
	}
}
