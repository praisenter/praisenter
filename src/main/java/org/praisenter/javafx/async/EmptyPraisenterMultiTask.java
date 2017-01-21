package org.praisenter.javafx.async;

import java.util.concurrent.ExecutorService;

public final class EmptyPraisenterMultiTask<T, V> extends PraisenterMultiTask<T, V> {
	private EmptyPraisenterMultiTask() {
		super("", null);
	}
	
	public static final <T, V> EmptyPraisenterMultiTask<T, V> create() {
		return new EmptyPraisenterMultiTask<T, V>();
	}
	
	@Override
	protected Void call() throws Exception {
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
