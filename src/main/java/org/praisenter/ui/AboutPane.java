package org.praisenter.ui;

import java.awt.Desktop;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.RuntimeProperties;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

final class AboutPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String ABOUT_CSS = "p-about";
	private static final String ABOUT_RIGHT_CSS = "p-about-right";
	private static final String ABOUT_LEFT_CSS = "p-about-left";
	
	private class DataPoint {
		String name;
		String value;
		
		public DataPoint() {}
		
		public DataPoint(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	public AboutPane() {
		this.getStyleClass().add(ABOUT_CSS);
		
		Label lbl = new Label(Translations.get("about.text"));
		lbl.setWrapText(true);
		lbl.maxWidthProperty().bind(this.widthProperty());
		
		TableView<DataPoint> tv = new TableView<>();
		
		TableColumn<DataPoint, String> tc1 = new TableColumn<>(Translations.get("about.property"));
		tc1.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().name));
		tc1.setCellFactory(p -> new TableCell<DataPoint, String>() {
			@Override
			protected void updateItem(String string, boolean empty) {
				super.updateItem(string, empty);
				if (string == null || empty) {
					setText(null);
				} else {
					setText(string);
				}
			}
		});
		TableColumn<DataPoint, String> tc2 = new TableColumn<>(Translations.get("about.value"));
		tc2.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().value));
		tc2.setCellFactory(p -> new TableCell<DataPoint, String>() {
			@Override
			protected void updateItem(String string, boolean empty) {
				super.updateItem(string, empty);
				if (string == null || empty) {
					setText(null);
				} else {
					setText(string);
				}
			}
		});
		
		tv.getColumns().add(tc1);
		tv.getColumns().add(tc2);
		
		tv.getItems().add(new DataPoint(Translations.get("about.version"), Version.STRING));
		tv.getItems().add(new DataPoint(Translations.get("about.version.java"), RuntimeProperties.JAVA_VERSION));
		tv.getItems().add(new DataPoint(Translations.get("about.version.javafx"), System.getProperties().get("javafx.runtime.version").toString()));
		tv.getItems().add(new DataPoint(Translations.get("about.version.lucene"), org.apache.lucene.util.Version.LATEST.toString()));
		tv.getItems().add(new DataPoint(Translations.get("about.os"), RuntimeProperties.OPERATING_SYSTEM));
		tv.getItems().add(new DataPoint(Translations.get("about.arch"), RuntimeProperties.ARCHITECTURE));
		
		tv.setPrefHeight(175);
		tv.setPrefWidth(100);
		
		VBox layout = new VBox();
		layout.getStyleClass().add(ABOUT_RIGHT_CSS);
		layout.getChildren().addAll(lbl, tv);
		
		VBox.setVgrow(lbl, Priority.SOMETIMES);
		VBox.setVgrow(tv, Priority.NEVER);
		
		StackPane left = new StackPane();
		left.getStyleClass().add(ABOUT_LEFT_CSS);
		ImageView logo = new ImageView(AboutPane.class.getResource("/org/praisenter/logo/icon128x128.png").toExternalForm());
		left.getChildren().add(logo);
		
		StackPane.setAlignment(logo, Pos.TOP_CENTER);
		
		Hyperlink link = new Hyperlink(Constants.WEBSITE);
		layout.getChildren().add(link);
		
		link.setOnAction(e -> {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				try {
				    Desktop.getDesktop().browse(new URI(Constants.WEBSITE));
				} catch (Exception ex) {
					LOGGER.error("Failed to open default browser for URL: " + Constants.WEBSITE, ex);
				}
			}
		});
		
		this.setLeft(left);
		this.setCenter(layout);
	}
}
