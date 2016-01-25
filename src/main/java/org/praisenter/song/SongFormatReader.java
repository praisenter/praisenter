package org.praisenter.song;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.praisenter.song.openlyrics.OpenLyricsSong;

public interface SongFormatReader {
	public List<OpenLyricsSong> read(Path path) throws IOException, SongFormatReaderException;
}
