package org.praisenter.ui.document;

import java.util.concurrent.CompletableFuture;

import org.praisenter.data.Persistable;
import org.praisenter.ui.Action;

public interface DocumentEditor<T extends Persistable> {
	public DocumentContext<T> getDocumentContext();
	
	public boolean isActionEnabled(Action action);
	public boolean isActionVisible(Action action);
	public CompletableFuture<Void> executeAction(Action action);
	
	public void setDefaultFocus();
}
