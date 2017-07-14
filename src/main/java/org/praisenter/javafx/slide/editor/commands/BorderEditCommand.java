package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.graphics.SlideStroke;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class BorderEditCommand extends SlideRegionValueChangedEditCommand<SlideStroke, ObservableSlideRegion<?>> implements EditCommand {
	private final Node focusNode;
	
	public BorderEditCommand(SlideStroke oldValue, SlideStroke newValue, ObservableSlideRegion<?> region, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, region, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setBorder(this.newValue);
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
		this.region.setBorder(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.setBorder(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}
