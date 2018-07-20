package org.praisenter.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

import org.praisenter.data.DataReadResult;

public interface DataFormatProvider<T> {
	public boolean isSupported(Path path);
	public boolean isSupported(String mimeType);
	public boolean isSupported(String resourceName, InputStream stream) throws IOException;
	
	public void write(Path path, T data) throws IOException;
	public void write(OutputStream stream, T data) throws IOException;
	
	public List<DataReadResult<T>> read(Path path) throws IOException;
	public List<DataReadResult<T>> read(String resourceName, InputStream stream) throws IOException;
}
