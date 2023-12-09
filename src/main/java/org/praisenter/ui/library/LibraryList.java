package org.praisenter.ui.library;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.Editable;
import org.praisenter.data.ImportExportFormat;
import org.praisenter.data.Persistable;
import org.praisenter.data.media.Media;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.graphics.ScaleType;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.data.workspace.ReadOnlyDisplayConfiguration;
import org.praisenter.ui.Action;
import org.praisenter.ui.ActionPane;
import org.praisenter.ui.DataFormats;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Icons;
import org.praisenter.ui.Option;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.controls.FastScrollPane;
import org.praisenter.ui.controls.FlowListCell;
import org.praisenter.ui.controls.FlowListSelectionModel;
import org.praisenter.ui.controls.FlowListView;
import org.praisenter.ui.controls.WindowHelper;
import org.praisenter.ui.events.ActionStateChangedEvent;
import org.praisenter.ui.events.FlowListViewSelectionEvent;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.StringManipulator;

import atlantafx.base.controls.CustomTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class LibraryList extends BorderPane implements ActionPane {
	private static final String LIBRARY_LIST_CSS = "p-library-list";
	private static final String LIBRARY_LIST_FILTER_BAR_CSS = "p-library-list-filter-bar";
	private static final String LIBRARY_LIST_ITEMS_CSS = "p-library-list-items";
	private static final String LIBRARY_LIST_RIGHT_CSS = "p-library-list-right";
	private static final String LIBRARY_LIST_SEARCH_CSS = "p-library-list-search";
	
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Collator COLLATOR = Collator.getInstance();
	
	private final GlobalContext context;
	private final Node sortAsc = Icons.getIcon(Icons.SORT_ASCENDING);
	private final Node sortDesc = Icons.getIcon(Icons.SORT_DESCENDING);
	private final Node search = Icons.getIcon(Icons.SEARCH);
	
	// sorting and filtering
	
	private final StringProperty textFilter;
	private final ObjectProperty<Option<LibraryListType>> typeFilter;
	private final ObjectProperty<Option<LibraryListSortField>> sortField;
	private final BooleanProperty sortAscending;

	// ui
	
	private final ObservableList<Persistable> source;
	private final FlowListView<Persistable> view;
	
	private final BooleanProperty detailsPaneVisible;
	private final BooleanProperty filterVisible;
	private final BooleanProperty searchVisible;
	private final BooleanProperty sortVisible;
	
	// export
	
	private final ObjectProperty<ExportRequest> exportRequest;
	private final Stage dlgExport;
	
	public LibraryList(GlobalContext context, Orientation orientation, LibraryListType... filterTypes) {
		this.getStyleClass().add(LIBRARY_LIST_CSS);
		
		this.context = context;
		
		this.textFilter = new SimpleStringProperty();
		this.typeFilter = new SimpleObjectProperty<>(new Option<LibraryListType>());
		this.sortField = new SimpleObjectProperty<>(new Option<LibraryListSortField>(LibraryListSortField.NAME.getName(), LibraryListSortField.NAME));
		this.sortAscending = new SimpleBooleanProperty(true);
		
		this.detailsPaneVisible = new SimpleBooleanProperty(true);
		this.filterVisible = new SimpleBooleanProperty(true);
		this.searchVisible = new SimpleBooleanProperty(true);
		this.sortVisible = new SimpleBooleanProperty(true);
		
		this.view = new FlowListView<>(orientation, (item) -> {
			LibraryListCell cell = new LibraryListCell(item);
			
			// support drag n drop with library items
			// in particular the slide editor
			cell.setOnDragDetected(e -> {
				Persistable p = cell.getData();
				
				List<UUID> ids = new ArrayList<>();
				ids.add(p.getId());

				List<File> files = new ArrayList<File>();
				Path path = context.getWorkspaceManager().getFilePath(p);
				
				if (p instanceof Media) {
					Media media = (Media)p;
					path = media.getMediaPath();
				}
				
				if (path != null) {
					files.add(path.toFile());
				}
				
				Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
				ClipboardContent content = new ClipboardContent();
				if (files.size() > 0) {
					content.putFiles(files);
				}
				content.putString(p.getName());
				content.put(DataFormats.PRAISENTER_ID_LIST, ids);
				db.setContent(content);
				
				e.consume();
			});
			
			return cell;
		});
		this.view.getStyleClass().add(LIBRARY_LIST_ITEMS_CSS);
		
		// override the selection model's identity provider to check the 
		// ids instead of object equality.  This is because of the way the
		// library is managed -> we always create a copy of an item, update
		// it, then overwrite the item in the library - so object equality
		// is not preserved.  To ensure the selected item stays selected
		// under these conditions we need to use the identity of the object
		// instead of it's object reference
		this.view.getSelectionModel().setIdentityProvider((a, b) -> {
			if (a == b) return true;
			if (a == null) return false;
			if (b == null) return false;
			if (a.getId().equals(b.getId())) return true;
			return false;
		});
		
		this.source = FXCollections.observableArrayList();
		final FilteredList<Persistable> filtered = new FilteredList<Persistable>(this.source, (p) -> true);
		final SortedList<Persistable> sorted = new SortedList<>(filtered, (a, b) -> 0);
		
		final Runnable filterListener = () -> {
			final String search = this.textFilter.get();
			final String term = !StringManipulator.isNullOrEmpty(search) ? search.trim().toLowerCase() : null;
			final Option<LibraryListType> optTypeFilter = this.typeFilter.get();
			final LibraryListType typeFilter = optTypeFilter != null ? optTypeFilter.getValue() : null;
			filtered.setPredicate((i) -> {
				if (i == null) return false;
				if (typeFilter != null) {
					if (typeFilter != LibraryListType.from(i)) {
						return false;
					}
				}
				
				// single name search (more complex searching will be handled elsewhere)
				if (!StringManipulator.isNullOrEmpty(search)) {
					boolean matchesName = i.getName().toLowerCase().contains(term);
					boolean matchesAnyTag = i.getTags().stream().anyMatch(t -> t.getName().toLowerCase().contains(term));
					if (!matchesAnyTag && !matchesName) {
						return false;
					}
				}
				
				return true;
			});
		};
		
		final Runnable sortListner = () -> {
			final LibraryListSortField sortField = this.sortField.get().getValue();
			final boolean sortAscending = this.sortAscending.get();
			sorted.setComparator((a, b) -> {
				if (a == b) return 0;
				if (a == null) return 1;
				if (b == null) return -1;
				
				String an = a.getName();
				String bn = b.getName();
				
				if (an == null) an = "";
				if (bn == null) bn = "";
				
				int value = 0;
				if (sortField == null || sortField == LibraryListSortField.NAME) {
					// sort by name
					value = COLLATOR.compare(an, bn);
				} else if (sortField == LibraryListSortField.TYPE) {
					// sort by type, then name
					LibraryListType at = LibraryListType.from(a);
					LibraryListType bt = LibraryListType.from(b);
					value = at.getOrder() - bt.getOrder();
					if (value == 0) {
						value = COLLATOR.compare(an, bn);
					}
				} else if (sortField == LibraryListSortField.CREATED_ON) {
					// sort by created
					Instant at = a.getCreatedDate();
					Instant bt = b.getCreatedDate();
					value = at.compareTo(bt);
				} else if (sortField == LibraryListSortField.MODIFIED_ON) {
					// sort by created
					Instant at = a.getModifiedDate();
					Instant bt = b.getModifiedDate();
					value = at.compareTo(bt);
				} else {
					throw new RuntimeException("The sort field '" + sortField + "' is not supported.");
				}
				
				return value * (sortAscending ? 1 : -1);
			});
		};
		
		this.textFilter.addListener((obs, ov, nv) -> filterListener.run());
		this.typeFilter.addListener((obs, ov, nv) -> filterListener.run());
		this.sortField.addListener((obs, ov, nv) -> sortListner.run());
		this.sortAscending.addListener((obs, ov, nv) -> sortListner.run());
		
		filterListener.run();
		sortListner.run();
		
		Bindings.bindContent(this.view.getItems(), sorted);
		
		this.view.addEventHandler(FlowListViewSelectionEvent.DOUBLE_CLICK, (e) -> {
			FlowListCell<?> view = (FlowListCell<?>)e.getTarget();
        	Object item = view.getData();
        	Editable annotation = item.getClass().getAnnotation(Editable.class);
        	if (annotation != null) {
        		this.context.openDocument(((Persistable)item).copy());
        	}
        });
		
		// just show all types no matter what?
		List<Option<LibraryListType>> typeOptions = new ArrayList<>();
		typeOptions.addAll(Arrays.stream(filterTypes)
				.sorted((a,b) -> a.getOrder() - b.getOrder())
				.map(t -> new Option<LibraryListType>(t.getName(), t))
				.collect(Collectors.toList()));
		typeOptions.add(0, new Option<LibraryListType>());
		ObservableList<Option<LibraryListType>> typeFilters = FXCollections.observableArrayList(typeOptions);
		
		// filtering and sorting
		
		ObservableList<Option<LibraryListSortField>> sortFields = FXCollections.observableArrayList();
		sortFields.addAll(Arrays.asList(LibraryListSortField.values())
        		.stream()
        		.map(t -> new Option<LibraryListSortField>(t.getName(), t))
        		.collect(Collectors.toList()));
        
        Label lblFilter = new Label(Translations.get("list.filter.type"));
        Separator sepFilter = new Separator(Orientation.VERTICAL);
        ChoiceBox<Option<LibraryListType>> cbFilterType = new ChoiceBox<Option<LibraryListType>>(typeFilters);
        cbFilterType.setValue(new Option<>());
        cbFilterType.valueProperty().bindBidirectional(this.typeFilter);
        lblFilter.visibleProperty().bind(this.filterVisible);
        lblFilter.managedProperty().bind(lblFilter.visibleProperty());
        cbFilterType.visibleProperty().bind(this.filterVisible);
        cbFilterType.managedProperty().bind(cbFilterType.visibleProperty());
        sepFilter.visibleProperty().bind(this.filterVisible);
        sepFilter.managedProperty().bind(sepFilter.visibleProperty());
		
        Label lblSort = new Label(Translations.get("list.sort.field"));
        Separator sepSort = new Separator(Orientation.VERTICAL);
        ChoiceBox<Option<LibraryListSortField>> cbSortType = new ChoiceBox<Option<LibraryListSortField>>(sortFields);
        cbSortType.valueProperty().bindBidirectional(this.sortField);
        ToggleButton tglSortDirection = new ToggleButton(null, this.sortAsc);
        tglSortDirection.selectedProperty().bindBidirectional(this.sortAscending);
        tglSortDirection.selectedProperty().addListener((obs, ov, nv) -> {
        	if (nv) {
        		tglSortDirection.setGraphic(this.sortAsc);
        	} else {
        		tglSortDirection.setGraphic(this.sortDesc);
        	}
        });
        lblSort.visibleProperty().bind(this.sortVisible);
        lblSort.managedProperty().bind(lblSort.visibleProperty());
        cbSortType.visibleProperty().bind(this.sortVisible);
        cbSortType.managedProperty().bind(cbSortType.visibleProperty());
        tglSortDirection.visibleProperty().bind(this.sortVisible);
        tglSortDirection.managedProperty().bind(tglSortDirection.visibleProperty());
        sepSort.visibleProperty().bind(this.sortVisible);
        sepSort.managedProperty().bind(sepSort.visibleProperty());
        
        CustomTextField txtSearch = new CustomTextField();
        txtSearch.setPromptText(Translations.get("list.filter.search"));
        txtSearch.textProperty().bindBidirectional(this.textFilter);
        txtSearch.setLeft(this.search);
        txtSearch.visibleProperty().bind(this.searchVisible);
        txtSearch.managedProperty().bind(txtSearch.visibleProperty());
        txtSearch.getStyleClass().add(LIBRARY_LIST_SEARCH_CSS);
        
        ToolBar toolbar = new ToolBar(
        		txtSearch,
        		sepFilter,
        		lblFilter, cbFilterType,
        		sepSort,
        		lblSort, cbSortType, tglSortDirection);
        toolbar.getStyleClass().add(LIBRARY_LIST_FILTER_BAR_CSS);
        toolbar.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
        	return this.searchVisible.get() ||
        		   this.filterVisible.get() ||
        		   this.sortVisible.get();
        }, this.searchVisible, this.filterVisible, this.sortVisible));
        toolbar.managedProperty().bind(toolbar.visibleProperty());
        
        LibraryItemDetails details = new LibraryItemDetails(context);
        details.setMinWidth(0);
        details.itemProperty().bind(this.view.getSelectionModel().selectedItemProperty());
        
        ScrollPane detailsScroller = new FastScrollPane(details, 2.0);
		detailsScroller.setFitToWidth(true);
		detailsScroller.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		detailsScroller.setHbarPolicy(ScrollBarPolicy.NEVER);
		detailsScroller.getStyleClass().add(LIBRARY_LIST_RIGHT_CSS);
		details.prefWidthProperty().bind(detailsScroller.widthProperty());
		
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(
				this.createMenuItem(Action.OPEN),
				this.createMenuItem(Action.DUPLICATE),
				this.createMenuItem(Action.QUICK_SLIDE_FROM_MEDIA),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.SELECT_ALL),
				this.createMenuItem(Action.SELECT_INVERT),
				this.createMenuItem(Action.SELECT_NONE),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.COPY),
				this.createMenuItem(Action.PASTE),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.RENAME),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.DELETE),
				this.createMenuItem(Action.EXPORT)
			);
		this.view.setContextMenu(menu);
		
		SplitPane split = new SplitPane(this.view, detailsScroller);
		split.setDividerPosition(0, 0.70);
		SplitPane.setResizableWithParent(detailsScroller, false);
		
		this.detailsPaneVisible.addListener((obs, ov, nv) -> {
			if (nv) {
				split.getItems().add(0, this.view);
				this.setCenter(split);
			} else {
				split.getItems().remove(this.view);
				this.setCenter(this.view);
			}
		});

		this.setTop(toolbar);
		this.setCenter(split);
		
		LibraryExportPane lep = new LibraryExportPane(this.context);
		
		Button btnCancel = new Button(Translations.get("cancel"));
		Button btnOk = new Button(Translations.get("ok"));
		btnOk.setDefaultButton(true);
		
		ButtonBar.setButtonData(btnCancel, ButtonData.CANCEL_CLOSE);
		ButtonBar.setButtonData(btnOk, ButtonData.OK_DONE);
		
		this.exportRequest = new SimpleObjectProperty<ExportRequest>();
		this.dlgExport = Dialogs.createStageDialog(
				context, 
				Translations.get("action.export"), 
				StageStyle.DECORATED, 
				Modality.WINDOW_MODAL, 
				lep, 
				btnCancel, btnOk);
		
		this.dlgExport.setResizable(true);
		this.dlgExport.setMinWidth(600);
		this.dlgExport.setMinHeight(350);
		this.dlgExport.setWidth(600);
		this.dlgExport.setHeight(350);
		btnCancel.setOnAction(e -> {
			this.exportRequest.set(null);
			this.dlgExport.hide();
		});
		btnOk.setOnAction(e -> {
			ExportRequest request = lep.getValue();
			this.exportRequest.set(request);
			this.dlgExport.hide();
		});
		btnOk.disableProperty().bind(lep.valueProperty().isNull());
	}

	private MenuItem createMenuItem(Action action) {
		MenuItem mnu = new MenuItem(Translations.get(action.getMessageKey()));
		if (action.getGraphicSupplier() != null) {
			mnu.setGraphic(action.getGraphicSupplier().get());
		}
		// NOTE: due to bug in JavaFX, we don't apply the accelerator here
		//mnu.setAccelerator(value);
		mnu.setOnAction(e -> this.executeAction(action));
		mnu.disableProperty().bind(this.context.getActionEnabledProperty(action).not());
		mnu.setUserData(action);
		return mnu;
	}
	
	public ObservableList<Persistable> getItems() {
		return this.source;
	}
	
	public FlowListSelectionModel<Persistable> getSelectionModel() {
		return this.view.getSelectionModel();
	}
	
	@Override
	public ObservableList<?> getSelectedItems() {
		return this.view.getSelectionModel().getSelectedItems();
	}
	
	@Override
	public void setDefaultFocus() {
		this.view.requestFocus();
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		List<Persistable> selection = this.view.getSelectionModel().getSelectedItems();
		Persistable selected = selection.size() > 0 ? selection.get(0) : null;
		switch (action) {
			case OPEN:
				return selection.size() == 1 && selected.getClass().isAnnotationPresent(Editable.class);
			case DUPLICATE:
				return !selection.isEmpty() && selection.stream().anyMatch(p -> p.getClass().isAnnotationPresent(Editable.class));
			case QUICK_SLIDE_FROM_MEDIA:
				return selection.size() == 1 && selection.get(0) instanceof Media;
			case SELECT_ALL:
			case SELECT_NONE:
			case SELECT_INVERT:
				return true;
			case DELETE:
				return !selection.isEmpty();
			case RENAME:
				return selection.size() == 1;
			case COPY:
				// even if you can't paste media in praisenter you can still copy and
				// paste it elsewhere, so it's enabled if anything is selected
				return !selection.isEmpty();
			case PASTE:
				Clipboard clipboard = Clipboard.getSystemClipboard();
				return clipboard.hasContent(DataFormats.PRAISENTER_ID_LIST);
			case EXPORT:
				return !selection.isEmpty();
			default:
				return false;
		}
	}
	
	@Override
	public boolean isActionVisible(Action action) {
		return false;
	}
	
	@Override
	public CompletableFuture<Void> executeAction(Action action) {
		switch (action) {
			case OPEN:
				this.openDocument();
	        	break;
			case DUPLICATE:
				this.duplicate();
				break;
			case QUICK_SLIDE_FROM_MEDIA:
				this.createSlideFromMedia();
				break;
			case SELECT_ALL:
				this.view.getSelectionModel().selectAll();
				break;
			case SELECT_INVERT:
				this.view.getSelectionModel().invert();
				break;
			case SELECT_NONE:
				this.view.getSelectionModel().clear();
				break;
			case DELETE:
				return delete();
			case RENAME:
				return rename();
			case COPY:
				return copy();
			case PASTE:
				return paste();
			case EXPORT:
				return export();
			default:
				break;
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private void openDocument() {
		List<Persistable> selection = this.view.getSelectionModel().getSelectedItems();
		Persistable selected = selection.size() > 0 ? selection.get(0) : null;
		if (selected != null) {
	    	if (selected.getClass().isAnnotationPresent(Editable.class)) {
	    		this.context.openDocument(selected.copy());
	    	}
		}
	}
	
	private void duplicate() {
		ObservableList<Persistable> items = this.view.getSelectionModel().getSelectedItems();
		
		if (items == null)
			return;
		
		if (items.size() == 0)
			return;
		
		List<Persistable> copies = new ArrayList<>();
		for (Persistable item : items) {
			// we only duplicate editable things (not media for example)
			if (!item.getClass().isAnnotationPresent(Editable.class)) {
				continue;
			}
			
			Persistable copy = item.copy();
			copy.setId(UUID.randomUUID());
			copy.setName(Translations.get("action.copy.name", item.getName()));
			copy.setCreatedDate(Instant.now());
			copy.setModifiedDate(copy.getCreatedDate());
			copies.add(copy);
		}
		
		this.context.saveAll(copies).thenAccept((exceptions) -> {
			if (exceptions != null && exceptions.size() > 0) {
				Platform.runLater(() -> {
					Alert errorAlert = Dialogs.exception(
							this.context.getStage(), 
							null, null,	null, 
							exceptions);
					errorAlert.show();
				});
			}
		}).exceptionally((t) -> {
			LOGGER.error("The duplications of items failed: ", t);
			Platform.runLater(() -> {
				Alert errorAlert = Dialogs.exception(this.context.getStage(), t);
				errorAlert.show();
			});
			return null;
		});
	}
	
	private void createSlideFromMedia() {
		Persistable persistable = this.view.getSelectionModel().getSelectedItem();
		
		if (persistable == null)
			return;
		
		Media media = null;
		if (persistable instanceof Media) {
			media = (Media)persistable;
		} else {
			return;
		}
		
		double width = 1920;
		double height = 1080;
		// default the size of the slide to the primary display target
		ReadOnlyDisplayConfiguration primary = this.context.getWorkspaceConfiguration().getPrimaryDisplayConfiguration();
		if (primary != null) {
			width = primary.getWidth();
			height = primary.getHeight();
		}
		
		// build the background
		SlideColor bg = new SlideColor(0, 0, 0, 1);
		
		// build the media
		MediaObject mo = new MediaObject();
		mo.setLoopEnabled(true);
		mo.setMediaId(media.getId());
		mo.setMediaName(media.getName());
		mo.setMediaType(media.getMediaType());
		mo.setMuted(false);
		mo.setScaleType(ScaleType.UNIFORM);
		
		MediaComponent comp = new MediaComponent();
		comp.setHeight(height);
		comp.setMedia(mo);
		comp.setWidth(width);
		comp.setX(0);
		comp.setY(0);
		
		Slide slide = new Slide(Translations.get("action.new.untitled", media.getName()));
		slide.setBackground(bg);
		slide.setHeight(height);
		slide.setWidth(width);
		
		slide.getComponents().add(comp);
		
		this.context.openDocument(slide, true);
	}
	
	private CompletableFuture<Void> delete() {
		// take a snapshot of the selected items
		final List<Persistable> items = new ArrayList<>(this.view.getSelectionModel().getSelectedItems());
		
		// clear the selection to free up any MediaPlayers that need to be disposed
		this.view.getSelectionModel().clear();
		
		// execute later on the Java FX thread to give the MediaPlayers time to be disposed properly
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		Platform.runLater(() -> {
			// then attempt to delete the files
			// NOTE: the MediaAdapter should gracefully handle a media file still being open at this time
			this.confirmDelete(items).thenRun(() -> {
				future.complete(null);
			}).exceptionally((t) -> {
				future.completeExceptionally(t);
				return null;
			});
		});
		
		return future;
	}
	
	private CompletableFuture<Void> confirmDelete(List<Persistable> items) {
		int n = items.size();
		if (n > 0) {
			// collect all dependencies
			Set<UUID> dependendencyIds = new HashSet<>();
			for (Persistable item : items) {
				dependendencyIds.add(item.getId());
			}
			
			// iterate ALL items to find those dependent on these items
			List<Persistable> dependents = new ArrayList<>();
			for (Persistable p : this.context.getWorkspaceManager().getItemsUnmodifiable()) {
				if (p.getDependencies().isEmpty())
					continue;
				
				for (UUID id : p.getDependencies()) {
					if (dependendencyIds.contains(id)) {
						dependents.add(p);
						break;
					}
				}
			}
			
			String contentText = Translations.get("action.confirm.delete");
			if (!dependents.isEmpty()) {
				String dependentNames = String.join(", ", dependents.stream().map(d -> {
					return Translations.get("action.delete.dependency.pattern", d.getName(), this.context.getFriendlyItemType(d));
				}).collect(Collectors.toList()));
				
				String dependencyWarning = Translations.get("action.delete.dependency.warning", dependentNames);
				contentText = dependencyWarning + "\n\n" + contentText;
			}
			
			Alert alert = Dialogs.confirm(
					this.context.getStage(), 
					Modality.WINDOW_MODAL, 
					Translations.get("action.delete"),
					Translations.get("action.confirm"), 
					contentText);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				return this.context.delete(items).thenAccept((exceptions) -> {
					if (exceptions != null && exceptions.size() > 0) {
						Platform.runLater(() -> {
							Alert errorAlert = Dialogs.exception(
									this.context.getStage(), 
									null, null,	null, 
									exceptions);
							errorAlert.show();
						});
					}
				}).exceptionally((t) -> {
					LOGGER.error("The bulk delete of the items failed: ", t);
					Platform.runLater(() -> {
						Alert errorAlert = Dialogs.exception(this.context.getStage(), t);
						errorAlert.show();
					});
					return null;
				});
			}
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> rename() {
		Persistable item = this.view.getSelectionModel().getSelectedItem();
		if (item != null) {
			String oldName = item.getName();
	    	TextInputDialog prompt = Dialogs.textInput(
	    			this.context.getStage(), 
	    			Modality.WINDOW_MODAL, 
	    			oldName, 
	    			Translations.get("action.rename"), 
	    			Translations.get("action.rename.newname"), 
	    			Translations.get("action.rename.name"));
	    	Optional<String> result = prompt.showAndWait();
	    	if (result.isPresent()) {
	    		String newName = result.get();
	    		if (!Objects.equals(oldName, newName)) {
	    			return this.context.rename(item, newName).exceptionally(t -> {
	    				Platform.runLater(() -> {
	    					Alert alert = Dialogs.exception(this.context.getStage(), t);
							alert.show();
	    				});
	    				return null;
	    			});
	    		}
	    	}
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> copy() {
		List<Persistable> items = new ArrayList<>(this.view.getSelectionModel().getSelectedItems());
		int n = items.size();
		if (n > 0) {
			// get all names
			List<String> names = items.stream().map(i -> i.getName()).collect(Collectors.toList());
			
			// get all files
			List<File> files = items.stream().map(i -> {
				Path path = this.context.getWorkspaceManager().getFilePath(i);
				if (path == null) return null;
				return path.toFile();
			}).filter(f -> f != null).collect(Collectors.toList());
			
			// get all ids
			List<UUID> ids = items.stream().map(i -> {
				// we can't paste media, so just skip any of those for
				// the ids data
				if (i instanceof Media) return null;
				return i.getId();
			}).filter(i -> i != null).collect(Collectors.toList());
			
			// we can put an image if there's only one item
			Image image = null;
			if (n == 1) {
				Persistable item = items.get(0);
				if (item instanceof Slide) {
					Slide slide = (Slide)item;
					image = this.context.getImageCache().getOrLoadThumbnail(slide.getId(), slide.getThumbnailPath());
				} else if (item instanceof Media) {
					Media media = (Media)item;
					image = this.context.getImageCache().getOrLoadImage(media.getId(), media.getMediaImagePath());
				}
			}
			
			ClipboardContent content = new ClipboardContent();
			content.putString(String.join(Constants.NEW_LINE, names));
			if (image != null) content.putImage(image);
			if (!files.isEmpty()) content.putFiles(files);
			if (!ids.isEmpty()) content.put(DataFormats.PRAISENTER_ID_LIST, ids);
			
			Clipboard clipboard = Clipboard.getSystemClipboard();
			clipboard.setContent(content);
			
			this.fireEvent(new ActionStateChangedEvent(this, this, ActionStateChangedEvent.CLIPBOARD));
		}
		
		return CompletableFuture.completedFuture(null);
	}

	private CompletableFuture<Void> paste() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		if (clipboard.hasContent(DataFormats.PRAISENTER_ID_LIST)) {
			Object data = clipboard.getContent(DataFormats.PRAISENTER_ID_LIST);
			if (data != null && data instanceof List) {
				List<?> entries = (List<?>)data;
				List<Persistable> items = new ArrayList<>();
				boolean itemNoLongerExists = false;
				for (Object entry : entries) {
					if (entry instanceof UUID) {
						UUID id = (UUID)entry; 
						Persistable item = this.context.getWorkspaceManager().getPersistableById(id);
						if (item != null) {
							item = item.copy();
							item.setId(UUID.randomUUID());
							item.setName(Translations.get("action.copy.name", item.getName()));
							item.setCreatedDate(Instant.now());
							item.setModifiedDate(item.getCreatedDate());
							items.add(item);
						} else {
							itemNoLongerExists = true;
						}
					}
				}
				
				// check for the user pasting items that no longer exist
				// if they do, then show a warning and don't bother waiting
				// for them to acknowledge
				if (itemNoLongerExists) {
					Platform.runLater(() -> {
						Alert alert = Dialogs.warn(
								this.context.getStage(), 
								Modality.WINDOW_MODAL, 
								Translations.get("action.paste"), 
								null, 
								Translations.get("action.paste.missing"));
						alert.show();
					});
				}
				
				return this.context.saveAll(items).thenAccept((exceptions) -> {
					if (exceptions != null && exceptions.size() > 0) {
						Platform.runLater(() -> {
							Alert errorAlert = Dialogs.exception(
									this.context.getStage(), 
									null, null,	null, 
									exceptions);
							errorAlert.show();
						});
					}
				}).exceptionally((t) -> {
					LOGGER.error("The bulk copy/paste of items failed: ", t);
					Platform.runLater(() -> {
						Alert errorAlert = Dialogs.exception(this.context.getStage(), t);
						errorAlert.show();
					});
					return null;
				});
			}
		}
		return CompletableFuture.completedFuture(null);
	}

	private CompletableFuture<Void> export() {
		List<Persistable> items = new ArrayList<>(this.view.getSelectionModel().getSelectedItems());
		int n = items.size();
		if (n > 0) {
			LOGGER.trace("Prompting for format and location/file for export");
			this.exportRequest.set(null);
			
			this.dlgExport.setWidth(600);
			this.dlgExport.setHeight(350);
			this.dlgExport.setMaximized(false);
			WindowHelper.centerOnParent(this.getScene().getWindow(), this.dlgExport);
		    this.dlgExport.showAndWait();
		    
		    LOGGER.trace("User cancelled or completed export dialog");
		    ExportRequest value = this.exportRequest.get();
		    if (value != null) {
		    	Path path = value.getPath();
		    	ImportExportFormat format = value.getFormat();
		    	LOGGER.debug("User selected format '{}' and path '{}' for export of {} items", format, path, n);
		    	LOGGER.trace("Validating path '{}' (exists and is a regular file)", path);
		    	if (Files.isRegularFile(path) || !Files.exists(path)) {
		    		LOGGER.trace("Path '{}' is valid, attempting export", path);
			    	return this.context.export(items, path, format).exceptionally(t -> {
		    			// get the root exception
		    			if (t instanceof CompletionException) {
		    				t = t.getCause();
		    			}
		    			
						// show it to the user
		    			final Throwable ex = t;
						Platform.runLater(() -> {
							Dialogs.exception(this.context.getStage(), ex).show();
						});
						
		    			return null;
		    		});
		    	}
		    }
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	public boolean isMultiSelectEnabled() {
		return this.view.isMultipleSelectionEnabled();
	}
	
	public void setMultiSelectEnabled(boolean enabled) {
		this.view.setMultipleSelectionEnabled(enabled);
	}
	
	public BooleanProperty multiSelectEnabledProperty() {
		return this.view.multipleSelectionProperty();
	}
	
	public boolean isDetailsPaneVisible() {
		return this.detailsPaneVisible.get();
	}
	
	public void setDetailsPaneVisible(boolean enabled) {
		this.detailsPaneVisible.set(enabled);
	}
	
	public BooleanProperty detailsPaneVisibleProperty() {
		return this.detailsPaneVisible;
	}
	
	public boolean isFilterVisible() {
		return this.filterVisible.get();
	}
	
	public void setFilterVisible(boolean enabled) {
		this.filterVisible.set(enabled);
	}
	
	public BooleanProperty filterVisibleProperty() {
		return this.filterVisible;
	}
	
	public boolean isSortVisible() {
		return this.sortVisible.get();
	}
	
	public void setSortVisible(boolean enabled) {
		this.sortVisible.set(enabled);
	}
	
	public BooleanProperty sortVisibleProperty() {
		return this.sortVisible;
	}

	public boolean isSearchVisible() {
		return this.searchVisible.get();
	}
	
	public void setSearchVisible(boolean enabled) {
		this.searchVisible.set(enabled);
	}
	
	public BooleanProperty searchVisibleProperty() {
		return this.searchVisible;
	}
}
