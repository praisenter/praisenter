package org.praisenter.javafx.async;

import java.util.List;
import java.util.concurrent.Callable;

public final class AsyncTaskFactory {
	private AsyncTaskFactory() {}
	
	public static <T> AsyncTask<T> empty() {
		return new CallableAsyncTask<T>(() -> { return null; });
	}
	
	public static <T> AsyncTask<T> single(Callable<T> lambda) {
		return new CallableAsyncTask<T>(lambda);
	}
	
	public static <T> AsyncTask<T> single(String name, Callable<T> lambda) {
		return new CallableAsyncTask<T>(name, lambda);
	}
	
	public static <T extends AsyncTask<?>> AsyncGroupTask<T> none() {
		return new AsyncGroupTask<T>(null);
	}
	
	public static <T extends AsyncTask<?>> AsyncGroupTask<T> group(List<T> tasks) {
		return new AsyncGroupTask<T>(tasks);
	}
	
	public static <T extends AsyncTask<?>> AsyncGroupTask<T> group(String name, List<T> tasks) {
		return new AsyncGroupTask<T>(name, tasks);
	}
}
