package org.praisenter.javafx.slide.editor.events;

import java.io.Serializable;

import org.praisenter.javafx.command.EditCommand;

import javafx.event.EventTarget;

public class SlideChangedEvent extends SlideEditorEvent implements Serializable {
	final EditCommand command;
	
	public SlideChangedEvent(Object source, EventTarget target, EditCommand command) {
		super(source, target, SlideEditorEvent.CHANGED);
		this.command = command;
	}
	
	public EditCommand getCommand() {
		return this.command;
	}
}
