package org.praisenter.data.song;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.praisenter.data.AbstractPersistAdapter;
import org.praisenter.data.BasicPathResolver;
import org.praisenter.data.ImportExportFormat;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.PraisenterFormatProvider;
import org.praisenter.data.RawExportFormatProvider;
import org.praisenter.data.json.JsonIO;
import org.praisenter.utility.MimeType;

public final class SongPersistAdapter extends AbstractPersistAdapter<Song, BasicPathResolver<Song>> implements PersistAdapter<Song> {
	private static final String EXTENSION = "json";

	public SongPersistAdapter(Path path) {
		super(new BasicPathResolver<>(path, "songs", EXTENSION));
		
		// always attempt to detect in this order
		
		// JSON - no other supported formats are JSON
		this.importExportProviders.put(ImportExportFormat.PRAISENTER3, new PraisenterFormatProvider<>(Song.class));
		// XML but has schema definition which defines it specifically
		this.importExportProviders.put(ImportExportFormat.OPENLYRICSSONG, new OpenLyricsSongFormatProvider());
		// XML but has a very unique starting element
		this.importExportProviders.put(ImportExportFormat.CHURCHVIEWSONG, new ChurchViewSongFormatProvider());
		// XML but should have a version string = 2.0.0
		this.importExportProviders.put(ImportExportFormat.PRAISENTER2, new Praisenter2SongFormatProvider());
		// XML not much to differentiate between other formats
		this.importExportProviders.put(ImportExportFormat.PRAISENTER1, new Praisenter1SongFormatProvider());
		// plain text
		this.importExportProviders.put(ImportExportFormat.CHORDPRO, new ChordProSongFormatProvider());
		// raw (export as-is from library)
		this.importExportProviders.put(ImportExportFormat.RAW, new RawExportFormatProvider<Song>());
	}
	
	@Override
	protected Song load(Path path) throws IOException {
		if (Files.isRegularFile(path)) {
			if (MimeType.JSON.check(path)) {
				try (InputStream is = Files.newInputStream(path)) {
					return JsonIO.read(is, Song.class);
				}
			}
		}
		return null;
	}
	
	@Override
	protected void create(Path path, Song item) throws IOException {
		JsonIO.write(path, item);
	}
	
	@Override
	protected void update(Path path, Song item) throws IOException {
		JsonIO.write(path, item);
	}
	
	@Override
	protected void delete(Path path, Song item) throws IOException {
		Files.deleteIfExists(path);
	}
}
