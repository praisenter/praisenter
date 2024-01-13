package org.praisenter.ui;

import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.DesktopLauncher;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import me.walkerknapp.devolay.Devolay;

final class AboutPane extends BorderPane {
	private static final String ABOUT_CSS = "p-about";
	private static final String ABOUT_RIGHT_CSS = "p-about-right";
	private static final String ABOUT_LEFT_CSS = "p-about-left";
	
	private class DataPoint {
		String name;
		String value;
		
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
		
		Hyperlink lnkPraisenter = new Hyperlink(Constants.WEBSITE);
		lnkPraisenter.setOnAction(e -> {
			DesktopLauncher.browse(Constants.WEBSITE);
		});
		
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
		
		tc1.setPrefWidth(200);
		tc2.setPrefWidth(300);
		
		tv.getColumns().add(tc1);
		tv.getColumns().add(tc2);
		
		tv.getItems().add(new DataPoint(Translations.get("about.version"), Version.STRING));
		tv.getItems().add(new DataPoint(Translations.get("about.version.java"), RuntimeProperties.JAVA_VERSION));
		tv.getItems().add(new DataPoint(Translations.get("about.version.javafx"), System.getProperties().get("javafx.runtime.version").toString()));
		tv.getItems().add(new DataPoint(Translations.get("about.version.lucene"), org.apache.lucene.util.Version.LATEST.toString()));
		tv.getItems().add(new DataPoint(Translations.get("about.os"), RuntimeProperties.OPERATING_SYSTEM));
		tv.getItems().add(new DataPoint(Translations.get("about.arch"), RuntimeProperties.ARCHITECTURE));
		tv.getItems().add(new DataPoint(Translations.get("ndi.version"), Devolay.getNDIVersion()));
		
		tv.setPrefHeight(175);
		tv.setPrefWidth(100);
		
		VBox layout = new VBox();
		layout.getStyleClass().add(ABOUT_RIGHT_CSS);
		layout.getChildren().addAll(lbl, lnkPraisenter, tv);
		
		VBox.setVgrow(lbl, Priority.NEVER);
		VBox.setVgrow(lnkPraisenter, Priority.NEVER);
		VBox.setVgrow(tv, Priority.ALWAYS);
		
		StackPane left = new StackPane();
		left.getStyleClass().add(ABOUT_LEFT_CSS);
		ImageView logo = new ImageView(AboutPane.class.getResource("/org/praisenter/logo/icon128x128.png").toExternalForm());
		left.getChildren().add(logo);
		
		StackPane.setAlignment(logo, Pos.TOP_CENTER);
		
		// for FFmpeg attribution
		Label lblFFmpeg = new Label(Translations.get("ffmpeg.usage"));
		lblFFmpeg.setWrapText(true);
		Hyperlink lnkFFmpeg = new Hyperlink(Translations.get("ffmpeg.link"));
		lnkFFmpeg.setOnAction(e -> {
			DesktopLauncher.browse(Translations.get("ffmpeg.link"));
		});
		
		// per NDI license requirements
		Label lblNDITrademark = new Label(Translations.get("ndi.trademark"));
		lblNDITrademark.setWrapText(true);
		Hyperlink lnkNDI = new Hyperlink(Translations.get("ndi.link"));
		lnkNDI.setOnAction(e -> {
			DesktopLauncher.browse(Translations.get("ndi.link"));
		});
		
		layout.getChildren().addAll(lblFFmpeg, lnkFFmpeg, lblNDITrademark, lnkNDI);
		
		this.setLeft(left);
		this.setCenter(layout);
	}
}
