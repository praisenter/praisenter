package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.graphics.Rectangle;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class SizeEditCommand extends SlideRegionValueChangedEditCommand<Rectangle, ObservableSlideRegion<?>> implements EditCommand {
	private final Node focusNode;
	
	public SizeEditCommand(Rectangle oldValue, Rectangle newValue, ObservableSlideRegion<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, component, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setX(this.newValue.getX());
		this.region.setY(this.newValue.getY());
		this.region.setWidth(this.newValue.getWidth());
		this.region.setHeight(this.newValue.getHeight());
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof SizeEditCommand) {
			SizeEditCommand sec = (SizeEditCommand)command;
			return sec.region == this.region;
		}
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof SizeEditCommand) {
			SizeEditCommand sec = (SizeEditCommand)command;
			return new SizeEditCommand(
					sec.oldValue,
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
		this.region.setWidth(this.oldValue.getWidth());
		this.region.setHeight(this.oldValue.getHeight());
	}
	
	@Override
	public void redo() {
		this.region.setX(this.newValue.getX());
		this.region.setY(this.newValue.getY());
		this.region.setWidth(this.newValue.getWidth());
		this.region.setHeight(this.newValue.getHeight());
	}
}
