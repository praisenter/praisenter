package org.praisenter.data.slide;

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
import java.util.HashMap;
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

public final class SlideShowPersistAdapter implements PersistAdapter<SlideShow> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String EXTENSION = "json";
	
	private final BasicPathResolver<SlideShow> pathResolver;

	private final LockMap<UUID> locks;
	private final Object exportLock;
	
	private final Map<KnownFormat, DataFormatProvider<SlideShow>> formatProviders;
	
	public SlideShowPersistAdapter(Path basePath) {
		this.pathResolver = new BasicPathResolver<>(basePath, EXTENSION);
		
		this.locks = new LockMap<>();
		this.exportLock = new Object();
		
		this.formatProviders = new HashMap<>();
		this.formatProviders.put(KnownFormat.PRAISENTER3, new PraisenterFormatProvider<>(SlideShow.class));
	}
	
	@Override
	public void initialize() throws IOException {
		this.pathResolver.initialize();
	}
	
	@Override
	public List<SlideShow> load() throws IOException {
		List<SlideShow> items = new ArrayList<SlideShow>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.pathResolver.getBasePath())) {
			for (Path file : stream) {
				if (Files.isRegularFile(file)) {
					String mimeType = MimeType.get(file);
					if (MimeType.JSON.is(mimeType)) {
						try (InputStream is = Files.newInputStream(file)) {
							SlideShow s = JsonIO.read(is, SlideShow.class);
							items.add(s);
						} catch (Exception ex) {
							LOGGER.error("Failed to load '" + file.toAbsolutePath() + "' due to: " + ex.getMessage(), ex);
						}
					}
				}
			}
		}
		return items;
	}
	
	@Override
	public void create(SlideShow item) throws IOException {
		Path path = this.pathResolver.getPath(item);
		synchronized (this.locks.get(item.getId())) {
			if (Files.exists(path)) {
				throw new FileAlreadyExistsException(path.toAbsolutePath().toString());
			}
			JsonIO.write(path, item);
		}
	}
	
	@Override
	public void update(SlideShow item) throws IOException {
		synchronized (this.exportLock) {
			Path path = this.pathResolver.getPath(item);
			synchronized (this.locks.get(item.getId())) {
				JsonIO.write(path, item);
			}
		}
	}
	
	@Override
	public void delete(SlideShow item) throws IOException {
		synchronized (this.exportLock) {
			Path path = this.pathResolver.getPath(item);
			synchronized (this.locks.get(item.getId())) {
				Files.deleteIfExists(path);
			}
		}
	}
	
	@Override
	public DataImportResult<SlideShow> importData(Path path) throws IOException {
		if (!Files.isRegularFile(path)) {
			throw new UnsupportedOperationException("Cannot import data from '" + path.toAbsolutePath() + "' because it's not a regular file.");
		}
		
		Collection<DataFormatProvider<SlideShow>> providers = this.formatProviders.values();
		
		// do any providers support the file as is
		List<DataReadResult<SlideShow>> results = new ArrayList<>();
		for (DataFormatProvider<SlideShow> provider : providers) {
			if (provider.isSupported(path)) {
				// stop here and use this importer
				try {
					results = provider.read(path);
					break;
				} catch (Exception ex) {
					LOGGER.warn("Failed to read '" + path.toAbsolutePath()+ "' using format provider '" + provider.getClass().getName() + "'.", ex);
				}
			}
		}
		
		// no provider could read the file directly
		// is it a zip file that could contain more than
		// one slide in it?
		if (results.isEmpty() && MimeType.ZIP.check(path)) {
			// it is, so iterate the entries for slides to import
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
						List<DataFormatProvider<SlideShow>> supported = new ArrayList<>();
						for (DataFormatProvider<SlideShow> provider : providers) {
							if (provider.isSupported(mimeType)) {
								supported.add(provider);
							}
						}
						
						// if there are supported providers, then read the file fully
						if (!supported.isEmpty()) {
							List<DataReadResult<SlideShow>> itemResults = null;
							ByteArrayInputStream bais = new ByteArrayInputStream(Streams.read(zbis));
							
							// now iterate the providers to see which are supported based on content
							for (DataFormatProvider<SlideShow> provider : supported) {
								if (provider.isSupported(entry.getName(), bais)) {
									// if it's supported, then try to read it in
									try {
										bais.reset();
										itemResults = provider.read(entry.getName(), bais);
										if (itemResults != null && !itemResults.isEmpty()) {
											results.addAll(itemResults);
											break;
										} else {
											LOGGER.info("No slides were found in '" + entry.getName() + "' by provider '" + provider.getClass().getName() + "'.");
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
		
		return this.importSlides(results);
	}
	
	private DataImportResult<SlideShow> importSlides(List<DataReadResult<SlideShow>> results) throws IOException {
		DataImportResult<SlideShow> result = new DataImportResult<>();
		
		for (DataReadResult<SlideShow> drr : results) {
			if (drr == null) continue;
			SlideShow show = drr.getData();
			if (show == null) continue;
			try {
				boolean isUpdate = Files.exists(this.pathResolver.getPath(show));
				if (isUpdate) {
					this.update(show);
					result.getUpdated().add(show);
				} else {
					this.create(show);
					result.getCreated().add(show);
				}
				result.getWarnings().addAll(drr.getWarnings());
			} catch (Exception ex) {
				result.getErrors().add(ex);
			}
		}
		
		return result;
	}
	
	@Override
	public void exportData(KnownFormat format, Path path, SlideShow item) throws IOException {
		synchronized (this.exportLock) {
			DataFormatProvider<SlideShow> provider = this.formatProviders.get(format);
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
	public void exportData(KnownFormat format, ZipOutputStream destination, List<SlideShow> items) throws IOException {
		DataFormatProvider<SlideShow> provider = this.formatProviders.get(format);
		if (provider == null) {
			throw new UnknownFormatException(format.name());
		}
		
		synchronized (this.exportLock) {
			for (SlideShow item : items) {
				ZipEntry entry = new ZipEntry(FilenameUtils.separatorsToUnix(this.pathResolver.getRelativePath(item).toString()));
				destination.putNextEntry(entry);
				provider.write(destination, item);
				destination.closeEntry();
			}
		}
	}
}
