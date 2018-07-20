package org.praisenter.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DataParser<T> {
	public List<T> parse(String resourceName, InputStream stream) throws IOException;
}
