package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.animation.SlideAnimation;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class AddAnimationEditCommand extends SlideRegionEditCommand<ObservableSlide<?>> implements EditCommand {
	private final SlideAnimation animation;
	private final ObservableSlideRegion<?> selected;
	private final Node focusNode;

	public AddAnimationEditCommand(SlideAnimation animation, ObservableSlide<?> slide, ObservableSlideRegion<?> selected, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(slide, selection);
		this.animation = animation;
		this.selected = selected;
		this.focusNode = focusNode;
	}

	@Override
	public void execute() {
		this.region.addAnimation(this.animation);
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
		this.region.removeAnimation(this.animation);
		
		if (this.selected == null) {
			this.selectRegion();
		} else {
			this.select(this.selected);
		}
		
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.addAnimation(this.animation);
		
		if (this.selected == null) {
			this.selectRegion();
		} else {
			this.select(this.selected);
		}
		
		this.focus(this.focusNode);
	}
}
