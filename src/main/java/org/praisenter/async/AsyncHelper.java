package org.praisenter.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import org.praisenter.ThrowableConsumer;
import org.praisenter.ThrowableFunction;
import org.praisenter.ThrowableRunnable;

import javafx.application.Platform;

public final class AsyncHelper {
	private AsyncHelper() {}

	public static <T> Function<T, CompletableFuture<Void>> onJavaFXThreadAndWait(final ThrowableConsumer<T> operation) {
		return AsyncHelper.onSpecificThreadAndWait(operation, true);
	}

	public static <T, E> Function<T, CompletableFuture<E>> onJavaFXThreadAndWait(final ThrowableFunction<T, E> operation) {
		return AsyncHelper.onSpecificThreadAndWait(operation, true);
	}
	
	public static <T> Function<T, CompletableFuture<Void>> onJavaFXThreadAndWait(final ThrowableRunnable operation) {
		return AsyncHelper.onSpecificThreadAndWait(operation, true);
	}
	
	public static <T> Function<T, CompletableFuture<Void>> offJavaFXThreadAndWait(final ThrowableConsumer<T> operation) {
		return AsyncHelper.onSpecificThreadAndWait(operation, false);
	}

	public static <T, E> Function<T, CompletableFuture<E>> offJavaFXThreadAndWait(final ThrowableFunction<T, E> operation) {
		return AsyncHelper.onSpecificThreadAndWait(operation, false);
	}
	
	public static <T> Function<T, CompletableFuture<Void>> offJavaFXThreadAndWait(final ThrowableRunnable operation) {
		return AsyncHelper.onSpecificThreadAndWait(operation, false);
	}
	
	public static void onJavaFXThread(Runnable operation) {
		if (Platform.isFxApplicationThread()) {
			operation.run();
		} else {
			Platform.runLater(operation);
		}
	}

	public static List<Throwable> getExceptions(CompletableFuture<?>[] futures) {
		return AsyncHelper.getExceptions(Arrays.asList(futures));
	}
	
	public static List<Throwable> getExceptions(Iterable<CompletableFuture<?>> futures) {
		List<Throwable> exceptions = new ArrayList<>();
		for (CompletableFuture<?> future : futures) {
			try {
				future.join();
			} catch (CompletionException ex) {
				// handle re-wrapped completion exceptions
				Throwable t = ex.getCause();
				while (t instanceof CompletionException) {
					t = t.getCause();
				}
				exceptions.add(t);
			} catch (Exception ex) {
				exceptions.add(ex);
			}
		}
		return exceptions;
	}
	
	// Consumer<T>
	
	private static <T> Function<T, CompletableFuture<Void>> onSpecificThreadAndWait(final ThrowableConsumer<T> operation, boolean runOnJavaFXThread) {
		return (T data) -> {
			CompletableFuture<Void> future = new CompletableFuture<Void>();
			
			boolean isJavaFXThread = Platform.isFxApplicationThread();
			if (runOnJavaFXThread && !isJavaFXThread) {
				// then run it on the JavaFX thread
			    Platform.runLater(() -> {
			        consume(operation, data, future);
			    });
			} else if (!runOnJavaFXThread && isJavaFXThread) {
				// then run it on a thread-pool thread
				CompletableFuture.runAsync(() -> {
					consume(operation, data, future);
				});
			} else {
				// otherwise, run it on the current thread
				consume(operation, data, future);
			}
			
			return future;
		};
	}

	private static <T> void consume(final ThrowableConsumer<T> operation, final T data, CompletableFuture<Void> future) {
		try {
            operation.accept(data);
            future.complete(null);
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
	}
	
	// Function<T,E>
	
	private static <T, E> Function<T, CompletableFuture<E>> onSpecificThreadAndWait(final ThrowableFunction<T, E> operation, boolean runOnJavaFXThread) {
		return (T data) -> {
			CompletableFuture<E> future = new CompletableFuture<E>();
			
			boolean isJavaFXThread = Platform.isFxApplicationThread();
			if (runOnJavaFXThread && !isJavaFXThread) {
				// then run it on the JavaFX thread
			    Platform.runLater(() -> {
			    	apply(operation, data, future);
			    });
			} else if (!runOnJavaFXThread && isJavaFXThread) {
				// then run it on a thread-pool thread
				CompletableFuture.runAsync(() -> {
					apply(operation, data, future);
				});
			} else {
				// otherwise, run it on the current thread
				apply(operation, data, future);
			}
			
			return future;
		};
	}
	
	private static <T, E> void apply(final ThrowableFunction<T, E> operation, final T data, CompletableFuture<E> future) {
		try {
			future.complete(operation.apply(data));
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
	}
	
	// Runnable
	
	private static <T> Function<T, CompletableFuture<Void>> onSpecificThreadAndWait(final ThrowableRunnable operation, boolean runOnJavaFXThread) {
		return (T data) -> {
			CompletableFuture<Void> future = new CompletableFuture<Void>();
			
			boolean isJavaFXThread = Platform.isFxApplicationThread();
			if (runOnJavaFXThread && !isJavaFXThread) {
				// then run it on the JavaFX thread
			    Platform.runLater(() -> {
			    	run(operation, future);
			    });
			} else if (!runOnJavaFXThread && isJavaFXThread) {
				// then run it on a thread-pool thread
				CompletableFuture.runAsync(() -> {
					run(operation, future);
				});
			} else {
				// otherwise, run it on the current thread
				run(operation, future);
			}
			
			return future;
		};
	}
	
	private static <T, E> void run(final ThrowableRunnable operation, CompletableFuture<E> future) {
		try {
			operation.run();
			future.complete(null);
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
	}
}
