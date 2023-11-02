package org.praisenter.data.media;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.DeleteFilesShutdownHook;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.PraisenterFormatProvider;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.json.PraisenterFormat;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;

final class PraisenterMediaFormatProvider extends PraisenterFormatProvider<Media> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	public PraisenterMediaFormatProvider() {
		super(Media.class);
	}

	@Override
	public boolean isSupported(Path path) {
		return this.isSupported(MimeType.get(path));
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return MimeType.ZIP.is(mimeType);
	}
	
	@Override
	public boolean isSupported(String name, InputStream stream) throws IOException {
		if (!stream.markSupported()) {
			LOGGER.warn("Mark is not supported on the given input stream.");
		}
		
		return this.isSupported(MimeType.get(stream, name));
	}
	
	@Override
	public void exp(PersistAdapter<Media> adapter, OutputStream stream, Media data) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void exp(PersistAdapter<Media> adapter, Path path, Media data) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void exp(PersistAdapter<Media> adapter, ZipOutputStream stream, Media data) throws IOException {
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		
		// the metadata
		LOGGER.trace("Exporting media metadata '{}'", data.getName());
		Path path = mpr.getExportPath(data);
		ZipEntry e = new ZipEntry(FilenameUtils.separatorsToUnix(path.toString()));
		stream.putNextEntry(e);
		JsonIO.write(stream, data);
		stream.closeEntry();
		
		// the media
		LOGGER.trace("Exporting media '{}'", data.getName());
		path = mpr.getExportMediaPath(data);
		e = new ZipEntry(FilenameUtils.separatorsToUnix(path.toString()));
		stream.putNextEntry(e);
		Files.copy(mpr.getMediaPath(data), stream);
		stream.closeEntry();
		
		// the image (video only)
		if (data.getMediaType() == MediaType.VIDEO) {
			LOGGER.trace("Exporting media image '{}'", data.getName());
			path = mpr.getExportImagePath(data);
			e = new ZipEntry(FilenameUtils.separatorsToUnix(path.toString()));
			stream.putNextEntry(e);
			Files.copy(mpr.getImagePath(data), stream);
			stream.closeEntry();
		}
		
		// the thumb
		LOGGER.trace("Exporting media thumbnail '{}'", data.getName());
		path = mpr.getExportThumbPath(data);
		e = new ZipEntry(FilenameUtils.separatorsToUnix(path.toString()));
		stream.putNextEntry(e);
		Files.copy(mpr.getThumbPath(data), stream);
		stream.closeEntry();
	}

	@Override
	public DataImportResult<Media> imp(PersistAdapter<Media> adapter, Path path) throws IOException {
		DataImportResult<Media> result = new DataImportResult<>();
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		
		String name = path.getFileName().toString();
		
		// check for praisenter format package format by reading any metadata files first
		LOGGER.trace("Searching contents of '{}' for media metadata", name);
		List<Media> metadata = new ArrayList<>();
		List<String> files = new ArrayList<>();
		
		// NOTE: Native java.util.zip package can't support zips 4GB or bigger or elements 2GB or bigger
        try (ZipFile zipFile = new ZipFile(path)) {
        	Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        	while (entries.hasMoreElements()) {
        		ZipArchiveEntry entry = entries.nextElement();
        		
        		if (entry.isDirectory()) 
        			continue;
        		
        		if (!zipFile.canReadEntryData(entry)) {
        			LOGGER.warn("Unable to read entry '{}'. This is usually caused by encryption or an unsupported compression algorithm.", entry.getName());
        			continue;
        		}
        		
				String entryName = entry.getName();
				// read all entries
				files.add(entryName);

				// wrap the stream in a stream that can reset
				BufferedInputStream zbis = new BufferedInputStream(zipFile.getInputStream(entry));
				zbis.mark(Integer.MAX_VALUE);

				// check if the entry is a media json file
				LOGGER.trace("Checking if '{}' is media metadata", entryName);
				if (MimeType.JSON.check(zbis, entryName)) {
					zbis.reset();
					try {
						// read the stream to the end
						byte[] data = Streams.read(zbis);
						ByteArrayInputStream bais = new ByteArrayInputStream(data);
						PraisenterFormat format = JsonIO.getPraisenterFormat(bais);
						
						// check the format
						if (format != null && format.is(Media.class)) {
							LOGGER.trace("Entry '{}' is media metadata", entryName);
							bais.reset();
							Media media = JsonIO.read(bais, Media.class);
							if (media.getMediaType() == MediaType.IMAGE) {
								media.setMediaImagePath(mpr.getMediaPath(media));	
							} else if (media.getMediaType() == MediaType.AUDIO) {
								media.setMediaImagePath(mpr.getThumbPath(media));
							} else {
								media.setMediaImagePath(mpr.getImagePath(media));
							}
							media.setMediaPath(mpr.getPath(media));
							media.setMediaThumbnailPath(mpr.getThumbPath(media));
							metadata.add(media);
						}
					} catch (Exception ex) {
						LOGGER.warn("Failed to read resource '" + entryName + "' as a Media object.", ex);
					}
				}
			}
		}
		
		if (!metadata.isEmpty()) {
			LOGGER.trace("Media metadata found in '{}'", name);
			for (Media media : metadata) {
				LOGGER.trace("Importing '{}'", media.getName());
				DataImportResult<Media> res = this.load(adapter, path, media, files);
				result.add(res);
			}
		}
		
		return result;
	}
	
	private DataImportResult<Media> load(PersistAdapter<Media> adapter, Path path, Media media, List<String> files) throws IOException {
		DataImportResult<Media> result = new DataImportResult<>();
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		
		// get the export paths for the media
		String dep = FilenameUtils.separatorsToUnix(mpr.getExportPath(media).toString()); 	 // /{exportPath}/media/{id}.json
		String mep = FilenameUtils.separatorsToUnix(mpr.getExportMediaPath(media).toString()); // /{exportPath}/media/media/{id}.{ext}
		String iep = FilenameUtils.separatorsToUnix(mpr.getExportImagePath(media).toString()); // /{exportPath}/media/images/{id}.jpg
		String tep = FilenameUtils.separatorsToUnix(mpr.getExportThumbPath(media).toString()); // /{exportPath}/media/thumbs/{id}.png

		// get the file paths for the media
		Path dp = mpr.getPath(media);
		Path mp = mpr.getMediaPath(media);
		Path ip = mpr.getImagePath(media);
		Path tp = mpr.getThumbPath(media);
		
		// backup paths
		Path bdp = mpr.getImportPath().resolve(mpr.getFileName(media.getId(), "dpback"));
		Path bmp = mpr.getImportPath().resolve(mpr.getFileName(media.getId(), "mpback"));
		Path bip = mpr.getImportPath().resolve(mpr.getFileName(media.getId(), "ipback"));
		Path btp = mpr.getImportPath().resolve(mpr.getFileName(media.getId(), "tpback"));
		
		// verify all components exist
		LOGGER.trace("Verifying all files exist for '{}' (media, image, thumb, etc)", media.getName());
		boolean mpFound = false;
		boolean ipFound = media.getMediaType() != MediaType.VIDEO; // image is only for video media
		boolean tpFound = false;
		for (String entry : files) {
			if (entry.equals(mep)) mpFound = true;
			if (entry.equals(iep)) ipFound = true;
			if (entry.equals(tep)) tpFound = true;
		}
		
		if (mpFound && ipFound && tpFound) {
			LOGGER.trace("All files exist for '{}', attempting import", media.getName());
			// lock the file path since it may exist
			LOGGER.trace("Getting lock for '{}'", media.getId());
			synchronized(adapter.getLock(media.getId())) {
				LOGGER.trace("Lock for '{}' obtained", media.getId());
				// does the media already exist?
				boolean update = Files.exists(mpr.getPath(media));
				
				if (update) {
					LOGGER.info("Found exiting media with a matching id '" + media.getId() + "', backing up current data.");
					try {
						if (Files.exists(dp)) Files.move(dp, bdp, StandardCopyOption.REPLACE_EXISTING);
						if (Files.exists(mp)) Files.move(mp, bmp, StandardCopyOption.REPLACE_EXISTING);
						if (Files.exists(ip)) Files.move(ip, bip, StandardCopyOption.REPLACE_EXISTING);
						if (Files.exists(tp)) Files.move(tp, btp, StandardCopyOption.REPLACE_EXISTING);
					} catch (Exception ex) {
						LOGGER.warn("Failed to backup all existing files for media '" + media.getName() + "' before performing update.", ex);
					}
				}
				
				// extract the files from the zip
				LOGGER.trace("Extracting files from zip for '{}'", media.getName());
				boolean success = true;
				// NOTE: Native java.util.zip package can't support zips 4GB or bigger or elements 2GB or bigger
		        try (ZipFile zipFile = new ZipFile(path)) {
		        	Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
		        	while (entries.hasMoreElements()) {
		        		ZipArchiveEntry entry = entries.nextElement();
		        		
		        		if (entry.isDirectory()) 
		        			continue;
		        		
		        		if (!zipFile.canReadEntryData(entry)) {
		        			LOGGER.warn("Unable to read entry '{}'. This is usually caused by encryption or an unsupported compression algorithm.", entry.getName());
		        			continue;
		        		}
		        		
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
							LOGGER.trace("Extracting file '{}'", name);
							Files.copy(zipFile.getInputStream(entry), outputPath, StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception ex) {
							success = false;
							LOGGER.warn("Failed to copy zip entry '" + entry.getName() + "' to '" + outputPath + "' due to: " + ex.getMessage(), ex);
							result.getErrors().add(ex);
							break;
						}
					}
				}
					
				if (success) {
					LOGGER.trace("Extract/import successful");
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
					LOGGER.trace("Extract/import failed, one or more files were not found or failed to unzip");
					// recovery
					if (update) {
						try {
							if (Files.exists(bdp))Files.move(bdp, dp, StandardCopyOption.REPLACE_EXISTING);
							if (Files.exists(bmp))Files.move(bmp, mp, StandardCopyOption.REPLACE_EXISTING);
							if (Files.exists(bip))Files.move(bip, ip, StandardCopyOption.REPLACE_EXISTING);
							if (Files.exists(btp))Files.move(btp, tp, StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception ex) {
							LOGGER.warn("Failed to recover all existing files for media '" + media.getName() + "' after update failed", ex);
							
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
		
		return result;
	}

	private void deleteWithShutdownFallback(Path path) {
		try {
			Files.deleteIfExists(path);
		} catch (Exception ex) {
			LOGGER.warn("Failed to delete path '" + path.toAbsolutePath().toString() + "' due to: " + ex.getMessage() + ". Will try again at shutdown.");
			DeleteFilesShutdownHook.deleteOnShutdown(path);
		}
	}
}
