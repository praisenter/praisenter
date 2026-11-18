package org.praisenter.ui.bind;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.CoalescingInOrderExecutionManager;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

final class AsyncBinding<T> implements ObservableValue<T> {
	private static final Logger LOGGER = LogManager.getLogger();

    private T value;
    private Supplier<T> func;
    
    private CoalescingInOrderExecutionManager asyncExecutionManager;
    
    private List<InvalidationListener> invalidationListeners;
    private List<ChangeListener<? super T>> changeListeners;
	
    private InvalidationListener invalidationListener;
    
    public AsyncBinding(Supplier<T> func, Observable... dependencies) {
    	this.value = null;
    	this.func = func;
    	
    	this.asyncExecutionManager = new CoalescingInOrderExecutionManager();
    	this.invalidationListeners = new ArrayList<InvalidationListener>(1);
    	this.changeListeners = new ArrayList<ChangeListener<? super T>>(1);
    	
        this.invalidationListener = (obs) -> {
        	this.applyChange(true);
        };
        
        for (Observable o : dependencies) {
        	o.addListener(this.invalidationListener);
        }
	}
    
    private void applyChange(boolean isInvalidation) {
    	this.asyncExecutionManager.execute(() -> {
    		return CompletableFuture.runAsync(() -> {
    			T result = this.func.get();
    			Platform.runLater(() -> {
    				this.applyResult(result, isInvalidation);
    			});
    		}).whenComplete((v, f) -> {
				if (f != null) {
					LOGGER.error("Failed to recompute async binding value", f);
				}
			});
    	});
    }
    
    private void applyResult(T newValue, boolean isInvalidation) {
    	T oldValue = this.value;
    	this.value = newValue;
    	if (isInvalidation) {
	    	for (var listener : this.invalidationListeners) {
	    		listener.invalidated(this);
	    	}
    	} else {
	    	for (var listener : this.changeListeners) {
	    		listener.changed(this, oldValue, newValue);
	    	}
    	}
    }
    
	@Override
	public void addListener(InvalidationListener listener) {
		this.invalidationListeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		this.invalidationListeners.remove(listener);
	}

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		this.changeListeners.add(listener);
	}

	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		this.changeListeners.remove(listener);
	}

	@Override
	public T getValue() {
		return value;
	}

}
