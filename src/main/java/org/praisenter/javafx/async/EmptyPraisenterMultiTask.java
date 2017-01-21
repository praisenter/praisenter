package org.praisenter.javafx.async;

import java.util.List;
import java.util.concurrent.ExecutorService;

public final class EmptyPraisenterMultiTask<T extends PraisenterTask<?, ?>> extends PraisenterMultiTask<T> {
	private EmptyPraisenterMultiTask() {
		super("", null);
	}
	
	public static final <T extends PraisenterTask<?, ?>> EmptyPraisenterMultiTask<T> create() {
		return new EmptyPraisenterMultiTask<T>();
	}
	
	@Override
	protected List<T> call() throws Exception {
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
