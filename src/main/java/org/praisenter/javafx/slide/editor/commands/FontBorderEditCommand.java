package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlideStroke;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class FontBorderEditCommand extends SlideRegionValueChangedEditCommand<SlideStroke, ObservableTextComponent<?>> implements EditCommand {
	private final Node focusNode;
	
	public FontBorderEditCommand(SlideStroke oldValue, SlideStroke newValue, ObservableTextComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, component, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setTextBorder(this.newValue);
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
		this.region.setTextBorder(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.setTextBorder(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}
