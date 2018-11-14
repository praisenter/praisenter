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
package org.praisenter.javafx.slide;

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
import org.praisenter.data.Tag;
import org.praisenter.data.json.JsonIO;
import org.praisenter.javafx.ApplicationAction;
import org.praisenter.javafx.ApplicationContextMenu;
import org.praisenter.javafx.ApplicationEvent;
import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.ApplicationPane;
import org.praisenter.javafx.ApplicationPaneEvent;
import org.praisenter.javafx.DataFormats;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.controls.FlowListCell;
import org.praisenter.javafx.controls.FlowListView;
import org.praisenter.javafx.controls.SortGraphic;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.SlideShow;
import org.praisenter.ui.events.SelectionEvent;
import org.praisenter.ui.translations.Translations;

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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * A custom pane for listing slides.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideShowLibraryPane extends BorderPane implements ApplicationPane {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The collator for locale dependent sorting */
	private static final Collator COLLATOR = Collator.getInstance();
	
	// selection
	
	/** The selected slide */
	private final ObjectProperty<SlideShow> selected = new SimpleObjectProperty<SlideShow>();
	
	/** True if the selection is being changed */
	private boolean selecting = false;
	
	// data
	
	/** The context */
	private final PraisenterContext context;

	// nodes
	
	/** The slide listing */
	private final FlowListView<SlideShowListItem> lstSlideShows;

	// filtering

	/** The search */
	private final StringProperty textFilter = new SimpleStringProperty();

	// sorting
	
	/** The sort property */
	private final ObjectProperty<Option<SlideSortField>> sortField = new SimpleObjectProperty<Option<SlideSortField>>(new Option<SlideSortField>(SlideSortField.NAME.getName(), SlideSortField.NAME));
	
	/** The sort direction */
	private final BooleanProperty sortDescending = new SimpleBooleanProperty(true);
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 * @param orientation the orientation
	 */
	public SlideShowLibraryPane(PraisenterContext context, Orientation orientation) {
		this.getStyleClass().add("slide-show-library-pane");
		
		this.context = context;

		final ObservableSet<Tag> tags = context.getTags();
		
        // add sorting and filtering capabilities
		ObservableList<SlideShowListItem> theList = context.getSlideLibrary().getSlideShowItems();
        FilteredList<SlideShowListItem> filtered = theList.filtered(p -> true);
        SortedList<SlideShowListItem> sorted = filtered.sorted();
        
        // define a general listener for all the filters and sorting
        InvalidationListener filterListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable obs) {
				String text = textFilter.get();
				SlideSortField field = sortField.get().getValue();
				boolean desc = sortDescending.get();
				filtered.setPredicate(s -> {
					if (!s.isLoaded()) {
						return true;
					}
					if (s.isLoaded()) {
						if (text == null || text.length() == 0) {
							return true;
						} else if (s.getName().toLowerCase().contains(text.toLowerCase())) { // search name
							return true;
						}
						// TODO search other stuff?
					}
					return false;
				});
				sorted.setComparator(new Comparator<SlideShowListItem>() {
					@Override
					public int compare(SlideShowListItem o1, SlideShowListItem o2) {
						int value = 0;
						if (field == SlideSortField.NAME) {
							value = COLLATOR.compare(o1.getName(), o2.getName());
						} else {
							// check for loaded vs. not loaded slides
							// sort non-loaded slides to the end
							if (o1.getSlideShow() == null && o2.getSlideShow() == null) return 0;
							if (o1.getSlideShow() == null && o2.getSlideShow() != null) return 1;
							if (o1.getSlideShow() != null && o2.getSlideShow() == null) return -1;
							
							if (field == SlideSortField.LAST_MODIFIED_DATE) {
								value = -1 * (o1.getSlideShow().getLastModifiedDate().compareTo(o2.getSlideShow().getLastModifiedDate()));
							} else if (field == SlideSortField.CREATED_DATE) {
								value = -1 * (o1.getSlideShow().getCreatedDate().compareTo(o2.getSlideShow().getCreatedDate()));
							}
						}
						return (desc ? 1 : -1) * value;
					}
				});
			}
		};
		this.textFilter.addListener(filterListener);
		this.sortField.addListener(filterListener);
		this.sortDescending.addListener(filterListener);
		filterListener.invalidated(null);
		
		this.lstSlideShows = new FlowListView<SlideShowListItem>(orientation, new Callback<SlideShowListItem, FlowListCell<SlideShowListItem>>() {
        	@Override
        	public FlowListCell<SlideShowListItem> call(SlideShowListItem item) {
				return new SlideShowFlowListCell(context, item, 100);
			}
        });
		this.lstSlideShows.itemsProperty().bindContent(sorted);
        this.lstSlideShows.setOnDragOver(this::onDragOver);
        this.lstSlideShows.setOnDragDropped(this::onDragDropped);

		VBox right = new VBox();
		VBox importSteps = new VBox();
		
//		Label lblStep1 = new Label(Translations.get("bible.import.howto.list1"));
//		Label lblStep2 = new Label(Translations.get("bible.import.howto.list2"));
//		Label lblStep1Text = new Label(Translations.get("bible.import.howto.step1"));
//		Label lblStep2Text = new Label(Translations.get("bible.import.howto.step2"));
//		
//		Hyperlink lblUnbound = new Hyperlink(Translations.get("bible.import.howto.unbound"));
//		lblUnbound.setOnAction(e -> {
//			context.getJavaFXContext().getApplication().getHostServices().showDocument("https://unbound.biola.edu/index.cfm?method=downloads.showDownloadMain");
//		});
//		Hyperlink lblZefania = new Hyperlink(Translations.get("bible.import.howto.zefania"));
//		lblZefania.setOnAction(e -> {
//			context.getJavaFXContext().getApplication().getHostServices().showDocument("https://sourceforge.net/projects/zefania-sharp/files/Bibles/");
//		});
//		Hyperlink lblOpenSong = new Hyperlink(Translations.get("bible.import.howto.opensong"));
//		lblOpenSong.setOnAction(e -> {
//			context.getJavaFXContext().getApplication().getHostServices().showDocument("http://www.opensong.org/home/download");
//		});
		
//		lblUnbound.setPadding(new Insets(0, 0, 0, 20));
//		lblZefania.setPadding(new Insets(0, 0, 0, 20));
//		lblOpenSong.setPadding(new Insets(0, 0, 0, 20));
//		
//		lblStep1.setMinWidth(20);
//		lblStep2.setMinWidth(20);
//		lblStep1Text.setWrapText(true);
//		lblStep2Text.setWrapText(true);
//		
//		importSteps.getChildren().addAll(
//				new HBox(lblStep1, lblStep1Text),
//				new HBox(lblUnbound),
//				new HBox(lblZefania),
//				new HBox(lblOpenSong),
//				new HBox(lblStep2, lblStep2Text));

		SlideShowPropertiesPane ssip = new SlideShowPropertiesPane(context);

		//TitledPane ttlImport = new TitledPane(Translations.get("slide.import.howto.title"), importSteps);
		TitledPane ttlMetadata = new TitledPane(Translations.get("slide.properties.title"), ssip);
		
		right.getChildren().addAll(/*ttlImport, */ttlMetadata);
		
        ScrollPane rightScroller = new ScrollPane();
        rightScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setFitToWidth(true);
        rightScroller.setContent(right);
        rightScroller.setMinWidth(250);
        
        // FILTERING & SORTING
		
		ObservableList<Option<SlideSortField>> sortFields = FXCollections.observableArrayList();
		sortFields.addAll(Arrays.asList(SlideSortField.values())
        		.stream()
        		.map(t -> new Option<SlideSortField>(t.getName(), t))
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
		
        Label lblSort = new Label(Translations.get("field.sort"));
        ChoiceBox<Option<SlideSortField>> cbSort = new ChoiceBox<Option<SlideSortField>>(sortFields);
        cbSort.valueProperty().bindBidirectional(this.sortField);
        SortGraphic sortGraphic = new SortGraphic();
        ToggleButton tgl = new ToggleButton(null, sortGraphic);
        tgl.selectedProperty().bindBidirectional(this.sortDescending);
        sortGraphic.flipProperty().bind(this.sortDescending);
        
        TextField txtSearch = new TextField();
        txtSearch.setPromptText(Translations.get("field.search.placeholder"));
        txtSearch.textProperty().bindBidirectional(this.textFilter);
        
        Label lblFilter = new Label(Translations.get("field.filter"));
        HBox pFilter = new HBox(lblFilter, txtSearch); 
        pFilter.setAlignment(Pos.BASELINE_LEFT);
        pFilter.setSpacing(5);
        
        HBox pSort = new HBox(lblSort, cbSort, tgl);
        pSort.setAlignment(Pos.CENTER_LEFT);
        pSort.setSpacing(5);
        
        FlowPane top = new FlowPane();
        top.setHgap(5);
        top.setVgap(5);
        top.setAlignment(Pos.BASELINE_LEFT);
        top.setPadding(new Insets(5));
        top.setPrefWrapLength(0);
        
        top.getChildren().addAll(pFilter, pSort);

		SplitPane split = new SplitPane(this.lstSlideShows, rightScroller);
		split.setDividerPositions(0.75);
		SplitPane.setResizableWithParent(rightScroller, false);
		
        this.setTop(top);
        this.setCenter(split);
        
        // BINDINGS & EVENTS

		// update the local selection
		this.lstSlideShows.getSelectionModel().selectionsProperty().addListener((obs, ov, nv) -> {
			if (selecting) return;
			selecting = true;
        	if (nv == null || nv.size() != 1) {
        		this.selected.set(null);
        	} else {
        		this.selected.set(nv.get(0).getSlideShow());
        	}
        	selecting = false;
        	this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
        });
        this.selected.addListener((obs, ov, nv) -> {
        	if (selecting) return;
        	selecting = true;
        	if (nv == null) {
        		lstSlideShows.getSelectionModel().clear();
        	} else {
        		lstSlideShows.getSelectionModel().selectOnly(context.getSlideLibrary().getSlideShowListItem(nv.getId()));
        	}
        	selecting = false;
        	this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
        });
        
		this.lstSlideShows.addEventHandler(SelectionEvent.DOUBLE_CLICK, (e) -> {
			@SuppressWarnings("unchecked")
			FlowListCell<SlideShowListItem> view = (FlowListCell<SlideShowListItem>)e.getTarget();
			SlideShowListItem item = view.getData();
			if (item.isLoaded()) {
	    		fireEvent(new ApplicationEvent(e.getSource(), e.getTarget(), ApplicationEvent.ALL, ApplicationAction.EDIT, item.getSlideShow()));
	    	}
		});
		
		// setup the context menu
		ApplicationContextMenu menu = new ApplicationContextMenu(this);
		menu.getItems().addAll(
				menu.createMenuItem(ApplicationAction.OPEN),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.NEW_SLIDE_SHOW, Translations.get("action.new")),
				menu.createMenuItem(ApplicationAction.COPY),
				menu.createMenuItem(ApplicationAction.PASTE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.RENAME),
				menu.createMenuItem(ApplicationAction.DELETE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.IMPORT_SLIDES, Translations.get("action.import"), ApplicationGlyphs.MENU_IMPORT.duplicate()),
				menu.createMenuItem(ApplicationAction.EXPORT),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.SELECT_ALL),
				menu.createMenuItem(ApplicationAction.SELECT_NONE),
				menu.createMenuItem(ApplicationAction.SELECT_INVERT));
		this.lstSlideShows.setContextMenu(menu);

        // wire up the selected slide to the slide info view with a unidirectional binding
        ssip.slideShowProperty().bind(this.lstSlideShows.getSelectionModel().selectionProperty());
        
        // setup the event handler for application events
        this.addEventHandler(ApplicationEvent.ALL, this::onApplicationEvent);
	}

	/**
	 * Called when something is dragged over the element.
	 * @param event the event
	 */
	private void onDragOver(DragEvent event) {
		Dragboard db = event.getDragboard();
		if (db.hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY);
		} else {
			event.consume();
		}
	}
	
	/**
	 * Handler for when files have been drag and dropped to import.
	 * @param event the drag event
	 */
	private void onDragDropped(DragEvent event) {
		// get the dragboard
		Dragboard db = event.getDragboard();
		// make sure it contains files
		if (db.hasFiles()) {
			// get the files
			final List<File> files = db.getFiles();
			
			// convert to paths
			final List<Path> paths = new ArrayList<Path>();
			for (File file : files) {
				paths.add(file.toPath());
			}
			
			SlideActions.slideImport(
					this.context, 
					this.getScene().getWindow(), 
					paths)
			.execute(this.context.getExecutorService());
		}
		event.setDropCompleted(true);
		event.consume();
	}
	
	/**
	 * Handler for when slides are deleted.
	 */
	private void promptDelete() {
		List<SlideShow> shows = new ArrayList<SlideShow>();
		for (SlideShowListItem item : this.lstSlideShows.getSelectionModel().selectionsProperty().get()) {
			// can't delete items that are still being imported
			if (item.isLoaded()) {
				shows.add(item.getSlideShow());
			}
		}
		
		SlideActions.showPromptDelete(
				this.context.getSlideLibrary(), 
				this.getScene().getWindow(), 
				shows)
		.execute(this.context.getExecutorService());
	}

    /**
     * Event handler for renaming slides.
     * @param event the event
     */
    private final void promptRename(SlideShow show) {
    	SlideActions.showPromptRename(
    			this.context.getSlideLibrary(), 
    			this.getScene().getWindow(), 
    			show)
    	.execute(this.context.getExecutorService());
    }
    
    /**
     * Event handler for copying slides.
     */
    private final void copy() {
    	Clipboard cb = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		List<String> names = new ArrayList<String>();
		List<String> data = new ArrayList<String>();
		for (SlideShowListItem item : this.lstSlideShows.getSelectionModel().selectionsProperty()) {
			if (item.isLoaded()) {
				try {
					data.add(JsonIO.write(item.getSlideShow()));
					names.add(item.getName());
				} catch (Exception ex) {
					LOGGER.warn("Failed to copy slide '" + item.getName() + "' to clipboard.", ex);
				}
			}
		}
		content.putString(String.join(", ", names));
		content.put(DataFormats.SLIDE_SHOWS, data);
		cb.setContent(content);
		this.stateChanged(ApplicationPaneEvent.REASON_DATA_COPIED);
    }
    
    /**
     * Event handler for the paste action.
     */
    private final void paste() {
    	Clipboard cb = Clipboard.getSystemClipboard();
		Object data = cb.getContent(DataFormats.SLIDE_SHOWS);
		if (data != null && data instanceof List) {
			// make a copy
			List<?> shows = (List<?>)data;
			for (Object show : shows) {
				if (show instanceof String) {
					try {
						SlideShow copy = JsonIO.read((String)show, SlideShow.class);
						SlideActions.showCopy(
								this.context.getSlideLibrary(),
								this.getScene().getWindow(),
								copy)
						.execute(this.context.getExecutorService());
					} catch (Exception ex) {
						LOGGER.warn("Failed to paste slide show '" + show + "' from clipboard.", ex);
					}
				}
			}
		}
    }

    /**
     * Event handler for exporting slides.
     */
    private final void promptExport() {
    	List<SlideShowListItem> items = this.lstSlideShows.getSelectionModel().selectionsProperty().get();
		List<SlideShow> shows = new ArrayList<SlideShow>();
		for (SlideShowListItem item : items) {
			if (item.isLoaded()) {
				shows.add(item.getSlideShow());
			}
		}
    	
    	SlideActions.slidePromptExport(
    			this.context, 
    			this.getScene().getWindow(), 
    			null,
    			shows)
    	.execute(this.context.getExecutorService());
    }
    
    /**
     * Event handler for editing the selected item.
     */
    private void editSelected() {
    	SlideShowListItem item = this.lstSlideShows.getSelectionModel().selectionProperty().get();
    	if (item != null && item.isLoaded()) {
    		fireEvent(new ApplicationEvent(this, this, ApplicationEvent.ALL, ApplicationAction.EDIT, item.getSlideShow()));
    	}
    }
    
    /**
     * Event handler for application events.
     * @param event the event
     */
    private final void onApplicationEvent(ApplicationEvent event) {
    	Node focused = this.getScene().getFocusOwner();
    	boolean isFocused = focused == this || Fx.isNodeInFocusChain(focused, this.lstSlideShows);
    	
    	ApplicationAction action = event.getAction();
    	SlideShow selected = this.selected.get();
    	
    	switch (action) {
    		case RENAME:
    			if (selected != null) {
    				this.promptRename(selected);
    			}
    			break;
    		case OPEN:
    			this.editSelected();
    			break;
    		case COPY:
				if (isFocused) {
    				this.copy();
				}
    			break;
    		case PASTE:
				if (isFocused) {
					this.paste();
				}
    			break;
    		case DELETE:
				if (isFocused) {
					this.promptDelete();
				}
    			break;
    		case SELECT_ALL:
				if (isFocused) {
	    			this.lstSlideShows.getSelectionModel().selectAll();
	    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
				}
    			break;
    		case SELECT_NONE:
    			this.lstSlideShows.getSelectionModel().clear();
    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
    			break;
    		case SELECT_INVERT:
    			this.lstSlideShows.getSelectionModel().invert();
    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
    			break;
    		case EXPORT:
    			this.promptExport();
    			break;
    		default:
    			break;
    	}
    }
    
    /**
     * Called when the state of this pane changes.
     * @param reason the reason
     */
    private final void stateChanged(String reason) {
    	Scene scene = this.getScene();
    	// don't bother if there's no place to send the event to
    	if (scene != null) {
    		fireEvent(new ApplicationPaneEvent(this.lstSlideShows, SlideShowLibraryPane.this, ApplicationPaneEvent.STATE_CHANGED, SlideShowLibraryPane.this, reason));
    	}
    }
    
    /* (non-Javadoc)
     * @see org.praisenter.javafx.ApplicationPane#isApplicationActionEnabled(org.praisenter.javafx.ApplicationAction)
     */
	@Override
	public boolean isApplicationActionEnabled(ApplicationAction action) {
    	Node focused = this.getScene().getFocusOwner();
		
		List<SlideShowListItem> selected = this.lstSlideShows.getSelectionModel().selectionsProperty().get();
		
		boolean isSingleSelected = selected.size() == 1;
    	boolean isMultiSelected = selected.size() > 0;
    	boolean isFocused = focused == this || Fx.isNodeInFocusChain(focused, this.lstSlideShows);
    	boolean isLoaded = selected.stream().allMatch(b -> b.isLoaded());
		
		switch (action) {
			case OPEN:
			case RENAME:
				return isFocused && isLoaded && isSingleSelected;
			case COPY:
			case DELETE:
			case EXPORT:
				// check for focused text input first
				return isFocused && (isSingleSelected || isMultiSelected) && isLoaded;
			case PASTE:
				// check for focused text input first
				if (isFocused) {
					Clipboard cb = Clipboard.getSystemClipboard();
					return cb.hasContent(DataFormats.SLIDE_SHOWS);
				}
				break;
			case SELECT_ALL:
			case SELECT_NONE:
			case SELECT_INVERT:
				if (isFocused) {
					return true;
				}
				break;
			case IMPORT_SLIDES:
			case NEW_SLIDE:
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
	 * @see org.praisenter.javafx.ApplicationPane#setDefaultFocus()
	 */
	@Override
	public void setDefaultFocus() {
		this.requestFocus();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationPane#cleanup()
	 */
	@Override
	public void cleanup() {
		// clear the selection
		this.lstSlideShows.getSelectionModel().clear();
		
		// reset sort/filter
		this.textFilter.set(null);
		this.sortDescending.set(true);
		this.sortField.set(new Option<SlideSortField>(SlideSortField.NAME.getName(), SlideSortField.NAME));
	}
}
