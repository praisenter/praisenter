package org.praisenter.javafx.slide.editor.commands;

import java.time.LocalDateTime;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class CountdownTargetEditCommand extends SlideRegionValueChangedEditCommand<LocalDateTime, ObservableCountdownComponent> implements EditCommand {
	private final Node focusNode;
	
	public CountdownTargetEditCommand(LocalDateTime oldValue, LocalDateTime newValue, ObservableCountdownComponent component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, component, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setCountdownTarget(this.newValue);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.region.setCountdownTarget(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.setCountdownTarget(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}
