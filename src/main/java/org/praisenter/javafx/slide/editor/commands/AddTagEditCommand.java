package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.Tag;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.CommandOperation;
import org.praisenter.javafx.slide.ObservableSlide;

public final class AddTagEditCommand extends ActionsEditCommand<CommandOperation> implements EditCommand {
	private final TagListView tagView;
	private final ObservableSlide<?> slide;
	private final Tag tag;
	// FIXME add other properties so that we can redo/undo the command
	
	@SafeVarargs
	public AddTagEditCommand(TagListView tagView, ObservableSlide<?> slide, Tag tag, CommandAction<CommandOperation>... actions) {
		this(tagView, slide, tag, Arrays.asList(actions));
	}

	public AddTagEditCommand(TagListView tagView, ObservableSlide<?> slide, Tag tag, List<CommandAction<CommandOperation>> actions) {
		super(null, actions);
		this.tagView = tagView;
		this.slide = slide;
		this.tag = tag;
	}

	@Override
	public void execute() {
		this.slide.addTag(this.tag);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return this.slide != null && this.tag != null && !this.slide.getTags().contains(this.tag);
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.slide.removeTag(this.tag);
		this.tagView.tagsProperty().remove(this.tag);
		super.undo();
	}
	
	@Override
	public void redo() {
		this.slide.addTag(this.tag);
		this.tagView.tagsProperty().add(this.tag);
		super.redo();
	}
}
