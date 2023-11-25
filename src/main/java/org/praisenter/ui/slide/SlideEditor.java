package org.praisenter.ui.slide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.Persistable;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.media.Media;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.graphics.ScaleType;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.data.slide.text.CountdownComponent;
import org.praisenter.data.slide.text.DateTimeComponent;
import org.praisenter.data.slide.text.SlideFont;
import org.praisenter.data.slide.text.SlideFontPosture;
import org.praisenter.data.slide.text.SlideFontWeight;
import org.praisenter.data.slide.text.TextComponent;
import org.praisenter.data.slide.text.TextPlaceholderComponent;
import org.praisenter.ui.Action;
import org.praisenter.ui.DataFormats;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bind.MappedList;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentEditor;
import org.praisenter.ui.events.ActionStateChangedEvent;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.undo.UndoManager;
import org.praisenter.utility.Scaling;
import org.praisenter.utility.StringManipulator;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

// FEATURE (L-M) Allow multi select of components for moving or other actions
// FEATURE (L-M) Allow grouping of components
//		UUID group property on each slide component, when grouped sizing and moving work on the group, can't select individual when grouped; or grouped at selection level only
public final class SlideEditor extends BorderPane implements DocumentEditor<Slide> {
	private static final String SLIDE_EDITOR_CSS = "p-slide-editor";
	
	private static final Logger LOGGER = LogManager.getLogger();

	private final GlobalContext context;
	private final DocumentContext<Slide> document;
	
	private final MappedList<EditNode, SlideComponent> componentMapping;
	private final ObjectProperty<Slide> slide;
	private final ObservableList<SlideComponent> components;
	private final ObjectProperty<EditNode> selected;
	
	private final StackPane view;
	private final SlideView slideView;
	
	private final UndoManager undoManager;
	
	public SlideEditor(
			GlobalContext context, 
			DocumentContext<Slide> document) {
		this.getStyleClass().add(SLIDE_EDITOR_CSS);
		
		this.context = context;
		this.document = document;
		
		this.slide = new SimpleObjectProperty<>();
		this.components = FXCollections.observableArrayList();
		this.selected = new SimpleObjectProperty<>();
		
		this.view = new StackPane();
		this.slideView = new SlideView(context);
		this.slideView.setViewMode(SlideMode.EDIT);
		this.slideView.setFitToWidthEnabled(true);
		this.slideView.setFitToHeightEnabled(true);
		this.slideView.setViewScaleAlignCenter(true);
		
		this.undoManager = document.getUndoManager();
		
		this.slide.addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.components, ov.getComponents());
			}
			if (nv != null) {
				Bindings.bindContent(this.components, nv.getComponents());
			}
		});
		
		this.componentMapping = new MappedList<>(this.components, (c) -> {
			EditNode n = new EditNode(document, c);
			n.scaleProperty().bind(this.slideView.viewScaleFactorProperty());
			n.snapToGridEnabledProperty().bind(context.snapToGridEnabledProperty());
			// restore selection (this happens when nodes are rearranged
			// in the source list - since they have to be removed then
			// re-added)
			EditNode selected = this.selected.get();
			if (selected != null && selected.getComponent() == c && !n.isSelected()) {
				n.setSelected(true);
				Platform.runLater(() -> {
					n.setSelected(true);
				});
			}
			n.selectedProperty().addListener((obs, ov, nv) -> {
				if (nv) {
					this.selected.set(n);
				}
			});
			return n;
		});
		
		this.selected.addListener((obs, ov, nv) -> {
			// set the view order to make sure the selected node is brought
			// to the foreground for editing
			if (ov != null) {
				ov.setViewOrder(0);
			}
			if (nv != null) {
				nv.setViewOrder(-1);
			}
			
			clearSelectionExceptFor(nv);
			if (nv != null) {
				this.document.getSelectedItems().setAll(nv);
			} else {
				this.document.getSelectedItems().clear();
			}
			
			// if a node was selected ensure the editor has focus
			// if it doesn't then some of the controls in the action
			// bar won't show up
			this.requestFocus();
		});

		this.slide.bind(document.documentProperty());
		
		// NOTE: this works because the SlideView will create a SlideNode from the given slide
		//       the SlideNode is bound to the given slide, so any updates to the slide will update
		//       the SlideView.
		this.slideView.render(document.getDocument(), null, false);
//		.thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
//		}));
		
		Pane editContainer = new Pane();
		editContainer.maxWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			Scaling s = this.slideView.getViewScale();
			return s.width;
		}, this.slideView.viewScaleProperty()));
		editContainer.maxHeightProperty().bind(Bindings.createDoubleBinding(() -> {
			Scaling s = this.slideView.getViewScale();
			return s.height;
		}, this.slideView.viewScaleProperty()));
		Bindings.bindContent(editContainer.getChildren(), this.componentMapping);
		
		this.view.getChildren().addAll(this.slideView, editContainer);
		
		this.setCenter(this.view);
		BorderPane.setAlignment(this.view, Pos.CENTER);
		//this.setBorder(new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 4, 0, null), null, new BorderWidths(4))));
//		this.addEventFilter(MouseEvent.ANY, e -> {
//			this.requestFocus();
//		});
		
		// if the user clicks on anything other than an EditNode, then set the selected value to null
		this.addEventHandler(MouseEvent.ANY, e -> {
			EventType<?> et = e.getEventType();
			if (et == MouseEvent.MOUSE_CLICKED || et == MouseEvent.MOUSE_PRESSED || et == MouseEvent.MOUSE_RELEASED) {
				if (!this.isEventTargetAnEditNode(e.getTarget())) {
					this.selected.set(null);
				}
			}
		});
		
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(
			this.createMenuItem(Action.NEW_SLIDE_TEXT_COMPONENT),
			this.createMenuItem(Action.NEW_SLIDE_PLACEHOLDER_COMPONENT),
			this.createMenuItem(Action.NEW_SLIDE_DATETIME_COMPONENT),
			this.createMenuItem(Action.NEW_SLIDE_COUNTDOWN_COMPONENT),
			this.createMenuItem(Action.NEW_SLIDE_MEDIA_COMPONENT),
			new SeparatorMenuItem(),
			this.createMenuItem(Action.COPY),
			this.createMenuItem(Action.CUT),
			this.createMenuItem(Action.PASTE),
			new SeparatorMenuItem(),
			this.createToggleMenuItem(Action.SLIDE_COMPONENT_SNAP_TO_GRID, context.snapToGridEnabledProperty()),
			new SeparatorMenuItem(),
			this.createMenuItem(Action.SLIDE_COMPONENT_MOVE_UP),
			this.createMenuItem(Action.SLIDE_COMPONENT_MOVE_DOWN),
			this.createMenuItem(Action.SLIDE_COMPONENT_MOVE_FRONT),
			this.createMenuItem(Action.SLIDE_COMPONENT_MOVE_BACK),
			new SeparatorMenuItem(),
			this.createMenuItem(Action.DELETE)
		);
		menu.setAutoHide(true);
		
		// when the user clicks anywhere, make sure the context menu is hidden
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (e.isPopupTrigger()) return;
			menu.hide();
		});

		this.setOnContextMenuRequested(e -> {
			menu.show(this, e.getScreenX(), e.getScreenY());
		});
		
		// allow drag-n-drop of library items onto the editor
		this.setOnDragOver(e -> {
			if (e.getDragboard().hasContent(DataFormat.FILES) ||
				e.getDragboard().hasContent(DataFormat.IMAGE)) {
				e.acceptTransferModes(TransferMode.COPY);
			}
			e.consume();
		});
		
		this.setOnDragDropped(e -> {
			Dragboard db = e.getDragboard();
			
			if (db.hasContent(DataFormat.FILES)) {
				e.consume();
				
				List<File> files = db.getFiles();
				
				this.context.importFiles(files).thenComposeAsync(AsyncHelper.onJavaFXThreadAndWait(items -> {
					this.addMediaComponents(items);
					e.setDropCompleted(true);
				})).exceptionally(t -> {
					LOGGER.error("Failed to import media: ", t);
					// show the error message
					return null;
				}).thenRun(() -> {
					e.setDropCompleted(true);
				});
				
			} else if (db.hasContent(DataFormat.IMAGE)) {
				String name = "Utitled.png";
				Image image = db.getImage();
				
				this.context.importImage(name, image).thenComposeAsync(AsyncHelper.onJavaFXThreadAndWait(item -> {
					List<Persistable> items = new ArrayList<>();
					items.add(item);
					this.addMediaComponents(items);
					e.setDropCompleted(true);
				})).exceptionally(t -> {
					LOGGER.error("Failed to import media: ", t);
					// show the error message
					return null;
				}).thenRun(() -> {
					e.setDropCompleted(true);
				});
			}
		});
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
	
	private MenuItem createToggleMenuItem(Action action, BooleanProperty target) {
		CheckMenuItem mnu = new CheckMenuItem(Translations.get(action.getMessageKey()));
		mnu.selectedProperty().bindBidirectional(target);
		if (action.getGraphicSupplier() != null) {
			mnu.setGraphic(action.getGraphicSupplier().get());
		}
		// NOTE: due to bug in JavaFX, we don't apply the accelerator here
		//mnu.setAccelerator(value);
		mnu.disableProperty().bind(this.context.getActionEnabledProperty(action).not());
		mnu.setUserData(action);
		return mnu;
	}
	
	private void clearSelectionExceptFor(EditNode node) {
		for (EditNode n : this.componentMapping) {
			if (n != node) {
				n.setSelected(false);
			}
		}
	}
	
	/**
	 * Returns true if the EventTarget has an EditNode in it's node hierarchy.
	 * @param target
	 * @return
	 */
	private boolean isEventTargetAnEditNode(EventTarget target) {
		if (target instanceof Node) {
			Node node = (Node)target;
			while (node != null) {
				if (node instanceof EditNode) {
					return true;
				}
				node = node.getParent();
			}
		}
		return false;
	}
	
	private void selectComponent(SlideComponent component) {
		Platform.runLater(() -> {
			for (EditNode n : this.componentMapping) {
				if (n.getComponent() == component) {
					this.selected.set(n);
					n.setSelected(true);
					break;
				}
			}
		});
	}
	
	@Override
	public DocumentContext<Slide> getDocumentContext() {
		return this.document;
	}

	@Override
	public void setDefaultFocus() {
		this.requestFocus();
	}
	
	@Override
	public CompletableFuture<Void> executeAction(Action action) {
		switch (action) {
			case COPY:
				return this.copy(false);
			case PASTE:
				return this.paste();
			case CUT:
				return this.copy(true);
			case DELETE:
				return this.delete();
			case NEW_SLIDE_TEXT_COMPONENT:
				return this.createNewTextComponent();
			case NEW_SLIDE_MEDIA_COMPONENT:
				return this.createNewMediaComponent();
			case NEW_SLIDE_PLACEHOLDER_COMPONENT:
				return this.createNewPlaceholderComponent();
			case NEW_SLIDE_DATETIME_COMPONENT:
				return this.createNewDateTimeComponent();
			case NEW_SLIDE_COUNTDOWN_COMPONENT:
				return this.createNewCountdownComponent();
			case SLIDE_COMPONENT_MOVE_BACK:
			case SLIDE_COMPONENT_MOVE_DOWN:
			case SLIDE_COMPONENT_MOVE_FRONT:
			case SLIDE_COMPONENT_MOVE_UP:
				return this.moveComponent(action);
			default:
				return CompletableFuture.completedFuture(null);
		}
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		DocumentContext<Slide> ctx = this.document;
		switch (action) {
			case COPY:
				return ctx.getSelectedCount() > 0;
			case CUT:
				return ctx.getSelectedCount() > 0;
			case PASTE:
				return Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_SLIDE_COMPONENT_ARRAY) ||
						Clipboard.getSystemClipboard().hasImage();
			case DELETE:
				return ctx.getSelectedCount() > 0;
			case NEW_SLIDE_TEXT_COMPONENT:
			case NEW_SLIDE_MEDIA_COMPONENT:
			case NEW_SLIDE_PLACEHOLDER_COMPONENT:
			case NEW_SLIDE_DATETIME_COMPONENT:
			case NEW_SLIDE_COUNTDOWN_COMPONENT:
				return true;
			case SAVE:
				return ctx.hasUnsavedChanges();
			case REDO:
				return ctx.getUndoManager().isRedoAvailable();
			case UNDO:
				return ctx.getUndoManager().isUndoAvailable();
			case SLIDE_COMPONENT_SNAP_TO_GRID:
				return true;
			case SLIDE_COMPONENT_MOVE_BACK:
			case SLIDE_COMPONENT_MOVE_FRONT:
			case SLIDE_COMPONENT_MOVE_DOWN:
			case SLIDE_COMPONENT_MOVE_UP:
				return ctx.getSelectedCount() > 0;
			default:
				return false;
		}
	}
	
	@Override
	public boolean isActionVisible(Action action) {
		// specifically show these actions
		switch (action) {
			case NEW_SLIDE_TEXT_COMPONENT:
			case NEW_SLIDE_MEDIA_COMPONENT:
			case NEW_SLIDE_PLACEHOLDER_COMPONENT:
			case NEW_SLIDE_DATETIME_COMPONENT:
			case NEW_SLIDE_COUNTDOWN_COMPONENT:
			case SLIDE_COMPONENT_MOVE_BACK:
			case SLIDE_COMPONENT_MOVE_FRONT:
			case SLIDE_COMPONENT_MOVE_DOWN:
			case SLIDE_COMPONENT_MOVE_UP:
			case SLIDE_COMPONENT_SNAP_TO_GRID:
				return true;
			default:
				return false;
		}
	}
	
	private CompletableFuture<Void> copy(boolean isCut) {
		List<Object> selected = new ArrayList<>(this.document.getSelectedItems());
		if (selected.size() > 0) {
			try {
				List<SlideComponent> items = new ArrayList<>();
				for (Object o : selected) {
					if (o instanceof EditNode) {
						SlideComponent sc = ((EditNode)o).getComponent();
						items.add(sc);
					}
				}
				
				if (items.size() > 0) {
					String data = JsonIO.write(items.toArray(new SlideComponent[0]));
					ClipboardContent content = new ClipboardContent();
					// FEATURE (L-L) Output in powerpoint format
					content.put(DataFormats.PRAISENTER_SLIDE_COMPONENT_ARRAY, data);
					
					Clipboard clipboard = Clipboard.getSystemClipboard();
					clipboard.setContent(content);
					
					if (isCut) {
						this.slide.get().getComponents().removeAll(items);
					}
					
					// handle the selection state changing
					this.fireEvent(new ActionStateChangedEvent(this, this, ActionStateChangedEvent.CLIPBOARD));
				}
			} catch (Exception ex) {
				LOGGER.warn("Failed to create ClipboardContent for current selection (copy/cut)", ex);
			}
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> paste() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		if (clipboard.hasContent(DataFormats.PRAISENTER_SLIDE_COMPONENT_ARRAY)) {
			try {
				SlideComponent[] components = JsonIO.read((String)clipboard.getContent(DataFormats.PRAISENTER_SLIDE_COMPONENT_ARRAY), SlideComponent[].class);
				// offset them slightly
				for (SlideComponent sc : components) {
					sc.setId(UUID.randomUUID());
					sc.translate(20, 20);
				}
				this.slide.get().getComponents().addAll(components);
				
				// select the last component
				if (components.length > 0) {
					this.selectComponent(components[components.length - 1]);
				}
			} catch (Exception ex) {
				LOGGER.warn("Failed to paste clipboard content (likely due to a JSON deserialization error", ex);
			}
		} else if (clipboard.hasContent(DataFormat.IMAGE)) {
			String name = StringManipulator.toFileName(Translations.get("action.new.untitled", "") + ".png", "");
			Image image = clipboard.getImage();
			
			this.context.importImage(name, image).thenComposeAsync(AsyncHelper.onJavaFXThreadAndWait(item -> {
				List<Persistable> items = new ArrayList<>();
				items.add(item);
				this.addMediaComponents(items);
			})).exceptionally(t -> {
				LOGGER.error("Failed to import media: ", t);
				// show the error message
				return null;
			});
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> delete() {
		List<Object> items = new ArrayList<Object>(this.document.getSelectedItems());
		try {
			List<SlideComponent> toRemove = new ArrayList<>();
			for (Object item : items) {
				if (item instanceof EditNode) {
					SlideComponent sc = ((EditNode)item).getComponent();
					toRemove.add(sc);
				}
			}
			
			if (!toRemove.isEmpty()) {
				this.undoManager.beginBatch("Delete");
				this.slide.get().getComponents().removeAll(toRemove);
				this.undoManager.completeBatch();
				// clear the selected items
				this.document.getSelectedItems().clear();
			}
		} catch (Exception ex) {
			LOGGER.error("Failed to delete the selected items", ex);
			this.undoManager.discardBatch();
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private void addMediaComponents(List<Persistable> items) {
		Slide slide = this.slide.get();
		
		List<Media> media = new ArrayList<>();
		// are any items media?
		for (Persistable item : items) {
			if (item instanceof Media) {
				media.add((Media)item);
			}
		}
		
		// if so, then add them to the slide
		for (Media m : media) {
			MediaObject mo = new MediaObject();
			mo.setMediaId(m.getId());
			mo.setMediaName(m.getName());
			mo.setMediaType(m.getMediaType());
			mo.setScaleType(ScaleType.UNIFORM);
			
			// then create a new media component with this media
			MediaComponent mc = new MediaComponent();
			mc.setMedia(mo);
			
			// set the width/height as a function of the slide and media size
			double mw = slide.getWidth() * 0.75;
			double mh = slide.getHeight() * 0.75;
			double w = m.getWidth();
			double h = m.getHeight();
			if (w > mw || h > mh) {
				w = mw;
				h = mh;
			}
			
			mc.setX(60);
			mc.setY(60);
			mc.setWidth(mw);
			mc.setHeight(mh);
			
			slide.getComponents().add(mc);
			this.selectComponent(mc);
		}
		
		this.context.getStage().requestFocus();
	}
	
	private CompletableFuture<Void> createNewTextComponent() {
		TextComponent component = new TextComponent(Translations.get("slide.component.text.default"));
		Slide slide = this.slide.get();
		if (slide != null) {
			component.setWidth(slide.getWidth() * 0.75);
			component.setHeight(100);
			component.setX(slide.getWidth() * 0.125);
			component.setY(slide.getHeight() * 0.125);
			component.setFont(new SlideFont("arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 50.0));
			slide.getComponents().add(component);
			this.selectComponent(component);
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> createNewMediaComponent() {
		MediaComponent component = new MediaComponent();
		Slide slide = this.slide.get();
		if (slide != null) {
			component.setBackground(new SlideColor());
			component.setHeight(slide.getHeight() * 0.5);
			component.setWidth(slide.getWidth() * 0.5);
			component.setX(slide.getWidth() * 0.125);
			component.setY(slide.getHeight() * 0.125);
			slide.getComponents().add(component);
			this.selectComponent(component);
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> createNewPlaceholderComponent() {
		TextPlaceholderComponent component = new TextPlaceholderComponent();
		Slide slide = this.slide.get();
		if (slide != null) {
			component.setWidth(slide.getWidth() * 0.75);
			component.setHeight(100);
			component.setX(slide.getWidth() * 0.125);
			component.setY(slide.getHeight() * 0.125);
			component.setFont(new SlideFont("arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 50.0));
			slide.getComponents().add(component);
			this.selectComponent(component);
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> createNewDateTimeComponent() {
		DateTimeComponent component = new DateTimeComponent();
		Slide slide = this.slide.get();
		if (slide != null) {
			component.setWidth(slide.getWidth() * 0.75);
			component.setHeight(100);
			component.setX(slide.getWidth() * 0.125);
			component.setY(slide.getHeight() * 0.125);
			component.setFont(new SlideFont("arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 50.0));
			slide.getComponents().add(component);
			this.selectComponent(component);
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> createNewCountdownComponent() {
		CountdownComponent component = new CountdownComponent();
		Slide slide = this.slide.get();
		if (slide != null) {
			component.setWidth(slide.getWidth() * 0.75);
			component.setHeight(100);
			component.setX(slide.getWidth() * 0.125);
			component.setY(slide.getHeight() * 0.125);
			component.setFont(new SlideFont("arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 50.0));
			slide.getComponents().add(component);
			this.selectComponent(component);
		}
		return CompletableFuture.completedFuture(null);
	}

	private CompletableFuture<Void> moveComponent(Action action) {
		Slide slide = this.slide.get();
		List<Object> items = this.document.getSelectedItems();
		if (slide != null && items != null) {
			boolean moved = false;
			UndoManager undoManager = this.document.getUndoManager();
			undoManager.beginBatch("movecomponent");
			for (Object item : items) {
				if (item instanceof EditNode) {
					SlideComponent component = ((EditNode)item).getComponent();
					switch (action) {
						case SLIDE_COMPONENT_MOVE_BACK:
							moved |= slide.moveComponentBack(component);
							break;
						case SLIDE_COMPONENT_MOVE_DOWN:
							moved |= slide.moveComponentDown(component);
							break;
						case SLIDE_COMPONENT_MOVE_FRONT:
							moved |= slide.moveComponentFront(component);
							break;
						case SLIDE_COMPONENT_MOVE_UP:
							moved |= slide.moveComponentUp(component);
							break;
						default:
							break;
					}
				}
			}
			if (moved) {
				undoManager.completeBatch();
			} else {
				undoManager.discardBatch();
			}
		}
		return CompletableFuture.completedFuture(null);
	}
}
