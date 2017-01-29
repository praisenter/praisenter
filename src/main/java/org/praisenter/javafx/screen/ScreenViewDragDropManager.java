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
package org.praisenter.javafx.screen;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * Represents a drag drop manager for the screen views.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class ScreenViewDragDropManager {
	/** The screen view being dragged */
	private ScreenView dragged;
	
	/**
	 * Called when the drag dropped event is fired and is valid.
	 * @param view1 the original view
	 * @param view2 the new view
	 */
	public abstract void swap(ScreenView view1, ScreenView view2);
	
	/**
	 * Called when a drag guesture is detected.
	 * @param screen the screen view that is being dragged
	 * @param e the event
	 */
	public void dragDetected(ScreenView screen, MouseEvent e) {
		this.dragged = screen;
		
		// start the drag
		Dragboard db = screen.startDragAndDrop(TransferMode.MOVE);
		ClipboardContent cc = new ClipboardContent();
		// we have to put something in there to make sure the d&d works
		cc.put(DataFormat.PLAIN_TEXT, screen.toString());
		db.setContent(cc);
        db.setDragView(screen.snapshot(null, null));
	}
	
	/**
	 * Called when a drag exited event is detected.
	 * @param screen the screen view that is being exited
	 * @param e the event
	 */
	public void dragExited(ScreenView screen, DragEvent e) {}
	
	/**
	 * Called when a drag entered event is detected.
	 * @param screen the screen view that is being entered
	 * @param e the event
	 */
	public void dragEntered(ScreenView screen, DragEvent e) {}
	
	/**
	 * Called when a drag over event is detected.
	 * @param screen the screen view that the mouse is over
	 * @param e the event
	 */
	public void dragOver(ScreenView screen, DragEvent e) {
		if (screen == this.dragged) {
			e.acceptTransferModes(TransferMode.NONE);
		} else {
			e.acceptTransferModes(TransferMode.MOVE);
		}
	}
	
	/**
	 * Called when a drag drop event is detected.
	 * @param screen the screen view that is being replaced
	 * @param e the event
	 */
	public void dragDropped(ScreenView screen, DragEvent e) {
		if (this.dragged != null && this.dragged != screen) {
			this.swap(screen, this.dragged);
			e.setDropCompleted(true);
		}
	}
	
	/**
	 * Called when a drag done event is detected.
	 * @param screen the screen view where the drag action finished
	 * @param e the event
	 */
	public void dragDone(ScreenView screen, DragEvent e) {
		
	}
}
