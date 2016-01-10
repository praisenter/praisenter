package org.praisenter.media;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public interface MediaLoader {

	public abstract boolean isSupported(String mimeType);
	
	public abstract LoadedMedia load(Path path) throws IOException, FileNotFoundException, MediaFormatException;
	
	public abstract MediaThumbnailSettings getThumbnailSettings();
}
