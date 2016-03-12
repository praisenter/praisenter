package org.praisenter.javafx.media;

import org.praisenter.Tag;
import org.praisenter.javafx.Praisenter;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaType;

import javafx.collections.ObservableSet;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

final class MediaDialog extends BorderPane {
	final Stage dialog;
	
	final MediaLibraryPane mlp;
	final Button btnOK;
	final Button btnCancel;
	
	public MediaDialog(
			final MediaLibrary library, 
    		ObservableSet<Tag> tags,
    		MediaType... types) {
		dialog = new Stage();
		dialog.setTitle("Media Library");
		
		mlp = new MediaLibraryPane(library, Orientation.HORIZONTAL, tags, types);
		btnOK = new Button("OK");
		btnCancel = new Button("Cancel");
		
		this.setCenter(mlp);
		
		HBox bottom = new HBox();
		bottom.setAlignment(Pos.BASELINE_RIGHT);
		bottom.getChildren().addAll(btnOK, btnCancel);
		
		this.setBottom(bottom);
		
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setWidth(650);
		dialog.setHeight(400);
		dialog.setResizable(true);
		
		
		Scene scene = new Scene(this);
		scene.getStylesheets().add(Praisenter.THEME_CSS);
		dialog.setScene(scene);
	}
	
	public void show() {
		dialog.show();
	}
}
