package org.praisenter.javafx.bible;

import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.FailedOperation;
import org.praisenter.WarningOperation;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.UnboundBibleImporter;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.FlowListView;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;

public final class BibleLibraryPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	public BibleLibraryPane(BibleLibrary bibleLibrary) {
		
		List<Bible> bibles = null;
		try {
			bibles = bibleLibrary.getBibles();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ObservableList<BibleListItem> items = FXCollections.observableArrayList();
		for (Bible bible : bibles) {
			items.add(new BibleListItem(bible));
		}
		
		FlowListView<BibleListItem> lstBibles = new FlowListView<BibleListItem>(new BibleListViewCellFactory());
		lstBibles.itemsProperty().bindContent(items);
		
		lstBibles.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					event.acceptTransferModes(TransferMode.COPY);
				} else {
					event.consume();
				}
			}
        });
		
		lstBibles.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					final List<File> files = db.getFiles();
					
					// add some loading items
					List<BibleListItem> loadings = new ArrayList<BibleListItem>();
					if (files != null && files.size() > 0) {
						for (File file : files) {
							loadings.add(new BibleListItem(file.toPath().getFileName().toString()));
						}
						items.addAll(loadings);
					}
					
					// import the media
					Task<Void> task = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							if (files != null && files.size() > 0) {
								final List<WarningOperation<Media>> warnings = new ArrayList<WarningOperation<Media>>();
								final List<FailedOperation<File>> failed = new ArrayList<FailedOperation<File>>();
								
								for (File file : files) {
									try {
										final BibleListItem loading = new BibleListItem(file.toPath().getFileName().toString());
										// import the bible
										final Bible bible = new UnboundBibleImporter(bibleLibrary).execute(file.toPath());
										BibleListItem success = new BibleListItem(bible);
										Platform.runLater(() -> {
											// remove the loading
											items.remove(loading);
											items.add(success);
										});
									} catch (Exception e) {
										LOGGER.error("Failed to add bible '" + file.toPath().toAbsolutePath().toString() + "' to the bible library.", e);
										failed.add(new FailedOperation<File>(file, e));
									}
								}
								
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										items.removeAll(loadings);
										
										if (warnings.size() > 0) {
											// get the warning files
											String[] wFileNames = warnings.stream().map(f -> f.getMessage()).collect(Collectors.toList()).toArray(new String[0]);
											// get the failed media
											String list = String.join(", ", wFileNames);
											Alert alert = new Alert(AlertType.INFORMATION);
											alert.initOwner(getScene().getWindow());
											alert.initModality(Modality.WINDOW_MODAL);
											// TODO fix translations
											alert.setTitle(Translations.get("media.import.info.title"));
											alert.setHeaderText(Translations.get("media.import.info.header"));
											alert.setContentText(list);
											alert.show();
										}
										
										if (failed.size() > 0) {
											// get the exceptions
											Exception[] exceptions = failed.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
											// get the failed media
											String list = String.join(", ", failed.stream().map(f -> f.getData().getName()).collect(Collectors.toList()));
											Alert alert = Alerts.exception(
													getScene().getWindow(),
													null, 
													null,
													// TODO fix translations
													MessageFormat.format(Translations.get("media.import.error"), list), 
													exceptions);
											alert.show();
										}
									}
								});
							}
							return null;
						}
					};
					new Thread(task).start();
				}
				event.setDropCompleted(true);
				event.consume();
			}
        });
		
		this.setCenter(lstBibles);
	}
}
