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
package org.praisenter.javafx.command.action;

import org.praisenter.javafx.command.operation.CommandOperation;
import org.praisenter.utility.Numbers;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Base action for TreeView TreItem selection.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the TreeView data type
 * @param <V> the {@link CommandOperation} type
 */
public abstract class TreeItemCommandAction<T, V extends CommandOperation> implements CommandAction<V> {
	/** The tree */
	protected final TreeView<T> tree;
	
	/** The item */
	protected final TreeItem<T> item;
	
	/** The item's original parent */
	protected final TreeItem<T> parent;
	
	/**
	 * Minimal constructor.
	 * @param tree the tree
	 * @param item the item
	 */
	public TreeItemCommandAction(TreeView<T> tree, TreeItem<T> item) {
		this.tree = tree;
		this.item = item;
		this.parent = item != null ? item.getParent() : null;
	}

	/**
	 * Minimal constructor.
	 * @param tree the tree
	 * @param item the item
	 * @param parent the parent item (if not currently the parent)
	 */
	public TreeItemCommandAction(TreeView<T> tree, TreeItem<T> item, TreeItem<T> parent) {
		this.tree = tree;
		this.item = item;
		this.parent = parent;
	}
	
	/**
	 * Selects the tree item in the tree view.
	 */
	public void selectItem() {
		if (this.item != null && this.tree != null) {
			this.selectItem(this.item);
		}
	}
	
	/**
	 * Selects the parent tree item in the tree view.
	 */
	public void selectParent() {
		if (this.parent != null && this.tree != null) {
			this.selectItem(this.parent);
		}
	}
	
	/**
	 * Selects the given item.
	 * @param item the item
	 */
	protected void selectItem(TreeItem<T> item) {
		item.setExpanded(true);
		
		int index = this.tree.getRow(item);
		this.tree.getSelectionModel().clearAndSelect(index);
		
		// scroll to it (well, close to it, we don't want it at the top)
		index = Numbers.clamp(index - 5, 0, index);
		this.tree.scrollTo(index);
	}
}
