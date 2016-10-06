package org.praisenter.javafx.bible;

import javafx.scene.control.TreeCell;

final class BibleTreeCell extends TreeCell<TreeData> {
	private final BibleEditorEventManager manager;
	
	public BibleTreeCell(BibleEditorEventManager manager) {
		this.manager = manager;
		
		setOnDragDetected(e -> {
			manager.dragDetected(this, e);
		});
		
		setOnDragExited(e -> {
			manager.dragExited(this, e);
		});
		
		setOnDragEntered(e -> {
			manager.dragEntered(this, e);
		});
		
		setOnDragOver(e -> {
			manager.dragOver(this, e);
		});
		
		setOnDragDropped(e -> {
			manager.dragDropped(this, e);
		});
	}
	
    @Override
    //by using Number we don't have to parse a String
    protected void updateItem(TreeData item, boolean empty) {
        super.updateItem(item, empty);
        this.textProperty().unbind();
    	this.graphicProperty().unbind();
        if (!empty && item != null) {
            this.textProperty().bind(item.label);
            this.graphicProperty().bind(item.graphic);
        } else {
        	this.setText(null);
        	this.setGraphic(null);
        }
    }
}
