package org.praisenter.javafx.media;

import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.praisenter.Tag;
import org.praisenter.media.Media;

public final class MediaPicker extends VBox {
	final Button button;
	final MediaDialog dialog;
	
	final ObjectProperty<Media> value = new SimpleObjectProperty<Media>();
	
	// TODO translate
	public MediaPicker(Media media, MediaLibrary library, ObservableSet<Tag> tags, MediaType... types) {
		this.dialog = new MediaDialog(library, tags, types);
		
		// wire up the value property with the selected property
		this.value.bindBidirectional(this.dialog.mlp.selectedProperty());
		
		this.button = new Button("Browse...");
		this.button.setOnAction((e) -> {
			dialog.dialog.initOwner(button.getScene().getWindow());
			dialog.show();
		});
		
		this.getChildren().add(this.button);
		
		// set the initial value
		this.value.set(media);
	}
	
	public ObjectProperty<Media> valueProperty() {
		return this.value;
	}
	
	public Media getValue() {
		return this.value.get();
	}
	
	public void setValue(Media media) {
		this.value.set(media);
	}
}
