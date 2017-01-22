package org.praisenter.javafx.async;

import java.util.concurrent.Callable;

public final class CallableAsyncTask<T> extends AsyncTask<T> {
	private final Callable<T> lambda;
	
	public CallableAsyncTask(Callable<T> lambda) {
		this(null, lambda);
	}
	
	public CallableAsyncTask(String name, Callable<T> lambda) {
		super(name);
		this.lambda = lambda;
	}
	
	@Override
	protected T call() throws Exception {
		return this.lambda.call();
	}
}
