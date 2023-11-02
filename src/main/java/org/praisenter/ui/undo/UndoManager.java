package org.praisenter.ui.undo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class UndoManager {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final ObservableList<Edit> undos;
	private final ObservableList<Edit> redos;

	private final BooleanProperty redoAvailable;
	private final BooleanProperty undoAvailable;

	private final IntegerProperty undoCount;
	private final IntegerProperty redoCount;
	
	private final BooleanProperty marked;
	private final BooleanProperty topMarked;
	private final BooleanProperty notTopMarked;
	
	private boolean isOperating;
	private boolean isBatching;
	private String batchName;
	private List<Edit> batch;
	
	// for watching 
	
	private final ObjectProperty<Object> target;
	private final EditWatcher watcher;
	
	public UndoManager() {
		this.undos = FXCollections.observableArrayList();
		this.redos = FXCollections.observableArrayList();
		
		this.undoAvailable = new SimpleBooleanProperty();
		this.redoAvailable = new SimpleBooleanProperty();
		this.undoCount = new SimpleIntegerProperty();
		this.redoCount = new SimpleIntegerProperty();
		this.marked = new SimpleBooleanProperty();
		this.topMarked = new SimpleBooleanProperty();
		this.notTopMarked = new SimpleBooleanProperty();
		
		this.isOperating = false;

		this.isBatching = false;
		this.batchName = null;
		this.batch = null;
		
		this.target = new SimpleObjectProperty<>();
		this.watcher = new EditWatcher((edit) -> {
			if (!this.isOperating) {
				if (this.isBatching && this.batch != null) {
					this.batch.add(edit);
				} else {
					this.addEdit(edit);
				}
			}
		});
		
		this.marked.bind(Bindings.createBooleanBinding(() -> {
			return this.undos.contains(Edit.MARK);
		}, this.undos));
		
		this.topMarked.bind(Bindings.createBooleanBinding(() -> {
			int usize = this.undos.size();
			boolean redoHasMark = this.redos.stream().anyMatch(r -> r == Edit.MARK);
			// if there's nothing to undo, and the redos do not contain
			// the MARK, then we are back to the original state
			if (usize <= 0 && !redoHasMark) return true;
			// if the MARK is in the redo stack, then that means that the
			// save has occurred since it was opened and if you undo past
			// the MARK, it's now considered changed again
			
			// or if the top item on the undo stack is MARK
			if (usize > 0) {
				Edit edit = this.undos.get(usize - 1);
				return edit == Edit.MARK;
			}
			return false;
		}, this.undos, this.redos));
		
		this.notTopMarked.bind(Bindings.createBooleanBinding(() -> {
			return !this.topMarked.get();
		}, this.topMarked));

		// these bindings are after the ones above since we need them updated
		// before these are updated since its more likely that these will be
		// listened to
		
		this.undoAvailable.bind(Bindings.createBooleanBinding(() -> {
			return this.undos.stream().filter(u -> u != Edit.MARK && !(u instanceof MarkPosition)).count() > 0;
		}, this.undos));
		
		this.redoAvailable.bind(Bindings.createBooleanBinding(() -> {
			return !this.redos.isEmpty();
		}, this.redos));
		
		this.undoCount.bind(Bindings.createIntegerBinding(() -> {
			return this.undos.size();
		}, this.undos));
		
		this.redoCount.bind(Bindings.createIntegerBinding(() -> {
			return this.redos.size();
		}, this.redos));
		
		this.target.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.reset();
			}
			if (nv != null) {
				this.watcher.register(nv);
			}
		});
	}

	public void undo() {
		if (this.isOperating) return;
		this.isOperating = true;
		
		try {
			int size = this.undos.size();
			
			if (size == 0) { 
				return;
			}
			
			Edit undo = this.undos.remove(size - 1);
			undo.undo();
			this.redos.add(undo);
			size--;
			
			while ((undo == Edit.MARK || undo instanceof MarkPosition) && size > 0) {
				undo = this.undos.remove(size - 1);
				undo.undo();
				this.redos.add(undo);
				size--;
			}
		} finally {
			this.isOperating = false;
		}
	}
	
	public void redo() {
		if (this.isOperating) return;
		this.isOperating = true;
		
		try {
			int size = this.redos.size();
			
			if (size == 0) {
				return;
			}
			
			Edit redo = this.redos.remove(size - 1);
			redo.redo();
			this.undos.add(redo);
			size--;
			
			// there may be marks after the one we just un-did
			while (size > 0) {
				// peek at the next item
				redo = this.redos.get(size - 1);
				// is it a mark?
				if (redo == Edit.MARK || redo instanceof MarkPosition) {
					// if so, remove it and "redo" it
					this.redos.remove(size - 1);					
					redo.redo();
					this.undos.add(redo);
					size--;
				} else {
					// if it's not a mark then exit
					break;
				}
			}
		} finally {
			this.isOperating = false;
		}
	}
	
	public void addEdit(Edit edit) {
		if (!this.isOperating) {
			int size = this.undos.size();
			if (size > 0) {
				Edit top = this.undos.get(size - 1);
				if (top.isMergeSupported(edit)) {
					Edit merged = edit.merge(top);
					LOGGER.trace(() -> "Merged edit '" + edit + "' with '" + top + "' to produce '" + merged + "'");
					this.undos.set(size - 1, merged);
					return;
				}
			}
			this.undos.add(edit);
			this.redos.clear();
		}
	}
	
	public void print() {
		LOGGER.debug("====== Undos ======");
		for (Edit edit : this.undos) {
			LOGGER.debug(edit);
		}
		LOGGER.debug("====== Redos ======");
		for (Edit edit : this.redos) {
			LOGGER.debug(edit);
		}
	}
	
	public void mark() {
		// remove any prior marks
		this.undos.removeIf(c -> c == Edit.MARK);
		this.redos.removeIf(c -> c == Edit.MARK);
		// add a mark at this location
		this.undos.add(Edit.MARK);
	}
	
	public void unmark() {
		this.undos.removeIf(c -> c == Edit.MARK);
		this.redos.removeIf(c -> c == Edit.MARK);
	}
	
	public void reset() {
		this.undos.clear();
		this.redos.clear();
		this.watcher.unregister();
		this.batchName = null;
		this.batch = null;
		this.isBatching = false;
		this.isOperating = false;
	}
	
	public void beginBatch(String name) {
		this.batchName = name;
		this.batch = new ArrayList<>();
		this.isBatching = true;
	}
	
	public void completeBatch() {
		if (this.batch != null && this.batch.size() > 0) {
			this.undos.add(new CompositeEdit(this.batchName, this.batch));
		}
		this.isBatching = false;
		this.batchName = null;
		this.batch = null;
		this.redos.clear();
	}
	
	public void discardBatch() {
		this.isBatching = false;
		this.batchName = null;
		this.batch = null;
	}
	
	public Object storePosition() {
		MarkPosition position = new MarkPosition();
		this.undos.add(position);
		return position;
	}
	
	public void markPosition(Object position) {
		int uindex = this.undos.indexOf(position);
		int rindex = this.redos.indexOf(position);
		if (uindex >= 0) {
			this.unmark();
			uindex = this.undos.indexOf(position);
			this.undos.set(uindex, Edit.MARK);
		} else if (rindex >= 0) {
			this.unmark();
			rindex = this.redos.indexOf(position);
			this.redos.set(rindex, Edit.MARK);
		}
	}
	
	public void clearPosition(Object position) {
		this.undos.removeIf(u -> u == position);
		this.redos.removeIf(r -> r == position);
	}
	
	public Object getTarget() {
		return this.target.get();
	}
	
	public void setTarget(Object target) {
		this.target.set(target);
	}
	
	public ObjectProperty<Object> targetProperty() {
		return this.target;
	}
	
	public boolean isUndoAvailable() {
		return this.undoAvailable.get();
	}
	
	public ReadOnlyBooleanProperty undoAvailableProperty() {
		return this.undoAvailable;
	}
	
	public boolean isRedoAvailable() {
		return this.redoAvailable.get();
	}
	
	public ReadOnlyBooleanProperty redoAvailableProperty() {
		return this.redoAvailable;
	}
	
	public boolean isMarked() {
		return this.marked.get();
	}
	
	public ReadOnlyBooleanProperty markedProperty() {
		return this.marked;
	}
	
	public boolean isTopMarked() {
		return this.topMarked.get();
	}
	
	public ReadOnlyBooleanProperty topMarkedProperty() {
		return this.topMarked;
	}

	public boolean isNotTopMarked() {
		return this.notTopMarked.get();
	}
	
	public ReadOnlyBooleanProperty notTopMarkedProperty() {
		return this.notTopMarked;
	}
	
	public int getUndoCount() {
		return this.undoCount.get();
	}
	
	public ReadOnlyIntegerProperty undoCountProperty() {
		return this.undoCount;
	}
	
	public int getRedoCount() {
		return this.redoCount.get();
	}
	
	public ReadOnlyIntegerProperty redoCountProperty() {
		return this.redoCount;
	}
}
