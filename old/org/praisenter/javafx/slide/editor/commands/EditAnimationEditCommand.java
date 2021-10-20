package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.animation.SlideAnimation;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ListView;

public final class EditAnimationEditCommand extends SlideRegionValueChangedEditCommand<SlideAnimation, ObservableSlide<?>> implements EditCommand {
	private final ListView<SlideAnimation> list;

	public EditAnimationEditCommand(SlideAnimation oldValue, SlideAnimation newValue, ObservableSlide<?> slide, ObjectProperty<ObservableSlideRegion<?>> selection, ListView<SlideAnimation> list) {
		super(oldValue, newValue, slide, selection);
		this.list = list;
	}

	@Override
	public void execute() {
		this.region.removeAnimation(this.oldValue);
		this.region.addAnimation(this.newValue);
		this.list.getSelectionModel().select(this.newValue);
		this.focus(this.list);
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
		
		this.focus(this.list);
		this.list.getSelectionModel().select(this.oldValue);
		this.focus(this.list);
	}
	
	@Override
	public void redo() {
		this.region.removeAnimation(this.oldValue);
		this.region.addAnimation(this.newValue);
		
		this.focus(this.list);
		this.list.getSelectionModel().select(this.newValue);
		this.focus(this.list);
	}
}
