package org.praisenter.data.bible;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.LockMap;
import org.praisenter.data.BasicPathResolver;
import org.praisenter.data.DataFormatProvider;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.DataReadResult;
import org.praisenter.data.KnownFormat;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.PraisenterFormatProvider;
import org.praisenter.data.UnknownFormatException;
import org.praisenter.data.json.JsonIO;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;

public final class BiblePersistAdapter implements PersistAdapter<Bible> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String EXTENSION = "json";

	private final BasicPathResolver<Bible> pathResolver;
	
	private final LockMap<UUID> locks;
	private final Object exportLock;
	
	private final Map<KnownFormat, DataFormatProvider<Bible>> formatProviders;
	
	public BiblePersistAdapter(Path path) {
		this.pathResolver = new BasicPathResolver<>(path, "bibles", EXTENSION);
		this.locks = new LockMap<>();
		this.exportLock = new Object();
		this.formatProviders = new LinkedHashMap<>();
		
		this.formatProviders.put(KnownFormat.PRAISENTER3, new PraisenterFormatProvider<>(Bible.class));
		this.formatProviders.put(KnownFormat.UNBOUNDBIBLE, new UnboundBibleFormatProvider());
		this.formatProviders.put(KnownFormat.ZEFANIABIBLE, new ZefaniaBibleFormatProvider());
		this.formatProviders.put(KnownFormat.OPENSONGBIBLE, new OpenSongBibleFormatProvider());
	}

	@Override
	public void initialize() throws IOException {
		this.pathResolver.initialize();
	}
	
	@Override
	public List<Bible> load() throws IOException {
		List<Bible> bibles = new ArrayList<Bible>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.pathResolver.getBasePath())) {
			for (Path file : stream) {
				if (Files.isRegularFile(file)) {
					if (MimeType.JSON.check(file)) {
						try (InputStream is = Files.newInputStream(file)) {
							Bible bible = JsonIO.read(is, Bible.class);
							bibles.add(bible);
						} catch (Exception ex) {
							LOGGER.warn("Failed to load bible '" + file.toAbsolutePath().toString() + "'", ex);
						}
					}
				}
			}
		}
		return bibles;
	}
	
	@Override
	public void create(Bible item) throws IOException {
		Path path = this.pathResolver.getPath(item);
		synchronized (this.locks.get(item.getId())) {
			if (Files.exists(path)) {
				throw new FileAlreadyExistsException(path.toAbsolutePath().toString());
			}
			JsonIO.write(path, item);
		}
	}
	
	@Override
	public void update(Bible item) throws IOException {
		synchronized (this.exportLock) {
			Path path = this.pathResolver.getPath(item);
			synchronized (this.locks.get(item.getId())) {
				JsonIO.write(path, item);
			}
		}
	}
	
	@Override
	public void delete(Bible item) throws IOException {
		synchronized (this.exportLock) {
			Path path = this.pathResolver.getPath(item);
			synchronized (this.locks.get(item.getId())) {
				Files.deleteIfExists(path);
			}
		}
	}
	
	@Override
	public void exportData(KnownFormat format, Path path, Bible item) throws IOException {
		synchronized (this.exportLock) {
			DataFormatProvider<Bible> provider = this.formatProviders.get(format);
			if (provider == null) {
				throw new UnknownFormatException(format.name());
			}
			try (FileOutputStream fos = new FileOutputStream(path.toFile());
				BufferedOutputStream bos = new BufferedOutputStream(fos)) {
				provider.write(bos, item);
			}
		}
	}
	
	@Override
	public void exportData(KnownFormat format, ZipOutputStream destination, List<Bible> items) throws IOException {
		DataFormatProvider<Bible> provider = this.formatProviders.get(format);
		if (provider == null) {
			throw new UnknownFormatException(format.name());
		}
		
		synchronized (this.exportLock) {
			for (Bible item : items) {
				ZipEntry entry = new ZipEntry(FilenameUtils.separatorsToUnix(this.pathResolver.getExportPath(item).toString()));
				destination.putNextEntry(entry);
				provider.write(destination, item);
				destination.closeEntry();
			}
		}
	}
	
	@Override
	public DataImportResult<Bible> importData(Path path) throws IOException {
		if (!Files.isRegularFile(path)) {
			throw new UnsupportedOperationException("Cannot import data from '" + path.toAbsolutePath() + "' because it's not a regular file.");
		}
		
		Collection<DataFormatProvider<Bible>> providers = this.formatProviders.values();
		
		// do any providers support the file as is
		List<DataReadResult<Bible>> results = new ArrayList<>();
		for (DataFormatProvider<Bible> provider : providers) {
			if (provider.isSupported(path)) {
				// stop here and use this importer
				try {
					LOGGER.info("Reading '{}' using provider '{}'", path, provider.getClass());
					results = provider.read(path);
					break;
				} catch (Exception ex) {
					LOGGER.warn("Failed to read '" + path.toAbsolutePath() + "' using format provider '" + provider.getClass().getName() + "'.", ex);
				}
			}
		}
		
		// no provider could read the file directly
		// is it a zip file that could contain more than
		// one bible in it?
		if (results.isEmpty() && MimeType.ZIP.check(path)) {
			// it is, so iterate the entries for bibles to import
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if (!entry.isDirectory()) {
						BufferedInputStream zbis = new BufferedInputStream(zis);
						
						// get the mime type of the file
						zbis.mark(Integer.MAX_VALUE);
						String mimeType = MimeType.get(zbis, entry.getName());
						zbis.reset();
						
						// get the set of supported providers for the mimetype
						List<DataFormatProvider<Bible>> supported = new ArrayList<>();
						for (DataFormatProvider<Bible> provider : providers) {
							if (provider.isSupported(mimeType)) {
								supported.add(provider);
							}
						}
						
						// if there are supported providers, then read the file fully
						if (!supported.isEmpty()) {
							List<DataReadResult<Bible>> itemResults = null;
							ByteArrayInputStream bais = new ByteArrayInputStream(Streams.read(zbis));
							
							// now iterate the providers to see which are supported based on content
							for (DataFormatProvider<Bible> provider : supported) {
								if (provider.isSupported(entry.getName(), bais)) {
									// if it's supported, then try to read it in
									try {
										bais.reset();
										itemResults = provider.read(entry.getName(), bais);
										if (itemResults != null && !itemResults.isEmpty()) {
											results.addAll(itemResults);
											break;
										} else {
											LOGGER.info("No bibles were found in '" + entry.getName() + "' by provider '" + provider.getClass().getName() + "'.");
										}
									} catch (Exception ex) {
										LOGGER.warn("Failed to read '" + entry.getName() + "' using format provider '" + provider.getClass().getName() + "'.", ex);
									}
								}
								bais.reset();
							}
							
							if (itemResults == null || itemResults.isEmpty()) {
								LOGGER.warn("The content of the file '" + entry.getName() + "' was not recognized by any supported format provider.");
							}
						} else {
							LOGGER.warn("The mime type '" + mimeType + "' of the file '" + entry.getName() + "' was not recognized by any supported format provider.");
						}
					}
				}
			}
		}
		
		return this.importBibles(results);
	}
	
	private DataImportResult<Bible> importBibles(List<DataReadResult<Bible>> results) throws IOException {
		DataImportResult<Bible> result = new DataImportResult<>();
		
		for (DataReadResult<Bible> drr : results) {
			if (drr == null) continue;
			Bible bible = drr.getData();
			if (bible == null) continue;
			try {
				boolean isUpdate = Files.exists(this.pathResolver.getPath(bible));
				if (isUpdate) {
					this.update(bible);
					result.getUpdated().add(bible);
				} else {
					this.create(bible);
					result.getCreated().add(bible);
				}
				result.getWarnings().addAll(drr.getWarnings());
			} catch (Exception ex) {
				result.getErrors().add(ex);
			}
		}
		
		return result;
	}
	
	@Override
	public Path getFilePath(Bible item) {
		return this.pathResolver.getPath(item);
	}
	
//	@Override
//	protected BibleSearchResult processSearchResult(SearchResult result) {
//		Document document = result.getDocument();
//		
//		// get the item from the library
//		UUID bibleId = UUID.fromString(document.get(FIELD_ID));
//		Bible item = this.getItem(bibleId);
//		
//		// make sure it exists in the library
//		if (item == null) return null;
//		
//		// get other data from the document
//		int bookNumber = document.getField(FIELD_BOOK_NUMBER).numericValue().intValue();
//		int chapterNumber = document.getField(FIELD_VERSE_CHAPTER).numericValue().intValue();
//		int verseNumber = document.getField(FIELD_VERSE_NUMBER).numericValue().intValue();
//		
//		// try to find the verse before returning it
//		LocatedVerse lv = item.getVerse(bookNumber, chapterNumber, verseNumber);
//		
//		// make sure the location exists in the bible still
//		if (lv != null) {
//			return new BibleSearchResult(item, lv.getBook(), lv.getChapter(), lv.getVerse(), result.getMatches(), result.getScore());
//		}
//		return null;
//	}
}
