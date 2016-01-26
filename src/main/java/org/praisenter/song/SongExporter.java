package org.praisenter.song;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SongExporter {
	public void write(Path path, List<Song> songs) throws IOException, SongExportException;
}
