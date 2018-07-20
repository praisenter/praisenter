package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class VerticalTextAlignmentEditCommand extends SlideRegionValueChangedEditCommand<VerticalTextAlignment, ObservableTextComponent<?>> implements EditCommand {
	private final Node focusNode;
	
	public VerticalTextAlignmentEditCommand(VerticalTextAlignment oldValue, VerticalTextAlignment newValue, ObservableTextComponent<?> region, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, region, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setVerticalTextAlignment(this.newValue);
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
		this.region.setVerticalTextAlignment(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.setVerticalTextAlignment(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}

