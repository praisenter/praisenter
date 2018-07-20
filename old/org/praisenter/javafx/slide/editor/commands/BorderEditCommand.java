package org.praisenter.javafx.slide.editor.commands;

import java.util.Objects;

import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.graphics.SlideStroke;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class BorderEditCommand extends SlideRegionValueChangedEditCommand<SlideStroke, ObservableSlideRegion<?>> implements EditCommand {
	private final Node focusNode;
	private final ActionEditCommand actions;
	
	public BorderEditCommand(SlideStroke oldValue, SlideStroke newValue, ObservableSlideRegion<?> region, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode, ActionEditCommand actions) {
		super(oldValue, newValue, region, selection);
		this.focusNode = focusNode;
		this.actions = actions;
	}
	
	@Override
	public void execute() {
		this.region.setBorder(this.newValue);
		if (this.actions != null) {
			this.actions.execute();
		}
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof BorderEditCommand) {
			BorderEditCommand other = (BorderEditCommand)command;
			if (other.region == this.region) {
				// check if everything is equal and the paints are "close"
				if (this.isClose(other.newValue, this.newValue)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof BorderEditCommand) {
			BorderEditCommand other = (BorderEditCommand)command;
			if (other.region == this.region) {
				return new BorderEditCommand(
						other.oldValue, 
						this.newValue, 
						this.region, 
						this.selection, 
						this.focusNode, 
						this.actions);
			}
		}
		return null;
	}
	
	@Override
	public void undo() {
		this.region.setBorder(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.undo();
		}
	}
	
	@Override
	public void redo() {
		this.region.setBorder(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.redo();
		}
	}
}
