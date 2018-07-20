package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.effects.SlideShadow;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class ShadowEditCommand extends SlideRegionValueChangedEditCommand<SlideShadow, ObservableSlideComponent<?>> implements EditCommand {
	private final Node focusNode;
	private final ActionEditCommand actions;
	
	public ShadowEditCommand(SlideShadow oldValue, SlideShadow newValue, ObservableSlideComponent<?> region, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode, ActionEditCommand actions) {
		super(oldValue, newValue, region, selection);
		this.focusNode = focusNode;
		this.actions = actions;
	}
	
	@Override
	public void execute() {
		this.region.setShadow(this.newValue);
		
		if (this.actions != null) {
			this.actions.execute();
		}
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof ShadowEditCommand) {
			ShadowEditCommand other = (ShadowEditCommand)command;
			if (other.region == this.region) {
				return this.isClose(other.newValue, this.newValue);
			}
		}
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof ShadowEditCommand) {
			ShadowEditCommand other = (ShadowEditCommand)command;
			return new ShadowEditCommand(
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
		this.region.setShadow(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.undo();
		}
	}
	
	@Override
	public void redo() {
		this.region.setShadow(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
		
		if (this.actions != null) {
			this.actions.redo();
		}
	}
}
