package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.animation.SlideAnimation;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class EditAnimationEditCommand extends SlideRegionValueChangedEditCommand<SlideAnimation, ObservableSlide<?>> implements EditCommand {
	private final ObservableSlideRegion<?> selected;
	private final Node focusNode;

	public EditAnimationEditCommand(SlideAnimation oldValue, SlideAnimation newValue, ObservableSlide<?> slide, ObservableSlideRegion<?> selected, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, slide, selection);
		this.selected = selected;
		this.focusNode = focusNode;
	}

	@Override
	public void execute() {
		this.region.removeAnimation(this.oldValue);
		this.region.addAnimation(this.newValue);
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
		this.region.removeAnimation(this.newValue);
		this.region.addAnimation(this.oldValue);
		
		if (this.selected == null) {
			this.selectRegion();
		} else {
			this.select(this.selected);
		}
		
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.removeAnimation(this.oldValue);
		this.region.addAnimation(this.newValue);
		
		if (this.selected == null) {
			this.selectRegion();
		} else {
			this.select(this.selected);
		}
		
		this.focus(this.focusNode);
	}
}
