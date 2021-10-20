package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.text.HorizontalTextAlignment;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class HorizontalTextAlignmentEditCommand extends SlideRegionValueChangedEditCommand<HorizontalTextAlignment, ObservableTextComponent<?>> implements EditCommand {
	private final Node focusNode;
	
	public HorizontalTextAlignmentEditCommand(HorizontalTextAlignment oldValue, HorizontalTextAlignment newValue, ObservableTextComponent<?> region, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, region, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setHorizontalTextAlignment(this.newValue);
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
		this.region.setHorizontalTextAlignment(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.setHorizontalTextAlignment(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}
