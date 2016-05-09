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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;

import org.praisenter.FailedOperation;
import org.praisenter.WarningOperation;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.media.MediaListItem;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.resources.translations.Translations;

public final class BibleLibraryPane extends BorderPane {
	public BibleLibraryPane(BibleLibrary bibleLibrary) {
		
		List<Bible> bibles = null;
		try {
			bibles = bibleLibrary.getBibles();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObservableList<BibleTableItem> items = FXCollections.observableArrayList();
		for (Bible bible : bibles) {
			items.add(new BibleTableItem(bible));
		}
		
		TableView<BibleTableItem> tblBibles = new TableView<BibleTableItem>(items);
		
		TableColumn<BibleTableItem, Integer> id = new TableColumn<BibleTableItem, Integer>("Id");
		id.setCellValueFactory(new PropertyValueFactory<BibleTableItem, Integer>("id"));
		tblBibles.getColumns().add(id);
		
		TableColumn<BibleTableItem, String> name = new TableColumn<BibleTableItem, String>("Name");
		name.setCellValueFactory(new PropertyValueFactory<BibleTableItem, String>("name"));
		tblBibles.getColumns().add(name);
		
		tblBibles.setOnDragOver(new EventHandler<DragEvent>() {
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
		tblBibles.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					final List<File> files = db.getFiles();
					
					// add some loading items
					List<MediaListItem> loadings = new ArrayList<MediaListItem>();
					if (files != null && files.size() > 0) {
						for (File file : files) {
							loadings.add(new MediaListItem(file.toPath().getFileName().toString()));
						}
						thelist.addAll(loadings);
						master.addAll(loadings);
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
										final MediaListItem loading = new MediaListItem(file.toPath().getFileName().toString());
										final Media media = library.add(file.toPath());
										boolean allowed = false;
										for (MediaType allowedType : mediaTypes) {
											if (media.getMetadata().getType() == allowedType) {
												allowed = true;
												break;
											}
										}
										if (allowed) {
											MediaListItem success = new MediaListItem(media);
											Platform.runLater(() -> {
												// remove the loading
												thelist.remove(loading);
												master.remove(loading);
												thelist.add(success);
												master.add(success);
											});
										} else {
											LOGGER.info("Media {} was added at a time where its type was not selectable.", file.toPath().toAbsolutePath().toString());
											warnings.add(new WarningOperation<Media>(media, file.toPath().getFileName().toString()));
										}
									} catch (Exception e) {
										LOGGER.error("Failed to add media '" + file.toPath().toAbsolutePath().toString() + "' to the media library.", e);
										failed.add(new FailedOperation<File>(file, e));
									}
								}
								
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										thelist.removeAll(loadings);
										master.removeAll(loadings);
										
										if (warnings.size() > 0) {
											// get the warning files
											String[] wFileNames = warnings.stream().map(f -> f.getMessage()).collect(Collectors.toList()).toArray(new String[0]);
											// get the failed media
											String list = String.join(", ", wFileNames);
											Alert alert = new Alert(AlertType.INFORMATION);
											alert.initOwner(getScene().getWindow());
											alert.initModality(Modality.WINDOW_MODAL);
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
		
		this.setCenter(tblBibles);
	}
}
