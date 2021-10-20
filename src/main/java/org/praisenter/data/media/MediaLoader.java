package org.praisenter.data.media;

import java.io.IOException;
import java.nio.file.Path;

interface MediaLoader {
	public abstract boolean isSupported(String mimeType);
	public abstract Media load(Path path) throws IOException;
}
