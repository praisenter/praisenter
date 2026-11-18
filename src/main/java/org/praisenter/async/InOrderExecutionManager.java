package org.praisenter.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class InOrderExecutionManager {
	private CompletableFuture<Void> lastOperation;
	
	public InOrderExecutionManager() {
		this.lastOperation = CompletableFuture.completedFuture(null);
	}
	
	public synchronized CompletableFuture<Void> execute(Supplier<CompletableFuture<Void>> operation) {
		this.lastOperation = this.lastOperation.handle((o, ex) -> { 
			return null; 
		}).thenComposeAsync((o) -> {
			return operation.get();
		});
		return this.lastOperation.copy();
	}
}
