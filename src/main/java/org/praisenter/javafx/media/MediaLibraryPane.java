package org.praisenter.javafx.media;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.praisenter.FailedOperation;
import org.praisenter.Tag;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.FilterOption;
import org.praisenter.javafx.FlowListView;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.media.MediaType;
import org.praisenter.resources.translations.Translations;
import org.praisenter.utility.ClasspathLoader;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    // TODO add ability to rename
    // FEATURE allow preview of audio/video
    // TODO translations
    @Override
    public void start(Stage primaryStage) {
    	primaryStage.setTitle("Media Library");
    	
    	// TODO should be moved eventually
    	FileSystem system = FileSystems.getDefault();
		Path path = system.getPath("D:\\Personal\\Praisenter\\testmedialibrary");
		MediaThumbnailSettings settings = new MediaThumbnailSettings(
				100, 100,
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/image-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/music-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/video-default-thumbnail.png"));
    	MediaLibrary library = null;
		try {
			library = MediaLibrary.open(path, new JavaFXMediaImportFilter(path), settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		final MediaLibrary lbr = library;
        Set<Tag> tags = new TreeSet<Tag>(library.getTags());
        
        List<MediaListItem> master = new ArrayList<MediaListItem>();
        for (Media media : library.all()) {
        	master.add(new MediaListItem(media));
        }
        
        ObservableList<MediaListItem> display = FXCollections.observableArrayList();
        display.addAll(master);
        // by default sort by name asc
        Collections.sort(display);
        
        // the right side of the split pane
        FlowListView<MediaListItem> left = new FlowListView<MediaListItem>(new MediaListViewCellFactory());
        left.itemsProperty().bindContent(display);
        
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
											display.removeAll(succeeded);
											master.removeAll(succeeded);
											
											if (failed.size() > 0) {
												// get the exceptions
												Exception[] exceptions = failed.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
												// get the failed media
												String list = String.join(", ", failed.stream().map(f -> f.getData().getMetadata().getName()).collect(Collectors.toList()));
												Alert alert = Alerts.exception(MessageFormat.format(Translations.getTranslation("media.remove.error"), list), exceptions);
												alert.show();
											}
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
						display.addAll(loadings);
						master.addAll(loadings);
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
										display.removeAll(loadings);
										master.removeAll(loadings);
										display.addAll(succeeded);
										master.addAll(succeeded);
										
										if (failed.size() > 0) {
											// get the exceptions
											Exception[] exceptions = failed.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
											// get the failed media
											String list = String.join(", ", failed.stream().map(f -> f.getData().getName()).collect(Collectors.toList()));
											Alert alert = Alerts.exception(MessageFormat.format(Translations.getTranslation("media.import.error"), list), exceptions);
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

        ObservableSet<Tag> allTags = FXCollections.observableSet(tags);
        MediaMetadataView right = new MediaMetadataView(library, allTags);
        
        // wire up the selected media to the media metadata view with a unidirectional binding
        right.mediaProperty().bind(left.singleSelectionProperty());
        
        ScrollPane rightScroller = new ScrollPane();
        rightScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setFitToWidth(true);
        rightScroller.setContent(right);
        rightScroller.setMinWidth(150);
        
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
        
        // FILTERING
        
        final MediaFilter filter = new MediaFilter();
        
        final Supplier<List<MediaListItem>> filterFunction = new Supplier<List<MediaListItem>>() {
        	@Override
        	public List<MediaListItem> get() {
        		final MediaType mType = filter.typeProperty().get().getData();
				final Tag mTag = filter.tagProperty().get().getData();
				final String mSearch = filter.searchProperty().get();
				System.out.println("filtering " + mSearch);
        		// filter the master list
        		List<MediaListItem> filtered = master.stream().filter(m -> {
        			// if the media is being imported
        			if (!m.loaded || 
        				((mType == null || m.media.getMetadata().getType() == mType) &&
        				 (mTag == null || m.media.getMetadata().getTags().contains(mTag)) &&
        				 (mSearch == null || mSearch.length() == 0 || m.media.getMetadata().getName().contains(mSearch)))) {
        				return true;
        			}
        			return false;
        		}).collect(Collectors.toList());
        		return filtered;
        	}
		};
        
        ObservableList<FilterOption<MediaType>> opTypes = FXCollections.observableArrayList();
        // add the all option
        opTypes.add(new FilterOption<>());
        // add the current options
        opTypes.addAll(Arrays.asList(MediaType.values()).stream().map(t -> new FilterOption<MediaType>(t.getName(), t)).collect(Collectors.toList()));
        
        Label lblFilter = new Label("filter by:");
        ComboBox<FilterOption<MediaType>> cbTypes = new ComboBox<FilterOption<MediaType>>(opTypes);
        cbTypes.valueProperty().bindBidirectional(filter.typeProperty());
        cbTypes.setValue(new FilterOption<>());
        cbTypes.valueProperty().addListener(new ChangeListener<FilterOption<MediaType>>() {
        	@Override
        	public void changed(ObservableValue<? extends FilterOption<MediaType>> ob, FilterOption<MediaType> oldValue, FilterOption<MediaType> newValue) {
        		display.setAll(filterFunction.get());
        	}
		});
        
        ObservableList<FilterOption<Tag>> opTags = FXCollections.observableArrayList();
        // add the all option
        opTags.add(new FilterOption<>());
        // add the current options
        opTags.addAll(allTags.stream().map(t -> new FilterOption<Tag>(t.getName(), t)).collect(Collectors.toList()));
        // add a listener for more tags being added
        allTags.addListener(new SetChangeListener<Tag>() {
        	@Override
        	public void onChanged(SetChangeListener.Change<? extends Tag> change) {
        		if (change.wasRemoved()) {
        			opTags.removeIf(fo -> fo.getData().equals(change.getElementRemoved()));
        		}
        		if (change.wasAdded()) {
        			Tag tag = change.getElementAdded();
        			opTags.add(new FilterOption<Tag>(tag.getName(), tag));
        		}
        	}
		});
        
        ComboBox<FilterOption<Tag>> cbTags = new ComboBox<FilterOption<Tag>>(opTags);
        cbTags.valueProperty().bindBidirectional(filter.tagProperty());
        cbTags.setValue(new FilterOption<>());
        cbTags.valueProperty().addListener(new ChangeListener<FilterOption<Tag>>() {
        	@Override
        	public void changed(ObservableValue<? extends FilterOption<Tag>> ob, FilterOption<Tag> oldValue, FilterOption<Tag> newValue) {
        		display.setAll(filterFunction.get());
        	}
		});
        
        Label lblSort = new Label("sort by:");
        ChoiceBox<String> cbSort = new ChoiceBox<String>(FXCollections.observableArrayList("Name", "Type", "Date Added"));
        ToggleButton tgl = new ToggleButton("asc");
        
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("search");
        //txtSearch.textProperty().bindBidirectional(filter.searchProperty());
        txtSearch.textProperty().addListener(new ChangeListener<String>() {
        	@Override
        	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        		filter.tagProperty().set(new FilterOption<>());
        		filter.typeProperty().set(new FilterOption<>());
        		filter.searchProperty().set(newValue);
        		display.setAll(filterFunction.get());
        	}
        });
        
        top.getChildren().addAll(lblFilter, cbTypes, cbTags,
        						 lblSort, cbSort, tgl, txtSearch);
        
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(split);
        
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }
}