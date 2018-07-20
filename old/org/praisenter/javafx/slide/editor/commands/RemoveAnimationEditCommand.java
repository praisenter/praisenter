package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.animation.SlideAnimation;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ListView;

public final class RemoveAnimationEditCommand extends SlideRegionEditCommand<ObservableSlide<?>> implements EditCommand {
	private final SlideAnimation animation;
	private final ListView<SlideAnimation> list;

	public RemoveAnimationEditCommand(SlideAnimation animation, ObservableSlide<?> slide, ObjectProperty<ObservableSlideRegion<?>> selection, ListView<SlideAnimation> list) {
		super(slide, selection);
		this.animation = animation;
		this.list = list;
	}

	@Override
	public void execute() {
		this.region.removeAnimation(this.animation);
		this.list.getSelectionModel().selectFirst();
		this.focus(this.list);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return super.isValid() && this.animation != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.region.addAnimation(this.animation);
		this.list.getSelectionModel().select(this.animation);
		this.focus(this.list);
	}
	
	@Override
	public void redo() {
		this.region.removeAnimation(this.animation);
		this.list.getSelectionModel().selectPrevious();
		this.focus(this.list);
	}
}
