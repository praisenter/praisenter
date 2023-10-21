package org.praisenter.data.media;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.LockMap;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.DeleteFilesShutdownHook;
import org.praisenter.data.KnownFormat;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.json.PraisenterFormat;
import org.praisenter.data.media.tools.MediaTools;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;

public final class MediaPersistAdapter implements PersistAdapter<Media> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String EXTENSION = "json";

	private final MediaConfiguration configuration;
	private final MediaPathResolver pathResolver;
	
	private final MediaTools tools;
	private final MediaLoader[] loaders;
	
	private final LockMap<UUID> locks;
	private final Object exportLock;
	
	public MediaPersistAdapter(Path path, MediaConfiguration configuration) {
		this.configuration = configuration;
		this.pathResolver = new MediaPathResolver(path, EXTENSION);
		this.tools = new MediaTools(this.pathResolver.getBasePath());
		this.loaders = new MediaLoader[] {
			new ImageMediaLoader(this.pathResolver, this.configuration, this.tools),
			new VideoMediaLoader(this.pathResolver, this.configuration, this.tools),
			new AudioMediaLoader(this.pathResolver, this.configuration, this.tools)
		};
		
		this.locks = new LockMap<UUID>();
		this.exportLock = new Object();
	}
	
	@Override
	public void initialize() throws IOException {
		this.pathResolver.initialize();
		this.tools.initialize();
	}
	
	@Override
	public List<Media> load() throws IOException {
		List<Media> items = new ArrayList<Media>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.pathResolver.getBasePath())) {
			for (Path file : stream) {
				if (Files.isRegularFile(file)) {
					String mimeType = MimeType.get(file);
					if (MimeType.JSON.is(mimeType)) {
						try (InputStream is = Files.newInputStream(file)) {
							Media m = JsonIO.read(is, Media.class);
							m.setMediaPath(this.pathResolver.getMediaPath(m));
							if (m.getMediaType() == MediaType.IMAGE) {
								m.setMediaImagePath(this.pathResolver.getMediaPath(m));
							} else if (m.getMediaType() == MediaType.AUDIO) {
								m.setMediaImagePath(this.pathResolver.getThumbPath(m));
							} else {
								m.setMediaImagePath(this.pathResolver.getImagePath(m));
							}
							m.setMediaThumbnailPath(this.pathResolver.getThumbPath(m));
							items.add(m);
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
	public void create(Media item) throws IOException {
		throw new UnsupportedOperationException("Media must be imported instead of created.");
	}

	@Override
	public void update(Media item) throws IOException {
		synchronized (this.exportLock) {
			Path path = this.pathResolver.getPath(item);
			synchronized (this.locks.get(item.getId())) {
				JsonIO.write(path, item);
			}
		}
	}

	@Override
	public void delete(Media item) throws IOException {
		synchronized (this.exportLock) {
			Path path = this.pathResolver.getPath(item);
			synchronized (this.locks.get(item.getId())) {
				// delete the item data first - this ensures that it doesn't show
				// up in the UI any more
				Files.deleteIfExists(path);
				
				// now, it's possible that media could be playing at the time this code
				// executes, so the best thing we can do is to try to delete when the
				// app shutsdown - deleting this stuff is really only for clean up anyway
				this.deleteWithShutdownFallback(this.pathResolver.getMediaPath(item));
				this.deleteWithShutdownFallback(this.pathResolver.getImagePath(item));
				this.deleteWithShutdownFallback(this.pathResolver.getThumbPath(item));
			}
		}
	}
	
	private void deleteWithShutdownFallback(Path path) {
		try {
			Files.deleteIfExists(path);
		} catch (Exception ex) {
			LOGGER.warn("Failed to delete path '" + path.toAbsolutePath().toString() + "' due to: " + ex.getMessage() + ". Will try again at shutdown.");
			DeleteFilesShutdownHook.deleteOnShutdown(path);
		}
	}
	
	@Override
	public void exportData(KnownFormat format, ZipOutputStream destination, List<Media> items) throws IOException {
		synchronized (this.exportLock) {
			for (Media item : items) {
				// is the format Praisenter (i.e. for re-import)?
				// /media
				//		*.json
				// 		/media
				//		/images
				//		/thumbs
				if (format == KnownFormat.PRAISENTER3) {
					// the metadata
					Path path = this.pathResolver.getExportPath(item);
					ZipEntry e = new ZipEntry(FilenameUtils.separatorsToUnix(path.toString()));
					destination.putNextEntry(e);
					JsonIO.write(destination, item);
					destination.closeEntry();
					
					// the media
					path = this.pathResolver.getExportMediaPath(item);
					e = new ZipEntry(FilenameUtils.separatorsToUnix(path.toString()));
					destination.putNextEntry(e);
					Files.copy(this.pathResolver.getMediaPath(item), destination);
					destination.closeEntry();
					
					// the image (video only)
					if (item.getMediaType() == MediaType.VIDEO) {
						path = this.pathResolver.getExportImagePath(item);
						e = new ZipEntry(FilenameUtils.separatorsToUnix(path.toString()));
						destination.putNextEntry(e);
						Files.copy(this.pathResolver.getImagePath(item), destination);
						destination.closeEntry();
					}
					
					// the thumb
					path = this.pathResolver.getExportThumbPath(item);
					e = new ZipEntry(FilenameUtils.separatorsToUnix(path.toString()));
					destination.putNextEntry(e);
					Files.copy(this.pathResolver.getThumbPath(item), destination);
					destination.closeEntry();
				} else {
					// otherwise output just the media
					// all in root
					Path path = this.pathResolver.getMediaFileName(item);
					ZipEntry e = new ZipEntry(FilenameUtils.separatorsToUnix(path.toString()));
					destination.putNextEntry(e);
					Files.copy(this.pathResolver.getMediaPath(item), destination);
					destination.closeEntry();
				}
			}
		}
	}

	@Override
	public void exportData(KnownFormat format, Path path, Media item) throws IOException {
		synchronized (this.exportLock) {
			Files.copy(this.pathResolver.getMediaPath(item), path);
		}
	}

	@Override
	public DataImportResult<Media> importData(Path path) throws IOException {
		DataImportResult<Media> result = new DataImportResult<>();
		
		if (!Files.isRegularFile(path)) {
			throw new UnsupportedOperationException("Cannot import data from '" + path.toAbsolutePath() + "' because it's not a regular file.");
		}
		
		// check for zip file
		if (MimeType.ZIP.check(path)) {
			// check for praisenter format package format by reading any metadata files first
			List<Media> metadata = new ArrayList<>();
			List<String> entries = new ArrayList<>();
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis)) {
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if (!entry.isDirectory()) {
						// read all entries
						entries.add(entry.getName());
						// read any metadata
						Media media = this.tryReadMetadata(entry.getName(), zis);
						if (media != null) {
							metadata.add(media);
						}
					}
				}
			}
			
			if (metadata.isEmpty()) {
				// not praisenter package format, attempt to read any files as media
				try (FileInputStream fis = new FileInputStream(path.toFile());
					 BufferedInputStream bis = new BufferedInputStream(fis);
					 ZipInputStream zis = new ZipInputStream(bis)) {
					ZipEntry entry = null;
					while ((entry = zis.getNextEntry()) != null) {
						if (!entry.isDirectory()) {
							try {
								Media media = this.tryImport(entry.getName(), zis);
								if (media != null) {
									result.getCreated().add(media);
								}
							} catch (MediaImportException ex) {
								result.getWarnings().add(ex.getMessage());
							} catch (Exception ex) {
								result.getErrors().add(ex);
							} 
						}
					}
				}
			} else {
				// the praisenter format
				return this.importPraisenterMedia(path, metadata, entries);
			}
		} else {
			// attempt to import as a single file
			result.getCreated().add(this.tryImport(path));
		}
		
		return result;
	}

	/**
	 * Returns a list of {@link MediaLoader}s for the given path.
	 * @param path the path
	 * @return List&lt;{@link MediaLoader}&gt;
	 */
	private List<MediaLoader> getMediaLoaders(Path path) {
		String mimeType = MimeType.get(path);
		List<MediaLoader> loaders = new ArrayList<>();
		for (MediaLoader loader : this.loaders) {
			if (loader.isSupported(mimeType)) {
				loaders.add(loader);
			}
		}
		return loaders;
	}
	
	/**
	 * Attempts to read the given input stream as a {@link Media} object.
	 * @param resourceName the resource name
	 * @param stream the file data
	 * @return {@link Media}
	 * @throws IOException
	 */
	private Media tryReadMetadata(String resourceName, InputStream stream) {
		if (MimeType.JSON.check(resourceName)) {
			try {
				// read the stream to the end
				byte[] data = Streams.read(stream);
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				PraisenterFormat format = JsonIO.getPraisenterFormat(bais);
				if (format != null && format.is(Media.class)) {
					bais.reset();
					Media media = JsonIO.read(bais, Media.class);
					if (media.getMediaType() == MediaType.IMAGE) {
						media.setMediaImagePath(this.pathResolver.getMediaPath(media));	
					} else if (media.getMediaType() == MediaType.AUDIO) {
						media.setMediaImagePath(this.pathResolver.getThumbPath(media));
					} else {
						media.setMediaImagePath(this.pathResolver.getImagePath(media));
					}
					media.setMediaPath(this.pathResolver.getPath(media));
					media.setMediaThumbnailPath(this.pathResolver.getThumbPath(media));
					return media;
				}
			} catch (Exception ex) {
				LOGGER.warn("Failed to read resource '" + resourceName + "' as a Media object.", ex);
			}
		}
		return null;
	}
	
	/**
	 * Returns a mapping of zip file entry name to path. If the respective path is null, then this indicates
	 * that the file from the zip should not be copied.
	 * @param metadata
	 * @param entries
	 * @return
	 */
	private DataImportResult<Media> importPraisenterMedia(Path path, List<Media> metadata, List<String> entries) throws IOException {
		DataImportResult<Media> result = new DataImportResult<>();

		for (Media media : metadata) {
			// get the export paths for the media
			String dep = FilenameUtils.separatorsToUnix(this.pathResolver.getExportPath(media).toString()); 	 // /{exportPath}/media/{id}.json
			String mep = FilenameUtils.separatorsToUnix(this.pathResolver.getExportMediaPath(media).toString()); // /{exportPath}/media/media/{id}.{ext}
			String iep = FilenameUtils.separatorsToUnix(this.pathResolver.getExportImagePath(media).toString()); // /{exportPath}/media/images/{id}.jpg
			String tep = FilenameUtils.separatorsToUnix(this.pathResolver.getExportThumbPath(media).toString()); // /{exportPath}/media/thumbs/{id}.png

			// get the file paths for the media
			Path dp = this.pathResolver.getPath(media);
			Path mp = this.pathResolver.getMediaPath(media);
			Path ip = this.pathResolver.getImagePath(media);
			Path tp = this.pathResolver.getThumbPath(media);
			
			// backup paths
			Path bdp = this.pathResolver.getImportPath().resolve(this.pathResolver.getFileName(media.getId(), "dpback"));
			Path bmp = this.pathResolver.getImportPath().resolve(this.pathResolver.getFileName(media.getId(), "mpback"));
			Path bip = this.pathResolver.getImportPath().resolve(this.pathResolver.getFileName(media.getId(), "ipback"));
			Path btp = this.pathResolver.getImportPath().resolve(this.pathResolver.getFileName(media.getId(), "tpback"));
			
			// verify all components exist
			boolean mpFound = false;
			boolean ipFound = media.getMediaType() != MediaType.VIDEO; // image is only for video media
			boolean tpFound = false;
			for (String entry : entries) {
				if (entry.equals(mep)) mpFound = true;
				if (entry.equals(iep)) ipFound = true;
				if (entry.equals(tep)) tpFound = true;
			}
			
			if (mpFound && ipFound && tpFound) {
				// lock the file path since it may exist
				synchronized(this.locks.get(media.getId())) {
					// does the media already exist?
					boolean update = Files.exists(this.pathResolver.getPath(media));
					
					if (update) {
						LOGGER.warn("Found exiting media with a matching id '" + media.getId() + "', backing up current data.");
						try {
							if (Files.exists(dp)) Files.move(dp, bdp, StandardCopyOption.REPLACE_EXISTING);
							if (Files.exists(mp)) Files.move(mp, bmp, StandardCopyOption.REPLACE_EXISTING);
							if (Files.exists(ip)) Files.move(ip, bip, StandardCopyOption.REPLACE_EXISTING);
							if (Files.exists(tp)) Files.move(tp, btp, StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception ex) {
							LOGGER.warn("Failed to backup all existing files for media '" + media.getName() + "' before performing update.", ex);
						}
					}
					
					boolean success = true;
					// extract the files from the zip
					try (FileInputStream fis = new FileInputStream(path.toFile());
						 BufferedInputStream bis = new BufferedInputStream(fis);
						 ZipInputStream zis = new ZipInputStream(bis)) {
						ZipEntry entry = null;
						while ((entry = zis.getNextEntry()) != null) {
							if (!entry.isDirectory()) {
								Path outputPath = null;
								String name = entry.getName();
								if (name.equals(dep)) outputPath = dp;
								if (name.equals(mep)) outputPath = mp;
								if (media.getMediaType() == MediaType.VIDEO && name.equals(iep)) outputPath = ip; // image is only for video
								if (name.equals(tep)) outputPath = tp;
								if (outputPath == null)  {
									continue;
								}
								try {
									Files.copy(zis, outputPath, StandardCopyOption.REPLACE_EXISTING);
								} catch (Exception ex) {
									success = false;
									LOGGER.warn("Failed to copy zip entry '" + entry.getName() + "' to '" + outputPath + "' due to: " + ex.getMessage(), ex);
									result.getErrors().add(ex);
									break;
								}
							}
						}
					} catch (Exception ex) {
						success = false;
						LOGGER.warn("Failed to extract file data for media '" + media.getName() + "' due to: " + ex.getMessage(), ex);
						result.getErrors().add(ex);
					}
					
					if (success) {
						if (update)  {
							// delete the backup files
							this.deleteWithShutdownFallback(bdp);
							this.deleteWithShutdownFallback(bmp);
							this.deleteWithShutdownFallback(bip);
							this.deleteWithShutdownFallback(btp);
							
							result.getUpdated().add(media);
						} else {
							result.getCreated().add(media);
						}
					} else {
						// recovery
						if (update) {
							try {
								if (Files.exists(bdp))Files.move(bdp, dp, StandardCopyOption.REPLACE_EXISTING);
								if (Files.exists(bmp))Files.move(bmp, mp, StandardCopyOption.REPLACE_EXISTING);
								if (Files.exists(bip))Files.move(bip, ip, StandardCopyOption.REPLACE_EXISTING);
								if (Files.exists(btp))Files.move(btp, tp, StandardCopyOption.REPLACE_EXISTING);
							} catch (Exception ex) {
								LOGGER.warn("Failed to recover all existing files for media '" + media.getName() + "' after update failed.", ex);
								
								// at this point we have to delete it all because it's in a unknown state
								this.deleteWithShutdownFallback(dp);
								this.deleteWithShutdownFallback(mp);
								this.deleteWithShutdownFallback(ip);
								this.deleteWithShutdownFallback(tp);
							}
						} else {
							// attempt to delete now, but delete on shutdown if necessary
							this.deleteWithShutdownFallback(dp);
							this.deleteWithShutdownFallback(mp);
							this.deleteWithShutdownFallback(ip);
							this.deleteWithShutdownFallback(tp);
						}
					}
				}
			} else {
				result.getErrors().add(new Exception("The given archive file doesn't include the proper data to import '" + media.getName() + "'."));
			}
		}
		
		return result;
	}

	/**
	 * Attempts to import the given path as a single media file.
	 * @param path the file to import
	 * @return {@link Media}
	 * @throws IOException
	 */
	private Media tryImport(Path path) throws IOException {
		String mimeType = MimeType.get(path);
		List<MediaLoader> loaders = this.getMediaLoaders(path);
		if (loaders != null && loaders.size() > 0) {
			for (MediaLoader loader : loaders) {
				try {
					return loader.load(path);
				} catch (Exception ex) {
					LOGGER.warn("Load of media '" + path.toAbsolutePath() + "' failed using '" + loader.getClass().getName() + "'.", ex);
				}
			}
		}
		
		throw new MediaImportException("The media '" + path.toAbsolutePath() + "' with mime type '" + mimeType + "' is not supported.");
	}
	
	/**
	 * Attempts to import the given input stream as a media file.
	 * @param resourceName the resource name
	 * @param stream the stream representing the file data
	 * @return {@link Media}
	 * @throws IOException
	 */
	private Media tryImport(String resourceName, InputStream stream) throws IOException {
		// see if its a supported file type
		String fileName = FilenameUtils.getName(resourceName);
		String mimeType = MimeType.get(resourceName);
		
		boolean isSupported = false;
		for (MediaLoader loader : this.loaders) {
			if (loader.isSupported(mimeType)) {
				isSupported = true;
				break;
			}
		}
		
		// do we think it's supported (based on the file name)?
		if (isSupported) {
			Path tempPath = null;
			try {
				// copy the file to a temp folder
				tempPath = Files.createTempDirectory(this.pathResolver.getImportPath(), "TEMP");
				Path target = tempPath.resolve(fileName);
				Files.copy(stream, target);
				
				// use the media loaders to try to import it
				List<MediaLoader> loaders = this.getMediaLoaders(target);
				if (loaders != null && loaders.size() > 0) {
					for (MediaLoader loader : loaders) {
						try {
							return loader.load(target);
						} catch (Exception ex) {
							LOGGER.warn("Load of media '" + resourceName + "' failed using '" + loader.getClass().getName() + "'.", ex);
						}
					}
					
					throw new MediaImportException("The media '" + resourceName + "' with mime type '" + mimeType + "' failed to be imported. Please view the logs for more details.");
				} 
			} finally {
				// clean up
				if (tempPath != null) {
					try {
						Files.walk(tempPath)
							.sorted((a, b) -> b.compareTo(a)) // reverse; files before dirs
							.forEach(p -> {
						        try { 
						        	Files.delete(p); 
						        } catch(IOException e) {
						        	LOGGER.warn("Failed to delete the temp file '" + p.toAbsolutePath().toString() + "'.", e);
						        }
						     });
					} catch (Exception ex) {
						LOGGER.warn("Failed to clean up temp directory '" + tempPath.toAbsolutePath() + "'.", ex);
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public Path getFilePath(Media media) {
		return this.pathResolver.getPath(media);
	}
}
