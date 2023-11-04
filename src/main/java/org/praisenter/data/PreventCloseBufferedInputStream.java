package org.praisenter.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class PreventCloseBufferedInputStream extends BufferedInputStream {

	public PreventCloseBufferedInputStream(InputStream in, int size) {
		super(in, size);
	}

	public PreventCloseBufferedInputStream(InputStream in) {
		super(in);
	}
	
	@Override
	public void close() throws IOException {
		// NOTE: we want to prevent close here
	}
}
