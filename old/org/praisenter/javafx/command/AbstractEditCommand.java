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

import java.util.function.Consumer;

import org.praisenter.utility.Numbers;

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
 * Implements some default functionality for edit commands like selection of nodes in a tree view,
 * selection of text in a text control, etc.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class AbstractEditCommand implements EditCommand {
	
	/**
	 * Executes the given method with the given argument.
	 * @param method the method
	 * @param argument the argument
	 */
	public final <T> void execute(Consumer<T> method, T argument) {
		if (method != null) {
			method.accept(argument);
		}
	}
	
	/**
	 * Selects the given item in the given tree.
	 * @param tree the tree
	 * @param item the item
	 */
	public final <T> void select(TreeView<T> tree, TreeItem<T> item) {
		if (tree != null && item != null) {
			item.setExpanded(true);
			
			int index = tree.getRow(item);
			tree.getSelectionModel().clearAndSelect(index);
			
			// scroll to it (well, close to it, we don't want it at the top)
			index = Numbers.clamp(index - 5, 0, index);
			tree.scrollTo(index);
		}
	}
	
	/**
	 * Focuses the given node.
	 * @param node the node
	 */
	public final void focus(Node node) {
		if (node != null) {
			node.requestFocus();
		}
	}
	
	/**
	 * Fires the given event on the given node.
	 * @param node the node
	 * @param event the event
	 */
	public final void event(Node node, Event event) {
		if (node != null && event != null) {
			node.fireEvent(event);
		}
	}
	
	/**
	 * Updates the given control with the new value and
	 * focuses it.
	 * @param check the control
	 * @param value the value
	 */
	public final void check(CheckBox check, boolean value) {
		if (check != null) {
			check.setSelected(value);
			if (check.isVisible()) {
				check.requestFocus();
			}
		}
	}
	
	/**
	 * Updates the given control with the new value and
	 * focuses it.
	 * @param combo the control
	 * @param value the value
	 */
	public final <T> void combo(ComboBox<T> combo, T value) {
		if (combo != null) {
			combo.setValue(value);
			if (combo.isVisible()) {
				combo.requestFocus();
			}
		}
	}
	
	/**
	 * Updates the given control with the new value and
	 * focuses it.
	 * @param spinner the control
	 * @param value the value
	 */
	public final <T> void spinner(Spinner<T> spinner, T value) {
		if (spinner != null) {
			spinner.getValueFactory().setValue(value);
			if (spinner.isVisible()) {
				spinner.requestFocus();
			}
		}
	}

	/**
	 * Updates the given control with the new value and
	 * focuses it.
	 * @param slider the control
	 * @param value the value
	 */
	public final void slider(Slider slider, double value) {
		if (slider != null) {
			slider.setValue(value);
			if (slider.isVisible()) {
				slider.requestFocus();
			}
		}
	}
	
	/**
	 * Updates the given control with the new value and
	 * focuses it and positions the caret.
	 * @param text the control
	 * @param value the value
	 */
	public final <T extends TextInputControl> void text(T text, String value) {
		if (text != null) {
			text.setText(value);
			if (text.isVisible()) {
				text.requestFocus();
			}
			if (value != null) {
				text.positionCaret(value.length());
			}
		}
	}
	
	/**
	 * Updates the given control with the new value and
	 * focuses it.
	 * @param toggle the control
	 * @param value the value
	 */
	public final void toggle(ToggleButton toggle, boolean value) {
		if (toggle != null) {
			toggle.setSelected(value);
			if (toggle.isVisible()) {
				toggle.requestFocus();
			}
		}
	}
}
