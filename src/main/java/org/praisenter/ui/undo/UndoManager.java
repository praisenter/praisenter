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
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final ObservableList<Edit> undos;
	private final ObservableList<Edit> redos;

	private final BooleanProperty redoAvailable;
	private final BooleanProperty undoAvailable;

	private final IntegerProperty undoCount;
	private final IntegerProperty redoCount;
	
	private final BooleanProperty marked;
	private final BooleanProperty topMarked;
	
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
					this.addUndoEdit(edit);
				}
			}
		});
		
		this.undoAvailable.bind(Bindings.createBooleanBinding(() -> {
			return !this.undos.isEmpty();
		}, this.undos));
		
		this.redoAvailable.bind(Bindings.createBooleanBinding(() -> {
			return !this.redos.isEmpty();
		}, this.undos));
		
		this.undoCount.bind(Bindings.createIntegerBinding(() -> {
			return this.undos.size();
		}, this.undos));
		
		this.redoCount.bind(Bindings.createIntegerBinding(() -> {
			return this.redos.size();
		}, this.redos));
		
		this.marked.bind(Bindings.createBooleanBinding(() -> {
			return this.undos.contains(Edit.MARK);
		}, this.undos));
		
		this.topMarked.bind(Bindings.createBooleanBinding(() -> {
			int size = this.undos.size();
			if (size <= 0) return false;
			Edit edit = this.undos.get(size - 1);
			return edit == Edit.MARK;
		}, this.undos));
		
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
			if (this.undos.isEmpty()) { 
				return;
			}
			
			Edit undo = this.undos.remove(this.undos.size() - 1);
			undo.undo();
			this.redos.add(undo);
			
			if (undo == Edit.MARK) {
				undo = this.undos.remove(this.undos.size() - 1);
				undo.undo();
				this.redos.add(undo);
			}
		} finally {
			this.printCounts();
			this.isOperating = false;
		}
	}
	
	public void redo() {
		if (this.isOperating) return;
		this.isOperating = true;
		
		try {
			if (this.redos.isEmpty()) {
				return;
			}
			
			Edit redo = this.redos.get(this.redos.size() - 1);
			redo.redo();
			this.undos.add(redo);
			
			if (redo == Edit.MARK) {
				redo = this.redos.get(this.redos.size() - 1);
				redo.redo();
				this.undos.add(redo);
			}
		} finally {
			this.printCounts();
			this.isOperating = false;
		}
	}
	
	private void addUndoEdit(Edit edit) {
		Edit toAdd = edit;
		int size = this.undos.size();
		if (size > 0) {
			Edit top = this.undos.get(size - 1);
			if (top.isMergeSupported(edit)) {
				Edit merged = toAdd = edit.merge(top);
				LOGGER.trace(() -> "Merged edit '" + edit + "' with '" + top + "' to produce '" + merged + "'");
			}
		}
		this.undos.add(toAdd);
	}
	
	private void printCounts() {
		LOGGER.trace("UNDO(" + this.undos.size() + ") REDO(" + this.redos.size() + ")");
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
	}
	
	public void discardBatch() {
		this.isBatching = false;
		this.batchName = null;
		this.batch = null;
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
