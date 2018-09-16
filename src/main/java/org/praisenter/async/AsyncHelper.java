package org.praisenter.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.praisenter.ThrowableConsumer;
import org.praisenter.ThrowableFunction;
import org.praisenter.ThrowableRunnable;

import javafx.application.Platform;
import javafx.concurrent.Task;

public final class AsyncHelper {
	
	public static final CompletableFuture<Void> NO_RETURN = nil();
	
	private AsyncHelper() {}
	
	public static <T> CompletableFuture<T> nil() {
		return CompletableFuture.completedFuture(null);
	}
	
	public static <T> Function<T, CompletableFuture<Void>> onJavaFXThreadAndWait(final ThrowableConsumer<T> operation) {
		return (T data) -> {
			CompletableFuture<Void> future = new CompletableFuture<Void>();
			
			if (Platform.isFxApplicationThread()) {
			       try {
			           operation.accept(data);
			           future.complete(null);
			       } catch (Throwable t) {
			           future.completeExceptionally(t);
			       }
			  } else {
			      Platform.runLater(() -> {
			          try {
			              operation.accept(data);
			              future.complete(null);
			          } catch (Throwable t) {
			              future.completeExceptionally(t);
			          }
			      });
			  }
			
			return future;
		};
	}
	
	public static <T, E> Function<T, CompletableFuture<E>> onJavaFXThreadAndWait(final ThrowableFunction<T, E> operation) {
		return (T data) -> {
			CompletableFuture<E> future = new CompletableFuture<E>();
			
			if (Platform.isFxApplicationThread()) {
			       try {
			           future.complete(operation.apply(data));
			       } catch (Throwable t) {
			           future.completeExceptionally(t);
			       }
			  } else {
			      Platform.runLater(() -> {
			          try {
			        	  future.complete(operation.apply(data));
			          } catch (Throwable t) {
			              future.completeExceptionally(t);
			          }
			      });
			  }
			
			return future;
		};
	}
	
	public static <T> Function<T, CompletableFuture<Void>> onJavaFXThreadAndWait(final ThrowableRunnable operation) {
		return (T data) -> {
			CompletableFuture<Void> future = new CompletableFuture<Void>();
			
			if (Platform.isFxApplicationThread()) {
			       try {
			    	   operation.run();
			           future.complete(null);
			       } catch (Throwable t) {
			           future.completeExceptionally(t);
			       }
			  } else {
			      Platform.runLater(() -> {
			          try {
			        	  operation.run();
			        	  future.complete(null);
			          } catch (Throwable t) {
			              future.completeExceptionally(t);
			          }
			      });
			  }
			
			return future;
		};
	}

	public static <T> Task<T> toJavaFXTask(CompletableFuture<T> future) {
		return new Task<T>() {
			@Override
			protected T call() throws Exception {
				return future.get();
			}
		};
	}

	public static void onJavaFXThread(Runnable operation) {
		if (Platform.isFxApplicationThread()) {
			operation.run();
		} else {
			Platform.runLater(operation);
		}
	}
}
