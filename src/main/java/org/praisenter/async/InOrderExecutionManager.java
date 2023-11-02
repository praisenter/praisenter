package org.praisenter.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class InOrderExecutionManager {
//	private static final Logger LOGGER = LogManager.getLogger();
	
	private CompletableFuture<Void> lastOperation;
	private Supplier<CompletableFuture<Void>> nextOperation;
	
	public InOrderExecutionManager() {
		this.lastOperation = CompletableFuture.completedFuture(null);
		this.nextOperation = null;
	}
	
	public synchronized CompletableFuture<Void> execute(Supplier<CompletableFuture<Void>> operation) {
		//LOGGER.debug("Setting next operation");
		final Supplier<CompletableFuture<Void>> nextOperation = this.nextOperation;
		this.nextOperation = operation;
		if (nextOperation == null) {
			//LOGGER.debug("Updating the future to include a run of the next operation");
			this.lastOperation = this.lastOperation.thenComposeAsync((o) -> {
				//LOGGER.debug("Next operation is about to be called, so clearing it");
				final Supplier<CompletableFuture<Void>> toExecute = this.nextOperation;
				this.nextOperation = null;
				//LOGGER.debug("Running the next operation");
				return toExecute.get();
			});
		}
		return this.lastOperation;
	}
}
