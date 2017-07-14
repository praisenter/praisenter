package org.praisenter.javafx.slide.editor.commands;

import java.util.List;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.animation.SlideAnimation;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class RemoveComponentEditCommand  extends SlideRegionEditCommand<ObservableSlideComponent<?>> implements EditCommand {
	private final ObservableSlide<?> slide;
	private final Node focusNode;
	private final List<SlideAnimation> animations;
	
	public RemoveComponentEditCommand(ObservableSlide<?> slide, ObservableSlideComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(component, selection);
		this.slide = slide;
		this.focusNode = focusNode;
		// record the animations that were attached to the component
		// so we can add them back on an undo
		this.animations = slide.getRegion().getAnimations(component.getId());
	}
	
	@Override
	public void execute() {
		// note: this will automatically remove animations
		this.slide.removeComponent(this.region);
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
		this.slide.addComponent(this.region);
		for (SlideAnimation animation : this.animations) {
			this.slide.addAnimation(animation);
		}
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.slide.removeComponent(this.region);
		
		this.select(this.slide);
		this.focus(this.focusNode);
	}
}
