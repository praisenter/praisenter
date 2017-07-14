package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.Tag;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;

public final class RemoveTagEditCommand extends SlideRegionEditCommand<ObservableSlide<?>> implements EditCommand {
	private final TagListView tagView;
	private final Tag tag;
	
	public RemoveTagEditCommand(ObservableSlide<?> slide, Tag tag, ObjectProperty<ObservableSlideRegion<?>> selection, TagListView tagView) {
		super(slide, selection);
		this.tagView = tagView;
		this.tag = tag;
	}

	@Override
	public void execute() {
		this.region.removeTag(this.tag);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return super.isValid() && this.tag != null && this.tagView != null && this.region.getTags().contains(this.tag);
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.region.addTag(this.tag);
		this.tagView.tagsProperty().remove(this.tag);
		
		this.selectRegion();
		this.focus(this.tagView);
	}
	
	@Override
	public void redo() {
		this.region.removeTag(this.tag);
		this.tagView.tagsProperty().add(this.tag);
		
		this.selectRegion();
		this.focus(this.tagView);
	}
}
