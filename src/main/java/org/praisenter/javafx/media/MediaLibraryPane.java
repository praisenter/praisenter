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
import java.nio.file.Path;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.MediaType;
import org.praisenter.Tag;
import org.praisenter.ThumbnailSettings;
import org.praisenter.javafx.ApplicationAction;
import org.praisenter.javafx.ApplicationContextMenu;
import org.praisenter.javafx.ApplicationEvent;
import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.ApplicationPane;
import org.praisenter.javafx.ApplicationPaneEvent;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.controls.FlowListCell;
import org.praisenter.javafx.controls.FlowListView;
import org.praisenter.javafx.controls.SortGraphic;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.media.Media;
import org.praisenter.resources.translations.Translations;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;

/**
 * Pane specifically for showing the media in a media library.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaLibraryPane extends BorderPane implements ApplicationPane {
	/** The class-level loader */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The collator for locale dependent sorting */
	private static final Collator COLLATOR = Collator.getInstance();

	// data
	
	/** The praisenter context */
	private final PraisenterContext context;
	
	// selection
	
	/** The selected media */
	private final ObjectProperty<Media> selected = new SimpleObjectProperty<Media>();
	
	/** True if the selection is being changed */
	private boolean selecting = false;
	
	// filters
	
	/** The media type filter */
	private final ObjectProperty<Option<MediaType>> typeFilter = new SimpleObjectProperty<>(new Option<MediaType>());
	
	/** The search */
	private final StringProperty textFilter = new SimpleStringProperty();
	
	// sorting
	
	/** The sort property */
	private final ObjectProperty<Option<MediaSortField>> sortField = new SimpleObjectProperty<Option<MediaSortField>>(new Option<MediaSortField>(MediaSortField.NAME.getName(), MediaSortField.NAME));
	
	/** The sort direction */
	private final BooleanProperty sortDescending = new SimpleBooleanProperty(true);
	
	// nodes
	
	/** The media list view */
	private final FlowListView<MediaListItem> lstMedia;
	
	/** The media metadata pane */
	private final MediaPropertiesPane pneMetadata;
	
	/** The media preview player */
	private final MediaPlayerPane pnePlayer;
	
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
    	this.context = context;
    	
    	this.getStyleClass().add("media-library-pane");
    	
		final ObservableMediaLibrary library = context.getMediaLibrary();
		final ObservableSet<Tag> tags = context.getTags();
		
        // add sorting and filtering capabilities
		ObservableList<MediaListItem> theList = library.getItems();
        FilteredList<MediaListItem> filtered = theList.filtered(p -> true);
        SortedList<MediaListItem> sorted = filtered.sorted();
        
        // define a general listener for all the filters and sorting
        InvalidationListener filterListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable obs) {
				MediaType type = typeFilter.get().getValue();
				String text = textFilter.get();
				MediaSortField field = sortField.get().getValue();
				boolean desc = sortDescending.get();
				filtered.setPredicate(m -> {
					if (!m.isLoaded() || 
						((type == null || m.getMedia().getType() == type) &&
						 (text == null || text.length() == 0 || m.getMedia().getName().toLowerCase().contains(text.toLowerCase()) || m.getTags().stream().anyMatch(t -> t.getName().toLowerCase().contains(text))))) {
						// make sure its in the available types
						if (types != null && types.length > 0 && m.isLoaded()) {
							for (MediaType t : types) {
								if (t == m.getMedia().getType()) {
									return true;
								}
							}
							return false;
						}
						return true;
					}
					return false;
				});
				sorted.setComparator(new Comparator<MediaListItem>() {
					@Override
					public int compare(MediaListItem o1, MediaListItem o2) {
						int value = 0;
						if (field == MediaSortField.NAME) {
							value = COLLATOR.compare(o1.getName(), o2.getName());
						} else {
							// check for loaded vs. not loaded media
							// sort non-loaded media to the end
							if (o1.getMedia() == null && o2.getMedia() == null) return 0;
							if (o1.getMedia() == null && o2.getMedia() != null) return 1;
							if (o1.getMedia() != null && o2.getMedia() == null) return -1;
							
							if (field == MediaSortField.TYPE) {
								value = o1.getMedia().getType().compareTo(o2.getMedia().getType());
							} else {
								value = -1 * (o1.getMedia().getDateAdded().compareTo(o2.getMedia().getDateAdded()));
							}
						}
						return (desc ? 1 : -1) * value;
					}
				});
			}
		};
		this.textFilter.addListener(filterListener);
		this.typeFilter.addListener(filterListener);
		this.sortField.addListener(filterListener);
		this.sortDescending.addListener(filterListener);
		filterListener.invalidated(null);
		this.addEventHandler(ApplicationEvent.ALL, this::onApplicationEvent);
        
        final MediaType[] mediaTypes = types != null && types.length > 0 ? types : MediaType.values();
        ObservableList<Option<MediaType>> opTypes = FXCollections.observableArrayList();
        // add the all option
        opTypes.add(new Option<>());
        // add the current options
        opTypes.addAll(Arrays.asList(mediaTypes).stream().map(t -> new Option<MediaType>(Translations.get(t.getClass().getName() + "." + t.name()), t)).collect(Collectors.toList()));

        // sorting options
        ObservableList<Option<MediaSortField>> sortFields = FXCollections.observableArrayList();
        sortFields.addAll(Arrays.asList(MediaSortField.values())
        		.stream()
        		// don't include the type sort if theres only one
        		.filter(t -> t != MediaSortField.TYPE || (t == MediaSortField.TYPE && (types == null || types.length != 1)))
        		.map(t -> new Option<MediaSortField>(t.getName(), t))
        		.collect(Collectors.toList()));
        
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
        ThumbnailSettings thumbnailSettings = context.getMediaLibrary().getThumbnailSettings();
        DefaultMediaThumbnails defaultThumbnails = new DefaultMediaThumbnails(thumbnailSettings);
        this.lstMedia = new FlowListView<MediaListItem>(orientation, new Callback<MediaListItem, FlowListCell<MediaListItem>>() {
        	@Override
        	public FlowListCell<MediaListItem> call(MediaListItem item) {
				return new MediaListCell(item, thumbnailSettings, defaultThumbnails);
			}
        });
        this.lstMedia.itemsProperty().bindContent(sorted);
        this.lstMedia.setOnDragDropped(this::onMediaDropped);
        this.lstMedia.setOnDragOver(this::onMediaDragOver);
        
        this.lstMedia.getSelectionModel().selectionsProperty().addListener((obs, ov, nv) -> {
        	if (selecting) return;
        	this.selecting = true;
        	if (nv == null || nv.size() != 1) {
        		this.selected.set(null);
        	} else {
        		this.selected.set(nv.get(0).getMedia());
        	}
        	this.selecting = false;
        	this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
        });
        this.selected.addListener((obs, ov, nv) -> {
        	if (selecting) return;
        	this.selecting = true;
        	lstMedia.getSelectionModel().clear();
        	if (nv != null) {
        		lstMedia.getSelectionModel().select(library.getListItem(nv.getId()));
        	}
        	this.selecting = false;
        	this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
        });
        
		// setup the context menu
		ApplicationContextMenu menu = new ApplicationContextMenu(this);
		menu.getItems().addAll(
				menu.createMenuItem(ApplicationAction.RENAME),
				menu.createMenuItem(ApplicationAction.DELETE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.IMPORT_MEDIA, Translations.get("action.import"), ApplicationGlyphs.MENU_IMPORT.duplicate()),
				menu.createMenuItem(ApplicationAction.EXPORT),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.SELECT_ALL),
				menu.createMenuItem(ApplicationAction.SELECT_NONE),
				menu.createMenuItem(ApplicationAction.SELECT_INVERT));
		this.lstMedia.setContextMenu(menu);
        
        this.pneMetadata = new MediaPropertiesPane(tags);
        // wire up the selected media to the media metadata view with a unidirectional binding
        this.pneMetadata.mediaProperty().bind(this.lstMedia.getSelectionModel().selectionProperty());
        this.pneMetadata.addEventHandler(MediaMetadataEvent.RENAME, e -> {
        	this.promptRename(e.getMedia());
        });
        this.pneMetadata.addEventHandler(MediaMetadataEvent.ADD_TAG, this::onMediaTagAdded);
        this.pneMetadata.addEventHandler(MediaMetadataEvent.REMOVE_TAG, this::onMediaTagRemoved);

        // setup preview pane for video/audio
        this.pnePlayer = new MediaPlayerPane();
        this.lstMedia.getSelectionModel().selectionsProperty().addListener((obs, oldValue, newValue) -> {
        	MediaPlayer player = null;
        	MediaType type = null;
        	if (newValue != null && newValue.size() == 1) {
        		MediaListItem item = newValue.get(0);
	        	if (item != null && item.getMedia() != null) {
	        		type = item.getMedia().getType();
	        		if (type == MediaType.AUDIO || type == MediaType.VIDEO) {
	        			try {
		        			javafx.scene.media.Media m = new javafx.scene.media.Media(item.getMedia().getPath().toUri().toString());
		        			Exception ex = m.getError();
		        			if (ex != null) {
		        				LOGGER.error("Error loading media " + item.getMedia().getName(), ex);
		        			} else {
		        				player = new MediaPlayer(m);
		        				ex = player.getError();
		        				if (ex != null) {
		        					player = null;
		        					LOGGER.error("Error creating media player for " + item.getMedia().getName(), ex);
		        				}
		        			}
	        			} catch (Exception ex) {
	        				LOGGER.error("Error loading media " + item.getMedia().getName(), ex);
	        			}
	        		}
	        	}
        	}
        	pnePlayer.setMediaPlayer(player, type);
        });
        
        Label lblImport = new Label(Translations.get("media.import.step1"));
        lblImport.setPadding(new Insets(7));
        lblImport.setWrapText(true);
        
        TitledPane ttlImport = new TitledPane(Translations.get("media.import.title"), lblImport);
        TitledPane ttlMetadata = new TitledPane(Translations.get("media.properties.title"), this.pneMetadata);
        TitledPane ttlPreview = new TitledPane(Translations.get("media.preview.title"), this.pnePlayer);
        
        VBox rightGroup = new VBox(ttlImport, ttlMetadata);
        if (!(mediaTypes.length == 1 && mediaTypes[0] == MediaType.IMAGE)) {
        	rightGroup.getChildren().add(ttlPreview);
        }
        ScrollPane rightScroller = new ScrollPane();
        rightScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setFitToWidth(true);
        rightScroller.setContent(rightGroup);
        rightScroller.setMinWidth(250);
        
        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        
        split.getItems().add(this.lstMedia);
        split.getItems().add(rightScroller);
        split.setDividerPositions(0.9);
        SplitPane.setResizableWithParent(rightScroller, Boolean.FALSE);
        
        // scale the media player pane's fit property to the scroller's width property minus some for padding
        this.pnePlayer.mediaFitWidthProperty().bind(rightScroller.widthProperty().subtract(20));
        this.pnePlayer.setPadding(new Insets(8, 0, 0, 0));
        
        // FILTERING & SORTING
        
        Label lblFilter = new Label(Translations.get("field.filter"));
        ComboBox<Option<MediaType>> cbTypes = new ComboBox<Option<MediaType>>(opTypes);
        cbTypes.setValue(new Option<>());
        cbTypes.valueProperty().bindBidirectional(this.typeFilter);
        
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
        
        pFilter.getChildren().add(lblFilter);
        // don't include the type filter if theres only one type
        if (types == null || types.length != 1) {
        	pFilter.getChildren().add(cbTypes);
        }
        pFilter.getChildren().addAll(txtSearch);
        
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
     * Event handler for dragging over the media library.
     * @param event the event
     */
    private final void onMediaDragOver(DragEvent event) {
		Dragboard db = event.getDragboard();
		if (db.hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY);
		} else {
			event.consume();
		}
    }
    
    /**
     * Event handler for dragging files to import media.
     * @param event the event
     */
    private final void onMediaDropped(DragEvent event) {
    	// check the type of information stored in the dragboard
		Dragboard db = event.getDragboard();
		if (db.hasFiles()) {
			// get the files
			final List<File> files = db.getFiles();
			
			// convert to list of paths
			final List<Path> paths = new ArrayList<Path>();
			for (File file : files) {
				paths.add(file.toPath());
			}
			
			// import
			MediaActions.mediaImport(
				this.context.getMediaLibrary(),
				this.getScene().getWindow(), 
				paths)
			.execute(this.context.getExecutorService());
		}
		event.setDropCompleted(true);
		event.consume();
	}
    
    /**
     * Event handler for the delete key for removing media.
     * @param event the event
     */
    private final void promptDelete() {
    	// make sure the file isn't being previewed
    	this.pnePlayer.setMediaPlayer(null, null);
		// collect the items that are selected
		List<Media> items = new ArrayList<Media>();
		for (MediaListItem item : this.lstMedia.getSelectionModel().selectionsProperty().get()) {
			// only include those that are imported
			if (item.isLoaded() && item.getMedia() != null) {
				items.add(item.getMedia());
			}
		}
		
		MediaActions.mediaPromptDelete(
			this.context, 
			this.getScene().getWindow(), 
			items)
		.execute(this.context.getExecutorService());
    }
    
    /**
     * Event handler for renaming media.
     * @param event the event
     */
    private final void promptRename(Media media) {
		// make sure the file isn't being previewed
    	this.pnePlayer.setMediaPlayer(null, null);
    	
    	MediaActions.mediaPromptRename(
			this.context.getMediaLibrary(), 
			this.getScene().getWindow(), 
			media)
    	.execute(this.context.getExecutorService());
    }
    
    /**
     * Event handler for adding a tag to a media item.
     * @param event the event
     */
    private final void onMediaTagAdded(MediaTagEvent event) {
    	MediaListItem item = event.getMediaListItem();
    	Media media = item.getMedia();
    	Tag tag = event.getTag();
    	
    	AsyncTask<?> task = MediaActions.mediaAddTag(
			this.context.getMediaLibrary(), 
			this.getScene().getWindow(), 
			media, 
			tag);
		task.addSuccessHandler((e) -> {
			this.context.getTags().add(tag);
		}).addCancelledOrFailedHandler((e) -> {
			// remove it from the tags
			item.getTags().remove(tag);
		}).execute(this.context.getExecutorService());
    }
    
    /**
     * Event handler for removing a tag from a media item.
     * @param event the event
     */
    private final void onMediaTagRemoved(MediaTagEvent event) {
    	MediaListItem item = event.getMediaListItem();
    	Media media = item.getMedia();
    	Tag tag = event.getTag();
    	
    	AsyncTask<?> task = MediaActions.mediaRemoveTag(
    			this.context.getMediaLibrary(), 
    			this.getScene().getWindow(), 
    			media, 
    			tag);
		task.addCancelledOrFailedHandler((e) -> {
			// add it back to the item
			item.getTags().add(tag);
		}).execute(this.context.getExecutorService());
    }
    
    /**
     * Event handler for application events.
     * @param event the event
     */
    private final void onApplicationEvent(ApplicationEvent event) {
    	Node focused = this.getScene().getFocusOwner();
    	boolean isFocused = focused == this || Fx.isNodeInFocusChain(focused, this.lstMedia);
    	
    	ApplicationAction action = event.getAction();
    	Media selected = this.selected.get();
    	
    	switch (action) {
			case RENAME:
				if (selected != null) {
					this.promptRename(selected);
				}
				break;
    		case DELETE:
				if (isFocused) {
					this.promptDelete();
				}
    			break;
    		case SELECT_ALL:
				if (isFocused) {
	    			this.lstMedia.getSelectionModel().selectAll();
	    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
				}
    			break;
    		case SELECT_NONE:
    			this.lstMedia.getSelectionModel().clear();
    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
    			break;
    		case SELECT_INVERT:
    			this.lstMedia.getSelectionModel().invert();
    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
    			break;
    		case EXPORT:
    			List<MediaListItem> items = this.lstMedia.getSelectionModel().selectionsProperty().get();
    			List<Media> media = new ArrayList<Media>();
    			for (MediaListItem item : items) {
    				if (item.isLoaded()) {
    					media.add(item.getMedia());
    				}
    			}
    			
    			MediaActions.mediaPromptExport(
    					this.context.getMediaLibrary(), 
    					this.getScene().getWindow(), 
    					media)
    			.execute(this.context.getExecutorService());
    			
    			break;
    		default:
    			break;
    	}
    }
    
    /**
     * Called when the state of this pane changes.
     */
    private final void stateChanged(String reason) {
    	Scene scene = this.getScene();
    	// don't bother if there's no place to send the event to
    	if (scene != null) {
    		fireEvent(new ApplicationPaneEvent(this.lstMedia, MediaLibraryPane.this, ApplicationPaneEvent.STATE_CHANGED, MediaLibraryPane.this, reason));
    	}
    }
    
    /* (non-Javadoc)
     * @see org.praisenter.javafx.ApplicationPane#setDefaultFocus()
     */
    @Override
    public void setDefaultFocus() {
    	this.lstMedia.requestFocus();
    }
    
    /* (non-Javadoc)
     * @see org.praisenter.javafx.ApplicationPane#isApplicationActionEnabled(org.praisenter.javafx.ApplicationAction)
     */
    @Override
    public boolean isApplicationActionEnabled(ApplicationAction action) {
    	Node focused = this.getScene().getFocusOwner();
		
		List<MediaListItem> selected = this.lstMedia.getSelectionModel().selectionsProperty().get();
		
		boolean isSingleSelected = selected.size() == 1;
    	boolean isMultiSelected = selected.size() > 0;
    	boolean isFocused = focused == this || Fx.isNodeInFocusChain(focused, this.lstMedia);
    	boolean isLoaded = selected.stream().allMatch(b -> b.isLoaded());
    	
    	switch (action) {
	    	case RENAME:
				return isFocused && isLoaded && isSingleSelected;
			case DELETE:
			case EXPORT:
				// check for focused text input first
				return isFocused && (isSingleSelected || isMultiSelected) && isLoaded;
			case SELECT_ALL:
			case SELECT_NONE:
			case SELECT_INVERT:
				if (isFocused) {
					return true;
				}
				break;
			case IMPORT_MEDIA:
				return true;
			default:
				break;
		}
    	return false;
    }

    /* (non-Javadoc)
     * @see org.praisenter.javafx.ApplicationPane#isApplicationActionVisible(org.praisenter.javafx.ApplicationAction)
     */
    @Override
    public boolean isApplicationActionVisible(ApplicationAction action) {
    	return true;
    }
    
    /* (non-Javadoc)
     * @see org.praisenter.javafx.ApplicationPane#cleanup()
     */
    @Override
    public void cleanup() {
    	// clear any selections
    	this.lstMedia.getSelectionModel().clear();
    	
    	// clear sorting/filtering
    	this.textFilter.set(null);
    	this.typeFilter.set(new Option<MediaType>());
    	this.sortDescending.set(true);
    	this.sortField.set(new Option<MediaSortField>(MediaSortField.NAME.getName(), MediaSortField.NAME));
    	
    	// make sure the media player is stopped and cleaned up
    	this.pnePlayer.stop();
    	this.pnePlayer.setMediaPlayer(null, null);
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