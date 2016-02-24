package org.praisenter.javafx.slide.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DragMoveInJavaFx {
	private static final String TAB_DRAG_KEY = "titledpane";
	private ObjectProperty<TitledPane> draggingTab;
	@Override
	public void start(Stage primaryStage) throws Exception {
	    draggingTab = new SimpleObjectProperty<TitledPane>();
	    VBox vbox=new VBox();
	    for(int i=0;i<4;i++) {
	        final TitledPane pane=new TitledPane();
	        pane.setText("pane"+(i+1));
	        vbox.getChildren().add(pane);
	        pane.setOnDragOver(new EventHandler<DragEvent>() {
	            @Override
	            public void handle(DragEvent event) {
	                final Dragboard dragboard = event.getDragboard();
	                if (dragboard.hasString()
	                        && TAB_DRAG_KEY.equals(dragboard.getString())
	                        && draggingTab.get() != null) {
	                    event.acceptTransferModes(TransferMode.MOVE);
	                    event.consume();
	                }
	            }
	        });
	        pane.setOnDragDropped(new EventHandler<DragEvent>() {
	            public void handle(final DragEvent event) {
	                Dragboard db = event.getDragboard();
	                boolean success = false;
	                if (db.hasString()) {
	                    Pane parent = (Pane) pane.getParent();
	                    Object source = event.getGestureSource();
	                    int sourceIndex = parent.getChildren().indexOf(source);
	                    int targetIndex = parent.getChildren().indexOf(pane);
	                    List<Node> nodes = new ArrayList<Node>(parent.getChildren());
	                    if (sourceIndex < targetIndex) {
	                        Collections.rotate(
	                                nodes.subList(sourceIndex, targetIndex + 1), -1);
	                    } else {
	                        Collections.rotate(
	                                nodes.subList(targetIndex, sourceIndex + 1), 1);
	                    }
	                    parent.getChildren().clear();
	                    parent.getChildren().addAll(nodes);
	                    success = true;
	                }
	                event.setDropCompleted(success);
	                event.consume();
	            }
	        });
	        pane.setOnDragDetected(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                Dragboard dragboard = pane.startDragAndDrop(TransferMode.MOVE);
	                ClipboardContent clipboardContent = new ClipboardContent();
	                clipboardContent.putString(TAB_DRAG_KEY);
	                dragboard.setContent(clipboardContent);
	                draggingTab.set(pane);
	                event.consume();
	            }
	        }); 
	    }
	    TitledPane pane=new TitledPane("MAIN",vbox);
	    primaryStage.setScene(new Scene(pane, 890, 570));
	    primaryStage.show();
	}
}
