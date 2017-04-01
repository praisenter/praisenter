package org.praisenter.javafx.slide;

import java.io.File;
import java.nio.file.Path;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.ApplicationAction;
import org.praisenter.javafx.ApplicationContextMenu;
import org.praisenter.javafx.ApplicationEvent;
import org.praisenter.javafx.ApplicationPane;
import org.praisenter.javafx.ApplicationPaneEvent;
import org.praisenter.javafx.DataFormats;
import org.praisenter.javafx.FlowListCell;
import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.SelectionEvent;
import org.praisenter.javafx.Styles;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.Slide;
import org.praisenter.xml.XmlIO;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

// TODO: undo/redo might be easier here since the size the XML documents is much smaller than bibles

public class SlideLibraryPane extends BorderPane implements ApplicationPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The collator for locale dependent sorting */
	private static final Collator COLLATOR = Collator.getInstance();
	
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	// selection
	
	/** The selected slide */
	private final ObjectProperty<Slide> selected = new SimpleObjectProperty<Slide>();
	
	/** True if the selection is being changed */
	private boolean selecting = false;
	
	// data
	
	/** The context */
	private final PraisenterContext context;

	// nodes
	
	/** The slide listing */
	private final FlowListView<SlideListItem> slides;

	// filtering

	/** The search */
	private final StringProperty textFilter = new SimpleStringProperty();

	// sorting
	
	/** The sort property */
	private final ObjectProperty<Option<SlideSortField>> sortField = new SimpleObjectProperty<Option<SlideSortField>>(new Option<SlideSortField>(SlideSortField.NAME.getName(), SlideSortField.NAME));
	
	/** The sort direction */
	private final BooleanProperty sortDescending = new SimpleBooleanProperty(true);
	
	public SlideLibraryPane(PraisenterContext context) {
		this.getStyleClass().add(Styles.SLIDE_LIBRARY_PANE);
		
		this.context = context;

        // add sorting and filtering capabilities
		ObservableList<SlideListItem> theList = context.getSlideLibrary().getItems();
        FilteredList<SlideListItem> filtered = theList.filtered(p -> true);
        SortedList<SlideListItem> sorted = filtered.sorted();
        
        // define a general listener for all the filters and sorting
        InvalidationListener filterListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable obs) {
				String text = textFilter.get();
				SlideSortField field = sortField.get().getValue();
				boolean desc = sortDescending.get();
				filtered.setPredicate(b -> {
					if (!b.isLoaded() || 
						((text == null || text.length() == 0 || b.getName().toLowerCase().contains(text.toLowerCase())))) {
						return true;
					}
					return false;
				});
				sorted.setComparator(new Comparator<SlideListItem>() {
					@Override
					public int compare(SlideListItem o1, SlideListItem o2) {
						int value = 0;
						if (field == SlideSortField.NAME) {
							value = COLLATOR.compare(o1.getName(), o2.getName());
						} else {
							// check for loaded vs. not loaded bibles
							// sort non-loaded bibles to the end
							if (o1.getSlide() == null && o2.getSlide() == null) return 0;
							if (o1.getSlide() == null && o2.getSlide() != null) return 1;
							if (o1.getSlide() != null && o2.getSlide() == null) return -1;
							
							if (field == SlideSortField.LAST_MODIFIED_DATE) {
								value = -1 * (o1.getSlide().getLastModifiedDate().compareTo(o2.getSlide().getLastModifiedDate()));
							} else if (field == SlideSortField.CREATED_DATE) {
								value = -1 * (o1.getSlide().getCreatedDate().compareTo(o2.getSlide().getCreatedDate()));
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
		
		this.slides = new FlowListView<SlideListItem>(Orientation.HORIZONTAL, new Callback<SlideListItem, FlowListCell<SlideListItem>>() {
        	@Override
        	public FlowListCell<SlideListItem> call(SlideListItem item) {
				return new SlideListCell(item, 100);
			}
        });
		this.slides.itemsProperty().bindContent(sorted);
        this.slides.setOnDragOver(this::onDragOver);
        this.slides.setOnDragDropped(this::onDragDropped);

//		VBox right = new VBox();
//		VBox importSteps = new VBox();
//		
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
//		
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
//
//		BibleInfoPane bmp = new BibleInfoPane();
//
//		TitledPane ttlImport = new TitledPane(Translations.get("bible.import.howto.title"), importSteps);
//		TitledPane ttlMetadata = new TitledPane(Translations.get("bible.properties.title"), bmp);
//		
//		right.getChildren().addAll(ttlImport, ttlMetadata);
//		
//        ScrollPane rightScroller = new ScrollPane();
//        rightScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        rightScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        rightScroller.setFitToWidth(true);
//        rightScroller.setContent(right);
//        rightScroller.setMinWidth(250);
        
		this.setCenter(this.slides);

        // BINDINGS & EVENTS

		// update the local selection
		this.slides.getSelectionModel().selectionsProperty().addListener((obs, ov, nv) -> {
			if (selecting) return;
			selecting = true;
        	if (nv == null || nv.size() != 1) {
        		this.selected.set(null);
        	} else {
        		this.selected.set(nv.get(0).getSlide());
        	}
        	selecting = false;
        	this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
        });
        this.selected.addListener((obs, ov, nv) -> {
        	if (selecting) return;
        	selecting = true;
        	if (nv == null) {
        		slides.getSelectionModel().clear();
        	} else {
        		slides.getSelectionModel().selectOnly(context.getSlideLibrary().getListItem(nv.getId()));
        	}
        	selecting = false;
        	this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
        });
        
		this.slides.addEventHandler(SelectionEvent.DOUBLE_CLICK, (e) -> {
			@SuppressWarnings("unchecked")
			FlowListCell<SlideListItem> view = (FlowListCell<SlideListItem>)e.getTarget();
			SlideListItem item = view.getData();
			if (item.isLoaded()) {
	    		fireEvent(new ApplicationEvent(e.getSource(), e.getTarget(), ApplicationEvent.ALL, ApplicationAction.EDIT, item.getSlide()));
	    	}
		});
		
		// setup the context menu
		ApplicationContextMenu menu = new ApplicationContextMenu(this);
		menu.getItems().addAll(
				menu.createMenuItem(ApplicationAction.OPEN),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.NEW_SLIDE, Translations.get("action.new")),
				menu.createMenuItem(ApplicationAction.COPY),
				menu.createMenuItem(ApplicationAction.PASTE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.RENAME),
				menu.createMenuItem(ApplicationAction.DELETE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.IMPORT_SLIDES, Translations.get("action.import"), FONT_AWESOME.create(FontAwesome.Glyph.LEVEL_DOWN)),
				menu.createMenuItem(ApplicationAction.EXPORT),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.SELECT_ALL),
				menu.createMenuItem(ApplicationAction.SELECT_NONE),
				menu.createMenuItem(ApplicationAction.SELECT_INVERT));
		this.slides.setContextMenu(menu);
        
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
		List<Slide> slides = new ArrayList<Slide>();
		for (SlideListItem item : this.slides.getSelectionModel().selectionsProperty().get()) {
			// can't delete items that are still being imported
			if (item.isLoaded()) {
				slides.add(item.getSlide());
			}
		}
		
		SlideActions.slidePromptDelete(
				this.context.getSlideLibrary(), 
				this.getScene().getWindow(), 
				slides)
		.execute(this.context.getExecutorService());
	}

    /**
     * Event handler for renaming slides.
     * @param event the event
     */
    private final void promptRename(Slide slide) {
    	SlideActions.slidePromptRename(
    			this.context.getSlideLibrary(), 
    			this.getScene().getWindow(), 
    			slide)
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
		for (SlideListItem item : this.slides.getSelectionModel().selectionsProperty()) {
			if (item.isLoaded()) {
				try {
					data.add(XmlIO.save(item.getSlide()));
					names.add(item.getName());
				} catch (Exception ex) {
					LOGGER.warn("Failed to copy slide '" + item.getName() + "' to clipboard.", ex);
				}
			}
		}
		content.put(DataFormat.PLAIN_TEXT, String.join(", ", names));
		content.put(DataFormats.SLIDES, data);
		cb.setContent(content);
		this.stateChanged(ApplicationPaneEvent.REASON_DATA_COPIED);
    }
    
    /**
     * Event handler for the paste action.
     */
    private final void paste() {
    	Clipboard cb = Clipboard.getSystemClipboard();
		Object data = cb.getContent(DataFormats.SLIDES);
		if (data != null && data instanceof List) {
			// make a copy
			List<?> slides = (List<?>)data;
			for (Object slide : slides) {
				if (slide instanceof String) {
					try {
						Slide copy = XmlIO.read((String)slide, BasicSlide.class);
						SlideActions.slideCopy(
								this.context.getSlideLibrary(),
								this.getScene().getWindow(),
								copy)
						.execute(this.context.getExecutorService());
					} catch (Exception ex) {
						LOGGER.warn("Failed to paste slide '" + slide + "' from clipboard.", ex);
					}
				}
			}
		}
    }

    /**
     * Event handler for exporting slides.
     */
    private final void promptExport() {
    	List<SlideListItem> items = this.slides.getSelectionModel().selectionsProperty().get();
		List<Slide> slides = new ArrayList<Slide>();
		for (SlideListItem item : items) {
			if (item.isLoaded()) {
				slides.add(item.getSlide());
			}
		}
    	
    	SlideActions.slidePromptExport(
    			this.context, 
    			this.getScene().getWindow(), 
    			slides)
    	.execute(this.context.getExecutorService());
    }
    
    /**
     * Event handler for editing the selected item.
     */
    private void editSelected() {
    	SlideListItem item = this.slides.getSelectionModel().selectionProperty().get();
    	if (item != null && item.isLoaded()) {
    		fireEvent(new ApplicationEvent(this, this, ApplicationEvent.ALL, ApplicationAction.EDIT, item.getSlide()));
    	}
    }
    
    /**
     * Event handler for application events.
     * @param event the event
     */
    private final void onApplicationEvent(ApplicationEvent event) {
    	Node focused = this.getScene().getFocusOwner();
    	boolean isFocused = focused == this || Fx.isNodeInFocusChain(focused, this.slides);
    	
    	ApplicationAction action = event.getAction();
    	Slide selected = this.selected.get();
    	
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
	    			this.slides.getSelectionModel().selectAll();
	    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
				}
    			break;
    		case SELECT_NONE:
    			this.slides.getSelectionModel().clear();
    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
    			break;
    		case SELECT_INVERT:
    			this.slides.getSelectionModel().invert();
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
    		fireEvent(new ApplicationPaneEvent(this.slides, SlideLibraryPane.this, ApplicationPaneEvent.STATE_CHANGED, SlideLibraryPane.this, reason));
    	}
    }
    
	@Override
	public boolean isApplicationActionEnabled(ApplicationAction action) {
    	Node focused = this.getScene().getFocusOwner();
		
		List<SlideListItem> selected = this.slides.getSelectionModel().selectionsProperty().get();
		
		boolean isSingleSelected = selected.size() == 1;
    	boolean isMultiSelected = selected.size() > 0;
    	boolean isFocused = focused == this || Fx.isNodeInFocusChain(focused, this.slides);
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
					return cb.hasContent(DataFormats.SLIDES);
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
				return true;
			default:
				break;
		}
		return false;
	}
	
	@Override
	public boolean isApplicationActionVisible(ApplicationAction action) {
		return true;
	}
	
	@Override
	public void setDefaultFocus() {
		this.requestFocus();
	}
}
