package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlideShadow;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class TextGlowEditCommand extends SlideRegionValueChangedEditCommand<SlideShadow, ObservableTextComponent<?>> implements EditCommand {
	private final Node focusNode;
	private final ActionEditCommand actions;
	
	public TextGlowEditCommand(SlideShadow oldValue, SlideShadow newValue, ObservableTextComponent<?> region, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode, ActionEditCommand actions) {
		super(oldValue, newValue, region, selection);
		this.focusNode = focusNode;
		this.actions = actions;
	}
	
	@Override
	public void execute() {
		this.region.setTextGlow(this.newValue);
		
		if (this.actions != null) {
			this.actions.execute();
		}
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof TextGlowEditCommand) {
			TextGlowEditCommand other = (TextGlowEditCommand)command;
			if (other.region == this.region) {
				return this.isClose(other.newValue, this.newValue);
			}
		}
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof TextGlowEditCommand) {
			TextGlowEditCommand other = (TextGlowEditCommand)command;
			return new TextGlowEditCommand(
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
		this.region.setTextGlow(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.undo();
		}
	}
	
	@Override
	public void redo() {
		this.region.setTextGlow(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.redo();
		}
	}
}

