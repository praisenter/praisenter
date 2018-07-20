package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class StackOrderEditCommand extends SlideRegionValueChangedEditCommand<Integer, ObservableSlideComponent<?>> implements EditCommand {
	private final ObservableSlide<?> slide;
	private final Node focusNode;
	
	public StackOrderEditCommand(Integer oldValue, Integer newValue, ObservableSlide<?> slide, ObservableSlideComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, component, selection);
		this.slide = slide;
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.slide.moveComponent(this.region, this.newValue);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return super.isValid() && this.slide != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.slide.moveComponent(this.region, this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.slide.moveComponent(this.region, this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}
