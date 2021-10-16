package org.praisenter.ui.slide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.Action;
import org.praisenter.ui.ActionPane;
import org.praisenter.ui.DataFormats;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.events.ActionStateChangedEvent;
import org.praisenter.ui.translations.Translations;

import javafx.collections.ObservableList;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public final class SlideList extends ListView<Slide> implements ActionPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String SLIDE_LIST_CLASS = "p-slide-list";
	
	private final GlobalContext context;
	
	public SlideList(GlobalContext context) {
		this.context = context;

		this.setCellFactory(s -> {
			SlideListCell cell = new SlideListCell(context);
			
			cell.setOnDragDetected(e -> this.dragDetected(e, cell));
			cell.setOnDragDone(e -> this.dragDone(e));
			cell.setOnDragDropped(e -> this.dragDropped(e, cell));
			cell.setOnDragEntered(e -> this.dragEntered(e, cell));
			cell.setOnDragExited(e -> this.dragExited(e, cell));
			cell.setOnDragOver(e -> this.dragOver(e, cell));

			return cell;
		});
		this.getStyleClass().add(SLIDE_LIST_CLASS);
		
		// these handle the case where there's no slides in the list to detect
		// dnd from another list
		this.setOnDragOver(e -> this.dragOver(e, null));
		this.setOnDragDropped(e -> this.dragDropped(e, null));
		
		// right click menu support
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(
			this.createMenuItem(Action.SELECT_NONE),
			new SeparatorMenuItem(),
			this.createMenuItem(Action.COPY),
			this.createMenuItem(Action.PASTE),
			new SeparatorMenuItem(),
			this.createMenuItem(Action.DELETE)
		);
		this.setContextMenu(menu);
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
	
	@Override
	public CompletableFuture<Void> executeAction(Action action) {
		switch (action) {
			case SELECT_NONE:
				this.getSelectionModel().clearSelection();
				break;
			case DELETE:
				return delete();
			case COPY:
				return copy();
			case PASTE:
				return paste();
			default:
				break;
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	@Override
	public ObservableList<?> getSelectedItems() {
		return this.getSelectionModel().getSelectedItems();
	}

	@Override
	public boolean isActionEnabled(Action action) {
		List<?> selection = this.getSelectionModel().getSelectedItems();
		switch (action) {
			case SELECT_NONE:
				return true;
			case DELETE:
				return !selection.isEmpty();
			case COPY:
				// even if you can't paste media in praisenter you can still copy and
				// paste it elsewhere, so it's enabled if anything is selected
				return !selection.isEmpty();
			case PASTE:
				Clipboard clipboard = Clipboard.getSystemClipboard();
				return clipboard.hasContent(DataFormats.PRAISENTER_SLIDE_ARRAY);
			default:
				return false;
		}
	}

	@Override
	public boolean isActionVisible(Action action) {
		return false;
	}

	@Override
	public void setDefaultFocus() {
		this.requestFocus();
	}
	
	@Override
	public void cleanUp() {
		// nothing to do
	}
	
	private CompletableFuture<Void> delete() {
		List<Slide> selected = new ArrayList<>(this.getSelectionModel().getSelectedItems());
		
		this.getSelectionModel().clearSelection();
		this.getItems().removeAll(selected);
		
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> copy() {
		List<Slide> items = new ArrayList<>(this.getSelectionModel().getSelectedItems());
		int n = items.size();
		if (n > 0) {
			// get all names
			List<String> names = items.stream().map(i -> i.getName()).collect(Collectors.toList());

			try {
				String data = JsonIO.write(items.toArray(new Slide[0]));
				
				ClipboardContent content = new ClipboardContent();
				content.putString(String.join(Constants.NEW_LINE, names));
				content.put(DataFormats.PRAISENTER_SLIDE_ARRAY, data);
				
				Clipboard clipboard = Clipboard.getSystemClipboard();
				clipboard.setContent(content);
				
				this.fireEvent(new ActionStateChangedEvent(this, this, ActionStateChangedEvent.CLIPBOARD));
			} catch (Exception e) {
				LOGGER.error("Failed to serialize the slide[] when copying: ", e);
			}
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> paste() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		if (clipboard.hasContent(DataFormats.PRAISENTER_SLIDE_ARRAY)) {
			Object data = clipboard.getContent(DataFormats.PRAISENTER_SLIDE_ARRAY);
			if (data != null && data instanceof String) {
				try {
					Slide[] slides = JsonIO.read((String)data, Slide[].class);
					
					this.getItems().addAll(slides);
				} catch (Exception e) {
					LOGGER.error("Failed to deserialize the slide[] when pasting: ", e);
				}
			}
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
    private void dragDetected(MouseEvent event, SlideListCell cell) {
    	Slide slide = cell.getItem();
    	
        if (slide == null) {
            return;
        }

        ObservableList<Slide> items = this.getItems();

        Dragboard dragboard = startDragAndDrop(TransferMode.COPY_OR_MOVE);
        ClipboardContent content = new ClipboardContent();

        // set the content
		try {
			String data = JsonIO.write(new Slide[] { slide });
			content.putString(slide.getName());
			content.put(DataFormats.PRAISENTER_INDEX, items.indexOf(slide));
			content.put(DataFormats.PRAISENTER_SLIDE_ARRAY, data);
		} catch (Exception e) {
			LOGGER.error("Failed to serialize the slide when starting drag-n-drop: ", e);
		}
		
		SnapshotParameters params = new SnapshotParameters();
		Image image = cell.snapshot(params, null);
		
		dragboard.setDragView(image);
        dragboard.setContent(content);

        event.consume();
    }

    private void dragOver(DragEvent event, SlideListCell cell) {
        if (event.getGestureSource() != cell &&
               event.getDragboard().hasContent(DataFormats.PRAISENTER_SLIDE_ARRAY)) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    private void dragEntered(DragEvent event, SlideListCell cell) {
        if (event.getGestureSource() != cell &&
                event.getDragboard().hasContent(DataFormats.PRAISENTER_SLIDE_ARRAY)) {
            cell.setOpacity(0.3);
        }
    }

    private void dragExited(DragEvent event, SlideListCell cell) {
        if (event.getGestureSource() != cell &&
                event.getDragboard().hasContent(DataFormats.PRAISENTER_SLIDE_ARRAY)) {
            cell.setOpacity(1);
        }
    }

    private void dragDropped(DragEvent event, SlideListCell cell) {
		Object source = event.getGestureSource();
        Dragboard db = event.getDragboard();
        TransferMode txMode = event.getAcceptedTransferMode();
        
        boolean success = false;
        
        // confirm we have the proper content in the dragboard
        if (db.hasContent(DataFormats.PRAISENTER_SLIDE_ARRAY) && db.hasContent(DataFormats.PRAISENTER_INDEX)) {
            ObservableList<Slide> items = this.getItems();
            
            int sourceIndex = (int)db.getContent(DataFormats.PRAISENTER_INDEX);
            Object content = db.getContent(DataFormats.PRAISENTER_SLIDE_ARRAY);
            
            // is it being dragged from this list or another list?
            ListView<Slide> sourceList = null;
    		boolean fromThis = false;
    		if (source instanceof SlideList) {
    			sourceList = (SlideList)source;
    			if (sourceList == this) {
    				fromThis = true;
    			}
    		}
            
    		// get the source slide
    		Slide sourceSlide = null; 
    		if (fromThis) {
    			// use the index to get the source slide
    			sourceSlide = items.get(sourceIndex);
    		} else if (content != null && content instanceof String) {
    			try {
            		sourceSlide = JsonIO.read((String)content, Slide[].class)[0];
    			} catch (Exception e) {
    				LOGGER.error("Failed to deserialize the slide when accepting a drag-n-drop: ", e);
				}
    		}
    		
    		// now figure out the target location
    		if (sourceSlide != null) {
    			int targetOffset = 0;
    			int targetIndex = items.size();
    			
    			if (cell != null) {
	    			// use the location of the mouse relative to the drop target
	    			// to determine if we should drop it before or after the hover
	    			// element
	    	    	double y = event.getY();
	    			double height = cell.getHeight();
	    			if (height > 0 && y / height > 0.5) {
	    				targetOffset = 1;
	    			}
	    			
	    			// check whether the target element is empty or not
	    			// if it's empty, then we just append it to the end
	    			// otherwise we need to find the index to place it
		            Slide targetSlide = cell.getItem();
		            if (targetSlide == null) {
		            	targetIndex = items.size();
		            } else {
		            	targetIndex = items.indexOf(targetSlide);	
		            }
    			}
	            
	            // if the source and target index is the same and the
	            // drag source is the same list as the drag target
	            // then we can do nothing
	            if (sourceIndex == targetIndex && fromThis) {
	            	event.setDropCompleted(true);
	            	event.consume();
	            	return;
	            }
	            
	            // now we need to apply the previously calculated drag
	            // offset based on the location of the mouse
	            targetIndex += targetOffset;
	            
	            // removing the item (if it's before the drop target)
	            // would change the index of the target
	            if (sourceIndex < targetIndex && fromThis) {
	            	targetIndex--;
	            }
	            
	            this.getSelectionModel().clearSelection();
	            
	            // remove the item from the source
	            if (txMode == TransferMode.MOVE) {
		            if (fromThis) {
		            	this.getItems().remove(sourceIndex);
		            } else if (sourceList != null) {
		            	sourceList.getItems().remove(sourceIndex);
		            }
	            }
	            
	            // add it to the target at the appropriate index
	            if (targetIndex >= items.size()) {
	            	this.getItems().add(sourceSlide);
	            } else {
	            	this.getItems().add(targetIndex, sourceSlide);
	            }
	            
	            // select the item that we added
	            this.getSelectionModel().select(targetIndex);
	            this.requestFocus();
	            
	            success = true;
    		}
        }
        
        event.setDropCompleted(success);
        event.consume();
    }

    private void dragDone(DragEvent event) {
    	event.consume();
    }
}
