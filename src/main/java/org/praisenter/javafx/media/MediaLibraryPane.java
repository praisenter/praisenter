package org.praisenter.javafx.media;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CosNaming.Binding;
import org.praisenter.FailedOperation;
import org.praisenter.Tag;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.SortGraphic;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaType;
import org.praisenter.resources.translations.Translations;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class MediaLibraryPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger(MediaLibraryPane.class);
	
    private final MediaFilter filter;
    
    // TODO need a generic way of communicating between multiple instances
    // TODO add ability to rename
    // FEATURE allow preview of audio/video; this may not be that hard to be honest; does the MediaView class come with controls?
    // TODO translate
    public MediaLibraryPane(final MediaLibrary library, Orientation orientation) {
        Set<Tag> tags = new TreeSet<Tag>(library.getTags());
        
        List<MediaListItem> master = new ArrayList<MediaListItem>();
        for (Media media : library.all()) {
        	master.add(new MediaListItem(media));
        }
        // by default sort by name asc
        Collections.sort(master);
        
        ObservableList<MediaListItem> display = FXCollections.observableArrayList();
        display.addAll(master);
        
        ObservableSet<Tag> allTags = FXCollections.observableSet(tags);

        ObservableList<Option<MediaType>> opTypes = FXCollections.observableArrayList();
        // add the all option
        opTypes.add(new Option<>());
        // add the current options
        opTypes.addAll(Arrays.asList(MediaType.values()).stream().map(t -> new Option<MediaType>(t.getName(), t)).collect(Collectors.toList()));
        
        ObservableList<Option<Tag>> opTags = FXCollections.observableArrayList();
        // add the all option
        opTags.add(new Option<>());
        // add the current options
        opTags.addAll(allTags.stream().map(t -> new Option<Tag>(t.getName(), t)).collect(Collectors.toList()));
        // add a listener for more tags being added
        allTags.addListener(new SetChangeListener<Tag>() {
        	@Override
        	public void onChanged(SetChangeListener.Change<? extends Tag> change) {
        		if (change.wasRemoved()) {
        			opTags.removeIf(fo -> fo.getValue().equals(change.getElementRemoved()));
        		}
        		if (change.wasAdded()) {
        			Tag tag = change.getElementAdded();
        			opTags.add(new Option<Tag>(tag.getName(), tag));
        		}
        	}
		});
        
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
												library.remove(media);
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
												Alert alert = Alerts.exception(null, null, MessageFormat.format(Translations.getTranslation("media.remove.error"), list), exceptions);
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
        left.setOrientation(orientation);
        if (orientation == Orientation.HORIZONTAL) {
        	leftScroller.setFitToWidth(true);
        } else {
        	leftScroller.setFitToHeight(true);
        }
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
        display.addListener(new ListChangeListener<MediaListItem>() {
        	@Override
        	public void onChanged(javafx.collections.ListChangeListener.Change<? extends MediaListItem> c) {
        		boolean added = false;
//        		while (c.next()) {
//        			added |= c.wasAdded();
//        		}
        		
        		if (added) {
        			leftScroller.setVvalue(leftScroller.getVmax());
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
										final Media media = library.add(file.toPath());
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
											Alert alert = Alerts.exception(null, null, MessageFormat.format(Translations.getTranslation("media.import.error"), list), exceptions);
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
        
        // FILTERING & SORTING
        
        this.filter = new MediaFilter(master, display);
        
        Label lblFilter = new Label("filter by:");
        ComboBox<Option<MediaType>> cbTypes = new ComboBox<Option<MediaType>>(opTypes);
        cbTypes.setValue(new Option<>());
        cbTypes.valueProperty().bindBidirectional(this.filter.typeFilterOptionProperty());
        
        ComboBox<Option<Tag>> cbTags = new ComboBox<Option<Tag>>(opTags);
        cbTags.valueProperty().bindBidirectional(this.filter.tagFilterOptionProperty());
        cbTags.setValue(new Option<>());
        
        // TODO translate
 		Option<Integer> nameOption = new Option<Integer>("Name", 0);
 		Option<Integer> typeOption = new Option<Integer>("Type", 1);
 		Option<Integer> dateOption = new Option<Integer>("Last Modified", 2);
 		
        Label lblSort = new Label("sort by:");
        ChoiceBox<Option<Integer>> cbSort = new ChoiceBox<Option<Integer>>(FXCollections.observableArrayList(nameOption, typeOption, dateOption));
        cbSort.valueProperty().bindBidirectional(this.filter.sortProperty());
        SortGraphic sortGraphic = new SortGraphic(17, 0, 4, 2, 4, Color.GRAY);
        ToggleButton tgl = new ToggleButton(null, sortGraphic);
        tgl.selectedProperty().bindBidirectional(this.filter.sortDescendingProperty());
        sortGraphic.flipProperty().bind(this.filter.sortDescendingProperty());
        
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("search");
        txtSearch.textProperty().bindBidirectional(this.filter.searchProperty());
        
        HBox pFilter = new HBox(); 
        pFilter.setAlignment(Pos.BASELINE_LEFT);
        pFilter.setSpacing(5);
        pFilter.getChildren().addAll(lblFilter, cbTypes, cbTags, txtSearch);
//        pFilter.setBorder(Testing.border(Color.GREEN));
        
        HBox pSort = new HBox();
        pSort.setAlignment(Pos.CENTER_LEFT);
        pSort.setSpacing(5);
        pSort.getChildren().addAll(lblSort, cbSort, tgl);
//        pSort.setBorder(Testing.border(Color.RED));
        
        FlowPane top = new FlowPane();
        top.setHgap(5);
        top.setVgap(5);
        top.setAlignment(Pos.BASELINE_LEFT);
        top.setPadding(new Insets(5));
        top.setPrefWrapLength(0);
//        top.setBorder(Testing.border(Color.YELLOW));
        
        top.getChildren().addAll(pFilter, pSort);
        
        this.setTop(top);
        this.setCenter(split);
    }
}