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

import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;

import javafx.scene.control.Spinner;

/**
 * Action that focuses and sets the value of the given Spinner.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the value type
 */
public final class SpinnerCommandAction<T> extends NodeCommandAction<Spinner<T>, ValueChangedCommandOperation<T>> implements CommandAction<ValueChangedCommandOperation<T>> {
	/**
	 * Minimal constructor.
	 * @param input the input control
	 */
	public SpinnerCommandAction(Spinner<T> input) {
		super(input);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.action.CommandAction#redo(org.praisenter.javafx.command.operation.CommandOperation)
	 */
	@Override
	public void redo(ValueChangedCommandOperation<T> operation) {
		// UI
		if (this.node != null) {
			this.node.getValueFactory().setValue(operation.getNewValue());
			if (this.node.isVisible()) {
				this.node.requestFocus();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.command.action.CommandAction#undo(org.praisenter.javafx.command.operation.CommandOperation)
	 */
	@Override
	public void undo(ValueChangedCommandOperation<T> operation) {
		// UI
		if (this.node != null) {
			this.node.getValueFactory().setValue(operation.getOldValue());
			if (this.node.isVisible()) {
				this.node.requestFocus();
			}
		}
	}
}
