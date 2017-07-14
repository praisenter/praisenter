package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.object.MediaObject;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public final class MediaEditCommand extends SlideRegionValueChangedEditCommand<MediaObject, ObservableMediaComponent> implements EditCommand {
	private final Node focusNode;
	
	public MediaEditCommand(MediaObject oldValue, MediaObject newValue, ObservableMediaComponent component, ObjectProperty<ObservableSlideRegion<?>> selection, Node focusNode) {
		super(oldValue, newValue, component, selection);
		this.focusNode = focusNode;
	}
	
	@Override
	public void execute() {
		this.region.setMedia(this.newValue);
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
		this.region.setMedia(this.oldValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
	
	@Override
	public void redo() {
		this.region.setMedia(this.newValue);
		
		this.selectRegion();
		this.focus(this.focusNode);
	}
}
