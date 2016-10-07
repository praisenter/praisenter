package org.praisenter.javafx.bible;

import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;

final class BibleTreeCell extends TreeCell<TreeData> {
	private final BibleEditorEventManager manager;
	
	private final Label graphic;
	
	public BibleTreeCell(BibleEditorEventManager manager) {
		this.manager = manager;
		this.graphic = new Label();
		this.graphic.setStyle("-fx-font-weight: bold; -fx-font-size: 0.8em;");
		
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
        this.graphic.textProperty().unbind();
        if (!empty && item != null) {
            this.textProperty().bind(item.label);
            this.graphic.textProperty().bind(item.list);
            this.setGraphic(this.graphic);
        } else {
        	this.setText(null);
        	this.graphic.setText(null);
        	this.setGraphic(null);
        }
    }
}
