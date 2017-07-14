package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlidePaint;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class TextPaintEditCommand extends SlideRegionValueChangedEditCommand<SlidePaint, ObservableTextComponent<?>> implements EditCommand {
	private final Node focusNode;
	
	public TextPaintEditCommand(SlidePaint oldValue, SlidePaint newValue, ObservableTextComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, component, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setTextPaint(this.newValue);
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
		this.region.setTextPaint(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.setTextPaint(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}
