package org.praisenter.ui.slide;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.ui.Action;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentEditor;
import org.praisenter.ui.events.ActionStateChangedEvent;
import org.praisenter.ui.undo.UndoManager;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

// FEATURE grouping, UUID group property on each slide component, when grouped sizing and moving work on the group, can't select individual when grouped; or grouped at selection level only

// TODO background, shadow, center
// context menu
public final class SlideEditor extends BorderPane implements DocumentEditor<Slide> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final DataFormat SLIDE_COMPONENT_DATA = new DataFormat("application/x-praisenter-json-list;class=" + SlideComponent.class.getName());

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
		this.context = context;
		this.document = document;
		
		this.slide = new SimpleObjectProperty<>();
		this.components = FXCollections.observableArrayList();
		this.selected = new SimpleObjectProperty<>();
		
		this.view = new StackPane();
		this.slideView = new SlideView(context);
		this.slideView.setViewMode(SlideMode.EDIT);
		this.slideView.setViewScalingEnabled(true);
		
		this.undoManager = document.getUndoManager();
		
		this.slide.addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.components, ov.getComponents());
			}
			if (nv != null) {
				Bindings.bindContent(this.components, nv.getComponents());
			}
		});
		
		this.slide.bind(document.documentProperty());
		this.componentMapping = new MappedList<>(this.components, (c) -> {
			EditNode n = new EditNode(document, c);
			n.scaleProperty().bind(this.slideView.viewScaleXProperty());
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
			// TODO allow multi select
			clearSelectionExceptFor(nv);
			if (nv != null) {
				this.document.getSelectedItems().setAll(nv);
			} else {
				this.document.getSelectedItems().clear();
			}
		});

		this.slideView.slideProperty().bind(document.documentProperty());
		
		Pane editContainer = new Pane();
		Bindings.bindContent(editContainer.getChildren(), this.componentMapping);
		
		
		this.view.getChildren().addAll(this.slideView, editContainer);
		
//		this.view.setMinSize(0, 0);
//		this.view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//		this.slideView.setMinSize(0, 0);
//		this.slideView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.setCenter(this.view);
		BorderPane.setAlignment(this.view, Pos.CENTER);
		//this.setBorder(new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 4, 0, null), null, new BorderWidths(4))));
		this.addEventFilter(MouseEvent.ANY, e -> {
			this.requestFocus();
		});
		this.addEventHandler(MouseEvent.ANY, e -> {
			EventType<?> et = e.getEventType();
			if (et == MouseEvent.MOUSE_CLICKED || et == MouseEvent.MOUSE_PRESSED || et == MouseEvent.MOUSE_RELEASED) {
				this.selected.set(null);
			}
		});
	}
	
	private void clearSelectionExceptFor(EditNode node) {
		for (EditNode n : this.componentMapping) {
			if (n != node) {
				n.setSelected(false);
			}
		}
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
//			case NEW_BOOK:
//			case NEW_CHAPTER:
//			case NEW_VERSE:
//				return this.create(action);
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
				return Clipboard.getSystemClipboard().hasContent(SLIDE_COMPONENT_DATA);
			case DELETE:
				return ctx.getSelectedCount() > 0;
//			case NEW_BOOK:
//				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() == Bible.class;
//			case NEW_CHAPTER:
//				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() == Book.class;
//			case NEW_VERSE:
//				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() == Chapter.class;
			case REDO:
				return ctx.getUndoManager().isRedoAvailable();
			case UNDO:
				return ctx.getUndoManager().isUndoAvailable();
//			case RENUMBER:
//				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() != Verse.class;
//			case REORDER:
//				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() != Verse.class;
			default:
				return false;
		}
	}
	
	@Override
	public boolean isActionVisible(Action action) {
		return false;
//		// specifically show these actions
//		switch (action) {
//			case NEW_BOOK:
//			case NEW_CHAPTER:
//			case NEW_VERSE:
//			case RENUMBER:
//			case REORDER:
//				return true;
//			default:
//				return false;
//		}
	}
	
//	private ClipboardContent getClipboardContentForSelection() throws Exception {
//		List<Object> items = new ArrayList<Object>(this.document.getSelectedItems());
//		
//		// in the case of Drag n' Drop, we don't need to serialize it
//		String data = JsonIO.write(items);
//		ClipboardContent content = new ClipboardContent();
////		content.putString(String.join(Constants.NEW_LINE, textData));
//		// TODO output PowerPoint data too?
//		content.put(SLIDE_COMPONENT_DATA, data);
//		
//		return content;
//	}
	
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
					// TODO output PowerPoint data too?
					content.put(SLIDE_COMPONENT_DATA, data);
					
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
		
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Void> paste() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		if (clipboard.hasContent(SLIDE_COMPONENT_DATA)) {
			try {
				SlideComponent[] components = JsonIO.read((String)clipboard.getContent(SLIDE_COMPONENT_DATA), SlideComponent[].class);
				// offset them slightly
				for (SlideComponent sc : components) {
					sc.setId(UUID.randomUUID());
					sc.translate(20, 20);
				}
				this.slide.get().getComponents().addAll(components);
			} catch (Exception ex) {
				LOGGER.warn("Failed to paste clipboard content (likely due to a JSON deserialization error", ex);
			}
		}
		
		return AsyncHelper.nil();
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
			}
		} catch (Exception ex) {
			LOGGER.error("Failed to delete the selected items", ex);
			this.undoManager.discardBatch();
		}
		return AsyncHelper.nil();
	}
}
