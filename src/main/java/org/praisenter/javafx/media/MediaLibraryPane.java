/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.media;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.FailedOperation;
import org.praisenter.Tag;
import org.praisenter.WarningOperation;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.SortGraphic;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaType;
import org.praisenter.resources.translations.Translations;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;

// TODO if only one type of media is allowed, sorting and filtering by type isn't useful, hide it?
// TODO we may need a generic way of communicating between multiple instances

/**
 * Pane specifically for showing the media in a media library.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaLibraryPane extends BorderPane {
	/** The class-level loader */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The collator for locale dependent sorting */
	private static final Collator COLLATOR = Collator.getInstance();
	
	// selection
	
	/** The selected media */
	private final ObjectProperty<Media> selected;
	
	// filters
	
	/** The media type filter */
	private final ObjectProperty<Option<MediaType>> typeFilter;
	
	/** The tag filter */
	private final ObjectProperty<Option<Tag>> tagFilter;
	
	/** The search */
	private final StringProperty textFilter;
	
	// sorting
	
	/** The sort property */
	private final ObjectProperty<Option<MediaSortField>> sortField;
	
	/** The sort direction */
	private final BooleanProperty sortDescending;
	
    /**
     * Full constructor.
     * @param context the {@link PraisenterContext}
     * @param orientation the orientation of the flow of items
     * @param types the desired {@link MediaType}s to show; null or empty to show all
     */
    public MediaLibraryPane(
    		PraisenterContext context, 
    		Orientation orientation, 
    		MediaType... types) {
		this.typeFilter = new SimpleObjectProperty<>(new Option<MediaType>());
		this.tagFilter = new SimpleObjectProperty<>(new Option<Tag>());
		this.textFilter = new SimpleStringProperty();
		this.sortField = new SimpleObjectProperty<Option<MediaSortField>>(new Option<MediaSortField>(MediaSortField.NAME.getName(), MediaSortField.NAME));
		this.sortDescending = new SimpleBooleanProperty(true);
    	
		this.selected = new SimpleObjectProperty<Media>();
		
		final MediaLibrary library = context.getMediaLibrary();
		final ObservableSet<Tag> tags = context.getTags();
		
        List<MediaListItem> master = new ArrayList<MediaListItem>();
        for (Media media : library.all(types)) {
        	master.add(new MediaListItem(media));
        }
        // by default sort by name asc
        Collections.sort(master);
        
        ObservableList<MediaListItem> thelist = FXCollections.observableArrayList();
        thelist.addAll(master);
        
        // add sorting and filtering capabilities
        FilteredList<MediaListItem> filtered = new FilteredList<MediaListItem>(thelist, p -> true);
        SortedList<MediaListItem> sorted = new SortedList<MediaListItem>(filtered);
        
        // define a general listener for all the filters and sorting
        InvalidationListener fs = new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				MediaType type = typeFilter.get().getValue();
				Tag tag = tagFilter.get().getValue();
				String text = textFilter.get();
				MediaSortField field = sortField.get().getValue();
				boolean desc = sortDescending.get();
				filtered.setPredicate(m -> {
					if (!m.loaded || 
						((type == null || m.media.getMetadata().getType() == type) &&
						 (tag == null || m.media.getMetadata().getTags().contains(tag)) &&
						 (text == null || text.length() == 0 || m.media.getMetadata().getName().toLowerCase().contains(text.toLowerCase())))) {
						return true;
					}
					return false;
				});
				sorted.setComparator(new Comparator<MediaListItem>() {
					@Override
					public int compare(MediaListItem o1, MediaListItem o2) {
						int value = 0;
						if (field == MediaSortField.NAME) {
							value = COLLATOR.compare(o1.name, o2.name);
						} else {
							// check for loaded vs. not loaded media
							// sort non-loaded media to the end
							if (o1.media == null && o2.media == null) return 0;
							if (o1.media == null && o2.media != null) return 1;
							if (o1.media != null && o2.media == null) return -1;
							
							if (field == MediaSortField.TYPE) {
								value = o1.media.getMetadata().getType().compareTo(o2.media.getMetadata().getType());
							} else {
								value = -1 * (o1.media.getMetadata().getDateAdded().compareTo(o2.media.getMetadata().getDateAdded()));
							}
						}
						return (desc ? 1 : -1) * value;
					}
				});
			}
		};
		textFilter.addListener(fs);
		typeFilter.addListener(fs);
		tagFilter.addListener(fs);
		sortField.addListener(fs);
		sortDescending.addListener(fs);
        
        final MediaType[] mediaTypes = types != null && types.length > 0 ? types : MediaType.values();
        ObservableList<Option<MediaType>> opTypes = FXCollections.observableArrayList();
        // add the all option
        opTypes.add(new Option<>());
        // add the current options
        opTypes.addAll(Arrays.asList(mediaTypes).stream().map(t -> new Option<MediaType>(Translations.get(t.getClass().getName() + "." + t.name()), t)).collect(Collectors.toList()));

        // sorting options
        ObservableList<Option<MediaSortField>> sortFields = FXCollections.observableArrayList();
        sortFields.addAll(Arrays.asList(MediaSortField.values()).stream().map(t -> new Option<MediaSortField>(t.getName(), t)).collect(Collectors.toList()));
        
        ObservableList<Option<Tag>> opTags = FXCollections.observableArrayList();
        // add the all option
        opTags.add(new Option<>());
        // add the current options
        opTags.addAll(tags.stream().map(t -> new Option<Tag>(t.getName(), t)).collect(Collectors.toList()));
        // add a listener for more tags being added
        tags.addListener(new SetChangeListener<Tag>() {
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
        FlowListView<MediaListItem> left = new FlowListView<MediaListItem>(new MediaListViewCellFactory(library.getThumbnailSettings().getHeight()));
        left.itemsProperty().bindContent(sorted);
        
        EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.DELETE) {
					List<MediaListItem> items = new ArrayList<MediaListItem>();
					for (MediaListItem item : left.selectionsProperty().get()) {
						// only include those that are imported
						if (item.loaded) {
							items.add(item);
						}
					}
					if (items.size() > 0) {
						// attempt to delete the selected media
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.initOwner(getScene().getWindow());
						alert.initModality(Modality.WINDOW_MODAL);
						alert.setTitle(Translations.get("media.remove.title"));
						alert.setContentText(Translations.get("media.remove.content"));
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
											thelist.removeAll(succeeded);
											master.removeAll(succeeded);
											
											if (failed.size() > 0) {
												// get the exceptions
												Exception[] exceptions = failed.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
												// get the failed media
												String list = String.join(", ", failed.stream().map(f -> f.getData().getMetadata().getName()).collect(Collectors.toList()));
												Alert alert = Alerts.exception(
														getScene().getWindow(),
														null, 
														null, 
														MessageFormat.format(Translations.get("media.remove.error"), list), 
														exceptions);
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
        
        // only scroll down when media is being added
        // we know this when there's medialistitems added
        // that have the loaded flag = false
        thelist.addListener(new ListChangeListener<MediaListItem>() {
        	@Override
        	public void onChanged(javafx.collections.ListChangeListener.Change<? extends MediaListItem> c) {
        		boolean added = false;
        		while (c.next()) {
        			if (c.wasAdded()) {
        				for (int i = 0; i < c.getAddedSize(); i++) {
        					MediaListItem item = c.getAddedSubList().get(i);
        					added |= item != null && item.loaded == false;
        				}
        			}
        		}
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

        left.selectionProperty().addListener((obs, ov, nv) -> {
        	if (nv == null) {
        		this.selected.set(null);
        	} else {
        		this.selected.set(nv.media);
        	}
        });
        this.selected.addListener((obs, ov, nv) -> {
        	if (nv == null) {
        		left.selectionProperty().set(null);
        	} else {
        		left.selectionProperty().set(new MediaListItem(nv));
        	}
        });
        
        MediaMetadataPane right = new MediaMetadataPane(library, tags);
        
        // wire up the selected media to the media metadata view with a unidirectional binding
        right.mediaProperty().bind(left.selectionProperty());
        
        // TODO translate
        TitledPane ttlMetadata = new TitledPane("Properties", right);

        // setup preview pane for video/audio
        MediaPlayerPane mediaPlayerPane = new MediaPlayerPane();
        left.selectionProperty().addListener((obs, oldValue, newValue) -> {
        	MediaPlayer player = null;
        	MediaType type = null;
        	if (newValue != null && newValue.media != null) {
        		type = newValue.media.getMetadata().getType();
        		if (type == MediaType.AUDIO || type == MediaType.VIDEO) {
        			javafx.scene.media.Media m = new javafx.scene.media.Media(newValue.media.getMetadata().getPath().toUri().toString());
        			Exception ex = m.getError();
        			if (ex != null) {
        				LOGGER.error("Error loading media " + newValue.media.getMetadata().getName(), ex);
        			} else {
        				player = new MediaPlayer(m);
        				ex = player.getError();
        				if (ex != null) {
        					player = null;
        					LOGGER.error("Error creating media player for " + newValue.media.getMetadata().getName(), ex);
        				}
        			}
        		}
        	}
        	mediaPlayerPane.setMediaPlayer(player, type);
        });
        TitledPane ttlPreview = new TitledPane("Preview", mediaPlayerPane);
        
        right.addEventHandler(MediaMetadataEvent.RENAME, (e) -> {
        	// make sure the file isn't being previewed
        	mediaPlayerPane.setMediaPlayer(null, null);
        	// update the media's name
    		try {
    			// attempt to rename
    			String name = e.getName();
    			Media m0 = e.getMedia();
				Media m1 = library.rename(m0, name);
				// update the list item
				int index = -1;
	        	for (int i = 0; i < thelist.size(); i++) {
	        		MediaListItem item = thelist.get(i);
	        		if (item.media.getMetadata().getId() == m0.getMetadata().getId()) {
	        			index = i;
	        			break;
	        		}
	        	}
	        	if (index >= 0) {
	        		thelist.set(index, new MediaListItem(m1));
	        	} else {
	        		thelist.add(new MediaListItem(m1));
	        	}
	        	// resort/filter
	        	fs.invalidated(null);
			} catch (Exception ex) {
				// log the error
				LOGGER.error("Failed to rename media from '{}' to '{}': {}", e.getMedia().getMetadata().getName(), e.getName(), ex.getMessage());
				// show an error to the user
				Alert alert = Alerts.exception(
						getScene().getWindow(),
						null, 
						null, 
						MessageFormat.format(Translations.get("media.metadata.rename.error"), e.getMedia().getMetadata().getName(), e.getName()), 
						ex);
				alert.show();
			}
        });
        
        VBox rightGroup = new VBox(ttlMetadata, ttlPreview);
        ScrollPane rightScroller = new ScrollPane();
        rightScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setFitToWidth(true);
        rightScroller.setContent(rightGroup);
        rightScroller.setMinWidth(170);
        
        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        
        split.getItems().add(leftScroller);
        split.getItems().add(rightScroller);
        split.setDividerPositions(0.75);
        SplitPane.setResizableWithParent(rightScroller, Boolean.FALSE);
        
        // scale the media player pane's fit property to the scroller's width property minus some for padding
        mediaPlayerPane.mediaFitWidthProperty().bind(rightScroller.widthProperty().subtract(20));
        mediaPlayerPane.setPadding(new Insets(8, 0, 0, 0));
        
        // FILTERING & SORTING
        
        Label lblFilter = new Label(Translations.get("field.filter"));
        ComboBox<Option<MediaType>> cbTypes = new ComboBox<Option<MediaType>>(opTypes);
        cbTypes.setValue(new Option<>());
        cbTypes.valueProperty().bindBidirectional(this.typeFilter);
        
        ComboBox<Option<Tag>> cbTags = new ComboBox<Option<Tag>>(opTags);
        cbTags.valueProperty().bindBidirectional(this.tagFilter);
        cbTags.setValue(new Option<>());
        
        Label lblSort = new Label(Translations.get("field.sort"));
        ChoiceBox<Option<MediaSortField>> cbSort = new ChoiceBox<Option<MediaSortField>>(sortFields);
        cbSort.valueProperty().bindBidirectional(this.sortField);
        SortGraphic sortGraphic = new SortGraphic(17, 0, 4, 2, 4);
        ToggleButton tgl = new ToggleButton(null, sortGraphic);
        tgl.selectedProperty().bindBidirectional(this.sortDescending);
        sortGraphic.flipProperty().bind(this.sortDescending);
        
        TextField txtSearch = new TextField();
        txtSearch.setPromptText(Translations.get("field.search.placeholder"));
        txtSearch.textProperty().bindBidirectional(this.textFilter);
        
        HBox pFilter = new HBox(); 
        pFilter.setAlignment(Pos.BASELINE_LEFT);
        pFilter.setSpacing(5);
        pFilter.getChildren().addAll(lblFilter, cbTypes, cbTags, txtSearch);
        
        HBox pSort = new HBox();
        pSort.setAlignment(Pos.CENTER_LEFT);
        pSort.setSpacing(5);
        pSort.getChildren().addAll(lblSort, cbSort, tgl);
        
        FlowPane top = new FlowPane();
        top.setHgap(5);
        top.setVgap(5);
        top.setAlignment(Pos.BASELINE_LEFT);
        top.setPadding(new Insets(5));
        top.setPrefWrapLength(0);
        
        top.getChildren().addAll(pFilter, pSort);
        
        this.setTop(top);
        this.setCenter(split);
    }
    
    /**
     * Returns the selected property.
     * @return ObjectProperty&lt;{@link Media}&gt;
     */
    public ObjectProperty<Media> selectedProperty() {
    	return this.selected;
    }
    
    /**
     * Returns the selected media.
     * @return {@link Media} or null
     */
    public Media getSelected() {
    	return this.selected.get();
    }
    
    /**
     * Sets the selected media.
     * @param media the media
     */
    public void setSelected(Media media) {
    	this.selected.set(media);
    }
}