package org.praisenter.ui.library;

import java.awt.Button;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.praisenter.Reference;
import org.praisenter.async.BackgroundTask;
import org.praisenter.data.Persistable;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideShow;
import org.praisenter.ui.Action;
import org.praisenter.ui.ActionPane;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.controls.FlowListCell;
import org.praisenter.ui.controls.FlowListView;
import org.praisenter.ui.events.FlowListViewSelectionEvent;
import org.praisenter.ui.translations.Translations;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;

// TODO sorting, searching, filtering, sort direction
// TODO select all, select none, select invert, delete, rename, edit (if applicable), copy, paste, export actions
// TODO show metadata (name, size, length, etc. tags?)
// TODO for media -> a media preview
// TODO for slides -> a slide preview?
// TODO for slide shows -> a preview?
// TODO allow selection via code rather than UI?
// TODO context menu
public final class LibraryList extends BorderPane implements ActionPane {
	private final GlobalContext context;
	private final FlowListView<Persistable> view;
	
	public LibraryList(GlobalContext context) {
		this.context = context;
		
		this.view = new FlowListView<>(Orientation.HORIZONTAL, (item) -> {
			return new LibraryListCell(item);
		});
		
		this.view.addEventHandler(FlowListViewSelectionEvent.DOUBLE_CLICK, (e) -> {
			FlowListCell<?> view = (FlowListCell<?>)e.getTarget();
        	Object item = view.getData();
        	// TODO add song (or any other editable data)
        	if (item instanceof Bible ||
        		item instanceof Slide ||
        		item instanceof SlideShow) {
        		this.context.openDocument((Persistable)item);
        	}
        });
			
		this.setCenter(this.view);
	}
	
	public ObservableList<Persistable> getItems() {
		return this.view.getItems();
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
	public void cleanUp() {
		
	}

	@Override
	public boolean isActionEnabled(Action action) {
		List<?> selection = this.view.getSelectionModel().getSelectedItems();
		switch (action) {
			case SELECT_ALL:
			case SELECT_NONE:
			case SELECT_INVERT:
				return true;
			case DELETE:
				return !selection.isEmpty();
			case RENAME:
				return selection.size() == 1;
			case COPY:
				return !selection.isEmpty();
			case PASTE:
				// TODO need to implement paste
				return false;
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
				break;
			case PASTE:
				break;
			case EXPORT:
				break;
			default:
				break;
		}
		// TODO Auto-generated method stub
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> delete() {
		List<Persistable> items = new ArrayList<>(this.view.getSelectionModel().getSelectedItems());
		int n = items.size();
		if (n > 0) {
			Alert alert = Alerts.confirm(
					this.context.getStage(), 
					Modality.WINDOW_MODAL, 
					Translations.get("action.delete"),
					Translations.get("action.confirm"), 
					Translations.get("action.confirm.delete"));
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				BackgroundTask task = new BackgroundTask();
				task.setName(Translations.get("action.delete.task", n));
				task.setMessage(Translations.get("action.delete.task", n));
				
				CompletableFuture<?>[] futures = new CompletableFuture<?>[n];
				int i = 0;
				for (Persistable item : items) {
					futures[i++] = this.context.getDataManager().delete(item);
				}
				
				this.context.addBackgroundTask(task);
				return CompletableFuture.allOf(futures).thenRun(() -> {
					task.setProgress(1.0);
				}).exceptionally((t) -> {
					task.setException(t);
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
	    	TextInputDialog prompt = new TextInputDialog(oldName);
	    	prompt.initOwner(this.context.getStage());
	    	prompt.initModality(Modality.WINDOW_MODAL);
	    	prompt.setTitle(Translations.get("action.rename"));
	    	prompt.setHeaderText(Translations.get("action.rename.newname"));
	    	prompt.setContentText(Translations.get("action.rename.name"));
	    	Optional<String> result = prompt.showAndWait();
	    	if (result.isPresent()) {
	    		String newName = result.get();
	    		if (!Objects.equals(oldName, newName)) {
	    			final Persistable copy = item.copy();
	    			copy.setName(newName);
	    			copy.setModifiedDate(Instant.now());
	    			
	    			BackgroundTask task = new BackgroundTask();
					task.setName(Translations.get("action.rename.task", oldName, newName));
					task.setMessage(Translations.get("action.rename.task", oldName, newName));
					
					this.context.addBackgroundTask(task);
					return this.context.getDataManager().update(copy).thenRun(() -> {
						task.setProgress(1.0);
					}).exceptionally((t) -> {
						task.setException(t);
						return null;
					});
	    		}
	    	}
		}
		return CompletableFuture.completedFuture(null);
	}

	
}
