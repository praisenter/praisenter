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
package org.praisenter.javafx.bible;

import java.io.File;
import java.nio.file.Path;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.FailedOperation;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.PraisenterBibleExporter;
import org.praisenter.javafx.Alerts;
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
import org.praisenter.javafx.SortGraphic;
import org.praisenter.javafx.utility.Fx;
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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.util.Callback;

/**
 * Pane specifically for showing the bibles in a bible library.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class BibleLibraryPane extends BorderPane implements ApplicationPane {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The collator for locale dependent sorting */
	private static final Collator COLLATOR = Collator.getInstance();
	
	// selection
	
	/** The selected bible */
	private final ObjectProperty<Bible> selected = new SimpleObjectProperty<Bible>();
	
	/** True if the selection is being changed */
	private boolean selecting = false;
	
	// data
	
	/** The context */
	private final PraisenterContext context;
	
	// nodes
	
	/** The bible listing */
	private final FlowListView<BibleListItem> lstBibles;
	
	// filtering

	/** The search */
	private final StringProperty textFilter = new SimpleStringProperty();

	// sorting
	
	/** The sort property */
	private final ObjectProperty<Option<BibleSortField>> sortField = new SimpleObjectProperty<Option<BibleSortField>>(new Option<BibleSortField>(BibleSortField.NAME.getName(), BibleSortField.NAME));
	
	/** The sort direction */
	private final BooleanProperty sortDescending = new SimpleBooleanProperty(true);
	
	/**
	 * Minimal constructor.
	 * @param context the praisenter context
	 */
	public BibleLibraryPane(PraisenterContext context) {
		this.context = context;
		
        // add sorting and filtering capabilities
		ObservableList<BibleListItem> theList = context.getBibleLibrary().getItems();
        FilteredList<BibleListItem> filtered = new FilteredList<BibleListItem>(theList, p -> true);
        SortedList<BibleListItem> sorted = new SortedList<BibleListItem>(filtered);
        
        // define a general listener for all the filters and sorting
        InvalidationListener filterListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable obs) {
				String text = textFilter.get();
				BibleSortField field = sortField.get().getValue();
				boolean desc = sortDescending.get();
				filtered.setPredicate(b -> {
					if (!b.isLoaded() || 
						((text == null || text.length() == 0 || b.getName().toLowerCase().contains(text.toLowerCase())))) {
						return true;
					}
					return false;
				});
				sorted.setComparator(new Comparator<BibleListItem>() {
					@Override
					public int compare(BibleListItem o1, BibleListItem o2) {
						int value = 0;
						if (field == BibleSortField.NAME) {
							value = COLLATOR.compare(o1.getName(), o2.getName());
						} else {
							// check for loaded vs. not loaded bibles
							// sort non-loaded bibles to the end
							if (o1.getBible() == null && o2.getBible() == null) return 0;
							if (o1.getBible() == null && o2.getBible() != null) return 1;
							if (o1.getBible() != null && o2.getBible() == null) return -1;
							
							if (field == BibleSortField.SOURCE) {
								value = COLLATOR.compare(o1.getBible().getSource(), o2.getBible().getSource());
							} else if (field == BibleSortField.LAST_MODIFIED_DATE) {
								value = -1 * (o1.getBible().getLastModifiedDate().compareTo(o2.getBible().getLastModifiedDate()));
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
		
		this.lstBibles = new FlowListView<BibleListItem>(Orientation.HORIZONTAL, new Callback<BibleListItem, FlowListCell<BibleListItem>>() {
			@Override
			public FlowListCell<BibleListItem> call(BibleListItem item) {
				return new BibleListCell(item);
			}
		});
		this.lstBibles.itemsProperty().bindContent(sorted);
        this.lstBibles.setOnDragOver(this::onBibleDragOver);
        this.lstBibles.setOnDragDropped(this::onBibleDragDropped);

		VBox right = new VBox();
		VBox importSteps = new VBox();
		
		Label lblStep1 = new Label(Translations.get("bible.import.list1"));
		Label lblStep2 = new Label(Translations.get("bible.import.list2"));
		Label lblStep1Text = new Label(Translations.get("bible.import.step1"));
		Label lblStep2Text = new Label(Translations.get("bible.import.step2"));
		
		Hyperlink lblUnbound = new Hyperlink(Translations.get("bible.import.unbound"));
		lblUnbound.setOnAction(e -> {
			context.getJavaFXContext().getApplication().getHostServices().showDocument("https://unbound.biola.edu/index.cfm?method=downloads.showDownloadMain");
		});
		Hyperlink lblZefania = new Hyperlink(Translations.get("bible.import.zefania"));
		lblZefania.setOnAction(e -> {
			context.getJavaFXContext().getApplication().getHostServices().showDocument("https://sourceforge.net/projects/zefania-sharp/files/Bibles/");
		});
		Hyperlink lblOpenSong = new Hyperlink(Translations.get("bible.import.opensong"));
		lblOpenSong.setOnAction(e -> {
			context.getJavaFXContext().getApplication().getHostServices().showDocument("http://www.opensong.org/home/download");
		});
		
		lblUnbound.setPadding(new Insets(0, 0, 0, 20));
		lblZefania.setPadding(new Insets(0, 0, 0, 20));
		lblOpenSong.setPadding(new Insets(0, 0, 0, 20));
		
		lblStep1.setMinWidth(20);
		lblStep2.setMinWidth(20);
		lblStep1Text.setWrapText(true);
		lblStep2Text.setWrapText(true);
		
		importSteps.getChildren().addAll(
				new HBox(lblStep1, lblStep1Text),
				new HBox(lblUnbound),
				new HBox(lblZefania),
				new HBox(lblOpenSong),
				new HBox(lblStep2, lblStep2Text));

		BibleMetadataPane bmp = new BibleMetadataPane();

		TitledPane ttlImport = new TitledPane(Translations.get("bible.import.title"), importSteps);
		TitledPane ttlMetadata = new TitledPane(Translations.get("bible.properties.title"), bmp);
		
		right.getChildren().addAll(ttlImport, ttlMetadata);
		
        ScrollPane rightScroller = new ScrollPane();
        rightScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setFitToWidth(true);
        rightScroller.setContent(right);
        rightScroller.setMinWidth(250);
        
        // FILTERING & SORTING
		
		ObservableList<Option<BibleSortField>> sortFields = FXCollections.observableArrayList();
		sortFields.addAll(Arrays.asList(BibleSortField.values())
        		.stream()
        		.map(t -> new Option<BibleSortField>(t.getName(), t))
        		.collect(Collectors.toList()));
        		
        Label lblSort = new Label(Translations.get("field.sort"));
        ChoiceBox<Option<BibleSortField>> cbSort = new ChoiceBox<Option<BibleSortField>>(sortFields);
        cbSort.valueProperty().bindBidirectional(this.sortField);
        SortGraphic sortGraphic = new SortGraphic();
        ToggleButton tgl = new ToggleButton(null, sortGraphic);
        tgl.selectedProperty().bindBidirectional(this.sortDescending);
        sortGraphic.flipProperty().bind(this.sortDescending);
        
        TextField txtSearch = new TextField();
        txtSearch.setPromptText(Translations.get("field.search.placeholder"));
        txtSearch.textProperty().bindBidirectional(this.textFilter);
        
        HBox pFilter = new HBox(txtSearch); 
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

		SplitPane split = new SplitPane(this.lstBibles, rightScroller);
		split.setDividerPositions(0.75);
		SplitPane.setResizableWithParent(rightScroller, false);
		
        this.setTop(top);
        this.setCenter(split);
        
        // BINDINGS & EVENTS

		// update the local selection
		this.lstBibles.getSelectionModel().selectionsProperty().addListener((obs, ov, nv) -> {
			if (selecting) return;
			selecting = true;
        	if (nv == null || nv.size() != 1) {
        		this.selected.set(null);
        	} else {
        		this.selected.set(nv.get(0).getBible());
        	}
        	selecting = false;
        	this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
        });
        this.selected.addListener((obs, ov, nv) -> {
        	if (selecting) return;
        	selecting = true;
        	if (nv == null) {
        		lstBibles.getSelectionModel().clear();
        	} else {
        		lstBibles.getSelectionModel().selectOnly(new BibleListItem(nv));
        	}
        	selecting = false;
        	this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
        });
        
        this.lstBibles.addEventHandler(SelectionEvent.DOUBLE_CLICK, (e) -> {
        	@SuppressWarnings("unchecked")
			FlowListCell<BibleListItem> view = (FlowListCell<BibleListItem>)e.getTarget();
        	BibleListItem item = view.getData();
        	if (item.isLoaded()) {
        		fireEvent(new ApplicationEvent(e.getSource(), e.getTarget(), ApplicationEvent.ALL, ApplicationAction.EDIT, item.getBible()));
        	}
        });

		// setup the context menu
		ApplicationContextMenu menu = new ApplicationContextMenu(this);
		menu.getItems().addAll(
				menu.createMenuItem(ApplicationAction.OPEN),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.COPY),
				menu.createMenuItem(ApplicationAction.PASTE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.RENAME),
				menu.createMenuItem(ApplicationAction.DELETE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.EXPORT),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.SELECT_ALL),
				menu.createMenuItem(ApplicationAction.SELECT_NONE),
				menu.createMenuItem(ApplicationAction.SELECT_INVERT));
		this.lstBibles.setContextMenu(menu);
        
        // wire up the selected bible to the bible metadata view with a unidirectional binding
        bmp.bibleProperty().bind(this.lstBibles.getSelectionModel().selectionProperty());
        
        // setup the event handler for application events
        this.addEventHandler(ApplicationEvent.ALL, this::onApplicationEvent);
	}
	
	/**
	 * Called when something is dragged over the element.
	 * @param event the event
	 */
	private void onBibleDragOver(DragEvent event) {
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
	private void onBibleDragDropped(DragEvent event) {
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
			
			// attempt to add to the library
			this.context.getBibleLibrary().add(
					paths, 
					(List<Bible> bibles) -> {
						// get the warning files
						String[] wFileNames = bibles.stream().filter(b -> b.hadImportWarning()).map(f -> f.getName()).collect(Collectors.toList()).toArray(new String[0]);
						if (wFileNames.length > 0) {
							// show a dialog of the files that had warnings
							String list = String.join(", ", wFileNames);
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.initOwner(getScene().getWindow());
							alert.initModality(Modality.WINDOW_MODAL);
							alert.setTitle(Translations.get("bible.import.info.title"));
							alert.setHeaderText(Translations.get("bible.import.info.header"));
							alert.setContentText(list);
							alert.show();
						}
					},
					(List<FailedOperation<Path>> failures) -> {
						// get the exceptions
						Exception[] exceptions = failures.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
						// get the failed bibles
						String list = String.join(", ", failures.stream().map(f -> f.getData().toAbsolutePath().toString()).collect(Collectors.toList()));
						Alert alert = Alerts.exception(
								getScene().getWindow(),
								null, 
								null,
								MessageFormat.format(Translations.get("bible.import.error"), list), 
								exceptions);
						alert.show();
					});
		}
		event.setDropCompleted(true);
		event.consume();
	}
	
	/**
	 * Handler for when bibles are deleted.
	 */
	private void promptDelete() {
		List<Bible> bibles = new ArrayList<Bible>();
		for (BibleListItem item : this.lstBibles.getSelectionModel().selectionsProperty().get()) {
			// can't delete items that are still being imported
			if (item.isLoaded()) {
				bibles.add(item.getBible());
			}
		}
		if (bibles.size() > 0) {
			// attempt to delete the selected bible
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initOwner(getScene().getWindow());
			alert.initModality(Modality.WINDOW_MODAL);
			alert.setTitle(Translations.get("bible.remove.title"));
			alert.setContentText(Translations.get("bible.remove.content"));
			alert.setHeaderText(null);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				// attempt to remove
				this.context.getBibleLibrary().remove(
						bibles, 
						() -> {
							// nothing to do on success
						}, 
						(List<FailedOperation<Bible>> failures) -> {
							// get the exceptions
							Exception[] exceptions = failures.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
							// get the failed bible
							String list = String.join(", ", failures.stream().map(f -> f.getData().getName()).collect(Collectors.toList()));
							Alert fAlert = Alerts.exception(
									getScene().getWindow(),
									null, 
									null, 
									MessageFormat.format(Translations.get("bible.remove.error"), list), 
									exceptions);
							fAlert.show();
						});
			}
		}
	}

    /**
     * Event handler for renaming bibles.
     * @param event the event
     */
    private final void promptRename(Bible bible) {
    	String old = bible.getName();
    	TextInputDialog prompt = new TextInputDialog(old);
    	prompt.initOwner(getScene().getWindow());
    	prompt.initModality(Modality.WINDOW_MODAL);
    	prompt.setTitle(Translations.get("bible.metadata.rename.title"));
    	prompt.setHeaderText(Translations.get("bible.metadata.rename.header"));
    	prompt.setContentText(Translations.get("bible.metadata.rename.content"));
    	Optional<String> result = prompt.showAndWait();
    	// check for the "OK" button
    	if (result.isPresent()) {
    		// actually rename it?
    		String name = result.get();
        	// update the bible's name
    		bible.setName(name);
        	this.context.getBibleLibrary().save(
        			MessageFormat.format(Translations.get("task.rename"), old, name),
        			bible, 
        			(Bible m) -> {
        				// nothing to do
        			}, 
        			(Bible m, Throwable ex) -> {
        				bible.setName(old);
        				// log the error
        				LOGGER.error("Failed to rename bible from '{}' to '{}': {}", old, name, ex.getMessage());
        				// show an error to the user
        				Alert alert = Alerts.exception(
        						getScene().getWindow(),
        						null, 
        						null, 
        						MessageFormat.format(Translations.get("bible.metadata.rename.error"), old, name), 
        						ex);
        				alert.show();
        			});
    	}
    }

    /**
     * Event handler for exporting bibles.
     * @param bibles the bibles to export
     */
    private final void promptExport(List<Bible> bibles) {
    	String name = Translations.get("bible.export.multiple.filename"); 
    	if (bibles.size() == 1) {
    		// make sure the file name doesn't have bad characters in it
    		name = BibleLibrary.createFileName(bibles.get(0));
    	}
    	name += ".zip";
    	FileChooser chooser = new FileChooser();
    	chooser.setInitialFileName(name);
    	chooser.setTitle(Translations.get("bible.export.title"));
    	chooser.getExtensionFilters().add(new ExtensionFilter(Translations.get("export.zip.name"), Translations.get("export.zip.extension")));
    	File file = chooser.showSaveDialog(getScene().getWindow());
    	if (file != null) {
    		PraisenterBibleExporter exporter = new PraisenterBibleExporter();
    		try {
				exporter.execute(file.toPath(), bibles);
			} catch (Exception ex) {
				// show an error to the user
				Alert alert = Alerts.exception(
						getScene().getWindow(),
						null, 
						null, 
						Translations.get("bible.export.error"), 
						ex);
				alert.show();
			}
    	}
    }
    
    /**
     * Event handler for editing the selected item.
     */
    private void editSelected() {
    	BibleListItem item = this.lstBibles.getSelectionModel().selectionProperty().get();
    	if (item != null && item.isLoaded()) {
    		fireEvent(new ApplicationEvent(this, this, ApplicationEvent.ALL, ApplicationAction.EDIT, item.getBible()));
    	}
    }
    
    /**
     * Event handler for application events.
     * @param event the event
     */
    private final void onApplicationEvent(ApplicationEvent event) {
    	Node focused = this.getScene().getFocusOwner();
    	boolean isFocused = focused == this || Fx.isNodeInFocusChain(focused, this.lstBibles);
    	
    	ApplicationAction action = event.getAction();
    	Bible selected = this.selected.get();
    	
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
	    			if (selected != null) {
	    				Clipboard cb = Clipboard.getSystemClipboard();
	    				ClipboardContent content = new ClipboardContent();
	    				content.put(DataFormat.PLAIN_TEXT, selected.getName());
	    				content.put(DataFormats.BIBLE_ID, selected.getId());
	    				cb.setContent(content);
	    				this.stateChanged(ApplicationPaneEvent.REASON_DATA_COPIED);
	    			}
				}
    			break;
    		case PASTE:
				if (isFocused) {
					Clipboard cb = Clipboard.getSystemClipboard();
					Object data = cb.getContent(DataFormats.BIBLE_ID);
					if (data != null && data instanceof UUID) {
						// make a copy
						Bible bible = this.context.getBibleLibrary().get((UUID)data);
						if (bible != null) {
							// make a copy
							Bible copy = bible.copy(false);
							// set the name to something else
							copy.setName(MessageFormat.format(Translations.get("bible.copy.name"), bible.getName()));
							// then save it
							this.context.getBibleLibrary().save(copy, saved -> {
								// nothing to do
							}, (failed, error) -> {
								// present message to user
								Alert alert = Alerts.exception(
										getScene().getWindow(),
										null, 
										null, 
										MessageFormat.format(Translations.get("bible.copy.error"), bible.getName()), 
										error);
								alert.show();
							});
						} else {
							LOGGER.warn("Failed to copy bible, it may have been removed.");
						}
					}
				}
    			break;
    		case DELETE:
				if (isFocused) {
					this.promptDelete();
				}
    			break;
    		case SELECT_ALL:
				if (isFocused) {
	    			this.lstBibles.getSelectionModel().selectAll();
	    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
				}
    			break;
    		case SELECT_NONE:
    			this.lstBibles.getSelectionModel().clear();
    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
    			break;
    		case SELECT_INVERT:
    			this.lstBibles.getSelectionModel().invert();
    			this.stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
    			break;
    		case EXPORT:
    			List<BibleListItem> items = this.lstBibles.getSelectionModel().selectionsProperty().get();
    			List<Bible> bibles = new ArrayList<Bible>();
    			for (BibleListItem item : items) {
    				if (item.isLoaded()) {
    					bibles.add(item.getBible());
    				}
    			}
    			this.promptExport(bibles);
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
    		fireEvent(new ApplicationPaneEvent(this.lstBibles, BibleLibraryPane.this, ApplicationPaneEvent.STATE_CHANGED, BibleLibraryPane.this, reason));
    	}
    }
    
    @Override
    public void setDefaultFocus() {
    	this.lstBibles.requestFocus();
    }
    
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationPane#isApplicationActionEnabled(org.praisenter.javafx.ApplicationAction)
	 */
	@Override
	public boolean isApplicationActionEnabled(ApplicationAction action) {
		Node focused = this.getScene().getFocusOwner();
		
		boolean isSingleSelected = this.selected.get() != null;
    	boolean isMultiSelected = this.lstBibles.getSelectionModel().selectionsProperty().size() > 0;
    	boolean isFocused = focused == this || Fx.isNodeInFocusChain(focused, this.lstBibles);
    	
    	switch (action) {
			case RENAME:
			case OPEN:
			case COPY:
				if (isFocused) {
					return isSingleSelected;
				}
				break;
			case DELETE:
			case EXPORT:
				// check for focused text input first
				if (isFocused) {
					return isSingleSelected || isMultiSelected;
				}
				break;
			case SELECT_ALL:
			case SELECT_NONE:
			case SELECT_INVERT:
				if (isFocused) {
					return true;
				}
				break;
			case PASTE:
				// check for focused text input first
				if (isFocused) {
					Clipboard cb = Clipboard.getSystemClipboard();
					return cb.hasContent(DataFormats.BIBLE_ID);
				}
				break;
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
}
