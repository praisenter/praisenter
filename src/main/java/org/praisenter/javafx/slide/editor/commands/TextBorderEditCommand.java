package org.praisenter.javafx.slide.editor.commands;

import java.util.Objects;

import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlideStroke;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class TextBorderEditCommand extends SlideRegionValueChangedEditCommand<SlideStroke, ObservableTextComponent<?>> implements EditCommand {
	private final Node focusNode;
	private final ActionEditCommand actions;
	
	public TextBorderEditCommand(SlideStroke oldValue, SlideStroke newValue, ObservableTextComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode, ActionEditCommand actions) {
		super(oldValue, newValue, component, selection);
		this.focusNode = focusNode;
		this.actions = actions;
	}
	
	@Override
	public void execute() {
		this.region.setTextBorder(this.newValue);
		if (this.actions != null) {
			this.actions.execute();
		}
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof TextBorderEditCommand) {
			TextBorderEditCommand other = (TextBorderEditCommand)command;
			if (other.region == this.region) {
				// check if everything is equal and the paints are "close"
				if (other.newValue != null && this.newValue != null &&
					other.newValue.getRadius() == this.newValue.getRadius() &&
					other.newValue.getWidth() == this.newValue.getWidth() &&
					Objects.equals(other.newValue.getStyle(), this.newValue.getStyle()) &&
					this.isClose(other.newValue.getPaint(), this.newValue.getPaint())) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof TextBorderEditCommand) {
			TextBorderEditCommand other = (TextBorderEditCommand)command;
			if (other.region == this.region) {
				return new TextBorderEditCommand(
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
		this.region.setTextBorder(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.undo();
		}
	}
	
	@Override
	public void redo() {
		this.region.setTextBorder(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.redo();
		}
	}
}
