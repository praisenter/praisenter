/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.command;

/**
 * Represents a user command.
 * <p>
 * Classes of this type are used to store state information for complex user
 * interface actions.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 * @param <B> the {@link #begin(Object)} method arguments type
 * @param <U> the {@link #update(Object)} method arguments type
 * @param <E> the {@link #end(Object)} method arguments type
 */
public abstract class Command<B, U, E> {
	/** The active flag */
	protected boolean active;
	
	/** The begin command arguments */
	protected B beginArguments;
	
	/** The update command arguments */
	protected U updateArguments;
	
	/** The end command arguments */
	protected E endArguments;
	
	/**
	 * Called when the action is begun.
	 * @param arguments the begin command arguments
	 */
	public void begin(B arguments) {
		this.active = true;
		this.beginArguments = arguments;
	}
	
	/**
	 * Called when the action should be updated.
	 * @param arguments the update command arguments
	 */
	public void update(U arguments) {
		this.updateArguments = arguments;
	}
	
	/**
	 * Called when the command should be ended
	 * @param arguments the end command arguments
	 */
	public void end(E arguments) {
		this.active = false;
		this.endArguments = arguments;
	}
	
	/**
	 * Convenience method to end a {@link Command}.
	 */
	public void end() {
		this.end(null);
	}
	
	/**
	 * Returns true if the command is currently active.
	 * @return boolean
	 */
	public synchronized boolean isActive() {
		return this.active;
	}
	
	/**
	 * Sets whether the command is active or not.
	 * @param flag true if the command should be flagged as active
	 */
	public synchronized void setActive(boolean flag) {
		this.active = flag;
	}

	/**
	 * Returns the begin arguments.
	 * @return B
	 */
	public B getBeginArguments() {
		return this.beginArguments;
	}

	/**
	 * Returns the update arguments.
	 * <p>
	 * This will return the current update arguments.
	 * @return U
	 */
	public U getUpdateArguments() {
		return this.updateArguments;
	}

	/**
	 * Returns the end arguments.
	 * @return E
	 */
	public E getEndArguments() {
		return this.endArguments;
	}
}
