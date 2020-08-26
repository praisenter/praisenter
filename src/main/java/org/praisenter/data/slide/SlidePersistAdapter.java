package org.praisenter.data.slide;

import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.LockMap;
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

public final class SlidePersistAdapter implements PersistAdapter<Slide> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String EXTENSION = "json";
	
	private final SlideConfiguration configuration;
	private final SlidePathResolver pathResolver;
	private final SlideRenderer renderer;

	private final LockMap<UUID> locks;
	private final Object exportLock;
	
	private final Map<KnownFormat, DataFormatProvider<Slide>> formatProviders;
	
	public SlidePersistAdapter(Path basePath, SlideRenderer renderer, SlideConfiguration configuration) {
		this.configuration = configuration;
		this.pathResolver = new SlidePathResolver(basePath, EXTENSION);
		this.renderer = renderer;
		
		this.locks = new LockMap<>();
		this.exportLock = new Object();
		
		this.formatProviders = new LinkedHashMap<>();
		this.formatProviders.put(KnownFormat.PRAISENTER3, new PraisenterFormatProvider<>(Slide.class));
	}
	
	@Override
	public void initialize() throws IOException {
		this.pathResolver.initialize();
	}
	
	@Override
	public List<Slide> load() throws IOException {
		List<Slide> items = new ArrayList<Slide>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.pathResolver.getBasePath())) {
			for (Path file : stream) {
				if (Files.isRegularFile(file)) {
					String mimeType = MimeType.get(file);
					if (MimeType.JSON.is(mimeType)) {
						try (InputStream is = Files.newInputStream(file)) {
							Slide s = JsonIO.read(is, Slide.class);
							s.setThumbnailPath(this.pathResolver.getThumbPath(s));
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
	public void create(Slide item) throws IOException {
		Path path = this.pathResolver.getPath(item);
		synchronized (this.locks.get(item.getId())) {
			if (Files.exists(path)) {
				throw new FileAlreadyExistsException(path.toAbsolutePath().toString());
			}
			Path thumbnailPath = this.pathResolver.getThumbPath(item);
			BufferedImage image = this.renderer.renderThumbnail(item, this.configuration.getThumbnailWidth(), this.configuration.getThumbnailHeight());
			ImageIO.write(image, this.pathResolver.getThumbExtension(), thumbnailPath.toFile());
			JsonIO.write(path, item);
			item.setThumbnailPath(thumbnailPath);
		}
	}
	
	@Override
	public void update(Slide item) throws IOException {
		synchronized (this.exportLock) {
			Path path = this.pathResolver.getPath(item);
			synchronized (this.locks.get(item.getId())) {
				BufferedImage image = this.renderer.renderThumbnail(item, this.configuration.getThumbnailWidth(), this.configuration.getThumbnailHeight());
				ImageIO.write(image, this.pathResolver.getThumbExtension(), this.pathResolver.getThumbPath(item).toFile());
				JsonIO.write(path, item);
			}
		}
	}
	
	@Override
	public void delete(Slide item) throws IOException {
		synchronized (this.exportLock) {
			Path path = this.pathResolver.getPath(item);
			synchronized (this.locks.get(item.getId())) {
				Files.deleteIfExists(path);
				Files.deleteIfExists(this.pathResolver.getThumbPath(item));
			}
		}
	}
	
	@Override
	public DataImportResult<Slide> importData(Path path) throws IOException {
		if (!Files.isRegularFile(path)) {
			throw new UnsupportedOperationException("Cannot import data from '" + path.toAbsolutePath() + "' because it's not a regular file.");
		}
		
		Collection<DataFormatProvider<Slide>> providers = this.formatProviders.values();
		
		// do any providers support the file as is
		List<DataReadResult<Slide>> results = new ArrayList<>();
		for (DataFormatProvider<Slide> provider : providers) {
			if (provider.isSupported(path)) {
				// stop here and use this importer
				try {
					results = provider.read(path);
					break;
				} catch (Exception ex) {
					LOGGER.trace("Failed to read '" + path.toAbsolutePath()+ "' using format provider '" + provider.getClass().getName() + "'.", ex);
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
						List<DataFormatProvider<Slide>> supported = new ArrayList<>();
						for (DataFormatProvider<Slide> provider : providers) {
							if (provider.isSupported(mimeType)) {
								supported.add(provider);
							}
						}
						
						// if there are supported providers, then read the file fully
						if (!supported.isEmpty()) {
							List<DataReadResult<Slide>> itemResults = null;
							ByteArrayInputStream bais = new ByteArrayInputStream(Streams.read(zbis));
							
							// now iterate the providers to see which are supported based on content
							for (DataFormatProvider<Slide> provider : supported) {
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
										LOGGER.trace("Failed to read '" + entry.getName() + "' using format provider '" + provider.getClass().getName() + "'.", ex);
									}
								}
								bais.reset();
							}
							
							if (itemResults == null || itemResults.isEmpty()) {
								LOGGER.trace("The content of the file '" + entry.getName() + "' was not recognized by any supported format provider.");
							}
						} else {
							LOGGER.trace("The mime type '" + mimeType + "' of the file '" + entry.getName() + "' was not recognized by any supported format provider.");
						}
					}
				}
			}
		}
		
		return this.importSlides(results);
	}
	
	private DataImportResult<Slide> importSlides(List<DataReadResult<Slide>> results) throws IOException {
		DataImportResult<Slide> result = new DataImportResult<>();
		
		for (DataReadResult<Slide> drr : results) {
			if (drr == null) continue;
			Slide slide = drr.getData();
			if (slide == null) continue;
			try {
				boolean isUpdate = Files.exists(this.pathResolver.getPath(slide));
				if (isUpdate) {
					this.update(slide);
					result.getUpdated().add(slide);
				} else {
					this.create(slide);
					result.getCreated().add(slide);
				}
				result.getWarnings().addAll(drr.getWarnings());
			} catch (Exception ex) {
				result.getErrors().add(ex);
			}
		}
		
		return result;
	}
	
	@Override
	public void exportData(KnownFormat format, Path path, Slide item) throws IOException {
		synchronized (this.exportLock) {
			DataFormatProvider<Slide> provider = this.formatProviders.get(format);
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
	public void exportData(KnownFormat format, ZipOutputStream destination, List<Slide> items) throws IOException {
		DataFormatProvider<Slide> provider = this.formatProviders.get(format);
		if (provider == null) {
			throw new UnknownFormatException(format.name());
		}
		
		synchronized (this.exportLock) {
			for (Slide item : items) {
				ZipEntry entry = new ZipEntry(FilenameUtils.separatorsToUnix(this.pathResolver.getExportPath(item).toString()));
				destination.putNextEntry(entry);
				provider.write(destination, item);
				destination.closeEntry();
			}
		}
	}
	
	@Override
	public Path getFilePath(Slide item) {
		return this.pathResolver.getPath(item);
	}
}
