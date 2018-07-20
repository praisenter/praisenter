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

/**
 * Represents an edit command that can be stored. The command can be undone and redone.
 * <p>
 * Generally speaking, commands should be immutable.
 * @author William Bittle
 * @version 3.0.0
 * @see EditManager
 */
public interface EditCommand {
	/**
	 * Executes the command for the first time.
	 */
	public void execute();
	
	/**
	 * Called when the command should be undone.
	 */
	public void undo();
	
	/**
	 * Called when the command should be redone.
	 */
	public void redo();
	
	/**
	 * Returns true if this command can be merged with the given command.
	 * <p>
	 * NOTE: EditCommands of different types are always unsupported.
	 * @param command the command
	 * @return boolean
	 */
	public boolean isMergeSupported(EditCommand command);
	
	/**
	 * Merges this command with the given command returning a new command that
	 * replaces both.
	 * @param command the command to merge with
	 * @return {@link EditCommand}
	 */
	public EditCommand merge(EditCommand command);
	
	/**
	 * Returns true if the command is valid.
	 * <p>
	 * This was done to make the code easier to read in the {@link #execute()},
	 * {@link #undo()} and {@link #redo()} methods and to allow the constructor
	 * for implementors to contain the code to get the command ready. It's 
	 * possible, in this case, that the command may not get initialized properly
	 * and as such would be invalid.
	 * @return boolean
	 */
	public boolean isValid();
}
