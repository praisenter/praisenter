/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.command;

import java.util.Deque;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class used to manage edit commands.
 * @author William Bittle
 * @version 3.0.0
 */
public final class EditManager {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The undo stack */
	private final Deque<EditCommand> undos;
	
	/** The redo stack */
	private final Deque<EditCommand> redos;
	
	/** True if an undo or redo action is taking place */
	private boolean isOperating = false;
	
	/** The MARK object for snapshoting */
	private static final EditCommand MARK = new EditCommand() {
		@Override
		public void execute() {}
		@Override
		public void redo() {}
		@Override
		public void undo() {}
		@Override
		public boolean isMergeSupported(EditCommand command) {
			if (command == this) {
				return true;
			}
			return false;
		}
		@Override
		public EditCommand merge(EditCommand command) {
			return this;
		}
		public boolean isValid() {
			return true;
		}
	};
	
	/**
	 * Default constructor.
	 */
	public EditManager() {
		this.undos = new LinkedList<>();
		this.redos = new LinkedList<>();
	}
	
	/**
	 * Performs the given command and adds it to this managaer.
	 * @param command the command
	 */
	public void execute(EditCommand command) {
		// prevent this affecting the manager if they are
		// being spawned by the call of undo or redo
		if (this.isOperating) {
			return;
		}
		
		if (command == null) {
			LOGGER.warn("Command null - Skipping operation.");
			return;
		}
		
		if (!command.isValid()) {
			LOGGER.warn("Command invalid - Skipping operation.");
			return;
		}
		
		// execute the command
		command.execute();
		
		// see if we need to merge commands (this is useful for text fields where the user is typing -
		// each individual character isn't relevant to undo/redo)
		EditCommand top = this.undos.peek();
		if (top != null && command.getClass().equals(top.getClass()) && command.isMergeSupported(top)) {
			top = this.undos.pop();
			// merge the commands
			command = command.merge(top);
			
			if (command == null) {
				LOGGER.warn("Command null - Skipping operation.");
				return;
			}
			
			if (!command.isValid()) {
				LOGGER.warn("Command invalid - Skipping operation.");
				return;
			}
			
			LOGGER.debug("Commands merged");
		}
		
		this.undos.push(command);
		this.redos.clear();
		
		this.printCounts();
	}
	
	/**
	 * Reverts the last command that was executed and returns it.
	 * @return {@link EditCommand}
	 */
	public EditCommand undo() {
		this.isOperating = true;
		
		if (this.undos.isEmpty()) { 
			return null;
		}
		
		EditCommand undo = this.undos.pop();
		undo.undo();
		this.redos.push(undo);
		
		if (undo == MARK) {
			undo = this.undos.pop();
			undo.undo();
			this.redos.push(undo);
		}
		
		this.printCounts();
		this.isOperating = false;
		
		return undo;
	}
	
	/**
	 * Applies the last command that was undone and returns it.
	 * @return {@link EditCommand}
	 */
	public EditCommand redo() {
		this.isOperating = true;
		
		if (this.redos.isEmpty()) {
			return null;
		}
		
		EditCommand redo = this.redos.pop();
		redo.redo();
		this.undos.push(redo);
		
		if (redo == MARK) {
			redo = this.redos.pop();
			redo.redo();
			this.undos.push(redo);
		}
		
		this.printCounts();
		this.isOperating = false;
		
		return redo;
	}
	
	private void printCounts() {
		LOGGER.debug("UNDO(" + this.undos.size() + ") REDO(" + this.redos.size() + ")");
	}
	
	/**
	 * Returns the most recent undo.
	 * @return {@link EditCommand}
	 */
	public EditCommand getLastUndo() {
		return this.undos.peek();
	}
	
	/**
	 * Returns the most recent redo.
	 * @return {@link EditCommand}
	 */
	public EditCommand getLastRedo() {
		return this.redos.peek();
	}
	
	/**
	 * Places the marker at the current state.
	 */
	public void mark() {
		// remove any prior marks
		this.undos.removeIf(c -> c == MARK);
		this.redos.removeIf(c -> c == MARK);
		// add a mark at this location
		this.undos.push(MARK);
	}
	
	/**
	 * Removes the marker.
	 */
	public void unmark() {
		this.undos.removeIf(c -> c == MARK);
		this.redos.removeIf(c -> c == MARK);
	}
	
	/**
	 * Returns true if there are commands that can be undone.
	 * @return boolean
	 */
	public boolean isUndoAvailable() {
		return !this.undos.isEmpty();
	}
	
	/**
	 * Returns true if there are commands that can be redone.
	 * @return boolean
	 */
	public boolean isRedoAvailable() {
		return !this.redos.isEmpty();
	}
	
	/**
	 * Returns true if the last command was the marker.
	 * @return boolean
	 * @see #mark()
	 */
	public boolean isTopMarked() {
		EditCommand command = this.undos.peek();
		return command == null || command == MARK;
	}
	
	/**
	 * Returns true if theres a marker anywhere in the set
	 * of undos.
	 * @return boolean
	 */
	public boolean isMarked() {
		return this.undos.contains(MARK);
	}
	
	/**
	 * Resets the manager to it's initial state.
	 */
	public void reset() {
		this.undos.clear();
		this.redos.clear();
	}
	
	/**
	 * Returns the number of undos currently being managed.
	 * @return int
	 */
	public int getUndoCount() {
		return this.undos.size();
	}
	
	/**
	 * Returns the number of redos currently being managed.
	 * @return int
	 */
	public int getRedoCount() {
		return this.redos.size();
	}
}
