package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.converters.PaintConverter;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradient;
import org.praisenter.slide.graphics.SlidePaint;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public final class BackgroundEditCommand extends SlideRegionValueChangedEditCommand<SlidePaint, ObservableSlideRegion<?>> implements EditCommand {
	private final Node focusNode;
	private final ActionEditCommand actions;
	
	public BackgroundEditCommand(SlidePaint oldValue, SlidePaint newValue, ObservableSlideRegion<?> region, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode, ActionEditCommand actions) {
		super(oldValue, newValue, region, selection);
		this.focusNode = focusNode;
		this.actions = actions;
	}
	
	@Override
	public void execute() {
		this.region.setBackground(this.newValue);
		if (this.actions != null) {
			this.actions.execute();
		}
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof BackgroundEditCommand) {
			BackgroundEditCommand other = (BackgroundEditCommand)command;
			if (other.region == this.region) {
				if (this.isClose(other.newValue, this.newValue)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof BackgroundEditCommand) {
			BackgroundEditCommand other = (BackgroundEditCommand)command;
			return new BackgroundEditCommand(
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
		this.region.setBackground(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.undo();
		}
	}
	
	@Override
	public void redo() {
		this.region.setBackground(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.redo();
		}
	}
}
