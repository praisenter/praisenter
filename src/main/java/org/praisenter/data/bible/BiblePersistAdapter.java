package org.praisenter.data.bible;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.praisenter.data.AbstractPersistAdapter;
import org.praisenter.data.BasicPathResolver;
import org.praisenter.data.ImportExportFormat;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.PraisenterFormatProvider;
import org.praisenter.data.json.JsonIO;
import org.praisenter.utility.MimeType;

public final class BiblePersistAdapter extends AbstractPersistAdapter<Bible, BasicPathResolver<Bible>> implements PersistAdapter<Bible> {
	private static final String EXTENSION = "json";

	public BiblePersistAdapter(Path path) {
		super(new BasicPathResolver<>(path, "bibles", EXTENSION));

		this.importExportProviders.put(ImportExportFormat.PRAISENTER3, new PraisenterFormatProvider<>(Bible.class));
		this.importExportProviders.put(ImportExportFormat.UNBOUNDBIBLE, new UnboundBibleFormatProvider());
		this.importExportProviders.put(ImportExportFormat.ZEFANIABIBLE, new ZefaniaBibleFormatProvider());
		this.importExportProviders.put(ImportExportFormat.OPENSONGBIBLE, new OpenSongBibleFormatProvider());
	}

	@Override
	protected Bible load(Path path) throws IOException {
		if (Files.isRegularFile(path)) {
			if (MimeType.JSON.check(path)) {
				return JsonIO.read(path, Bible.class);
			}
		}
		return null;
	}
	
	@Override
	protected void create(Path path, Bible item) throws IOException {
		JsonIO.write(path, item);
	}
	
	@Override
	protected void update(Path path, Bible item) throws IOException {
		JsonIO.write(path, item);
	}
	
	@Override
	protected void delete(Path path, Bible item) throws IOException {
		Files.deleteIfExists(path);
	}
}
