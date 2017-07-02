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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.praisenter.javafx.command.action.CheckboxCommandAction;
import org.praisenter.javafx.command.action.ComboCommandAction;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.action.FireEventCommandAction;
import org.praisenter.javafx.command.action.FocusNodeCommandAction;
import org.praisenter.javafx.command.action.FunctionCommandAction;
import org.praisenter.javafx.command.action.SelectTreeItemCommandAction;
import org.praisenter.javafx.command.action.SelectTreeItemRemovedCommandAction;
import org.praisenter.javafx.command.action.SliderCommandAction;
import org.praisenter.javafx.command.action.SpinnerCommandAction;
import org.praisenter.javafx.command.action.TextInputCommandAction;
import org.praisenter.javafx.command.action.ToggleButtonCommandAction;
import org.praisenter.javafx.command.operation.CommandOperation;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Helper class to compose and build commands.
 * @author William Bittle
 * @version 3.0.0
 */
public final class CommandFactory {
	/** Hidden constructor */
	private CommandFactory() {}

	/**
	 * Returns a command that executes the given commands in the order they are found in the given list.
	 * @param commands the commands
	 * @return {@link OrderedCompositeEditCommand}&lt;{@link OrderedWrappedEditCommand}&gt;
	 */
	public static final OrderedCompositeEditCommand<OrderedWrappedEditCommand> chain(List<EditCommand> commands) {
		List<OrderedWrappedEditCommand> ordered = new ArrayList<>();
		int i = 0;
		for (EditCommand command : commands) {
			ordered.add(new OrderedWrappedEditCommand(command, i++));
		}
		return new OrderedCompositeEditCommand<>(ordered);
	}
	
	/**
	 * Returns a command that executes the given commands in the order they are given.
	 * @param commands the commands
	 * @return {@link OrderedCompositeEditCommand}&lt;{@link OrderedWrappedEditCommand}&gt;
	 */
	public static final OrderedCompositeEditCommand<OrderedWrappedEditCommand> chain(EditCommand... commands) {
		return chain(new ArrayList<>(Arrays.asList(commands)));
	}
	
	/**
	 * Returns a command that executes the given commands in their natural order.
	 * @param commands the commands
	 * @return {@link OrderedCompositeEditCommand}&lt;T&gt;
	 */
	public static final <T extends EditCommand & Comparable<T>> OrderedCompositeEditCommand<T> sequence(List<T> commands) {
		return new OrderedCompositeEditCommand<>(commands);
	}
	
	/**
	 * Returns a command that executes the given commands in their natural order.
	 * @param commands the commands
	 * @return {@link OrderedCompositeEditCommand}&lt;T&gt;
	 */
	@SafeVarargs
	public static final <T extends EditCommand & Comparable<T>> OrderedCompositeEditCommand<T> sequence(T... commands) {
		return new OrderedCompositeEditCommand<>(commands);
	}
	
	/**
	 * Returns a command that executes the given actions in the order given.
	 * @param actions the actions
	 * @return {@link ActionsOnlyEditCommand}&lt;T&gt;
	 */
	@SafeVarargs
	public static final <T extends CommandOperation> ActionsOnlyEditCommand<T> chain(CommandAction<T>... actions) {
		return new ActionsOnlyEditCommand<>(actions);
	}
	
	// actions
	
	/**
	 * Returns a focus action for the given node.
	 * @param node the node
	 * @return {@link FocusNodeCommandAction}&lt;Node&gt;
	 */
	public static final <T extends CommandOperation> FocusNodeCommandAction<T> focus(Node node) {
		return new FocusNodeCommandAction<>(node);
	}
	
	/**
	 * Returns a TreeItem selection action for the given item.
	 * @param tree the tree
	 * @param item the item
	 * @return {@link SelectTreeItemCommandAction}&lt;T, V&gt;
	 */
	public static final <T, V extends CommandOperation> SelectTreeItemCommandAction<T, V> select(TreeView<T> tree, TreeItem<T> item) {
		return new SelectTreeItemCommandAction<>(tree, item);
	}
	
	/**
	 * Returns a TreeItem selection action for the given item with the knowledge it's being removed.
	 * @param tree the tree
	 * @param item the item
	 * @return {@link SelectTreeItemCommandAction}&lt;T, V&gt;
	 */
	public static final <T, V extends CommandOperation> SelectTreeItemRemovedCommandAction<T, V> selectRemoved(TreeView<T> tree, TreeItem<T> item) {
		return new SelectTreeItemRemovedCommandAction<>(tree, item);
	}
	
	/**
	 * Returns a text focus, set text, and caret position action for the given text control.
	 * @param input the input
	 * @return {@link TextInputCommandAction}&lt;T&gt;
	 */
	public static final <T extends TextInputControl> TextInputCommandAction<T> text(T input) {
		return new TextInputCommandAction<T>(input);
	}
	
	/**
	 * Returns a focus and set value action for the given spinner.
	 * @param input the input
	 * @return {@link SpinnerCommandAction}&lt;T&gt;
	 */
	public static final <T> SpinnerCommandAction<T> spinner(Spinner<T> input) {
		return new SpinnerCommandAction<>(input);
	}
	
	/**
	 * Returns a focus and set value action for the given combobox.
	 * @param input the input
	 * @return {@link ComboCommandAction}&lt;T&gt;
	 */
	public static final <T> ComboCommandAction<T> combo(ComboBox<T> input) {
		return new ComboCommandAction<>(input);
	}
	
	/**
	 * Returns a focus and set value action for the given toggle button.
	 * @param input the input
	 * @return {@link ToggleButtonCommandAction}
	 */
	public static final ToggleButtonCommandAction toggle(ToggleButton input) {
		return new ToggleButtonCommandAction(input);
	}

	/**
	 * Returns a focus and set value action for the given check box.
	 * @param input the input
	 * @return {@link CheckboxCommandAction}
	 */
	public static final CheckboxCommandAction check(CheckBox input) {
		return new CheckboxCommandAction(input);
	}
	
	/**
	 * Returns a focus and set value action for the given slider.
	 * @param input the input
	 * @return {@link SliderCommandAction}
	 */
	public static final SliderCommandAction slider(Slider input) {
		return new SliderCommandAction(input);
	}

	public static final <T extends Node, E extends CommandOperation> FireEventCommandAction<T, E> event(T node, Event event) {
		return new FireEventCommandAction<>(node, event);
	}
	
	public static final <T extends Node, E extends CommandOperation> FireEventCommandAction<T, E> event(T node, Event redo, Event undo) {
		return new FireEventCommandAction<>(node, redo, undo);
	}
	
	public static final <T extends CommandOperation> FunctionCommandAction<T> func(Consumer<T> action) {
		return new FunctionCommandAction<T>(action, action);
	}
	
	public static final <T extends CommandOperation> FunctionCommandAction<T> undo(Consumer<T> action) {
		return new FunctionCommandAction<T>(action, null);
	}
	
	public static final <T extends CommandOperation> FunctionCommandAction<T> redo(Consumer<T> action) {
		return new FunctionCommandAction<T>(null, action);
	}
	
	public static final <T extends CommandOperation> FunctionCommandAction<T> func(Consumer<T> undo, Consumer<T> redo) {
		return new FunctionCommandAction<T>(undo, redo);
	}
	
	// operations
	
	/**
	 * Returns a single value change operation.
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @return {@link ValueChangedCommandOperation}&lt;T&gt;
	 */
	public static final <T> ValueChangedCommandOperation<T> changed(T oldValue, T newValue) { 
		return new ValueChangedCommandOperation<T>(oldValue, newValue);
	}
}