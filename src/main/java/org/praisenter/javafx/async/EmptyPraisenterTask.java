package org.praisenter.javafx.async;

import java.util.concurrent.ExecutorService;

public final class EmptyPraisenterTask<T, V> extends PraisenterTask<T, V> {
	private EmptyPraisenterTask() {
		super("", null);
	}
	
	public static final <T, V> EmptyPraisenterTask<T, V> create() {
		return new EmptyPraisenterTask<T, V>();
	}
	
	@Override
	protected T call() throws Exception {
		this.setResultStatus(PraisenterTaskResultStatus.SUCCESS);
		return null;
	}
	
	@Override
	public void execute(ExecutorService service) {
		try {
			this.call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
