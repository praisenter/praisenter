package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlidePaint;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class TextPaintEditCommand extends SlideRegionValueChangedEditCommand<SlidePaint, ObservableTextComponent<?>> implements EditCommand {
	private final Node focusNode;
	private final ActionEditCommand actions;
	
	public TextPaintEditCommand(SlidePaint oldValue, SlidePaint newValue, ObservableTextComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode, ActionEditCommand actions) {
		super(oldValue, newValue, component, selection);
		this.focusNode = focusNode;
		this.actions = actions;
	}
	
	@Override
	public void execute() {
		this.region.setTextPaint(this.newValue);
		
		if (this.actions != null) {
			this.actions.execute();
		}
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof TextPaintEditCommand) {
			TextPaintEditCommand other = (TextPaintEditCommand)command;
			if (other.region == this.region) {
				return this.isClose(other.newValue, this.newValue);
			}
		}
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof TextPaintEditCommand) {
			TextPaintEditCommand other = (TextPaintEditCommand)command;
			return new TextPaintEditCommand(
					other.oldValue, 
					this.newValue, 
					this.region, 
					this.selection, 
					this.focusNode,
					this.actions);
		}
		return null;
	}
	
	@Override
	public void undo() {
		this.region.setTextPaint(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.undo();
		}
	}
	
	@Override
	public void redo() {
		this.region.setTextPaint(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.redo();
		}
	}
}
