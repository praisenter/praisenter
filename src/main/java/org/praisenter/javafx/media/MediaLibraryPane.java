package org.praisenter.javafx.media;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.praisenter.FailedOperation;
import org.praisenter.Tag;
import org.praisenter.javafx.FlowListView;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.media.MediaType;
import org.praisenter.utility.ClasspathLoader;

public class MediaLibraryPane extends Application {
	static {
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
	}
	
	private static final Logger LOGGER = LogManager.getLogger(MediaLibraryPane.class);
	
    public static void main(String[] args) {
        launch(args);
    }
    
    // TODO sorting by 1) name or 2) type-then-name
    // TODO filtering by 1) type and 2) tag(s)
    // TODO search by name
    // TODO need a generic way of communicating between multiple instances
    // TODO flag to control flow (horizontal or vertical)
    @Override
    public void start(Stage primaryStage) {
    	primaryStage.setTitle("Media Library");
    	
    	// TODO should be moved eventually
    	FileSystem system = FileSystems.getDefault();
		Path path = system.getPath("C:\\Users\\William\\Desktop\\test");
		MediaThumbnailSettings settings = new MediaThumbnailSettings(
				100, 100,
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/image-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/music-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/video-default-thumbnail.png"));
    	MediaLibrary library = null;
		try {
			library = MediaLibrary.open(path, settings);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		final MediaLibrary lbr = library;
        List<Media> all = library.all();
        Set<Tag> tags = new TreeSet<Tag>(library.getTags());
        
        ObservableList<MediaListItem> om = FXCollections.observableArrayList();
        for (Media media : all) {
        	om.add(new MediaListItem(media));
        }
        //om.addAll(all);
        // by default sort by name asc
        Collections.sort(om);
        
        // the right side of the split pane
        FlowListView<MediaListItem> left = new FlowListView<MediaListItem>(new MediaListViewCellFactory());
        left.itemsProperty().bindContent(om);
        
        EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.DELETE) {
					List<MediaListItem> items = left.selectionProperty().get();
					if (items.size() > 0) {
						// attempt to delete the selected media
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Delete these files");
						alert.setContentText("Are you sure you want to delete these media files?");
						alert.setHeaderText(null);
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							Task<Void> task = new Task<Void>() {
								@Override
								protected Void call() throws Exception {
									final List<MediaListItem> succeeded = new ArrayList<MediaListItem>();
									final List<FailedOperation<Media>> failed = new ArrayList<FailedOperation<Media>>();
									for (MediaListItem mli : items) {
										if (mli.loaded) {
											Media media = mli.media;
											try {
												lbr.remove(media);
												succeeded.add(mli);
											} catch (IOException e) {
												LOGGER.error("Failed to remove media '" + media.getMetadata().getPath().toAbsolutePath().toString() + "' from media library.", e);
												failed.add(new FailedOperation<Media>(media, e));
											}
										}
									}
									
									Platform.runLater(new Runnable() {
										@Override
										public void run() {
											om.removeAll(succeeded);
											// TODO then show a error report of those that failed?
										}
									});
									
									return null;
								}
							};
							new Thread(task).start();
						}
					}
				}
			}
		};

        ScrollPane leftScroller = new ScrollPane();
        leftScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScroller.setFitToWidth(true);
		leftScroller.addEventHandler(KeyEvent.KEY_PRESSED, handler);
		leftScroller.setFocusTraversable(true);
        leftScroller.setContent(left);
        
        leftScroller.setOnDragOver(new EventHandler<DragEvent>() {
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
        leftScroller.setOnDragDropped(new EventHandler<DragEvent>() {
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
						om.addAll(loadings);
					}
					
					// import the media
					Task<Void> task = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							if (files != null && files.size() > 0) {
								final List<MediaListItem> succeeded = new ArrayList<MediaListItem>();
								final List<FailedOperation<File>> failed = new ArrayList<FailedOperation<File>>();
								
								for (File file : files) {
									try {
										// TODO add an empty cell with a progress bar on top of it hook up to this action
										final Media media = lbr.add(file.toPath());
										succeeded.add(new MediaListItem(media));
									} catch (Exception e) {
										LOGGER.error("Failed to add media '" + file.toPath().toAbsolutePath().toString() + "' to the media library.", e);
										failed.add(new FailedOperation<File>(file, e));
									}
								}
								
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										om.removeAll(loadings);
										om.addAll(succeeded);
										// TODO show error report
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

        ObservableSet<Tag> allTags = FXCollections.observableSet(tags);
        MediaMetadataView right = new MediaMetadataView(library, allTags);
        
        // wire up the selected media to the media metadata view with a unidirectional binding
        right.mediaProperty().bind(left.singleSelectionProperty());
        
        ScrollPane rightScroller = new ScrollPane();
        rightScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setFitToWidth(true);
        rightScroller.setContent(right);
        
        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        
        split.getItems().add(leftScroller);
        split.getItems().add(rightScroller);
        split.setDividerPositions(0.75);
        SplitPane.setResizableWithParent(rightScroller, Boolean.FALSE);
        
        // TODO fix when resized too small
        HBox top = new HBox();
        top.setPadding(new Insets(5));
        top.setSpacing(5);
        top.setAlignment(Pos.BASELINE_LEFT);
        
        Label lblFilter = new Label("filter by:");
        ChoiceBox<MediaType> cbTypes = new ChoiceBox<MediaType>(FXCollections.observableArrayList(MediaType.values()));
        ChoiceBox<Tag> cbTags = new ChoiceBox<Tag>(FXCollections.observableArrayList(allTags));
        
        Label lblSort = new Label("sort by:");
        ChoiceBox<String> cbSort = new ChoiceBox<String>(FXCollections.observableArrayList("Name", "Type", "Date Added"));
        ToggleButton tgl = new ToggleButton("asc");
        
        top.getChildren().addAll(lblFilter, cbTypes, cbTags,
        						 lblSort, cbSort, tgl);
        
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(split);
        
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }
}