package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.graphics.SlidePaint;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class BackgroundEditCommand extends SlideRegionValueChangedEditCommand<SlidePaint, ObservableSlideRegion<?>> implements EditCommand {
	private final Node focusNode;
	
	public BackgroundEditCommand(SlidePaint oldValue, SlidePaint newValue, ObservableSlideRegion<?> region, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, region, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setBackground(this.newValue);
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
		this.region.setBackground(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.setBackground(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}
