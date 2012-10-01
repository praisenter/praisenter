package org.praisenter.settings.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of mutually exclusive commands.
 * @param <E> the {@link Command} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MutexCommandGroup<E extends Command> {
	/** The commands */
	protected List<E> commands;
	
	/**
	 * Default constructor.
	 */
	public MutexCommandGroup() {
		this(new ArrayList<E>());
	}
	
	/**
	 * Optional constructor.
	 * @param commands the initial commands
	 */
	public MutexCommandGroup(E[] commands) {
		this.commands = new ArrayList<E>();
		// add all the commands
		for (E c : commands) {
			this.commands.add(c);
		}
	}
	
	/**
	 * Optional constructor.
	 * @param commands the initial commands
	 */
	public MutexCommandGroup(List<E> commands) {
		this.commands = new ArrayList<E>();
		// add all the commands
		for (E c : commands) {
			this.commands.add(c);
		}
	}
	
	/**
	 * Returns true if a {@link Command} in this {@link MutexCommandGroup} is active.
	 * @return boolean
	 */
	public boolean isCommandActive() {
		for (E c : this.commands) {
			if (c.isActive()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a command to this command group.
	 * <p>
	 * If the given command is active and there already exists an active command in
	 * this {@link MutexCommandGroup}, then the given command is set to inactive, 
	 * but not ended, by calling the {@link Command#setActive(boolean)} method.
	 * @param command the command
	 */
	public void addCommand(E command) {
		if (command.isActive() && this.isCommandActive()) {
			command.setActive(false);
		}
		this.commands.add(command);
	}
	
	/**
	 * Removes the given command from this {@link MutexCommandGroup} and returns
	 * true if the command was found and removed.
	 * @param command the command
	 * @return boolean
	 */
	public boolean removeCommand(E command) {
		return this.commands.remove(command);
	}
	
	/**
	 * Returns the number of commands in this {@link MutexCommandGroup}.
	 * @return int
	 */
	public int getCommandCount() {
		return this.commands.size();
	}
	
	/**
	 * Returns the command at the given index.
	 * @param index the index
	 * @return E
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 */
	public E getCommand(int index) throws IndexOutOfBoundsException {
		return this.commands.get(index);
	}
	
	/**
	 * Removes all the commands in this {@link MutexCommandGroup}.
	 */
	public void removeAllCommands() {
		this.commands.clear();
	}
}
