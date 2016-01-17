package org.praisenter.data.song;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SongFormatReader {
	public List<Song> read(Path path) throws IOException, SongFormatReaderException;
}
