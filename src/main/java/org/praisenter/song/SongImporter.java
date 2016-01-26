package org.praisenter.song;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SongImporter {
	public List<Song> read(Path path) throws IOException, SongImportException;
}
