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
package org.praisenter.javafx;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * Represents a custom ContextMenu where {@link ApplicationAction}s can be easily bound.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class ApplicationContextMenu extends ContextMenu {
	/** The node this context menu is bound to */
	private final Node node;
	
	/** The scene for the attached node */
	private final ObjectProperty<Scene> scene = new SimpleObjectProperty<Scene>();
	
	/** The menu items */
	private final List<MenuItem> items = new ArrayList<MenuItem>();
	
	/**
	 * Minimal constructor.
	 * @param node the node this context menu is for
	 */
	public ApplicationContextMenu(Node node) {
		this.node = node;
		
		this.scene.bind(node.sceneProperty());
		
		// listen for the scene changing of the attached node so that
		// we can remove and re-add the items, this is due to Java FX
		// not removing the accelerators when the ContextMenu is removed
		// from the graph. We don't want accelerators to be executed
		// when the node is not part of the scene graph
		this.scene.addListener((obs, ov, nv) -> {
			if (nv == null) {
				// its been removed from the graph so remove all the items
				items.addAll(this.getItems());
				this.getItems().clear();
			} else {
				// its been added to the graph so add all the items back
				this.getItems().addAll(items);
				items.clear();
			}
		});
		
		// just re-evaluate the state when before the menu is shown
		this.setOnShowing(e -> {
			if (this.node instanceof ApplicationPane) {
				updateMenuState((ApplicationPane)this.node);
			}
		});
	}
	
	/**
	 * Helper method for making {@link ApplicationAction} MenuItems.
	 * @param action the action
	 * @return MenuItem
	 */
	public MenuItem createMenuItem(ApplicationAction action) {
		MenuItem item = action.toMenuItem();
		item.setOnAction(e -> {
			this.node.fireEvent(new ApplicationEvent(item, item, ApplicationEvent.ALL, action));
		});
		return item;
	}
	
	/**
	 * Updates the state of the context menu based on the given application pane's state.
	 * @param pane the pane
	 */
	private void updateMenuState(ApplicationPane pane) {
		Deque<MenuItem> menus = new LinkedList<MenuItem>();
		// seed with the menu bar's menus
		menus.addAll(this.getItems());
		while (menus.size() > 0) {
			MenuItem menu = menus.pop();
			
			// process this item
			Object data = menu.getUserData();
			if (data != null && data instanceof ApplicationAction) {
				ApplicationAction action = (ApplicationAction)data;
				// an action is disabled or hidden as long as both the root
				// and the currently focused application pane don't handle it
				boolean disabled = !pane.isApplicationActionEnabled(action);
				boolean visible = pane.isApplicationActionVisible(action);
				menu.setDisable(disabled);
				menu.setVisible(visible);
			}
			
			// add children
			if (menu instanceof Menu) {
				menus.addAll(((Menu)menu).getItems());
			}
		}
	}
}
