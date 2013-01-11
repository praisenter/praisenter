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
