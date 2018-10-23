package org.praisenter.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class InOrderExecutionManager {
	private CompletableFuture<Void> lastOperation;
	
	public InOrderExecutionManager() {
		this.lastOperation = CompletableFuture.completedFuture(null);
	}
	
	public synchronized CompletableFuture<Void> execute(Function<Object, CompletableFuture<Void>> operation) {
		this.lastOperation = this.lastOperation.thenComposeAsync(operation);
		return this.lastOperation;
	}
}
