package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlideShadow;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class TextGlowEditCommand extends SlideRegionValueChangedEditCommand<SlideShadow, ObservableTextComponent<?>> implements EditCommand {
	private final Node focusNode;
	
	public TextGlowEditCommand(SlideShadow oldValue, SlideShadow newValue, ObservableTextComponent<?> region, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, region, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setTextGlow(this.newValue);
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
		this.region.setTextGlow(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.setTextGlow(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}

