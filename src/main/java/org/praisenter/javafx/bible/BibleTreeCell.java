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
package org.praisenter.javafx.bible;

import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;

/**
 * Specialized TreeCell for the bible editor.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class BibleTreeCell extends TreeCell<TreeData> {
	/** The tree cell graphic */
	private final Label graphic;
	
	/**
	 * Default constructor.
	 */
	public BibleTreeCell() {
		// styled for verse
		this.graphic = new Label();
		this.graphic.setStyle("-fx-font-weight: bold; -fx-font-size: 0.8em;");
	}
	
	/* (non-Javadoc)
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
    @Override
    protected void updateItem(TreeData item, boolean empty) {
        super.updateItem(item, empty);
        this.textProperty().unbind();
        this.graphic.textProperty().unbind();
        if (!empty && item != null) {
            this.textProperty().bind(item.label);
            this.graphic.textProperty().bind(item.number);
            this.setGraphic(this.graphic);
        } else {
        	this.setText(null);
        	this.graphic.setText(null);
        	this.setGraphic(null);
        }
    }
}
