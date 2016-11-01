package org.praisenter.javafx.screen;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public abstract class ScreenViewDragDropManager {
	private ScreenView dragged;
	private final ScreenView missing;
	private ScreenView draggedOver;
	
	public ScreenViewDragDropManager() {
		this.missing = ScreenView.createUnassignedScreenView(this);
	}
	
	/**
	 * Called when the drag dropped event is fired and is valid.
	 * @param view1 
	 * @param view2 
	 */
	public abstract void swap(ScreenView view1, ScreenView view2);
	
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
	
	public void dragExited(ScreenView screen, DragEvent e) {}
	
	public void dragEntered(ScreenView screen, DragEvent e) {}
	
	public void dragOver(ScreenView screen, DragEvent e) {
		e.acceptTransferModes(TransferMode.MOVE);
	}
	
	public void dragDropped(ScreenView screen, DragEvent e) {
		if (this.dragged != null) {
			this.swap(screen, this.dragged);
			e.setDropCompleted(true);
		}
	}
	
	public void dragDone(ScreenView screen, DragEvent e) {}
}
